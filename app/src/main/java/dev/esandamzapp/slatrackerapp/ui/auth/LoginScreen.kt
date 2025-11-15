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
import dev.esandamzapp.slatrackerapp.R



@Composable
fun LoginScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(70.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logotata),
            contentDescription = "TCS Logo",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(300f / 121f)
                .padding(horizontal = 1.dp)
        )

        Spacer(modifier = Modifier.height(70.dp))

        // Login Form
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
                    Text(
                        text = "user",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                textStyle = TextStyle(fontSize = 20.sp),
                modifier = Modifier
                    .width(320.dp)
                    .heightIn(min = 56.dp)
                    .padding(vertical = 8.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF007DC4),
                unfocusedBorderColor = Color(0x668BC9E6),
                cursorColor = Color(0xFF007DC4),),
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        text = "Password",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                textStyle = TextStyle(fontSize = 18.sp),
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier
                    .width(320.dp)
                    .heightIn(min = 56.dp)
                    .padding(vertical = 8.dp),
                singleLine = true,

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF007DC4),
                    unfocusedBorderColor = Color(0x668BC9E6),
                    cursorColor = Color(0xFF007DC4)
                ),
                shape = RoundedCornerShape(10.dp)
            )

        }

        Spacer(modifier = Modifier.height(30.dp))

        // Login Button
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier
                .width(350.dp)
                .padding(vertical = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3280C4)
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                "INICIAR SESION",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("By Alianza_TCS_ESAN", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text("v.0.01", fontSize = 10.sp, color = Color.Gray)
        }
    }
}
