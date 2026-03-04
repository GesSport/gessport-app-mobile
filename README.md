# 🏟️ GeSport — Aplicación Android de Gestión Deportiva

> **Aplicación Android nativa desarrollada en Kotlin con Jetpack Compose** para la gestión integral de usuarios, instalaciones, equipos y reservas en un entorno deportivo.

---

## 📋 Índice

1. [¿Qué es GeSport?](#-qué-es-gesport)
2. [Tecnologías y Arquitectura](#-tecnologías-y-arquitectura)
3. [Estructura del Proyecto](#-estructura-del-proyecto)
4. [Capa de Modelos](#-capa-de-modelos-models)
5. [Capa de Base de Datos](#-capa-de-base-de-datos-database)
6. [Capa de Repositorios](#-capa-de-repositorios-repository--data)
7. [Capa de Dominio](#-capa-de-dominio-domain)
8. [Navegación](#-navegación-navigation)
9. [Capa de UI — ViewModels](#-capa-de-ui--viewmodels)
10. [Capa de UI — Pantallas](#-capa-de-ui--pantallas)
11. [Componentes Reutilizables](#-componentes-reutilizables-uicomponents)
12. [Sistema de Roles y Permisos](#-sistema-de-roles-y-permisos)
13. [Flujo Completo de la Aplicación](#-flujo-completo-de-la-aplicación)
14. [Evolución del Proyecto](#-evolución-del-proyecto)
15. [Preguntas Frecuentes para la Defensa](#-preguntas-frecuentes-para-la-defensa)

---

## 🎯 ¿Qué es GeSport?

**GeSport** es una aplicación Android orientada a la gestión de instalaciones deportivas. Permite:

- 🔐 **Autenticación** con sistema de roles
- 👤 **Gestión de usuarios** (altas, bajas, modificaciones)
- 🏟️ **Gestión de instalaciones** (pistas, canchas, pabellones)
- 👥 **Gestión de equipos** y sus jugadores
- 📅 **Reservas** de instalaciones por usuarios o equipos
- 🛡️ **Control de acceso** según el rol del usuario autenticado

### Roles de usuario

| Rol                     | Código            | Permisos principales                                |
|-------------------------|-------------------|-----------------------------------------------------|
| Administrador Deportivo | `ADMIN_DEPORTIVO` | Acceso total al panel de gestión                    |
| Entrenador              | `ENTRENADOR`      | Gestiona sus reservas personales y las de su equipo |
| Jugador                 | `JUGADOR`         | Solo gestiona sus reservas personales               |
| Árbitro                 | `ARBITRO`         | Solo gestiona sus reservas personales               |

---

## 🛠️ Tecnologías y Arquitectura

### Tecnologías utilizadas

| Tecnología                 | Para qué sirve en GeSport                                    |
|----------------------------|--------------------------------------------------------------|
| **Kotlin**                 | Lenguaje principal de desarrollo                             |
| **Jetpack Compose**        | Construcción de la interfaz de usuario declarativa           |
| **Room (SQLite)**          | Base de datos local persistente                              |
| **Navigation Compose**     | Navegación entre pantallas con rutas                         |
| **ViewModel + Coroutines** | Gestión de estado y operaciones asíncronas                   |
| **Flow (Kotlin)**          | Streams de datos reactivos (UI se actualiza automáticamente) |
| **Material Design 3**      | Sistema de diseño visual de Google                           |

### Patrón de Arquitectura: MVVM + Repository

La aplicación sigue el patrón **MVVM** (Model-View-ViewModel) combinado con el **patrón Repository**:

```
UI (Compose Screens)
        ↕  observa estado / llama funciones
ViewModel (lógica de presentación, estado de UI)
        ↕  llama métodos del repositorio
Repository Interface (contrato abstracto)
        ↕  implementa
Room DAO (acceso real a la base de datos SQLite)
        ↕
Base de Datos SQLite (datos persistentes en el dispositivo)
```

**¿Por qué MVVM + Repository?**
- **Separación de responsabilidades**: cada capa tiene una sola misión
- **Testabilidad**: puedes sustituir el repositorio real por uno de pruebas
- **Escalabilidad**: si mañana hay una API REST, solo cambias la implementación del repositorio sin tocar la UI
- **Reactividad**: con `Flow`, cuando cambia un dato en Room, la UI se actualiza sola

---

## 📁 Estructura del Proyecto

```
gesport/
├── MainActivity.kt                    # Punto de entrada de la app
│
├── models/                            # Entidades de datos
│   ├── User.kt                        # Usuario
│   ├── UserRoles.kt                   # Constantes de roles
│   ├── Facility.kt                    # Instalación deportiva
│   ├── Team.kt                        # Equipo
│   ├── Reservation.kt                 # Reserva de instalación
│   ├── Match.kt                       # Partido (entidad futura)
│   └── Sports.kt                      # Catálogo de deportes
│
├── database/                          # Capa Room (DAOs + AppDatabase)
│   ├── AppDatabase.kt                 # Singleton de la BD + seed admin
│   ├── UserDao.kt                     # Operaciones SQL para usuarios
│   ├── FacilityDao.kt                 # Operaciones SQL para instalaciones
│   ├── TeamDao.kt                     # Operaciones SQL para equipos
│   ├── ReservationDao.kt              # Operaciones SQL para reservas
│   └── MatchDao.kt                    # Operaciones SQL para partidos
│
├── repository/                        # Interfaces (contratos)
│   ├── UserRepository.kt
│   ├── FacilityRepository.kt
│   ├── TeamRepository.kt
│   └── ReservationRepository.kt
│
├── data/                              # Implementaciones de repositorios
│   ├── RoomUserRepository.kt          # Implementación real con Room
│   ├── RoomFacilityRepository.kt
│   ├── RoomTeamRepository.kt
│   ├── RoomReservationRepository.kt
│   ├── DataUserRepository.kt          # (Legado - datos en memoria)
│   ├── JsonUserRepository.kt          # (Legado - datos en JSON)
│   ├── ApiUserRepository.kt           # (Futuro - API REST)
│   └── LoginRepository.kt             # (Legado - lista hardcodeada)
│
├── domain/                            # Lógica de negocio pura
│   ├── LoginLogic.kt                  # Validación de credenciales y formularios
│   └── ReservationAccess.kt           # Reglas de acceso a reservas por rol
│
├── navigation/
│   └── Navigation.kt                  # Mapa completo de rutas de la app
│
└── ui/
    ├── theme/                         # Colores, tipografía, tema
    │   ├── Color.kt
    │   ├── Theme.kt
    │   ├── Type.kt
    │   └── Gradients.kt
    │
    ├── components/                    # Componentes Compose reutilizables
    │   ├── Buttons.kt
    │   ├── TextFields.kt
    │   ├── SelectField.kt
    │   ├── DatePickerField.kt
    │   ├── DashboardTile.kt
    │   ├── GeSportBackground.kt
    │   ├── FacilityCard.kt
    │   ├── ReservationCard.kt
    │   ├── TeamCard.kt
    │   └── UserCard.kt
    │
    ├── welcome/
    │   └── WelcomeScreen.kt           # Pantalla de bienvenida
    │
    ├── login/
    │   ├── LoginScreen.kt             # Inicio de sesión
    │   ├── RegisterScreen.kt          # Registro de nuevos usuarios
    │   └── RecoverPassScreen.kt       # Recuperación de contraseña
    │
    ├── home/
    │   └── HomeScreen.kt              # Panel principal del usuario
    │
    ├── dashboard/
    │   └── DashboardScreen.kt         # Panel de administración
    │
    └── backend/                       # Módulos CRUD de gestión
        ├── ges_user/
        │   ├── GesUserScreen.kt
        │   ├── AddUserScreen.kt
        │   ├── GesUserViewModel.kt
        │   └── GesUserViewModelFactory.kt
        ├── ges_facility/
        │   ├── GesFacilityScreen.kt
        │   ├── AddFacilityScreen.kt
        │   ├── GesFacilityViewModel.kt
        │   └── GesFacilityViewModelFactory.kt
        ├── ges_team/
        │   ├── GesTeamScreen.kt
        │   ├── AddTeamScreen.kt
        │   ├── GesTeamViewModel.kt
        │   └── GesTeamViewModelFactory.kt
        └── ges_reservation/
            ├── GesReservationScreen.kt
            ├── AddReservationScreen.kt
            ├── GesReservationViewModel.kt
            ├── GesReservationViewModelFactory.kt
            └── GesReservationTimeUtils.kt
```

---

## 🧱 Capa de Modelos (`models/`)

Los modelos son las **entidades de datos** de la aplicación. En GeSport, cada modelo es a la vez:
- Una **data class de Kotlin** (estructura de datos con equals, hashCode, copy... automáticos)
- Una **entidad de Room** (tabla en SQLite) gracias a `@Entity`

### `User.kt` — El usuario del sistema

```kotlin
@Entity(tableName = "usuarios")
@Serializable
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String,               // "ADMIN_DEPORTIVO", "ENTRENADOR", etc.
    val fechaNacimiento: String? = null,
    val telefono: String? = null,
    val posicion: String? = null,   // posición en el deporte (portero, base...)
    val equipoId: Int? = null       // FK hacia la tabla "equipos"
)
```

**Puntos clave:**
- `@PrimaryKey(autoGenerate = true)`: Room genera el ID automáticamente, como un AUTO_INCREMENT de SQL.
- Los campos con `?` (nullable) son opcionales. Si no se rellenan, se guardan como `NULL` en SQLite.
- `@Serializable` permite serializar el objeto a JSON (usado en la implementación legada con JSON).
- `equipoId` actúa como clave foránea hacia la tabla `equipos`, aunque Room no impone FK constraints por defecto.

---

### `UserRoles.kt` — Catálogo de roles

```kotlin
object UserRoles {
    const val ADMIN_DEPORTIVO = "ADMIN_DEPORTIVO"
    const val ENTRENADOR = "ENTRENADOR"
    const val JUGADOR = "JUGADOR"
    const val ARBITRO = "ARBITRO"

    val allRoles = mapOf(
        ADMIN_DEPORTIVO to "Admin",
        ENTRENADOR to "Entrenador",
        // ...
    )

    val roleColors = mapOf(
        ADMIN_DEPORTIVO to 0xFF4DA8DAL,  // Azul
        JUGADOR         to 0xFFF4A261L,  // Naranja
        // ...
    )
}
```

**¿Por qué `object`?** Porque es un **Singleton**: existe una sola instancia en toda la app. Así evitamos duplicar strings como `"ADMIN_DEPORTIVO"` por todo el código, lo que reduciría errores tipográficos.

---

### `Reservation.kt` — Una reserva de instalación

```kotlin
@Entity(tableName = "reservas")
data class Reservation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pistaId: Int,         // qué instalación se reserva
    val usuarioId: Int? = null,   // reserva personal (solo uno de los dos)
    val equipoId: Int? = null,    // reserva de equipo  (solo uno de los dos)
    val creadaPorUserId: Int,     // auditoría: quién creó la reserva
    val fecha: String,            // formato "yyyy-MM-dd"
    val horaInicio: String,       // formato "HH:mm"
    val horaFin: String,          // formato "HH:mm"
    val tipoUso: String? = null   // entrenamiento, partido, libre...
)
```

**Regla de negocio modelada en el dato:** Una reserva es de tipo personal (`usuarioId != null && equipoId == null`) O de equipo (`equipoId != null && usuarioId == null`). Nunca ambas ni ninguna. Esta restricción se valida en `ReservationAccess.kt`.

---

### `Facility.kt`, `Team.kt`, `Sports.kt`, `Match.kt`

```kotlin
// Instalación deportiva
@Entity(tableName = "instalaciones")
data class Facility(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val tipoDeporte: String,   // "FUTBOL", "PADEL", etc.
    val disponible: Boolean = true,
    val capacidad: Int? = null,
    val localizacion: String? = null
)

// Equipo
@Entity(tableName = "equipos")
data class Team(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val categoria: String,      // ej: "Senior", "Juvenil"
    val entrenadorId: Int? = null  // FK hacia usuarios (entrenador asignado)
)

// Catálogo de deportes (no es entidad Room, solo datos de configuración)
object Sports {
    val allSports = listOf(
        "FUTBOL" to "Fútbol",
        "PADEL" to "Pádel",
        "BALONCESTO" to "Baloncesto",
        // ...
    )
    fun labelFor(key: String): String { /* devuelve la etiqueta legible */ }
}
```

---

## 🗄️ Capa de Base de Datos (`database/`)

### `AppDatabase.kt` — El corazón de la persistencia

Es la clase principal de **Room**, que actúa como punto de acceso a toda la base de datos.

```kotlin
@Database(
    entities = [User::class, Team::class, Match::class, Facility::class, Reservation::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Métodos abstractos que Room implementa automáticamente
    abstract fun userDao(): UserDao
    abstract fun facilityDao(): FacilityDao
    // ...

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gessport_db"
                ).build()

                INSTANCE = instance

                // Seed: crea un admin si no existe ninguno
                CoroutineScope(Dispatchers.IO).launch {
                    ensureSeedAdmin(instance)
                }

                instance
            }
        }
    }
}
```

**Conceptos clave aquí:**

- **`@Volatile`**: garantiza que cuando un hilo escribe en `INSTANCE`, todos los demás hilos ven el valor actualizado inmediatamente. Evita condiciones de carrera.
- **`synchronized(this)`**: solo un hilo a la vez puede entrar en este bloque. Así evitamos crear dos instancias de la BD simultáneamente.
- **Singleton Pattern**: la BD se crea una sola vez y se reutiliza. Room es costoso de inicializar, crear múltiples instancias sería un error grave de rendimiento.
- **Seed Admin**: al arrancar la app, si no existe ningún admin en la BD, crea uno automáticamente (`admin@gesport.com` / `Admin1234`). Esto evita quedarse bloqueado sin poder entrar nunca.

---

### DAOs — Data Access Objects

Un **DAO** es una interfaz que define las operaciones SQL disponibles para cada entidad. Room genera automáticamente el código de implementación en tiempo de compilación.

#### `UserDao.kt`

```kotlin
@Dao
interface UserDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long   // devuelve el id generado

    // READ — Flow: emite nuevos valores cada vez que cambia la tabla
    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun getAll(): Flow<List<User>>

    @Query("SELECT * FROM usuarios WHERE rol = :role ORDER BY nombre ASC")
    fun getByRole(role: String): Flow<List<User>>

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): User?

    // UPDATE
    @Update
    suspend fun update(user: User): Int   // devuelve filas afectadas

    @Query("UPDATE usuarios SET equipoId = NULL, posicion = NULL WHERE equipoId = :teamId")
    suspend fun clearTeamFromUsers(teamId: Int): Int

    // DELETE
    @Delete
    suspend fun delete(user: User)
}
```

**¿Por qué `suspend`?** Las operaciones de base de datos son lentas. `suspend` permite ejecutarlas en una corrutina sin bloquear el hilo principal (la UI). Si bloqueásemos el hilo principal, la app se congelaría.

**¿Por qué `Flow<List<User>>` y no `suspend fun getAllUsers(): List<User>`?** Con `Flow`, Room observa la tabla. Cada vez que se inserta, actualiza o borra un usuario, Room emite automáticamente la lista actualizada. La UI la recibe sin necesidad de hacer polling (consultas periódicas). Es programación reactiva.

---

## 🔄 Capa de Repositorios (`repository/` + `data/`)

### ¿Por qué dos carpetas?

- **`repository/`** → contiene las **interfaces** (contratos). Define QUÉ operaciones existen.
- **`data/`** → contiene las **implementaciones**. Define CÓMO se realizan esas operaciones.

Esta separación es el **patrón Repository** y permite sustituir la fuente de datos sin cambiar nada de la lógica de negocio ni de la UI.

### `UserRepository.kt` — La interfaz (contrato)

```kotlin
interface UserRepository {

    // Flow para observar cambios en tiempo real
    fun getAllUsers(): Flow<List<User>>
    fun getUsersByRole(role: String): Flow<List<User>>
    fun getUsersByTeamId(teamId: Int): Flow<List<User>>

    // Operaciones puntuales (suspend)
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUserById(id: Int): User?
    suspend fun clearTeamFromUsers(teamId: Int): Int

    // CRUD
    suspend fun addUser(user: User): User
    suspend fun updateUser(user: User): Int
    suspend fun deleteUser(id: Int): Boolean
}
```

### `RoomUserRepository.kt` — La implementación real

```kotlin
class RoomUserRepository(private val userDao: UserDao) : UserRepository {

    override fun getAllUsers(): Flow<List<User>> =
        userDao.getAll()   // delega directamente al DAO

    override suspend fun getUserByEmail(email: String): User? =
        userDao.getByEmail(email)

    override suspend fun addUser(user: User): User {
        val id = userDao.insert(user)
        return user.copy(id = id.toInt())  // devuelve el usuario con su nuevo id
    }

    override suspend fun updateUser(user: User): Int =
        userDao.update(user)

    override suspend fun deleteUser(id: Int): Boolean {
        val user = userDao.getById(id)
        return if (user != null) {
            userDao.delete(user)
            true
        } else {
            false
        }
    }
}
```

### Evolución de las implementaciones (archivos legados en `data/`)

El proyecto muestra claramente cómo evolucionó el diseño. Los archivos legados están comentados pero conservados para ilustrar la progresión:

| Archivo                 | Estado      | Descripción                                          |
|-------------------------|-------------|------------------------------------------------------|
| `LoginRepository.kt`    | ❌ En desuso | Lista hardcodeada de usuarios en memoria             |
| `DataUserRepository.kt` | ❌ En desuso | Usuarios guardados en memoria (se pierden al cerrar) |
| `JsonUserRepository.kt` | ❌ En desuso | Usuarios persistidos en un archivo JSON local        |
| `RoomUserRepository.kt` | ✅ En uso    | Usuarios en SQLite mediante Room                     |
| `ApiUserRepository.kt`  | 🚧 Futuro   | Esqueleto para una futura API REST                   |

Esta evolución demuestra que el **patrón Repository** cumplió su promesa: al cambiar de JSON a Room, solo hubo que crear una nueva clase que implemente la misma interfaz. El ViewModel y la UI no necesitaron modificarse.

---

## 🧠 Capa de Dominio (`domain/`)

La capa de dominio contiene la **lógica de negocio pura**: reglas que no dependen de Room, de Compose ni de Android. Solo trabajan con modelos de datos de Kotlin.

### `LoginLogic.kt` — Validación y autenticación

```kotlin
class LoginLogic(
    private val userRepository: UserRepository? = null
) {

    // AUTENTICACIÓN: busca al usuario en Room y verifica la contraseña
    suspend fun checkLogin(email: String, password: String): User {
        if (email.isBlank() || password.isBlank())
            throw IllegalArgumentException("Los campos no pueden estar vacíos.")

        val user = userRepository?.getUserByEmail(email.trim())
            ?: throw IllegalArgumentException("Email o contraseña incorrectos.")

        if (user.password != password)
            throw IllegalArgumentException("Email o contraseña incorrectos.")

        return user
    }

    // VALIDACIONES de formulario (reutilizables en Login y Registro)
    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val passRegex  = Regex("^(?=.*[a-z])(?=.*[A-Z]).{6,}$")

    fun validateEmail(email: String) {
        if (!emailRegex.matches(email.trim()))
            throw IllegalArgumentException("El correo electrónico no es válido.")
    }

    fun validatePassword(password: String) {
        if (!passRegex.matches(password))
            throw IllegalArgumentException("La contraseña debe tener mayúscula, minúscula y al menos 6 caracteres.")
    }

    fun validateRepeat(password: String, repeat: String) {
        if (password != repeat)
            throw IllegalArgumentException("Las contraseñas no coinciden.")
    }
}
```

**¿Por qué lanzar excepciones en lugar de devolver `Boolean`?** Porque las excepciones transportan el mensaje de error concreto. La UI puede capturarlas y mostrar el mensaje exacto al usuario sin necesidad de un código adicional de traducción.

---

### `ReservationAccess.kt` — Reglas de acceso a reservas

```kotlin
object ReservationAccess {

    fun canManageReservation(
        currentUserId: Int,
        currentUserRole: String,
        reservation: Reservation,
        coachedTeamIds: Set<Int>   // equipos donde este usuario es entrenador
    ): Boolean {
        // El admin puede todo
        if (role == UserRoles.ADMIN_DEPORTIVO) return true

        val isPersonal = reservation.equipoId == null

        return when (role) {
            UserRoles.ENTRENADOR -> {
                // Puede gestionar sus reservas personales
                // O las reservas de equipos que entrena
                (isPersonal && reservation.usuarioId == currentUserId) ||
                (!isPersonal && coachedTeamIds.contains(reservation.equipoId))
            }
            else -> {  // JUGADOR, ARBITRO
                // Solo sus reservas personales
                isPersonal && reservation.usuarioId == currentUserId
            }
        }
    }
}
```

**¿Por qué `object` en lugar de `class`?** Porque no necesita estado propio. Es un conjunto de funciones puras: dadas las mismas entradas, siempre devuelven el mismo resultado. No tiene sentido crear instancias de él.

---

## 🧭 Navegación (`navigation/`)

### `Navigation.kt` — El mapa de rutas

La navegación en Jetpack Compose funciona como una web: cada pantalla tiene una **ruta (URL)** y se puede navegar pasando parámetros en ella.

```kotlin
@Composable
fun navigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Los ViewModels se crean aquí (una sola vez) y se pasan a las pantallas
    val gesUserViewModel: GesUserViewModel = viewModel(
        factory = GesUserViewModelFactory(context.applicationContext)
    )

    NavHost(navController = navController, startDestination = "welcome") {

        // Pantallas de autenticación
        composable("welcome") { WelcomeScreen(navController) }
        composable("login")   { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }

        // Pantalla principal con parámetros
        composable(
            route = "home/{userId}/{name}/{role}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("name")   { type = NavType.StringType },
                navArgument("role")   { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            val name   = Uri.decode(backStackEntry.arguments?.getString("name"))
            val role   = Uri.decode(backStackEntry.arguments?.getString("role"))
            HomeScreen(navController, userId, name, role)
        }

        // CRUD de usuarios
        composable("gesuser") {
            GesUserScreen(navController, gesUserViewModel)
        }
        composable("formuser/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId")
            AddUserScreen(navController, gesUserViewModel, userId)
        }
        // ... más rutas para instalaciones, equipos y reservas
    }
}
```

**¿Por qué `Uri.encode` / `Uri.decode`?** Los parámetros de navegación viajan en la URL. Si el nombre de un usuario contiene caracteres especiales (`ñ`, `é`, espacios), la URL se corrompe. `Uri.encode` los escapa (p.ej. `María` → `Mar%C3%ADa`) y `Uri.decode` los restaura.

**¿Por qué los ViewModels se crean en `Navigation` y no en cada pantalla?** Para que el ViewModel sobreviva a los cambios de pantalla. Si lo crearas dentro de `GesUserScreen`, se destruiría al salir y al volver empezaría de cero, perdiendo el estado. Creándolo en `Navigation`, el ViewModel tiene el ciclo de vida del composable padre y persiste mientras navega entre pantallas relacionadas.

---

## 📊 Capa de UI — ViewModels

### ¿Qué es un ViewModel?

El **ViewModel** es el intermediario entre la UI (pantallas Compose) y los datos (repositorios). Sus responsabilidades son:
1. Mantener el **estado de la UI** (lista de usuarios, filtros aplicados, mensajes de error...)
2. Ejecutar **operaciones de datos** (añadir, editar, borrar) sin bloquear la UI
3. **Sobrevivir a los cambios de configuración** (rotación de pantalla, modo oscuro...)

### `GesUserViewModel.kt` — Ejemplo completo anotado

```kotlin
class GesUserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    // Estado observable: Compose re-compone automáticamente cuando cambia
    private var _users by mutableStateOf<List<User>>(emptyList())
    val users: List<User> get() = _users  // solo lectura desde fuera

    private var _searchQuery by mutableStateOf("")
    val searchQuery: String get() = _searchQuery

    private var _isLoading by mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading

    private var _errorMessage by mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage

    private var usersJob: Job? = null

    init {
        observeUsers(role = null)  // al crear el ViewModel, empieza a observar
    }

    private fun observeUsers(role: String?) {
        usersJob?.cancel()  // cancela la observación anterior si existía

        usersJob = viewModelScope.launch {
            _isLoading = true
            val flow = if (role == null)
                userRepository.getAllUsers()
            else
                userRepository.getUsersByRole(role)

            flow.collectLatest { list ->
                _allUsers = list
                applyFilters()
                _isLoading = false
            }
        }
    }

    // Filtrado local en memoria (sin nueva consulta a BD)
    private fun applyFilters() {
        val q = _searchQuery.trim().lowercase()
        _users = if (q.isEmpty()) _allUsers
        else _allUsers.filter { user ->
            user.nombre.lowercase().contains(q) ||
            user.email.lowercase().contains(q)
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            try {
                _errorMessage = null
                userRepository.addUser(user)
                // Flow de Room emitirá la lista actualizada automáticamente
            } catch (e: CancellationException) {
                throw e  // siempre se relanza CancellationException
            } catch (e: Exception) {
                _errorMessage = e.message ?: "No se ha podido crear el usuario"
            }
        }
    }
}
```

**`viewModelScope.launch`**: crea una corrutina atada al ciclo de vida del ViewModel. Si el usuario abandona la pantalla, la corrutina se cancela automáticamente.

**`collectLatest`**: si llegan varios valores del Flow muy seguidos, descarta los intermedios y solo procesa el último. Evita trabajo innecesario.

---

### `GesUserViewModelFactory.kt` — La fábrica del ViewModel

```kotlin
class GesUserViewModelFactory(
    private val appContext: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(appContext)
        val userDao = database.userDao()
        val repo = RoomUserRepository(userDao)
        return GesUserViewModel(repo) as T
    }
}
```

**¿Por qué necesitamos una Factory?** El sistema de Android crea los ViewModels usando reflexión y solo sabe llamar a constructores vacíos. Como `GesUserViewModel` necesita un `UserRepository` en su constructor, debemos decirle al sistema cómo construirlo. La Factory es exactamente eso: las instrucciones de montaje.

---

## 📱 Capa de UI — Pantallas

### `MainActivity.kt` — El arranque

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // la UI llega hasta los bordes de la pantalla
        setContent {
            MaterialTheme {
                Navigation()  // todo lo demás lo gestiona Navigation
            }
        }
    }
}
```

La `Activity` es mínima a propósito. En Jetpack Compose, toda la lógica de UI y navegación vive en composables, no en la Activity.

---

### Flujo de pantallas de autenticación

```
WelcomeScreen
    ├── → LoginScreen
    │       ├── → HomeScreen (jugadores, árbitros, entrenadores)
    │       └── → DashboardScreen (solo administradores)
    └── → RegisterScreen
            └── → LoginScreen
```

**`LoginScreen.kt`**: recoge email y contraseña, llama a `LoginLogic.checkLogin()`, y según el rol del usuario navega a `home/...` o `dashboard/...`.

**`DashboardScreen.kt`**: verifica que el rol sea `ADMIN_DEPORTIVO`. Si no lo es, muestra un mensaje de acceso denegado en lugar del panel. Esta es una capa extra de seguridad: aunque alguien manipulase la navegación, no vería el contenido.

---

### Módulos CRUD del backend (`ui/backend/`)

Cada entidad gestionable (usuario, instalación, equipo, reserva) tiene su propio módulo con la misma estructura:

| Archivo                     | Descripción                                                  |
|-----------------------------|--------------------------------------------------------------|
| `GesXxxScreen.kt`           | Lista todos los registros con búsqueda y filtros             |
| `AddXxxScreen.kt`           | Formulario para crear O editar (mismo formulario, doble uso) |
| `GesXxxViewModel.kt`        | Gestiona el estado y las operaciones CRUD                    |
| `GesXxxViewModelFactory.kt` | Construye el ViewModel con sus dependencias                  |

**`AddUserScreen` en modo creación vs edición:**
```kotlin
// Si userId == null → modo creación
// Si userId != null → carga el usuario existente y rellena el formulario
@Composable
fun addUserScreen(
    navController: NavHostController,
    viewModel: GesUserViewModel,
    userId: Int?   // null = crear, entero = editar
) {
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadUserById(userId) { user ->
                // Rellena los campos del formulario con los datos existentes
                nombre = user?.nombre ?: ""
                email = user?.email ?: ""
                // ...
            }
        }
    }
    // ... UI del formulario
}
```

---

### `GesReservationTimeUtils.kt` — Utilidades de tiempo

Archivo de funciones puras para manejar horas de reservas: validar que `horaFin > horaInicio`, generar slots horarios disponibles, comprobar solapamientos, etc. Se separa en su propio archivo para mantener el ViewModel limpio y facilitar el testing de esta lógica.

---

## 🧩 Componentes Reutilizables (`ui/components/`)

En lugar de repetir código de UI, GeSport define componentes Compose reutilizables:

```kotlin
// TextFields.kt — campo de texto con estilo GeSport
@Composable
fun input(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) { /* implementación */ }

// Buttons.kt — botones con estilos predefinidos
@Composable
fun primaryButton(text: String, onClick: () -> Unit) { /* ... */ }

@Composable
fun googleButton(text: String, onClick: () -> Unit) { /* ... */ }

// Cards — visualización de entidades
@Composable
fun userCard(user: User, onEdit: () -> Unit, onDelete: () -> Unit) { /* ... */ }

@Composable
fun facilityCard(facility: Facility, onEdit: () -> Unit, onDelete: () -> Unit) { /* ... */ }
```

**Ventajas de componentizar:**
- **Consistencia visual**: todos los botones primarios tienen el mismo aspecto
- **Mantenimiento**: cambiar el estilo de un botón lo cambia en toda la app
- **Legibilidad**: `PrimaryButton("Guardar") { guardar() }` es más claro que 20 líneas de Compose repetidas

---

## 🛡️ Sistema de Roles y Permisos

### Tabla de permisos completa

| Acción                                    | ADMIN | ENTRENADOR | JUGADOR | ÁRBITRO  |
|-------------------------------------------|:-----:|:----------:|:-------:|:--------:|
| Acceder al Dashboard                      |   ✅   |     ❌      |    ❌    |    ❌     |
| Gestionar usuarios (CRUD)                 |   ✅   |     ❌      |    ❌    |    ❌     |
| Gestionar instalaciones (CRUD)            |   ✅   |     ❌      |    ❌    |    ❌     |
| Gestionar equipos (CRUD)                  |   ✅   |     ❌      |    ❌    |    ❌     |
| Ver todas las reservas                    |   ✅   |     ❌      |    ❌    |    ❌     |
| Crear reserva personal                    |   ✅   |     ✅      |    ✅    |    ✅     |
| Gestionar reserva personal propia         |   ✅   |     ✅      |    ✅    |    ✅     |
| Crear reserva de equipo (como entrenador) |   ✅   |     ✅      |    ❌    |    ❌     |
| Gestionar reservas de su equipo           |   ✅   |     ✅      |    ❌    |    ❌     |

### Implementación del control de acceso

El control ocurre en **dos niveles**:

1. **Navegación** (`DashboardScreen.kt`): si el rol no es `ADMIN_DEPORTIVO`, muestra pantalla de acceso denegado.
2. **Lógica de negocio** (`ReservationAccess.kt`): calcula si el usuario actual puede gestionar una reserva concreta.

```kotlin
// En GesReservationViewModel, antes de mostrar botón "Editar" en una reserva:
val canManage = ReservationAccess.canManageReservation(
    currentUserId = currentUserId,
    currentUserRole = currentUserRole,
    reservation = reservation,
    coachedTeamIds = myTeamsAsCoach.map { it.id }.toSet()
)
// Solo muestra el botón si canManage == true
```

---

## 🔄 Flujo Completo de la Aplicación

### Flujo 1: Login de un administrador

```
1. Usuario abre la app → WelcomeScreen
2. Pulsa "Iniciar sesión" → LoginScreen
3. Escribe email: admin@gesport.com / pass: Admin1234
4. Pulsa "Entrar"
5. LoginScreen llama a LoginLogic.checkLogin(email, password)
6. LoginLogic consulta RoomUserRepository.getUserByEmail(email)
7. RoomUserRepository delega a UserDao.getByEmail(email)
8. Room ejecuta: SELECT * FROM usuarios WHERE email = 'admin@gesport.com' LIMIT 1
9. Devuelve el User con rol = "ADMIN_DEPORTIVO"
10. LoginLogic verifica la contraseña → correcto
11. LoginScreen navega a: "dashboard/{userId}/{name}/{role}"
12. DashboardScreen recibe el rol, confirma que es ADMIN → muestra el panel
```

---

### Flujo 2: Crear una nueva instalación

```
1. Admin en DashboardScreen pulsa "Instalaciones"
2. Navega a "gesfacility" → GesFacilityScreen
3. GesFacilityViewModel observa Flow<List<Facility>> desde Room
4. Admin pulsa "+" → navega a "formfacility" → AddFacilityScreen (modo creación)
5. Admin rellena: nombre="Pista 1", deporte="PADEL", capacidad=4
6. Pulsa "Guardar"
7. AddFacilityScreen llama a gesFacilityViewModel.addFacility(facility)
8. GesUserViewModel lanza corrutina: facilityRepository.addFacility(facility)
9. RoomFacilityRepository delega a FacilityDao.insert(facility)
10. Room ejecuta: INSERT INTO instalaciones (nombre, tipoDeporte, ...) VALUES (...)
11. Room emite nuevo valor en el Flow de instalaciones
12. GesFacilityViewModel.collectLatest recibe la lista actualizada
13. GesFacilityScreen re-compone automáticamente mostrando la nueva instalación
14. navController.popBackStack() → vuelve a GesFacilityScreen
```

---

### Flujo 3: Un jugador reserva una pista

```
1. Jugador en HomeScreen pulsa "Mis Reservas"
2. Navega a "gesreservation/{userId}/{role}"
3. GesReservationViewModel carga reservas filtradas por userId
4. Jugador pulsa "Nueva reserva" → AddReservationScreen
5. Elige: pista, fecha, hora inicio, hora fin
6. GesReservationTimeUtils valida que horaFin > horaInicio
7. ReservationAccess.canCreateOrUpdateReservation verifica permisos
8. Jugador pulsa "Confirmar"
9. ViewModel llama a reservationRepository.addReservation(reservation)
10. Room inserta la reserva → emite Flow actualizado
11. La lista de reservas del jugador se actualiza automáticamente
```

---

## 📈 Evolución del Proyecto

El código fuente conserva las implementaciones antiguas (comentadas) como documentación viva de la evolución:

### Fase 1 — Datos hardcodeados
```kotlin
// LoginRepository.kt (en desuso)
private val users = listOf(
    User(1, "Ana López", "ana@correo.com", "1234", "ADMIN_DEPORTIVO"),
    // lista fija en el código fuente
)
```
**Problema**: los datos se reinician con cada arranque. Sin persistencia real.

### Fase 2 — Datos en JSON
```kotlin
// JsonUserRepository.kt (en desuso)
val jsonFile = context.assets.open("users.json").bufferedReader().use { it.readText() }
val users: List<User> = json.decodeFromString(jsonFile)
```
**Problema**: los assets de Android son de solo lectura. No se podían guardar cambios.

### Fase 3 — Room (SQLite) ← versión actual
```kotlin
// RoomUserRepository.kt (activo)
class RoomUserRepository(private val userDao: UserDao) : UserRepository {
    override fun getAllUsers(): Flow<List<User>> = userDao.getAll()
}
```
**Ventajas**: persistencia real, reactivo con Flow, tipado seguro.

### Fase 4 — API REST (esqueleto preparado)
```kotlin
// ApiUserRepository.kt (futuro)
class ApiUserRepository : UserRepository {
    override suspend fun getAllUsers(): List<User> {
        TODO("Llamada a Retrofit / Ktor")
    }
}
```
Gracias al patrón Repository, solo habría que implementar esta clase. El resto de la app no cambiaría.

---

## ❓ Preguntas Frecuentes

**P: ¿Por qué usas MVVM y no MVC?**
> En Android, el controlador (Activity) tiene acceso directo a la View, lo que lleva a clases enormes con mezcla de lógica y UI. MVVM separa el estado en el ViewModel, que no conoce ni depende de la View. Además, el ViewModel sobrevive a los cambios de configuración (rotaciones), cosa que el Controller no hace.

**P: ¿Qué es un Flow y por qué no usas LiveData?**
> Flow es la solución nativa de Kotlin para streams de datos asíncronos. A diferencia de LiveData, Flow es completamente independiente de Android y más fácil de testear. Room soporta Flow nativamente. LiveData sigue siendo válido, pero Flow es la dirección moderna del ecosistema Kotlin/Android.

**P: ¿Por qué el admin seed tiene la contraseña en texto plano?**
> Es una decisión de scope del proyecto (aplicación académica/local). En producción, las contraseñas se hashearían con bcrypt o Argon2 antes de guardarse. El campo `password` almacenaría el hash, no el texto plano.

**P: ¿Qué pasa si cambias el esquema de la base de datos?**
> Room lanzaría un error porque la versión de la BD (`version = 1`) no coincide con el nuevo schema. La solución es incrementar la versión e implementar una `Migration`. En el `AppDatabase` hay un comentario con `fallbackToDestructiveMigration()` que borra y recrea la BD (válido en desarrollo, nunca en producción).

**P: ¿Por qué `@Volatile` en el Singleton de AppDatabase?**
> En sistemas multihilo, cada hilo puede tener su propia caché de variables. `@Volatile` fuerza que cualquier escritura en `INSTANCE` se escriba directamente en la memoria principal y sea visible por todos los hilos inmediatamente. Sin ello, dos hilos podrían ver `INSTANCE = null` simultáneamente y crear dos instancias de la BD.

**P: ¿Cómo funciona el filtrado de usuarios por búsqueda?**
> El ViewModel mantiene la lista completa `_allUsers` y una copia filtrada `_users`. Cuando el usuario escribe en el buscador, `applyFilters()` recorre `_allUsers` y filtra los que coincidan. Esto evita consultas SQL adicionales para cada tecla pulsada: se filtra en memoria, que es inmediato.

**P: ¿Qué es `rememberSaveable` y por qué se usa en los formularios?**
> `remember` preserva el estado durante recomposiciones de Compose. `rememberSaveable` va más lejos: preserva el estado incluso si la Activity se recrea (por rotación de pantalla o por el sistema Android matando el proceso). Se usa en los campos de formulario para que el usuario no pierda lo que estaba escribiendo si rota el móvil.

---

## 🚀 Cómo ejecutar el proyecto

### Requisitos
- Android Studio Hedgehog o superior
- SDK mínimo: Android 8.0 (API 26)
- Kotlin 1.9+

### Pasos
```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/gesport.git

# 2. Abrir en Android Studio
# File → Open → seleccionar la carpeta gesport

# 3. Sincronizar Gradle
# Android Studio lo hará automáticamente al abrir

# 4. Ejecutar en emulador o dispositivo físico
# Run → Run 'app'
```

### Credenciales de acceso iniciales
```
Email:    admin@gesport.com
Password: Admin1234
```
> ⚠️ El admin seed se crea automáticamente si la base de datos está vacía.

---

## 📄 Licencia

Proyecto académico desarrollado como parte de la formación en desarrollo de aplicaciones móviles Android.

---

*Documentación generada para GeSport v1.0 — Kotlin + Jetpack Compose + Room*