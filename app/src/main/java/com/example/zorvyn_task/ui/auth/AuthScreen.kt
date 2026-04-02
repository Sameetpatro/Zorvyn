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
import com.example.zorvyn_task.ui.theme.*

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.authSuccess) {
        if (state.authSuccess) onAuthSuccess()
    }

    if (state.isLoading) {
        GlassBackground(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentBlue)
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
private fun SetupScreen(
    onSetup: (String, String) -> Unit,
    error: String?,
    onClearError: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    val displayError = error ?: localError

    GlassBackground(modifier = Modifier.fillMaxSize()) {
        // Ambient orbs
        Box(
            modifier = Modifier
                .size(350.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x3060A5FA), Color.Transparent)),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 70.dp, y = 70.dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x308B5CF6), Color.Transparent)),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo mark
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF60A5FA), Color(0xFF818CF8)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("₹", fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                "Welcome to Zorvyn",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Light
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Set up your secure finance profile",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                Text(
                    "Your Name",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextSecondary, letterSpacing = 0.8.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; onClearError() },
                    placeholder = { Text("e.g. Arjun", color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = authFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Create 4-digit PIN",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextSecondary, letterSpacing = 0.8.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 4) { pin = it.filter { c -> c.isDigit() }; onClearError() } },
                    placeholder = { Text("• • • •", color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    colors = authFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Confirm PIN",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextSecondary, letterSpacing = 0.8.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { if (it.length <= 4) { confirmPin = it.filter { c -> c.isDigit() }; localError = null } },
                    placeholder = { Text("• • • •", color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    colors = authFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                AnimatedVisibility(visible = displayError != null) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            displayError ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(color = AccentRed),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF60A5FA), Color(0xFF818CF8)))
                        )
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
                    Text(
                        "Create Account",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PinEntryScreen(
    userName: String,
    userId: String,
    onPinEntered: (String) -> Unit,
    error: String?,
    onClearError: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    val shakeAnim = remember { Animatable(0f) }

    LaunchedEffect(error) {
        if (error != null) {
            pin = ""
            shakeAnim.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    (-8f) at 50
                    8f at 100
                    (-6f) at 150
                    6f at 200
                    (-4f) at 250
                    4f at 300
                    0f at 400
                }
            )
        }
    }

    GlassBackground(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(350.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x3060A5FA), Color.Transparent)),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 70.dp, y = 70.dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x308B5CF6), Color.Transparent)),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF60A5FA), Color(0xFF818CF8)))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    userName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                    fontSize = 34.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Light
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Welcome back,",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )
            Text(
                userName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Light
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // User ID chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0x20FFFFFF))
                    .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    userId.take(16) + "…",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextTertiary,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(44.dp))

            // PIN dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.offset(x = shakeAnim.value.dp)
            ) {
                repeat(4) { index ->
                    val filled = index < pin.length
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(
                                if (filled)
                                    Brush.linearGradient(listOf(Color(0xFF60A5FA), Color(0xFF818CF8)))
                                else
                                    Brush.linearGradient(listOf(Color(0x33FFFFFF), Color(0x33FFFFFF)))
                            )
                    )
                }
            }

            AnimatedVisibility(visible = error != null) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        error ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(color = AccentRed),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Numpad
            val keys = listOf("1","2","3","4","5","6","7","8","9","","0","⌫")
            val gridWidth = 240.dp

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                keys.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        row.forEach { key ->
                            if (key.isEmpty()) {
                                Spacer(modifier = Modifier.size(68.dp))
                            } else {
                                NumpadKey(
                                    label = key,
                                    onClick = {
                                        onClearError()
                                        if (key == "⌫") {
                                            if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                        } else if (pin.length < 4) {
                                            pin += key
                                            if (pin.length == 4) onPinEntered(pin)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NumpadKey(label: String, onClick: () -> Unit) {
    val isBackspace = label == "⌫"
    Box(
        modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(Color(0x20FFFFFF))
            .border(1.dp, Color(0x25FFFFFF), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.titleLarge.copy(
                color = if (isBackspace) TextSecondary else TextPrimary,
                fontWeight = if (isBackspace) FontWeight.Light else FontWeight.Normal,
                fontSize = if (isBackspace) 20.sp else 22.sp
            )
        )
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedBorderColor = AccentBlue,
    unfocusedBorderColor = Color(0x40FFFFFF),
    cursorColor = AccentBlue,
    focusedContainerColor = Color(0x10FFFFFF),
    unfocusedContainerColor = Color(0x08FFFFFF)
)