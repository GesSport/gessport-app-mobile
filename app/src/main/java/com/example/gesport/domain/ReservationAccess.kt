package com.example.gesport.domain

import com.example.gesport.models.Reservation
import com.example.gesport.models.UserRoles

object ReservationAccess {

    /**
     * Regla:
     * - TODOS: pueden crear/cancelar SUS reservas personales.
     * - ENTRENADOR: personales + reservas de equipos donde es entrenador.
     * - ADMIN: acceso total.
     *
     * coachedTeamIds: ids de equipos donde currentUser es entrenador.
     */
    fun canManageReservation(
        currentUserId: Int,
        currentUserRole: String,
        reservation: Reservation,
        coachedTeamIds: Set<Int>
    ): Boolean {
        val role = currentUserRole.trim().ifEmpty { UserRoles.JUGADOR }
        if (role == UserRoles.ADMIN_DEPORTIVO) return true

        val isPersonal = reservation.equipoId == null
        return when (role) {
            UserRoles.ENTRENADOR -> {
                (isPersonal && reservation.usuarioId == currentUserId) ||
                        (!isPersonal && reservation.equipoId != null && coachedTeamIds.contains(reservation.equipoId))
            }
            else -> { // JUGADOR y otros
                isPersonal && reservation.usuarioId == currentUserId
            }
        }
    }

    /**
     * Validación adicional para evitar estados imposibles:
     * - Debe ser personal (usuarioId != null y equipoId == null)
     *   o de equipo (equipoId != null y usuarioId == null)
     */
    fun canCreateOrUpdateReservation(
        currentUserId: Int,
        currentUserRole: String,
        reservation: Reservation,
        coachedTeamIds: Set<Int>
    ): Boolean {
        val hasUser = reservation.usuarioId != null
        val hasTeam = reservation.equipoId != null

        // o ambos o ninguno => inválido
        if (hasUser == hasTeam) return false

        return canManageReservation(
            currentUserId = currentUserId,
            currentUserRole = currentUserRole,
            reservation = reservation,
            coachedTeamIds = coachedTeamIds
        )
    }
}