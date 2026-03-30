package com.learnkmp.habittrackerandroid.data.model

import com.google.firebase.firestore.DocumentId

data class Habit(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val userId: String = "",
    val completedDates: List<Long> = emptyList() // Timestamps of completion
)
