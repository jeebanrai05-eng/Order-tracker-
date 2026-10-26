package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NavigationTab
import com.example.ui.theme.BottomBarDark
import com.example.ui.theme.BottomBarItemUnselected
import com.example.ui.theme.MaterialThemeColors

@Composable
fun FloatingBottomBar(
  currentTab: NavigationTab,
  onTabSelected: (NavigationTab) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 20.dp, vertical = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .shadow(16.dp, RoundedCornerShape(32.dp))
        .clip(RoundedCornerShape(32.dp))
        .background(Color(0xFF070E1A).copy(alpha = 0.95f))
        .border(
          width = 1.dp,
          color = MaterialThemeColors.cardBorder,
          shape = RoundedCornerShape(32.dp)
        )
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      BottomNavItem(
        title = "Home",
        icon = Icons.Default.DateRange,
        selected = currentTab == NavigationTab.ORDERS,
        testTag = "nav_tab_orders",
        onClick = { onTabSelected(NavigationTab.ORDERS) }
      )
      BottomNavItem(
        title = "Chart",
        icon = Icons.Default.Equalizer,
        selected = currentTab == NavigationTab.CHART,
        testTag = "nav_tab_chart",
        onClick = { onTabSelected(NavigationTab.CHART) }
      )
      BottomNavItem(
        title = "Settings",
        icon = Icons.Default.Build,
        selected = currentTab == NavigationTab.SETTINGS,
        testTag = "nav_tab_settings",
        onClick = { onTabSelected(NavigationTab.SETTINGS) }
      )
    }
  }
}

@Composable
private fun BottomNavItem(
  title: String,
  icon: ImageVector,
  selected: Boolean,
  testTag: String,
  onClick: () -> Unit
) {
  val themeColors = MaterialThemeColors
  val primaryColor = themeColors.primary
  val targetColor = if (selected) primaryColor else BottomBarItemUnselected
  val animatedColor by animateColorAsState(
    targetValue = targetColor,
    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    label = "tab_color"
  )

  if (selected) {
    Row(
      modifier = Modifier
        .testTag(testTag)
        .clip(RoundedCornerShape(20.dp))
        .background(
          Brush.horizontalGradient(
            listOf(primaryColor.copy(alpha = 0.28f), themeColors.variant.copy(alpha = 0.16f))
          )
        )
        .border(1.2.dp, primaryColor.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
        .clickable(onClick = onClick)
        .padding(horizontal = 18.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = primaryColor,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = title,
        color = primaryColor,
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold
      )
    }
  } else {
    Column(
      modifier = Modifier
        .testTag(testTag)
        .clip(RoundedCornerShape(12.dp))
        .clickable(onClick = onClick)
        .padding(horizontal = 12.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = animatedColor,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = title,
        color = animatedColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
