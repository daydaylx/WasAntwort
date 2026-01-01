package de.grunert.replyhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import de.grunert.replyhelper.di.AppContainer
import de.grunert.replyhelper.ui.MainScreen
import de.grunert.replyhelper.ui.theme.ReplyHelperTheme
import de.grunert.replyhelper.viewmodel.MainViewModel

class App : ComponentActivity() {
    
    private lateinit var appContainer: AppContainer
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        appContainer = AppContainer(applicationContext)
        
        setContent {
            ReplyHelperTheme {
                val viewModel: MainViewModel = viewModel(
                    factory = appContainer.viewModelFactory
                )
                MainScreen(viewModel = viewModel)
            }
        }
    }
}


