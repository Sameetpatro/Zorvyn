package com.example.zorvyn_task.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zorvyn_task.ui.components.GlassBackground
import com.example.zorvyn_task.ui.components.GlassCard
import com.example.zorvyn_task.ui.theme.LocalAppColors

@Composable
fun AuthScreen(viewModel: AuthViewModel, onAuthSuccess: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(state.authSuccess) { if (state.authSuccess) onAuthSuccess() }

    if (state.isLoading) {
        val colors = LocalAppColors.current
        GlassBackground(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.accentBlue)
            }
        }
        return
    }

    if (state.isNewUser) {
        SetupScreen(
            onSetup = { name, pin -> viewModel.setupNewUser(name, pin) },
            error = state.error,
            onClearError = viewModel::clearError
        )
    } else {
        PinEntryScreen(
            userName = state.userName,
            userId = state.userId,
            onPinEntered = { pin -> viewModel.verifyPin(pin) },
            error = state.error,
            onClearError = viewModel::clearError
        )
    }
}

@Composable
private fun SetupScreen(onSetup: (String, String) -> Unit, error: String?, onClearError: () -> Unit) {
    val colors = LocalAppColors.current
    var name by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }
    val displayError = error ?: localError

    GlassBackground(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.size(350.dp).offset(x = (-80).dp, y = (-60).dp)
            .background(Brush.radialGradient(listOf(colors.accentBlue.copy(alpha = 0.18f), Color.Transparent)), CircleShape))
        Box(modifier = Modifier.size(280.dp).align(Alignment.BottomEnd).offset(x = 70.dp, y = 70.dp)
            .background(Brush.radialGradient(listOf(colors.accentGreen.copy(alpha = 0.15f), Color.Transparent)), CircleShape))

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(colors.accentBlue, colors.accentGreen))),
                contentAlignment = Alignment.Center
            ) {
                Text("₹", fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text("Welcome to Zorvyn",
                style = MaterialTheme.typography.headlineMedium.copy(color = colors.textPrimary, fontWeight = FontWeight.Light),
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Set up your secure finance profile",
                style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary),
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(36.dp))

            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                val fieldColors = authFieldColors()

                Text("Your Name", style = MaterialTheme.typography.labelMedium.copy(color = colors.textSecondary, letterSpacing = 0.8.sp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = name, onValueChange = { name = it; onClearError() },
                    placeholder = { Text("e.g. Arjun", color = colors.textTertiary) },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    colors = fieldColors, shape = RoundedCornerShape(12.dp))

                Spacer(modifier = Modifier.height(16.dp))
                Text("Create 4-digit PIN", style = MaterialTheme.typography.labelMedium.copy(color = colors.textSecondary, letterSpacing = 0.8.sp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = pin,
                    onValueChange = { if (it.length <= 4) { pin = it.filter { c -> c.isDigit() }; onClearError() } },
                    placeholder = { Text("• • • •", color = colors.textTertiary) },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    colors = fieldColors, shape = RoundedCornerShape(12.dp))

                Spacer(modifier = Modifier.height(16.dp))
                Text("Confirm PIN", style = MaterialTheme.typography.labelMedium.copy(color = colors.textSecondary, letterSpacing = 0.8.sp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = confirmPin,
                    onValueChange = { if (it.length <= 4) { confirmPin = it.filter { c -> c.isDigit() }; localError = null } },
                    placeholder = { Text("• • • •", color = colors.textTertiary) },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    colors = fieldColors, shape = RoundedCornerShape(12.dp))

                AnimatedVisibility(visible = displayError != null) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(displayError ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(color = colors.accentRed),
                            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(colors.accentBlue, colors.accentGreen)))
                        .clickable {
                            when {
                                name.isBlank() -> localError = "Please enter your name"
                                pin.length < 4 -> localError = "PIN must be 4 digits"
                                pin != confirmPin -> localError = "PINs do not match"
                                else -> onSetup(name.trim(), pin)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Create Account",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Medium))
                }
            }
        }
    }
}

@Composable
private fun PinEntryScreen(
    userName: String, userId: String,
    onPinEntered: (String) -> Unit,
    error: String?, onClearError: () -> Unit
) {
    val colors = LocalAppColors.current
    var pin by remember { mutableStateOf("") }
    val shakeAnim = remember { Animatable(0f) }

    LaunchedEffect(error) {
        if (error != null) {
            pin = ""
            shakeAnim.animateTo(0f, keyframes {
                durationMillis = 400
                (-8f) at 50; 8f at 100; (-6f) at 150; 6f at 200; (-4f) at 250; 4f at 300; 0f at 400
            })
        }
    }

    GlassBackground(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.size(350.dp).offset(x = (-80).dp, y = (-60).dp)
            .background(Brush.radialGradient(listOf(colors.accentBlue.copy(alpha = 0.18f), Color.Transparent)), CircleShape))
        Box(modifier = Modifier.size(280.dp).align(Alignment.BottomEnd).offset(x = 70.dp, y = 70.dp)
            .background(Brush.radialGradient(listOf(colors.accentGreen.copy(alpha = 0.15f), Color.Transparent)), CircleShape))

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape)
                    .background(Brush.linearGradient(listOf(colors.accentBlue, colors.accentGreen))),
                contentAlignment = Alignment.Center
            ) {
                Text(userName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                    fontSize = 34.sp, color = Color.White, fontWeight = FontWeight.Light)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Welcome back,", style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary))
            Text(userName, style = MaterialTheme.typography.headlineMedium.copy(
                color = colors.textPrimary, fontWeight = FontWeight.Light))
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier.clip(RoundedCornerShape(50))
                    .background(colors.glassWhite10)
                    .border(1.dp, colors.glassBorder, RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(userId.take(16) + "…",
                    style = MaterialTheme.typography.labelSmall.copy(color = colors.textTertiary, letterSpacing = 0.5.sp))
            }
            Spacer(modifier = Modifier.height(44.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.offset(x = shakeAnim.value.dp)) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier.size(18.dp).clip(CircleShape)
                            .background(
                                if (index < pin.length)
                                    Brush.linearGradient(listOf(colors.accentBlue, colors.accentGreen))
                                else
                                    Brush.linearGradient(listOf(colors.glassWhite15, colors.glassWhite15))
                            )
                    )
                }
            }

            AnimatedVisibility(visible = error != null) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(error ?: "", style = MaterialTheme.typography.bodySmall.copy(color = colors.accentRed),
                        textAlign = TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.height(40.dp))

            val keys = listOf("1","2","3","4","5","6","7","8","9","","0","⌫")
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                keys.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        row.forEach { key ->
                            if (key.isEmpty()) Spacer(modifier = Modifier.size(68.dp))
                            else NumpadKey(label = key, onClick = {
                                onClearError()
                                if (key == "⌫") {
                                    if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                } else if (pin.length < 4) {
                                    pin += key
                                    if (pin.length == 4) onPinEntered(pin)
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NumpadKey(label: String, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    val isBackspace = label == "⌫"
    Box(
        modifier = Modifier.size(68.dp).clip(CircleShape)
            .background(colors.glassWhite10)
            .border(1.dp, colors.glassBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, style = MaterialTheme.typography.titleLarge.copy(
            color = if (isBackspace) colors.textSecondary else colors.textPrimary,
            fontWeight = if (isBackspace) FontWeight.Light else FontWeight.Normal,
            fontSize = if (isBackspace) 20.sp else 22.sp))
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = LocalAppColors.current.textPrimary,
    unfocusedTextColor = LocalAppColors.current.textPrimary,
    focusedBorderColor = LocalAppColors.current.accentBlue,
    unfocusedBorderColor = LocalAppColors.current.glassBorder,
    cursorColor = LocalAppColors.current.accentBlue,
    focusedContainerColor = LocalAppColors.current.glassWhite10,
    unfocusedContainerColor = LocalAppColors.current.glassWhite10
)