package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.screens.MainTycoonScreen
import com.example.ui.theme.WallStreetTycoonTheme
import com.example.viewmodel.TycoonViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TycoonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WallStreetTycoonTheme {
                MainTycoonScreen(viewModel = viewModel)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onAppPaused()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onAppResumed()
    }
}
