package com.learnkmp.habittrackerandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.learnkmp.habittrackerandroid.data.model.Habit
import com.learnkmp.habittrackerandroid.data.repository.AuthRepository
import com.learnkmp.habittrackerandroid.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class HabitViewModel(
    private val habitRepository: HabitRepository = HabitRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    val currentUser = authRepository.currentUser

    val habits: StateFlow<List<Habit>> = currentUser.flatMapLatest { user ->
        if (user != null) {
            habitRepository.getHabits(user.uid)
        } else {
            flowOf(emptyList())
        }
    }.let { flow ->
        val stateFlow = MutableStateFlow<List<Habit>>(emptyList())
        viewModelScope.launch {
            flow.collect { stateFlow.value = it }
        }
        stateFlow
    }

    fun loginWithEmail(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.signInWithEmail(email, password)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Login failed")
            }
        }
    }

    fun registerWithEmail(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.signUpWithEmail(email, password)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Registration failed")
            }
        }
    }

    fun signInWithGoogle(credential: AuthCredential, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.signInWithCredential(credential)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Google Sign-In failed")
            }
        }
    }

    fun addHabit(name: String, description: String) {
        val userId = currentUser.value?.uid ?: return
        viewModelScope.launch {
            habitRepository.addHabit(Habit(name = name, description = description, userId = userId))
        }
    }

    fun toggleHabit(habit: Habit, timestamp: Long) {
        viewModelScope.launch {
            habitRepository.toggleHabitCompletion(habit, timestamp)
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
