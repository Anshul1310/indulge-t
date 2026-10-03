package com.anshul.indulge.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anshul.indulge.R
import com.anshul.indulge.models.Event
import com.anshul.indulge.network.ApiClient
import com.anshul.indulge.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun EventDetailsScreen(
    eventId: String? = null,
    event: Event? = null,
    onBackClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
    onPayNowClick: ((title: String, price: Int, city: String, date: String) -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var fetchedEvent by remember(eventId) { mutableStateOf<Event?>(event) }
    var isLoading by remember(eventId) { mutableStateOf(eventId != null && event == null) }
    var errorMessage by remember(eventId) { mutableStateOf<String?>(null) }

    LaunchedEffect(eventId) {
        if (eventId != null && event == null) {
            isLoading = true
            errorMessage = null
            try {
                val response = ApiClient.apiService.getEventById(eventId)
                if (response.success && response.data != null) {
                    fetchedEvent = response.data
                } else {
                    errorMessage = response.error ?: "Event not found"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load event details"
            } finally {
                isLoading = false
            }
        }
    }

    val activeEvent = fetchedEvent ?: event

    val eventTitle = activeEvent?.name?.ifBlank { "Social Mixer & Game Night" } ?: "Social Mixer & Game Night"
    val eventCategory = activeEvent?.category?.ifBlank { "18 yrs & above" } ?: "18 yrs & above"
    val eventCity = activeEvent?.city?.ifBlank { "New Delhi" } ?: "New Delhi"
    val eventPrice = activeEvent?.price ?: 499
    val eventDescription = if (!activeEvent?.description.isNullOrBlank()) {
        activeEvent!!.description
    } else {
        "Get ready to step into experiences where conversations flow naturally and connections feel effortless."
    }

    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var expandedFaqIndex by remember { mutableIntStateOf(0) }


    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                IndulgeTopHeader(onBackClick = onBackClick, onMenuClick = onMenuClick)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = IndulgePurplePrimary)
                }
            }
        }
        return
    }

    if (errorMessage != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                IndulgeTopHeader(onBackClick = onBackClick, onMenuClick = onMenuClick)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = errorMessage ?: "Failed to load event details",
                        color = MaterialTheme.colorScheme.error,
                        fontFamily = PoppinsFontFamily
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onBackClick?.invoke() },
                        colors = ButtonDefaults.buttonColors(containerColor = IndulgePurplePrimary)
                    ) {
                        Text("Go Back", fontFamily = PoppinsFontFamily)
                    }
                }
            }
        }
        return
    }

    val highlightsListState = rememberLazyListState()
    val highlights = remember {
        listOf(
            HighlightItem(
                title = "Conversations & Drinks",
                subtitle = "Effortless mingling with like-minded people",
                category = "Social",
                imageRes = R.drawable.highlight_1
            ),
            HighlightItem(
                title = "Games & Sports Fun",
                subtitle = "Exciting group activities to break the ice",
                category = "Activities",
                imageRes = R.drawable.highlight_2
            ),
            HighlightItem(
                title = "Creative Jams & Art",
                subtitle = "Workshop sessions and shared memories",
                category = "Creative",
                imageRes = R.drawable.highlight_3
            ),
            HighlightItem(
                title = "Social Mixer Evenings",
                subtitle = "Laughter, music and great vibes",
                category = "Vibe",
                imageRes = R.drawable.event_banner
            )
        )
    }


    val faqs = remember {
        listOf(
            FaqItem(
                question = "Who Can Host?",
                answer = "Anyone Looking To Meet Like-Minded People And Engage In Activities They're Passionate About."
            ),
            FaqItem(
                question = "Do I Need A Big Community?",
                answer = "No, whether you're solo or have a small group, Indulge connects you with an enthusiastic community instantly."
            ),
            FaqItem(
                question = "Can I Bring My Friends Along?",
                answer = "Absolutely! You can reserve multiple spots and bring your friends along to enjoy the experience together."
            ),
            FaqItem(
                question = "What Is The Cancellation Policy?",
                answer = "Free cancellation up to 24 hours before the event start time. Refunds are processed automatically."
            ),
            FaqItem(
                question = "Are Food & Beverages Included?",
                answer = "Complimentary welcome drink and signature appetizers are included with every ticket."
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            IndulgeTopHeader(
                onBackClick = onBackClick,
                onMenuClick = onMenuClick
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(bottom = 90.dp)
            ) {
                EventBannerPlaceholder(
                    eventName = eventTitle,
                    city = eventCity
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "About the Event",
                        fontFamily = BebasNeueFontFamily,
                        fontSize = 32.sp,
                        letterSpacing = 0.5.sp,
                        color = TextWhitePrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = eventDescription,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        lineHeight = 23.sp,
                        color = TextWhitePrimary.copy(alpha = 0.9f),
                        maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 3
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isDescriptionExpanded) "Show less" else "Show more",
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = IndulgePurplePrimary,
                        modifier = Modifier
                            .clickable { isDescriptionExpanded = !isDescriptionExpanded }
                            .padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    EventMetaInfoCard(
                        category = eventCategory,
                        whoItsFor = "Everyone",
                        tags = if (activeEvent?.tags?.isNotEmpty() == true) activeEvent.tags else listOf("Conversations", "Drinks", "Games", "Vibes")
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Highlights",
                            fontFamily = BebasNeueFontFamily,
                            fontSize = 32.sp,
                            letterSpacing = 0.5.sp,
                            color = TextWhitePrimary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF27272A))
                                    .clickable {
                                        coroutineScope.launch {
                                            val prevIndex = (highlightsListState.firstVisibleItemIndex - 1).coerceAtLeast(0)
                                            highlightsListState.animateScrollToItem(prevIndex)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Previous Highlight",
                                    tint = TextWhitePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF27272A))
                                    .clickable {
                                        coroutineScope.launch {
                                            val nextIndex = (highlightsListState.firstVisibleItemIndex + 1).coerceAtMost(highlights.size - 1)
                                            highlightsListState.animateScrollToItem(nextIndex)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Next Highlight",
                                    tint = TextWhitePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        state = highlightsListState,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(highlights) { index, highlight ->
                            HighlightPlaceholderCard(highlight = highlight, index = index)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Venue",
                        fontFamily = BebasNeueFontFamily,
                        fontSize = 32.sp,
                        letterSpacing = 0.5.sp,
                        color = TextWhitePrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    VenueCard(
                        venueName = if (event != null && event.city.isNotBlank()) "${event.city} Cultural Hub" else "Jawaharlal Nehru Stadium",
                        venueAddress = if (event != null && event.city.isNotBlank()) "Gate 2, Inner Circle, ${event.city}, India" else "Jawaharlal Nehru Stadium, Pragati Vihar, New Delhi, Delhi, India",
                        onGetDirectionsClick = {
                            val destination = if (event != null) "${event.city}, India" else "Jawaharlal Nehru Stadium, New Delhi"
                            val mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(destination))
                            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                            try {
                                context.startActivity(mapIntent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "Directions to: $destination", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        faqs.forEachIndexed { index, faq ->
                            val isExpanded = expandedFaqIndex == index
                            FaqAccordionCard(
                                faq = faq,
                                isExpanded = isExpanded,
                                onToggle = {
                                    expandedFaqIndex = if (isExpanded) -1 else index
                                }
                            )
                        }
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = DarkSurface.copy(alpha = 0.95f),
            tonalElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = if (eventPrice == 0) "Free" else "₹$eventPrice",
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextWhitePrimary
                    )
                    Text(
                        text = "per participant",
                        fontFamily = PoppinsFontFamily,
                        fontSize = 12.sp,
                        color = TextGraySecondary
                    )
                }

                Button(
                    onClick = {
                        val date = activeEvent?.date ?: "2026-10-28"
                        onPayNowClick?.invoke(eventTitle, eventPrice, eventCity, date)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IndulgePurplePrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = if (eventPrice == 0) "Reserve Spot" else "Pay Now",
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun IndulgeTopHeader(
    onBackClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    Surface(
        color = DarkBg,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhitePrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Image(
                    painter = painterResource(id = R.drawable.indulge_logo),
                    contentDescription = "Indulgeout Logo",
                    modifier = Modifier.height(28.dp),
                    contentScale = ContentScale.Fit
                )
            }

            IconButton(
                onClick = { onMenuClick?.invoke() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = TextWhitePrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun EventBannerPlaceholder(
    eventName: String,
    city: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.event_banner),
            contentDescription = "Event Banner",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.55f),
                            DarkBg
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Banner",
                fontFamily = BebasNeueFontFamily,
                fontSize = 56.sp,
                letterSpacing = 2.sp,
                color = TextWhitePrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "📍 $city • Curated Gathering",
                    fontFamily = PoppinsFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = IndulgePurpleLight
                )
            }
        }
    }
}


@Composable
fun EventMetaInfoCard(
    category: String,
    whoItsFor: String,
    tags: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF23232C)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = "Category",
                        tint = IndulgePurpleLight,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Category",
                        fontFamily = PoppinsFontFamily,
                        fontSize = 12.sp,
                        color = TextGraySecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = category,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextWhitePrimary
                    )
                }
            }

            HorizontalDivider(
                color = DarkCardBorder.copy(alpha = 0.5f),
                thickness = 0.5.dp
            )

            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF23232C)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "Who it's for",
                        tint = IndulgePurpleLight,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Who it's for",
                        fontFamily = PoppinsFontFamily,
                        fontSize = 12.sp,
                        color = TextGraySecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = whoItsFor,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextWhitePrimary
                    )
                }
            }

            HorizontalDivider(
                color = DarkCardBorder.copy(alpha = 0.5f),
                thickness = 0.5.dp
            )

            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF23232C)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "What you'll find here",
                        tint = IndulgePurpleLight,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "What you'll find here",
                        fontFamily = PoppinsFontFamily,
                        fontSize = 12.sp,
                        color = TextGraySecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        tags.take(3).forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "#$tag",
                                    fontFamily = PoppinsFontFamily,
                                    fontSize = 11.sp,
                                    color = TextWhitePrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class HighlightItem(
    val title: String,
    val subtitle: String,
    val category: String,
    val imageRes: Int
)

@Composable
fun HighlightPlaceholderCard(
    highlight: HighlightItem,
    index: Int
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(270.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2E3C))
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = highlight.imageRes),
                contentDescription = highlight.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.88f)
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = highlight.category,
                    fontFamily = PoppinsFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = IndulgePurpleLight
                )
            }

            Text(
                text = "0${index + 1}",
                fontFamily = BebasNeueFontFamily,
                fontSize = 42.sp,
                color = Color.White.copy(alpha = 0.35f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = highlight.title,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 19.sp,
                    color = TextWhitePrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = highlight.subtitle,
                    fontFamily = PoppinsFontFamily,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = TextWhitePrimary.copy(alpha = 0.8f)
                )
            }
        }
    }
}


@Composable
fun VenueCard(
    venueName: String,
    venueAddress: String,
    onGetDirectionsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VenueCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(VenuePurpleAction.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Venue",
                        tint = VenuePurpleAction,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = venueName,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = VenueTitleDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = venueAddress,
                        fontFamily = PoppinsFontFamily,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = VenueAddressDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .clickable { onGetDirectionsClick() }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NearMe,
                    contentDescription = "Get Directions",
                    tint = VenuePurpleAction,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Get Directions",
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = VenuePurpleAction
                )
            }
        }
    }
}

data class FaqItem(
    val question: String,
    val answer: String
)

@Composable
fun FaqAccordionCard(
    faq: FaqItem,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val containerBg = if (isExpanded) AccordionActiveBg else AccordionInactiveBg
    val borderColor = if (isExpanded) AccordionActiveBorder else AccordionInactiveBorder

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerBg)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onToggle() }
            .animateContentSize(animationSpec = tween(250))
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = TextWhitePrimary,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = if (isExpanded) TextWhitePrimary else TextGraySecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = faq.answer,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = TextWhitePrimary.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}
