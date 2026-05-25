package com.example.bingeboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bingeboard.data.repository.AuthRepository
import com.example.bingeboard.ui.navigation.BingeBoardNavGraph
import com.example.bingeboard.ui.theme.BingeBoardTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        setContent {
            BingeBoardTheme {
                BingeBoardNavGraph(authRepository = authRepository)
            }
        }
    }
}
