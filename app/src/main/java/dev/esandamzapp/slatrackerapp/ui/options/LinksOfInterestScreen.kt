package dev.esandamzapp.slatrackerapp.ui.options

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinksOfInterestScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Links de Interés") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(links) { link ->
                LinkCard(link)
            }
        }
    }
}

data class LinkItem(
    val title: String,
    val description: String,
    val url: String,
    val icon: ImageVector
)

val links = listOf(
    LinkItem(
        "Base de Conocimiento",
        "Artículos y guías para resolver problemas comunes.",
        "https://es.atlassian.com/itsm/knowledge-base",
        Icons.Default.Book
    ),
    LinkItem(
        "Políticas de Seguridad",
        "Consulte las políticas de seguridad de la información de la empresa.",
        "https://www.example.com/security-policy",
        Icons.Default.Security
    ),
    LinkItem(
        "Términos de Servicio",
        "Lea los términos y condiciones de uso de la aplicación.",
        "https://www.example.com/terms-of-service",
        Icons.Default.Policy
    )
)

@Composable
fun LinkCard(link: LinkItem) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                uriHandler.openUri(link.url)
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        ListItem(
            headlineContent = {
                Text(text = link.title, style = MaterialTheme.typography.titleMedium)
            },
            supportingContent = {
                Text(text = link.description, style = MaterialTheme.typography.bodyMedium)
            },
            leadingContent = {
                Icon(
                    imageVector = link.icon,
                    contentDescription = link.title,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Launch,
                    contentDescription = "Abrir enlace",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
    }
}
