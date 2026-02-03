package com.example.gesport.ui.backend.ges_user

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.gesport.data.RoomUserRepository
import com.example.gesport.database.AppDatabase


/*
Una factoría de ViewModel es una clase cuya única misión es saber cómo
crear (y con qué dependencias) un ViewModel.
se utiliza cuando tu ViewModel necesita parámetros en el constructor (por ejemplo,
un repositorio, un Context, etc.) y ya no puedes usar el constructor vacío típico.
*/

class GesUserViewModelFactory(
    private val appContext: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Crear la base de datos (NECESITA Context)
        val database = AppDatabase.getDatabase(appContext)

        // Obtener el DAO
        val userDao = database.userDao()

        val repo = RoomUserRepository(userDao)  // se crea tu repo real
        return GesUserViewModel(repo) as T  //se crea el ViewModel
    }
}