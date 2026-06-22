package com.tecsup.pc3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tecsup.pc3.navigation.AppNavGraph
import com.tecsup.pc3.ui.theme.Pc3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pc3Theme {
                AppNavGraph()
            }
        }
    }
}
