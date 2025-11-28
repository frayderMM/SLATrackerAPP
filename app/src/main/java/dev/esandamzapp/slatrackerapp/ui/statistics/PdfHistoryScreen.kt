package dev.esandamzapp.slatrackerapp.ui.statistics

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private val HeaderBlue = Color(0xFF071C4D)
private val AccentOrange = Color(0xFFFF7A00)
private val LightLilac = Color(0xFFF6F2FF)

data class PdfFile(
    val name: String,
    val file: File,
    val date: Date
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfHistoryScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var pdfFiles by remember { mutableStateOf<List<PdfFile>>(emptyList()) }

    // Cargar PDFs del directorio Downloads
    LaunchedEffect(Unit) {
        val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(
            android.os.Environment.DIRECTORY_DOWNLOADS
        )
        
        val files = downloadsDir.listFiles { file ->
            file.name.startsWith("Reporte_Indicadores") && file.name.endsWith(".pdf")
        }?.map { file ->
            PdfFile(
                name = file.name,
                file = file,
                date = Date(file.lastModified())
            )
        }?.sortedByDescending { it.date } ?: emptyList()
        
        pdfFiles = files
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Historial de Reportes",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HeaderBlue,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F8FF))
                .padding(paddingValues)
        ) {
            if (pdfFiles.isEmpty()) {
                // Estado vacío
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileCopy,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No hay reportes generados",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Los PDFs generados aparecerán aquí",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                // Lista de PDFs
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pdfFiles) { pdfFile ->
                        PdfFileCard(
                            pdfFile = pdfFile,
                            onOpenClick = {
                                try {
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        pdfFile.file
                                    )
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(uri, "application/pdf")
                                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "Error al abrir PDF: ${e.message}",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onShareClick = {
                                try {
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        pdfFile.file
                                    )
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "application/pdf"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Compartir PDF"))
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "Error al compartir PDF: ${e.message}",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PdfFileCard(
    pdfFile: PdfFile,
    onOpenClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de PDF
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color(0xFFD32F2F)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información del archivo
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pdfFile.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = HeaderBlue,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(pdfFile.date),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "${pdfFile.file.length() / 1024} KB",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Botones de acción
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Abrir
                IconButton(
                    onClick = onOpenClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Abrir",
                        tint = AccentOrange
                    )
                }
                
                // Botón Compartir
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartir",
                        tint = HeaderBlue
                    )
                }
            }
        }
    }
}
