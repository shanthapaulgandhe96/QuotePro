package uk.ac.tees.mad.quotepro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.tees.mad.quotepro.presentation.navigation.QuoteProNavGraph
import uk.ac.tees.mad.quotepro.ui.theme.QuoteProTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuoteProTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    QuoteProNavGraph()
                }
            }
        }
    }
}