package com.eduardo.laboratorio5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.eduardo.laboratorio5.ui.theme.Laboratorio5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Laboratorio5Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FormularioRegistro(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}