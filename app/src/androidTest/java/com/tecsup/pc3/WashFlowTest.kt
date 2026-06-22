package com.tecsup.pc3

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tecsup.pc3.data.session.SessionManager
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WashFlowTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun clearSessionAndGrantNotifications() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        runBlocking { SessionManager(instrumentation.targetContext).clearSession() }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            instrumentation.uiAutomation.executeShellCommand(
                "pm grant ${instrumentation.targetContext.packageName} ${Manifest.permission.POST_NOTIFICATIONS}",
            ).close()
        }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Sistema Logístico").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun completeWashFlowWorks() {
        login("wash", "incorrecta")
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodesWithText("Usuario o contraseña incorrectos").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Usuario o contraseña incorrectos").assertIsDisplayed()

        composeRule.onNodeWithText("Contraseña").performTextReplacement("123456")
        composeRule.onNodeWithText("Ingresar").performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Accesos rápidos").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Cajas registradas").assertIsDisplayed()
        composeRule.onNodeWithText("Salir").assertIsDisplayed()

        listOf("Almacén", "Cajas", "Despachos", "Carro IoT", "Configuración").forEach { destination ->
            composeRule.onNodeWithText(destination).performClick()
            composeRule.onNodeWithText("Volver").assertIsDisplayed().performClick()
        }

        composeRule.onNodeWithText("Notificaciones").performClick()
        val manager = composeRule.activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancelAll()
        composeRule.onNodeWithText("Probar notificación").assertIsDisplayed().performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) { manager.activeNotifications.isNotEmpty() }
        composeRule.onNodeWithText("Volver").performClick()

        composeRule.onNodeWithText("Salir").performClick()
        composeRule.onNodeWithText("Sistema Logístico").assertIsDisplayed()
        composeRule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        composeRule.onNodeWithText("Accesos rápidos").assertDoesNotExist()
    }

    private fun login(username: String, password: String) {
        composeRule.onNodeWithText("Usuario").performTextInput(username)
        composeRule.onNodeWithText("Contraseña").performTextInput(password)
        composeRule.onNodeWithText("Ingresar").performClick()
        composeRule.waitForIdle()
    }
}
