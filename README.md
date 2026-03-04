🏟️ GeSport
Aplicación Android en Kotlin para la gestión de un Centro Multideporte



📌 Descripción

GeSport es una aplicación Android desarrollada en Kotlin utilizando Jetpack Compose, diseñada para gestionar un Centro Multideporte de Alto Rendimiento.

Permite administrar:

👤 Usuarios

👥 Equipos

🏟️ Instalaciones

📅 Reservas

El sistema implementa control de acceso por roles, arquitectura MVVM y persistencia local mediante Room.

🧠 Arquitectura del Proyecto

El proyecto sigue una arquitectura basada en:

UI (Jetpack Compose)
↓
ViewModel
↓
Repository (Interface)
↓
Room Repository (Implementación)
↓
DAO
↓
Base de datos SQLite (Room)
🔹 Patrón utilizado

MVVM (Model - View - ViewModel)

Repository Pattern

Separación por capas (UI / Domain / Data / Database)

🏗️ Tecnologías Utilizadas

Kotlin

Jetpack Compose

Navigation Compose

ViewModel

Coroutines

Flow

Room (SQLite ORM)

Material 3

👥 Sistema de Roles
Rol	Permisos
ADMIN_DEPORTIVO	Acceso total al sistema
ENTRENADOR	Gestiona reservas propias y de sus equipos
JUGADOR	Gestiona reservas personales
ARBITRO	Reservas personales

Las reglas de permisos están centralizadas en:

domain/ReservationAccess.kt

Esto permite mantener la lógica de negocio separada de la interfaz.

🔐 Autenticación

La autenticación se realiza contra la base de datos Room.

Flujo de Login

El usuario introduce email y contraseña.

LoginLogic.kt valida formato y campos.

Se consulta Room mediante UserRepository.

Se verifica la contraseña.

Se redirige según rol:

Admin → Dashboard

Otros → Home

Código clave
suspend fun checkLogin(email: String, password: String): User {

    if (email.isBlank() || password.isBlank()) {
        throw IllegalArgumentException("Los campos no pueden estar vacíos.")
    }

    val user = userRepository.getUserByEmail(email)
        ?: throw IllegalArgumentException("Email o contraseña incorrectos.")

    if (user.password != password) {
        throw IllegalArgumentException("Email o contraseña incorrectos.")
    }

    return user
}
🗄️ Persistencia de Datos (Cómo se recogen los usuarios)

La aplicación utiliza Room + Flow, lo que permite que la UI se actualice automáticamente cuando cambian los datos.

1️⃣ Entidad
@Entity(tableName = "usuarios")
data class User(
@PrimaryKey(autoGenerate = true)
val id: Int = 0,
val nombre: String,
val email: String,
val password: String,
val rol: String
)
2️⃣ DAO
@Query("SELECT * FROM usuarios ORDER BY nombre ASC")
fun getAll(): Flow<List<User>>
3️⃣ ViewModel
flow.collectLatest { list ->
_users = list
}
✅ ¿Por qué es importante?

Flow permite reactividad automática.

Compose redibuja la UI cuando cambian los datos.

No se utilizan listas en memoria.

Arquitectura moderna recomendada por Google.

📅 Sistema de Reservas (Parte más Compleja)

Una reserva puede ser:

✅ Personal → usuarioId != null

✅ De equipo → equipoId != null

Nunca pueden existir ambos al mismo tiempo.

Modelo simplificado
data class Reservation(
val id: Int,
val facilityId: Int,
val usuarioId: Int?,
val equipoId: Int?,
val fecha: String,
val horaInicio: String,
val horaFin: String
)
🔎 Permisos de Reservas

Implementados en:

domain/ReservationAccess.kt

Ejemplo:

if (role == UserRoles.ADMIN_DEPORTIVO) return true

val isPersonal = reservation.equipoId == null

return when (role) {
UserRoles.ENTRENADOR -> {
(isPersonal && reservation.usuarioId == currentUserId) ||
(!isPersonal && coachedTeamIds.contains(reservation.equipoId))
}
else -> {
isPersonal && reservation.usuarioId == currentUserId
}
}
✔ Ventajas

Reglas centralizadas

Código más mantenible

Fácil de testear

No depende de la UI

📂 Estructura del Proyecto
app/
┣ models/
┣ database/
┣ repository/
┣ data/
┣ domain/
┣ navigation/
┣ ui/
┃ ┣ backend/
┃ ┣ components/
┃ ┗ theme/
┗ MainActivity.kt
👑 Usuario Administrador Seed

Si no existe un administrador, la base de datos crea automáticamente uno.

Credenciales:
Email: admin@gesport.com
Password: Admin1234

Ubicación:

AppDatabase.kt

Esto garantiza siempre acceso en presentaciones o demos.

🔍 Filtros y Búsqueda

Los filtros se realizan en el ViewModel:

val filtered = users.filter {
it.nombre.lowercase().contains(query) ||
it.email.lowercase().contains(query)
}

Ventaja:

No se hacen queries innecesarias.

Filtrado rápido en memoria sobre datos observados.

🚀 Cómo Ejecutar

Clonar el repositorio

Abrir en Android Studio

Sincronizar Gradle

Ejecutar en emulador o dispositivo

Iniciar sesión con el usuario administrador

🏆 Puntos Fuertes del Proyecto

Arquitectura limpia y escalable

Uso correcto de Flow

Separación de reglas de negocio

Repository Pattern

Control de permisos por rol

Seed automático de administrador

Código modular y organizado

Fácil migración futura a API REST

🔮 Posibles Mejoras

Encriptación de contraseñas (bcrypt)

Tests unitarios

Persistencia de sesión con DataStore

Conexión a API REST con Retrofit

Notificaciones push

Calendario visual de reservas

📌 Conclusión

GeSport es una aplicación completa que demuestra:

Dominio de Kotlin

Uso correcto de Jetpack Compose

Implementación profesional de Room

Arquitectura Android moderna

Separación clara de responsabilidades