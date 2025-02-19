package sh.elizabeth.fedisleep

import android.os.Bundle
import android.os.Debug
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import sh.elizabeth.fedisleep.ui.composable.Home
import sh.elizabeth.fedisleep.ui.theme.FediSleepTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Debug.waitForDebugger()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FediSleepTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Home(innerPadding = innerPadding)
                }
            }
        }
    }
}