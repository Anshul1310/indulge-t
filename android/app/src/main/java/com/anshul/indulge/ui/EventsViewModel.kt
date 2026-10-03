package com.anshul.indulge.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshul.indulge.models.Event
import com.anshul.indulge.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventsViewModel : ViewModel() {
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _tags = MutableStateFlow<List<String>>(emptyList())
    val tags: StateFlow<List<String>> = _tags.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag: StateFlow<String?> = _selectedTag.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedEvent = MutableStateFlow<Event?>(null)
    val selectedEvent: StateFlow<Event?> = _selectedEvent.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Fetch tags
                val tagsResponse = ApiClient.apiService.getTags()
                if (tagsResponse.success) {
                    _tags.value = tagsResponse.data
                }

                // Fetch categories
                val categoriesResponse = ApiClient.apiService.getCategories()
                if (categoriesResponse.success) {
                    _categories.value = categoriesResponse.data
                }

                // Fetch events
                fetchEvents()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load events"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = ApiClient.apiService.getEvents(
                    tag = _selectedTag.value,
                    category = _selectedCategory.value,
                    search = _searchQuery.value.ifBlank { null }
                )
                if (response.success) {
                    _events.value = response.data
                } else {
                    _errorMessage.value = response.error ?: "Failed to fetch events"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Network error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectTag(tag: String) {
        if (_selectedTag.value == tag) {
            _selectedTag.value = null // Toggle off
        } else {
            _selectedTag.value = tag
        }
        fetchEvents()
    }

    fun selectCategory(category: String) {
        if (_selectedCategory.value == category) {
            _selectedCategory.value = null // Toggle off
        } else {
            _selectedCategory.value = category
        }
        fetchEvents()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        fetchEvents()
    }

    fun clearFilters() {
        _selectedTag.value = null
        _selectedCategory.value = null
        _searchQuery.value = ""
        fetchEvents()
    }

    fun openEventDetails(event: Event) {
        _selectedEvent.value = event
    }

    fun closeEventDetails() {
        _selectedEvent.value = null
    }
}

