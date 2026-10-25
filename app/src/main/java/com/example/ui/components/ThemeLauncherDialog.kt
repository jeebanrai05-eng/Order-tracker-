package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.LUXURY_THEMES
import com.example.ui.theme.LuxuryThemeMeta
import com.example.ui.theme.MaterialThemeColors

/**
 * Ultra-Luxury Theme Launcher & Studio.
 * Lets users visually preview and switch between 8 custom handcrafted luxury themes
 * with live mini-widget previews and smooth animated transitions.
 */
@Composable
fun ThemeLauncherDialog(
  currentTheme: String,
  onThemeSelect: (String) -> Unit,
  onDismiss: () -> Unit
) {
  val themeColors = MaterialThemeColors

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .padding(vertical = 20.dp),
      shape = RoundedCornerShape(28.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
      shadowElevation = 20.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(themeColors.primary.copy(alpha = 0.16f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                tint = themeColors.primary,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Luxury Theme Studio",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Handcrafted Aesthetics & Silky Smooth Gradients",
                fontSize = 11.sp,
                color = themeColors.primary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Select a luxury vibe to transform your driver dashboard:",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Themes Grid
        LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          contentPadding = PaddingValues(bottom = 8.dp)
        ) {
          items(LUXURY_THEMES) { themeMeta ->
            val isSelected = themeMeta.name.equals(currentTheme, ignoreCase = true)
            LuxuryThemeCard(
              themeMeta = themeMeta,
              isSelected = isSelected,
              onClick = {
                onThemeSelect(themeMeta.name)
              }
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("btn_close_theme_studio"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = themeColors.primary,
            contentColor = Color.Black
          )
        ) {
          Text("Done", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun LuxuryThemeCard(
  themeMeta: LuxuryThemeMeta,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val scale by animateFloatAsState(
    targetValue = if (isSelected) 1.02f else 1.0f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMedium
    ),
    label = "scaleCard"
  )

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .scale(scale)
      .clip(RoundedCornerShape(18.dp))
      .clickable(onClick = onClick)
      .testTag("theme_card_${themeMeta.name.lowercase().replace(" ", "_")}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) {
        themeMeta.primary.copy(alpha = 0.12f)
      } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
      }
    ),
    border = BorderStroke(
      width = if (isSelected) 2.dp else 1.dp,
      color = if (isSelected) themeMeta.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = themeMeta.iconEmoji,
          fontSize = 18.sp
        )

        if (isSelected) {
          Box(
            modifier = Modifier
              .size(20.dp)
              .clip(CircleShape)
              .background(themeMeta.primary),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Selected",
              tint = Color.Black,
              modifier = Modifier.size(13.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = themeMeta.name,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = if (isSelected) themeMeta.primary else MaterialTheme.colorScheme.onSurface
      )

      Text(
        text = themeMeta.tagline,
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Live Color Swatch & Mini-Widget Preview
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(28.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(
            Brush.horizontalGradient(
              listOf(themeMeta.primary, themeMeta.variant, themeMeta.accent)
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "PREVIEW",
          fontSize = 9.sp,
          fontWeight = FontWeight.ExtraBold,
          color = Color.Black.copy(alpha = 0.8f),
          letterSpacing = 1.sp
        )
      }
    }
  }
}
