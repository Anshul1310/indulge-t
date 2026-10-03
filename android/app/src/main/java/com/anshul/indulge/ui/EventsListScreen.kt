package com.anshul.indulge.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anshul.indulge.R
import com.anshul.indulge.models.Event
import com.anshul.indulge.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsListScreen(
    viewModel: EventsViewModel = viewModel(),
    onEventClick: (String) -> Unit = {}
) {
    val events by viewModel.events.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val filteredTags = remember(tags, searchQuery) {
        if (searchQuery.isBlank()) {
            tags
        } else {
            val q = searchQuery.trim().removePrefix("#").lowercase()
            tags.filter { it.lowercase().contains(q) }
        }
    }

    val filteredEvents = remember(events, selectedTag, searchQuery) {
        events.filter { event ->
            val matchesTag = selectedTag == null || event.tags.any { it.equals(selectedTag, ignoreCase = true) }

            val q = searchQuery.trim().removePrefix("#").lowercase()
            val matchesQuery = if (q.isBlank()) {
                true
            } else {
                val matchesName = event.name.lowercase().contains(q)
                val matchesDesc = event.description.lowercase().contains(q)
                val matchesCity = event.city.lowercase().contains(q)
                val matchesMood = event.mood.lowercase().contains(q)
                val matchesTags = event.tags.any { it.lowercase().contains(q) }
                matchesName || matchesDesc || matchesCity || matchesMood || matchesTags
            }

            matchesTag && matchesQuery
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "EXPLORE EVENTS",
                        fontFamily = BebasNeueFontFamily,
                        fontSize = 32.sp,
                        letterSpacing = 0.5.sp,
                        color = TextWhitePrimary
                    )
                    Text(
                        text = if (searchQuery.isNotBlank() || selectedTag != null) {
                            "${filteredEvents.size} matching experiences found"
                        } else {
                            "${filteredEvents.size} curated experiences available"
                        },
                        fontFamily = PoppinsFontFamily,
                        fontSize = 12.sp,
                        color = TextGraySecondary
                    )
                }
                if (selectedTag != null || searchQuery.isNotBlank()) {
                    TextButton(onClick = {
                        viewModel.clearFilters()
                        searchQuery = ""
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp), tint = IndulgePurpleLight)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset", color = IndulgePurpleLight, fontFamily = PoppinsFontFamily)
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search events, vibes, tags, city...",
                        fontFamily = PoppinsFontFamily,
                        fontSize = 13.sp,
                        color = TextGraySecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = IndulgePurpleLight,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextGraySecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedBorderColor = IndulgePurplePrimary,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedTextColor = TextWhitePrimary,
                    unfocusedTextColor = TextWhitePrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (filteredTags.isNotEmpty()) {
            item {
                Column {
                    Text(
                        text = "Browse by Tag",
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextWhitePrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredTags) { tag ->
                            val isSelected = selectedTag.equals(tag, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) viewModel.clearFilters()
                                    else viewModel.selectTag(tag)
                                },
                                label = { Text("#$tag", fontFamily = PoppinsFontFamily, fontSize = 12.sp) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IndulgePurplePrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = TextGraySecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) IndulgePurplePrimary else DarkCardBorder,
                                    enabled = true,
                                    selected = isSelected
                                )
                            )
                        }
                    }
                }
            }
        }

        if (selectedTag != null) {
            item {
                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IndulgePurplePrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Tag, contentDescription = null, tint = IndulgePurpleLight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Filtered by Tag: #$selectedTag",
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = TextWhitePrimary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearFilters() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextGraySecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = IndulgePurplePrimary)
                }
            }
        } else if (errorMessage != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error, fontFamily = PoppinsFontFamily)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.fetchEvents() }, colors = ButtonDefaults.buttonColors(containerColor = IndulgePurplePrimary)) {
                        Text("Retry", fontFamily = PoppinsFontFamily)
                    }
                }
            }
        } else if (filteredEvents.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) "No events found matching '$searchQuery'" else "No events found.",
                        fontFamily = PoppinsFontFamily,
                        fontSize = 15.sp,
                        color = TextGraySecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = {
                            viewModel.clearFilters()
                            searchQuery = ""
                        },
                        border = androidx.compose.foundation.BorderStroke(1.dp, IndulgePurplePrimary)
                    ) {
                        Text("Clear Search & Filters", color = IndulgePurpleLight, fontFamily = PoppinsFontFamily)
                    }
                }
            }
        } else {
            items(filteredEvents) { event ->
                val eventId = event._id ?: event.id ?: ""
                EventListItemCard(
                    event = event,
                    selectedTag = selectedTag,
                    onTagClick = { tag ->
                        searchQuery = ""
                        viewModel.selectTag(tag)
                    },
                    onCardClick = { onEventClick(eventId) }
                )
            }
        }
    }
}

@Composable
fun EventListItemCard(
    event: Event,
    selectedTag: String?,
    onTagClick: (String) -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Image(
                painter = painterResource(id = R.drawable.event_banner),
                contentDescription = "Event banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = event.name,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = TextWhitePrimary
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${event.city} • Mood: ${event.mood.replaceFirstChar { it.uppercase() }}",
                fontFamily = PoppinsFontFamily,
                fontSize = 13.sp,
                color = TextGraySecondary
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (event.price == 0) "Free Entry" else "₹${event.price}",
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = IndulgePurpleLight
                )
                Text(
                    text = event.date,
                    fontFamily = PoppinsFontFamily,
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            if (event.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.description,
                    fontFamily = PoppinsFontFamily,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = TextWhitePrimary.copy(alpha = 0.8f),
                    maxLines = 2
                )
            }

            if (event.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(event.tags) { tag ->
                        val isCurrentTagSelected = selectedTag == tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isCurrentTagSelected) IndulgePurplePrimary
                                    else DarkSurfaceVariant
                                )
                                .clickable { onTagClick(tag) }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "#$tag",
                                fontFamily = PoppinsFontFamily,
                                fontSize = 11.sp,
                                color = if (isCurrentTagSelected) Color.White
                                else TextGraySecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
