package de.grunert.wasantwort

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import de.grunert.wasantwort.di.AppContainer
import de.grunert.wasantwort.ui.MainScreen
import de.grunert.wasantwort.ui.theme.WasAntwortTheme
import de.grunert.wasantwort.viewmodel.MainViewModel

class App : ComponentActivity() {
    
    private lateinit var appContainer: AppContainer
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        appContainer = AppContainer(applicationContext)
        
        setContent {
            WasAntwortTheme {
                val viewModel: MainViewModel = viewModel(
                    factory = appContainer.viewModelFactory
                )
                MainScreen(viewModel = viewModel)
            }
        }
    }
}



