package uk.ac.tees.mad.quotepro.presentation.screens.main.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import uk.ac.tees.mad.quotepro.R

@Composable
fun SettingsScreen(navController: NavController) {

}

@OptIn(ExperimentalGlideComposeApi::class)
@Preview(showBackground = true)
@Composable
fun SettingsContent(modifier: Modifier = Modifier) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            GlideImage(
                model = painterResource(R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
                    .size(120.dp)
                    .clip(
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "XYZ",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "XYZ",
                style = MaterialTheme.typography.labelLarge,
            )

        }
    }

}