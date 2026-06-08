import { useCallback, useEffect, useMemo, useState } from 'react';
import { createVehiculo, deleteVehiculo, getVehiculos, updateVehiculo } from '../api/endpoints';

const emptyForm = {
  placa: '',
  marca: '',
  capacidad_kg: '',
};

const normalizeVehicles = (response) => response.data?.results ?? response.data ?? [];

export default function Vehiculos() {
  const [vehiculos, setVehiculos] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const response = await getVehiculos();
      setVehiculos(normalizeVehicles(response));
    } catch {
      setError('No se pudo cargar la flota.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const stats = useMemo(() => {
    const totalCapacity = vehiculos.reduce((sum, item) => sum + Number(item.capacidad_kg || 0), 0);
    const averageCapacity = vehiculos.length ? totalCapacity / vehiculos.length : 0;

    return {
      count: vehiculos.length,
      totalCapacity,
      averageCapacity,
    };
  }, [vehiculos]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  };

  const resetForm = () => {
    setForm(emptyForm);
    setEditingId(null);
    setError('');
  };

  const handleEdit = (vehiculo) => {
    setEditingId(vehiculo.id_vehiculo);
    setForm({
      placa: vehiculo.placa ?? '',
      marca: vehiculo.marca ?? '',
      capacidad_kg: vehiculo.capacidad_kg ?? '',
    });
    setError('');
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');

    const payload = {
      placa: form.placa.trim().toUpperCase(),
      marca: form.marca.trim(),
      capacidad_kg: Number(form.capacidad_kg),
    };

    if (!payload.placa) {
      setError('Ingresa la placa del vehiculo.');
      return;
    }

    if (!payload.capacidad_kg || payload.capacidad_kg <= 0) {
      setError('La capacidad debe ser mayor que cero.');
      return;
    }

    setSaving(true);
    try {
      if (editingId) {
        await updateVehiculo(editingId, payload);
      } else {
        await createVehiculo(payload);
      }
      resetForm();
      await load();
    } catch (err) {
      const data = err.response?.data;
      const detail = data?.placa?.[0] || data?.capacidad_kg?.[0] || data?.detail;
      setError(detail || 'No se pudo guardar el vehiculo.');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (vehiculo) => {
    const ok = window.confirm(`Eliminar vehiculo ${vehiculo.placa}?`);
    if (!ok) return;

    setError('');
    try {
      await deleteVehiculo(vehiculo.id_vehiculo);
      if (editingId === vehiculo.id_vehiculo) resetForm();
      await load();
    } catch {
      setError('No se pudo eliminar el vehiculo.');
    }
  };

  if (loading && !vehiculos.length) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[300px] text-[#AFB3B7]">
        <div className="spinner mb-3"></div>
        Cargando vehiculos...
      </div>
    );
  }

  return (
    <>
      <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4 mb-6 fade-in">
        <div>
          <h2 className="text-2xl font-bold text-white flex items-center gap-3">
            <i className="bi bi-truck text-sky-400"></i> Gestion de Vehiculos
          </h2>
          <p className="text-slate-500 text-sm mt-1">Administra la flota disponible para despachos.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <div className="bg-surface border border-surface2/60 rounded-2xl p-5 shadow-xl shadow-black/10 fade-in">
          <div className="text-xs uppercase tracking-wider text-slate-500 font-bold mb-2">Vehiculos</div>
          <div className="text-3xl font-bold text-white">{stats.count}</div>
        </div>
        <div className="bg-surface border border-surface2/60 rounded-2xl p-5 shadow-xl shadow-black/10 fade-in fade-d1">
          <div className="text-xs uppercase tracking-wider text-slate-500 font-bold mb-2">Capacidad total</div>
          <div className="text-3xl font-bold text-white">{stats.totalCapacity.toFixed(2)} kg</div>
        </div>
        <div className="bg-surface border border-surface2/60 rounded-2xl p-5 shadow-xl shadow-black/10 fade-in fade-d2">
          <div className="text-xs uppercase tracking-wider text-slate-500 font-bold mb-2">Promedio</div>
          <div className="text-3xl font-bold text-white">{stats.averageCapacity.toFixed(2)} kg</div>
        </div>
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-12 gap-6">
        <div className="xl:col-span-4 fade-in">
          <form onSubmit={handleSubmit} className="bg-surface border border-surface2/60 rounded-2xl shadow-xl shadow-black/20 overflow-hidden">
            <div className="px-6 py-4 border-b border-surface2/60 bg-surface2/40">
              <span className="font-semibold text-white flex items-center gap-2">
                <i className={`bi ${editingId ? 'bi-pencil-square' : 'bi-plus-circle'} text-sky-400`}></i>
                {editingId ? 'Editar vehiculo' : 'Nuevo vehiculo'}
              </span>
            </div>

            <div className="p-6 space-y-5">
              {error && (
                <div className="bg-red-500/10 border border-red-500/30 text-red-300 rounded-xl px-4 py-3 text-sm">
                  {error}
                </div>
              )}

              <div>
                <label className="form-label">Placa</label>
                <input
                  name="placa"
                  value={form.placa}
                  onChange={handleChange}
                  className="form-control uppercase"
                  placeholder="ABC-123"
                  maxLength="20"
                />
              </div>

              <div>
                <label className="form-label">Marca / modelo</label>
                <input
                  name="marca"
                  value={form.marca}
                  onChange={handleChange}
                  className="form-control"
                  placeholder="Volvo FH"
                  maxLength="50"
                />
              </div>

              <div>
                <label className="form-label">Capacidad kg</label>
                <input
                  name="capacidad_kg"
                  value={form.capacidad_kg}
                  onChange={handleChange}
                  className="form-control"
                  type="number"
                  min="0.01"
                  step="0.01"
                  placeholder="1000.00"
                />
              </div>

              <div className="flex flex-col sm:flex-row gap-3">
                <button type="submit" disabled={saving} className="btn btn-primary flex-1 disabled:opacity-60 disabled:cursor-not-allowed">
                  <i className="bi bi-check2-circle"></i>
                  {saving ? 'Guardando...' : editingId ? 'Actualizar' : 'Registrar'}
                </button>
                {editingId && (
                  <button type="button" onClick={resetForm} className="btn btn-secondary">
                    Cancelar
                  </button>
                )}
              </div>
            </div>
          </form>
        </div>

        <div className="xl:col-span-8 fade-in fade-d1">
          <div className="bg-surface border border-surface2/60 rounded-2xl shadow-xl shadow-black/20 overflow-hidden">
            <div className="px-6 py-4 border-b border-surface2/60 bg-surface2/40 flex items-center justify-between">
              <span className="font-semibold text-white flex items-center gap-2">
                <i className="bi bi-list-check text-sky-400"></i> Flota registrada
              </span>
              <button type="button" onClick={load} className="text-slate-400 hover:text-white text-sm transition-colors">
                <i className="bi bi-arrow-clockwise mr-1"></i> Actualizar
              </button>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead className="bg-surface/95">
                  <tr className="border-b border-surface2/60 text-[11px] uppercase tracking-wider text-slate-400 font-bold">
                    <th className="px-6 py-4">Placa</th>
                    <th className="px-6 py-4">Marca / modelo</th>
                    <th className="px-6 py-4">Capacidad</th>
                    <th className="px-6 py-4 text-right">Acciones</th>
                  </tr>
                </thead>
                <tbody className="text-sm divide-y divide-surface2/30">
                  {vehiculos.map((vehiculo) => (
                    <tr key={vehiculo.id_vehiculo} className="hover:bg-surface2/30 transition-colors">
                      <td className="px-6 py-4">
                        <span className="font-mono text-white font-bold tracking-wide">{vehiculo.placa}</span>
                      </td>
                      <td className="px-6 py-4 text-slate-300">{vehiculo.marca || 'Sin marca'}</td>
                      <td className="px-6 py-4">
                        <span className="px-2.5 py-1 rounded-md text-xs font-semibold bg-emerald-600/20 text-emerald-400 border border-emerald-500/20">
                          {Number(vehiculo.capacidad_kg || 0).toFixed(2)} kg
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex justify-end gap-2">
                          <button
                            type="button"
                            onClick={() => handleEdit(vehiculo)}
                            className="text-slate-400 hover:text-sky-300 p-2 rounded-lg hover:bg-sky-400/10 transition-colors"
                            aria-label={`Editar ${vehiculo.placa}`}
                          >
                            <i className="bi bi-pencil-square"></i>
                          </button>
                          <button
                            type="button"
                            onClick={() => handleDelete(vehiculo)}
                            className="text-slate-500 hover:text-red-400 p-2 rounded-lg hover:bg-red-400/10 transition-colors"
                            aria-label={`Eliminar ${vehiculo.placa}`}
                          >
                            <i className="bi bi-trash"></i>
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}

                  {!vehiculos.length && (
                    <tr>
                      <td colSpan="4" className="text-center text-slate-500 py-12">
                        <i className="bi bi-truck text-5xl block mb-3 opacity-30"></i>
                        No hay vehiculos registrados.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
