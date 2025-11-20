package com.example.gesport.ui.backend.ges_user

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gesport.repository.UserRepository
import com.example.gesport.models.User
import kotlinx.coroutines.launch

class GesUserViewModel (val userRepository: UserRepository): ViewModel() {
    var users by mutableStateOf<List<User>>(emptyList())


    var selectedRole by mutableStateOf<String?>(null)


    init {
        viewModelScope.launch {
            users = userRepository.getAllUsers()
        }
    }
    fun onRoleSelected(rol: String?) {
        selectedRole = rol
        viewModelScope.launch {
            users = if (rol == null) {
                userRepository.getAllUsers()
            } else {
                userRepository.getUsersByRole(rol)
            }
        }
    }
}