package com.learnkmp.habittrackerandroid.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.learnkmp.habittrackerandroid.data.model.Habit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class HabitRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    fun getHabits(userId: String): Flow<List<Habit>> = callbackFlow {
        val subscription = db.collection("habits")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val habits = snapshot.toObjects(Habit::class.java)
                    trySend(habits)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addHabit(habit: Habit) {
        db.collection("habits").add(habit).await()
    }

    suspend fun toggleHabitCompletion(habit: Habit, dateTimestamp: Long) {
        val completedDates = habit.completedDates.toMutableList()
        if (completedDates.contains(dateTimestamp)) {
            completedDates.remove(dateTimestamp)
        } else {
            completedDates.add(dateTimestamp)
        }
        db.collection("habits").document(habit.id)
            .update("completedDates", completedDates).await()
    }
}
