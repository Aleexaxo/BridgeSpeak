package com.example.bridgespeak

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel

class HistoryViewModel : ViewModel() {
    // Use a SnapshotStateList so Compose observes changes automatically
    private val _history = mutableStateListOf<HistoryItem>()
    val historyList: SnapshotStateList<HistoryItem> get() = _history

    fun addHistoryItem(item: HistoryItem) {
        // Add newest at index 0
        _history.add(0, item)
    }

    fun removeHistoryItem(item: HistoryItem) {
        _history.remove(item)
    }

    fun clearHistory() {
        _history.clear()
    }
}