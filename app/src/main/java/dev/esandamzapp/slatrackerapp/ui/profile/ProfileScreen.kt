package dev.esandamzapp.slatrackerapp.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.esandamzapp.slatrackerapp.R
import dev.esandamzapp.slatrackerapp.data.remote.dto.PerfilCompletoResponse

// 🎨 PALETA OCEAN BREEZE
private val OceanMain = Color(0xFF0084A8)
private val OceanDark = Color(0xFF014A59)
private val OceanSoftCard = Color(0xFFEBF9FF)
private val OceanAqua = Color(0xFF00AACC)
private val LogoutWaterRed = Color(0x55FF4646) // Aumentado de 0x33 a 0x55 para más visibilidad

@Composable
fun ProfileScreen(
    token: String,
    userId: Int,
    onNotifications: () -> Unit = {},
    onReports: () -> Unit = {},
    onLinks: () -> Unit = {},
    onSettings: () -> Unit = {},
    onSecurity: () -> Unit = {},
    onHelpCenter: () -> Unit = {},
    onReportProblem: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val vm: ProfileViewModel = viewModel()
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadProfile(userId)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {

        when (state) {

            is ProfileState.Loading -> {
                Spacer(Modifier.height(120.dp))
                CircularProgressIndicator(
                    color = OceanMain,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            is ProfileState.Error -> {
                Spacer(Modifier.height(120.dp))
                Column(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = (state as ProfileState.Error).message,
                        color = Color.Red,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { vm.loadProfile(userId) }) {
                        Text("Reintentar")
                    }
                }
            }

            is ProfileState.Success -> {
                val usuario = (state as ProfileState.Success).usuario

                // ⭐ HEADER ORIGINAL (curva exacta + imagen exacta)
                ProfileHeaderOcean(usuario.username)

                Spacer(Modifier.height(70.dp))

                CardPersonalOcean(usuario)

                Spacer(Modifier.height(26.dp))

                CardMenuOcean(
                    onNotifications = onNotifications,
                    onReports = onReports,
                    onLinks = onLinks,
                    onSettings = onSettings,
                    onSecurity = onSecurity,
                    onHelpCenter = onHelpCenter,
                    onReportProblem = onReportProblem
                )

                Spacer(Modifier.height(30.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    LogoutButtonOcean(onLogout)
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

//
// ===================================================================
// ⭐ HEADER ORIGINAL — IMAGEN Y CURVA EXACTA, SOLO CAMBIO DE COLORES
// ===================================================================
//
@Composable
fun ProfileHeaderOcean(name: String) {

    Box(
        Modifier
            .fillMaxWidth()
            .height(310.dp)
    ) {

        // ⭐ Fondo EXACTO original
        Image(
            painter = painterResource(R.drawable.fondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // ⭐ Curva EXACTA original (NO CAMBIADA)
        Canvas(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(170.dp)
        ) {

            val w = size.width
            val h = size.height

            val path = Path().apply {

                moveTo(0f, h * 0.70f)

                cubicTo(
                    w * 0.23f, h * 0.70f,
                    w * 0.30f, h * 0.20f,
                    w * 0.50f, h * 0.20f
                )

                cubicTo(
                    w * 0.70f, h * 0.20f,
                    w * 0.76f, h * 0.70f,
                    w, h * 0.70f
                )

                lineTo(w, h)
                lineTo(0f, h)
                close()
            }

            drawPath(path, color = Color.White, style = Fill)
        }

        // ⭐ Avatar + nombre (structure untouched)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(R.drawable.alexa),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(5.dp, Color.White, CircleShape)
                    .shadow(14.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(8.dp))

            Text(
                name,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = OceanDark
            )
        }
    }
}

//
// ===================================================================
// ⭐ TARJETA INFORMACIÓN PERSONAL — Ocean Breeze
// ===================================================================
//
@Composable
fun CardPersonalOcean(data: dev.esandamzapp.slatrackerapp.data.remote.dto.UsuarioDto) {

    Card(
        Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(OceanSoftCard),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {

        Column(Modifier.padding(20.dp)) {

            Text(
                "Información Personal",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = OceanDark
            )

            Spacer(Modifier.height(12.dp))

            InfoRowOcean(R.drawable.ic_user, "Usuario", data.username)
            Divider()

            InfoRowOcean(R.drawable.ic_email, "Correo", data.correo)
            Divider()

            val rolText = when (data.idRolSistema) {
                1 -> "Administrador"
                2 -> "Usuario"
                3 -> "Supervisor"
                else -> "Rol ${data.idRolSistema}"
            }
            InfoRowOcean(R.drawable.ic_shield, "Rol", rolText)
        }
    }
}

@Composable
fun InfoRowOcean(icon: Int, label: String, value: String?) {

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = OceanMain,
            modifier = Modifier.size(23.dp)
        )

        Spacer(Modifier.width(18.dp))

        Column {
            Text(label, fontSize = 13.sp, color = Color(0xFF5A6168))
            if (value != null) {
                Text(value, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = OceanDark)
            }
        }
    }
}

//
// ===================================================================
// ⭐ MENÚ — Ocean Breeze UI
// ===================================================================
//
@Composable
fun CardMenuOcean(
    onNotifications: () -> Unit,
    onReports: () -> Unit,
    onLinks: () -> Unit,
    onSettings: () -> Unit,
    onSecurity: () -> Unit,
    onHelpCenter: () -> Unit,
    onReportProblem: () -> Unit
) {

    Card(
        Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(OceanSoftCard),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(22.dp)
    ) {

        Column(Modifier.padding(6.dp)) {

            MenuItemOcean(R.drawable.ic_notifications, "Notificaciones") { onNotifications() }
            Divider()

            MenuItemOcean(R.drawable.ic_bug, "Reportar un problema") { onReportProblem() }
            Divider()

            MenuItemOcean(R.drawable.ic_links, "Links de interés") { onLinks() }
            Divider()

            MenuItemOcean(R.drawable.ic_settings, "Configuración") { onSettings() }
            Divider()

            MenuItemOcean(R.drawable.ic_security, "Seguridad") { onSecurity() }
            Divider()

            MenuItemOcean(R.drawable.ic_help, "Centro de ayuda") { onHelpCenter() }
        }
    }
}

@Composable
fun MenuItemOcean(icon: Int, title: String, onClick: () -> Unit) {

    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = OceanAqua,
            modifier = Modifier.size(22.dp)
        )

        Spacer(Modifier.width(18.dp))

        Text(
            title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            color = OceanDark,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_forward),
            contentDescription = null,
            tint = OceanMain,
            modifier = Modifier.size(19.dp)
        )
    }
}

//
// ===================================================================
// ⭐ BOTÓN CERRAR SESIÓN — Ocean Breeze
// ===================================================================
//
@Composable
fun LogoutButtonOcean(onClick: () -> Unit) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .width(240.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LogoutWaterRed
        ),
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                painter = painterResource(id = R.drawable.ic_logout),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )

            Spacer(Modifier.width(12.dp))

            Text(
                "Cerrar sesión",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}
