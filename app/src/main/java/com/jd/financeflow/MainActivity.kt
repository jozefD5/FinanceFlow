package com.jd.financeflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

import com.jd.financeflow.ui.theme.FinanceFlowTheme
import com.jd.financeflow.navigation.presentation.MainNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FinanceFlowTheme {
                MainNavigation(modifier = Modifier.fillMaxSize())
            }
        }
    }
}



