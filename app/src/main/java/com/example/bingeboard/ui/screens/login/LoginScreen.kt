package com.example.bingeboard.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bingeboard.ui.theme.*

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSignup: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateToHome()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TOP SECTION
        Spacer(modifier = Modifier.weight(0.35f))
        
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(GoldAccent),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.movie_frame),
                contentDescription = "App Logo",
                modifier = Modifier.size(36.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "BingeBoard",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
            color = PrimaryText
        )
        
        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(48.dp))

        // FORM SECTION
        OutlinedTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Email address", color = SecondaryText, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null, tint = SecondaryText) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardSurface,
                unfocusedContainerColor = CardSurface,
                focusedBorderColor = GoldAccent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = GoldAccent,
                focusedTextColor = PrimaryText,
                unfocusedTextColor = PrimaryText
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Password", color = SecondaryText, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null, tint = SecondaryText) },
            trailingIcon = {
                IconButton(onClick = viewModel::togglePasswordVisibility) {
                    Icon(
                        imageVector = if (uiState.isPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = null,
                        tint = SecondaryText
                    )
                }
            },
            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardSurface,
                unfocusedContainerColor = CardSurface,
                focusedBorderColor = GoldAccent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = GoldAccent,
                focusedTextColor = PrimaryText,
                unfocusedTextColor = PrimaryText
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    viewModel.onLoginClicked()
                }
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
                viewModel.onLoginClicked()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GoldAccent,
                contentColor = Color.Black
            ),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = Color.Black,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = uiState.error!!,
                color = Color(0xFFFF453A),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // BOTTOM SECTION
        Row(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account? ",
                color = SecondaryText,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Sign Up",
                color = GoldAccent,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.clickable(onClick = onNavigateToSignup)
            )
        }
    }
}
