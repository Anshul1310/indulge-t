package com.anshul.indulge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anshul.indulge.ui.AiAssistantScreen
import com.anshul.indulge.ui.BookingSuccessInfo
import com.anshul.indulge.ui.BookingSuccessScreen
import com.anshul.indulge.ui.EventDetailsScreen
import com.anshul.indulge.ui.EventsListScreen
import com.anshul.indulge.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IndulgeTheme {
                var selectedTab by remember { mutableIntStateOf(1) }
                var viewingEventId by remember { mutableStateOf<String?>(null) }
                var bookingSuccessInfo by remember { mutableStateOf<BookingSuccessInfo?>(null) }
                val density = LocalDensity.current
                val isKeyboardOpen = WindowInsets.ime.getBottom(density) > 0

                BackHandler(enabled = bookingSuccessInfo != null || viewingEventId != null || selectedTab == 0) {
                    if (bookingSuccessInfo != null) {
                        bookingSuccessInfo = null
                    } else if (viewingEventId != null) {
                        viewingEventId = null
                    } else if (selectedTab == 0) {
                        selectedTab = 1
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBg
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = DarkBg,
                        bottomBar = {
                            if (!isKeyboardOpen && bookingSuccessInfo == null && viewingEventId == null && selectedTab != 0) {
                                SmoothIndulgeBottomNav(
                                    selectedTab = selectedTab,
                                    onTabSelected = { selectedTab = it }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (bookingSuccessInfo != null) {
                                BookingSuccessScreen(
                                    bookingInfo = bookingSuccessInfo!!,
                                    onDoneClick = {
                                        bookingSuccessInfo = null
                                        viewingEventId = null
                                        selectedTab = 1
                                    }
                                )
                            } else if (viewingEventId != null) {
                                EventDetailsScreen(
                                    eventId = viewingEventId,
                                    onBackClick = { viewingEventId = null },
                                    onPayNowClick = { title, price, city, date ->
                                        bookingSuccessInfo = BookingSuccessInfo(
                                            eventTitle = title,
                                            price = price,
                                            city = city,
                                            date = date
                                        )
                                    }
                                )
                            } else {
                                Crossfade(
                                    targetState = selectedTab,
                                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                                    label = "screenCrossfade"
                                ) { tab ->
                                    when (tab) {
                                        0 -> AiAssistantScreen(
                                            onBackClick = { selectedTab = 1 },
                                            onEventClick = { eventId -> viewingEventId = eventId }
                                        )
                                        1 -> EventsListScreen(
                                            onEventClick = { eventId -> viewingEventId = eventId }
                                        )
                                        2 -> EventDetailsScreen(
                                            eventId = null,
                                            onPayNowClick = { title, price, city, date ->
                                                bookingSuccessInfo = BookingSuccessInfo(
                                                    eventTitle = title,
                                                    price = price,
                                                    city = city,
                                                    date = date
                                                )
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
    }
}

@Composable
fun SmoothIndulgeBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .border(1.dp, Color(0xFF22222C), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        color = Color(0xFF101015),
        tonalElevation = 10.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmoothBottomNavItem(
                    title = "AI Chat",
                    icon = Icons.Default.AutoAwesome,
                    isSelected = selectedTab == 0,
                    modifier = Modifier.weight(1f),
                    onClick = { onTabSelected(0) }
                )
                SmoothBottomNavItem(
                    title = "Events",
                    icon = Icons.Default.Explore,
                    isSelected = selectedTab == 1,
                    modifier = Modifier.weight(1f),
                    onClick = { onTabSelected(1) }
                )
                SmoothBottomNavItem(
                    title = "Details",
                    icon = Icons.Default.ConfirmationNumber,
                    isSelected = selectedTab == 2,
                    modifier = Modifier.weight(1f),
                    onClick = { onTabSelected(2) }
                )
            }
        }
    }
}

@Composable
fun SmoothBottomNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color(0xFF888898),
        animationSpec = tween(durationMillis = 200),
        label = "iconColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) IndulgePurpleLight else Color(0xFF6E6E7C),
        animationSpec = tween(durationMillis = 200),
        label = "textColor"
    )

    val pillColor by animateColorAsState(
        targetValue = if (isSelected) IndulgePurplePrimary else Color.Transparent,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "pillColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
        label = "scale"
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 28.dp, color = IndulgePurpleLight)
            ) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .height(30.dp)
                .width(48.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(pillColor)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = title,
            fontFamily = PoppinsFontFamily,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 11.sp,
            color = textColor
        )
    }
}