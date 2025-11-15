package dev.esandamzapp.slatrackerapp.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.esandamzapp.slatrackerapp.R

// ===============================================================
// ⭐ PROFILE SCREEN COMPLETO (ESTILO iOS / PREMIUM MOCKUP)
// ===============================================================
@Composable
fun ProfileScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(modifier = Modifier.height(30.dp))

        // FOTO + INFO
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // FOTO
            Image(
                painter = painterResource(id = R.drawable.alexa),
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .shadow(4.dp, CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // NOMBRE
            Text(
                "Alexa Grasso",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // CORREO EN CAJITA SUAVE
            Box(
                modifier = Modifier
                    .background(Color(0xFFDDEAF5), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    "alexa@gmail.com",
                    fontSize = 13.sp,
                    color = Color(0xFF0065A1),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }

        // ======================================================
        // CARD INFO PERSONAL
        // ======================================================
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {

            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    "Información Personal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoRow(R.drawable.ic_user, "Nombre completo", "Alexa Grasso")
                Divider(color = Color(0xFFEAEAEA))

                InfoRow(R.drawable.ic_email, "Correo electrónico", "alexa@gmail.com")
                Divider(color = Color(0xFFEAEAEA))

                InfoRow(R.drawable.ic_department, "Departamento", "Recursos Humanos")
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // ======================================================
        // CARD DEL MENÚ
        // ======================================================
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {

            Column(modifier = Modifier.padding(10.dp)) {

                MenuItemCard(R.drawable.ic_notifications, "Notifications")
                Divider(color = Color(0xFFEAEAEA))

                MenuItemCard(R.drawable.ic_reports, "Reports")
                Divider(color = Color(0xFFEAEAEA))

                MenuItemCard(R.drawable.ic_links, "Links of Interest")
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // ======================================================
        // LOGOUT CLEAN — ESTILO iOS
        // ======================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 34.dp)
                .clickable { },
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_logout),
                contentDescription = null,
                tint = Color(0xFFE53935),
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                "Logout",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFE53935)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

// ===============================================================
// ⭐ INFO ROW (DATOS PERSONALES)
// ===============================================================
@Composable
fun InfoRow(icon: Int, label: String, value: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = Color(0xFF4A4A4A),
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {

            Text(label, fontSize = 13.sp, color = Color.Gray)

            Text(
                value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF222222),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ===============================================================
// ⭐ MENÚ (ITEMS)
// ===============================================================
@Composable
fun MenuItemCard(icon: Int, title: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
            .clickable { },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            tint = Color(0xFF363636),
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            title,
            fontSize = 17.sp,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_forward),
            contentDescription = null,
            tint = Color(0xFF999999),
            modifier = Modifier.size(20.dp)
        )
    }
}
