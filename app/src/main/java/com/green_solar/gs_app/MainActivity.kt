package com.green_solar.gs_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.green_solar.gs_app.ui.theme.GsTheme
import com.green_solar.gs_app.ui.navigation.AppNav
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GsTheme {
                AppNav()

            }
        }
    }


}
