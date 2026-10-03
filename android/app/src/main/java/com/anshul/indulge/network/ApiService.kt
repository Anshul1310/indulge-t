package com.anshul.indulge.network

import com.anshul.indulge.models.*
import retrofit2.http.*

interface ApiService {
    @POST("/api/ai/recommend")
    suspend fun getRecommendations(@Body request: RecommendRequest): RecommendResponse

    @POST("/api/events")
    suspend fun createEvent(@Body request: CreateEventRequest): CreateEventResponse

    @GET("/api/events")
    suspend fun getEvents(
        @Query("tag") tag: String? = null,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): EventsListResponse

    @GET("/api/events/{id}")
    suspend fun getEventById(@Path("id") id: String): EventDetailResponse

    @GET("/api/categories")
    suspend fun getCategories(): CategoriesResponse

    @GET("/api/events/tags")
    suspend fun getTags(): TagsResponse
}
