package com.example.pasteleriamilsabores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.pasteleriamilsabores.navigation.NavGraph
import com.example.pasteleriamilsabores.ui.theme.PasteleriaMilSaboresTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // Para que la UI ocupe toda la pantalla

        setContent {
            PasteleriaMilSaboresTheme {
                val navController = rememberNavController() // Controlador de navegación

                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->

                    // Aquí llamamos a nuestro NavGraph y le pasamos el padding
                    NavGraph(
                        appContext = this@MainActivity,
                        navController = navController,
                        paddingValues = innerPadding
                    )
                }
            }
        }
    }
}


