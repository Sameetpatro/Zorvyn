package com.example.zorvyn_task.ui.navigation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zorvyn_task.ui.components.HapticHelper
import com.example.zorvyn_task.ui.theme.LocalAppColors

enum class BottomTab { PROFILE, HOME, INSIGHTS }

@Composable
fun BottomNavBar(
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    onAddClick: () -> Unit,
    haptic: HapticHelper,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Nav pill background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .clip(RoundedCornerShape(34.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(colors.glassWhite15, colors.glassWhite10)
                    )
                )
                .border(1.dp, colors.glassBorderStrong, RoundedCornerShape(34.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    icon = Icons.Default.Person,
                    label = "Profile",
                    selected = currentTab == BottomTab.PROFILE,
                    onClick = { haptic.tick(); onTabSelected(BottomTab.PROFILE) },
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )

                // Center FAB placeholder (actual FAB overlaid)
                Spacer(modifier = Modifier.weight(1f))

                NavItem(
                    icon = Icons.Default.ShowChart,
                    label = "Insights",
                    selected = currentTab == BottomTab.INSIGHTS,
                    onClick = { haptic.tick(); onTabSelected(BottomTab.INSIGHTS) },
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Center FAB — Home
        Box(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.Center)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        if (colors.isDark)
                            listOf(Color(0xFF60A5FA), Color(0xFF818CF8))
                        else
                            listOf(Color(0xFF0D9E6A), Color(0xFF3DBE8A))
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    haptic.click()
                    onTabSelected(BottomTab.HOME)
                },
            contentAlignment = Alignment.Center
        ) {
            BubbleIcon(
                icon = Icons.Default.Home,
                selected = currentTab == BottomTab.HOME,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    colors: com.example.zorvyn_task.ui.theme.AppColors,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.12f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (selected)
                        colors.accentBlue.copy(alpha = if (colors.isDark) 0.25f else 0.15f)
                    else Color.Transparent
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) colors.accentBlue else colors.textTertiary,
                modifier = Modifier.size(22.dp)
            )
        }
        if (selected) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = colors.accentBlue
            )
        }
    }
}

@Composable
private fun BubbleIcon(icon: ImageVector, selected: Boolean, tint: Color) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bubbleScale"
    )
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = Modifier
            .size(28.dp)
            .scale(scale)
    )
}