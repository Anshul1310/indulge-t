package com.anshul.indulge.models

data class Event(
    val _id: String? = null,
    val id: String? = null,
    val name: String,
    val category: String,
    val mood: String,
    val city: String,
    val price: Int,
    val date: String,
    val description: String,
    val tags: List<String> = emptyList(),
    val score: Int? = null,
    val matchingTagsCount: Int? = null
)

data class RecommendResponse(
    val data: List<Event>,
    val message: String? = null,
    val preferences: Map<String, Any>? = null
)

data class RecommendRequest(
    val prompt: String
)

data class CreateEventRequest(
    val name: String,
    val category: String,
    val mood: String,
    val city: String,
    val price: Int,
    val date: String,
    val description: String,
    val tags: List<String> = emptyList()
)

data class CreateEventResponse(
    val success: Boolean,
    val message: String? = null,
    val data: Event? = null,
    val error: String? = null
)

data class EventsListResponse(
    val success: Boolean,
    val count: Int? = null,
    val data: List<Event> = emptyList(),
    val error: String? = null
)

data class EventDetailResponse(
    val success: Boolean,
    val data: Event? = null,
    val error: String? = null
)

data class CategoriesResponse(
    val success: Boolean,
    val data: List<String> = emptyList(),
    val error: String? = null
)

data class TagsResponse(
    val success: Boolean,
    val data: List<String> = emptyList(),
    val error: String? = null
)
