package com.tuuniversidad.laboratorio7

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PreferenciasPersistenciaTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun iniciarConPreferenciaActivada() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        runBlocking {
            PreferenciasRepository(context).actualizarMostrarDescripciones(true)
        }
    }

    @Test
    fun preferenciaActivadaActualizaUIYSeAlmacenaTrasRecrearActivity() {
        val switch = composeRule.onNodeWithTag("mostrarDescripcionesSwitch")
        switch.assertIsOn()
        composeRule.onNodeWithText("Experto en combate cuerpo a cuerpo").assertExists()

        switch.performClick()
        switch.assertIsOff()
        composeRule.onAllNodesWithText("Experto en combate cuerpo a cuerpo").assertCountEquals(0)

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        composeRule.waitUntil(timeoutMillis = 5_000) {
            runBlocking {
                !PreferenciasRepository(context).mostrarDescripciones.first()
            }
        }

        composeRule.activityRule.scenario.recreate()
        composeRule.onNodeWithTag("mostrarDescripcionesSwitch").assertIsOff()
        composeRule.onAllNodesWithText("Experto en combate cuerpo a cuerpo").assertCountEquals(0)
    }
}
