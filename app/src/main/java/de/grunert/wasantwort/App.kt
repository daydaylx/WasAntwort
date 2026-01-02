package de.grunert.wasantwort

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.core.util.Consumer
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
                
                // Handle initial intent
                LaunchedEffect(Unit) {
                    handleIntent(intent, viewModel)
                }
                
                // Handle new intents (e.g. when app is already running)
                DisposableEffect(Unit) {
                    val listener = Consumer<Intent> { newIntent ->
                        handleIntent(newIntent, viewModel)
                    }
                    addOnNewIntentListener(listener)
                    onDispose { removeOnNewIntentListener(listener) }
                }

                MainScreen(viewModel = viewModel)
            }
        }
    }
    
    private fun handleIntent(intent: Intent?, viewModel: MainViewModel) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let { sharedText ->
                viewModel.updateInput(sharedText)
            }
        }
    }
}