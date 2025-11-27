package dev.esandamzapp.slatrackerapp.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.esandamzapp.slatrackerapp.R

@Composable
fun LoginScreen(
    onLoginSuccess: (String, Int) -> Unit
) {
    val viewModel: LoginViewModel = viewModel()
    val loginState by viewModel.loginState.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(70.dp))

        // ⭐ TU LOGO ORIGINAL
        Image(
            painter = painterResource(id = R.drawable.logotata),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(300f / 121f)
        )

        Spacer(modifier = Modifier.height(70.dp))

        // ⭐ TU TARJETA DE LOGIN ORIGINAL
        Column(
            modifier = Modifier
                .width(350.dp)
                .border(3.dp, Color(0x2AFF9969), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = {
                    Text("Usuario", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                textStyle = TextStyle(fontSize = 20.sp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text("Contraseña", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                textStyle = TextStyle(fontSize = 20.sp),
                visualTransformation =
                    if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // ⭐ TU BOTÓN ORIGINAL
        Button(
            onClick = { viewModel.login(username, password) },
            modifier = Modifier.width(350.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3280C4)
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                "INICIAR SESIÓN",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // ⭐ ESTADOS
        when (loginState) {

            is LoginState.Loading ->
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))

            is LoginState.Error ->
                Text(
                    text = (loginState as LoginState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )

            is LoginState.Success -> {
                val data = loginState as LoginState.Success

                LaunchedEffect(Unit) {
                    onLoginSuccess(data.token, data.userId)
                }
            }

            else -> {}
        }
    }
}
