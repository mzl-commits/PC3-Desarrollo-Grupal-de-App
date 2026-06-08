# clasificacion/serializers.py
from rest_framework import serializers

from .models import (
    Caja, Ubicacion, Medida, Proveedor,
    Usuario, HistorialMovimientos, Despacho, EstadoCarro, Categoria, ConfigCarro,
    Vehiculo, Destino
)

class VehiculoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Vehiculo
        fields = '__all__'

    def validate_placa(self, value):
        placa = value.strip().upper()
        qs = Vehiculo.objects.filter(placa__iexact=placa)
        if self.instance:
            qs = qs.exclude(pk=self.instance.pk)
        if qs.exists():
            raise serializers.ValidationError('Ya existe un vehiculo con esta placa.')
        return placa

    def validate_capacidad_kg(self, value):
        if value <= 0:
            raise serializers.ValidationError('La capacidad debe ser mayor que cero.')
        return value

class DestinoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Destino
        fields = '__all__'


class ProveedorSerializer(serializers.ModelSerializer):
    class Meta:
        model = Proveedor
        fields = '__all__'



class CategoriaSerializer(serializers.ModelSerializer):
    class Meta:
        model = Categoria
        fields = '__all__'


class ConfigCarroSerializer(serializers.ModelSerializer):
    volumen_cm3 = serializers.ReadOnlyField()

    class Meta:
        model = ConfigCarro
        fields = '__all__'


class MedidaSerializer(serializers.ModelSerializer):
    class Meta:
        model = Medida
        fields = '__all__'


class ProveedorSerializer(serializers.ModelSerializer):
    class Meta:
        model = Proveedor
        fields = '__all__'


class UbicacionSerializer(serializers.ModelSerializer):
    coord_x = serializers.IntegerField(read_only=True)
    coord_y = serializers.IntegerField(read_only=True)

    class Meta:
        model = Ubicacion
        fields = '__all__'


class UsuarioSerializer(serializers.ModelSerializer):
    class Meta:
        model = Usuario
        fields = '__all__'


class CajaSerializer(serializers.ModelSerializer):
    class Meta:
        model = Caja
        fields = '__all__'


class HistorialSerializer(serializers.ModelSerializer):
    class Meta:
        model = HistorialMovimientos
        fields = '__all__'


class DespachoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Despacho
        fields = '__all__'


class EstadoCarroSerializer(serializers.ModelSerializer):
    class Meta:
        model = EstadoCarro
        fields = '__all__'
