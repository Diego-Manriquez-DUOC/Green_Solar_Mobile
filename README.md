# Green Solar App

## 1. Caso elegido y alcance
- **Caso:** Green-Solar
- **Alcance:** Diseño/UI, validaciones, navegación, gestión de estado, persistencia local y uso de recursos nativos como la cámara y la galería.

## 2. Requisitos y ejecución
- **Stack:**
    - **UI:** Jetpack Compose
    - **Arquitectura:** MVVM con principios de Clean Architecture (UI -> ViewModel -> Repository -> Data Source)
    - **Asincronía:** Kotlin Coroutines & Flow
    - **Navegación:** Jetpack Navigation Compose
    - **Networking:** Retrofit & Gson
    - **Carga de Imágenes:** Coil
    - **Persistencia Local:** Jetpack DataStore Preferences

- **Instalación:**
    1. Clonar el repositorio.
    2. Abrir el proyecto en Android Studio.
    3. Dejar que Gradle sincronice las dependencias.

- **Ejecución:**
    1. Seleccionar el dispositivo (emulador o físico).
    2. Elegir la configuración de ejecución `app`.
    3. Presionar el botón 'Run' .

## 3. Arquitectura y flujo

- **Estructura de carpetas:** El proyecto se organiza por capas funcionales para separar responsabilidades:
    - `ui`: Contiene los Composables (pantallas, componentes), ViewModels y la lógica de navegación.
    - `data`: Incluye las implementaciones de los repositorios, los DTOs, el cliente de Retrofit y el gestor de sesión local (`SessionManager`).
    - `domain`: Contiene los modelos de datos de la aplicación (ej: `User`) y las interfaces de los repositorios, definiendo el "contrato" de la capa de datos.
    - `core`: Utilidades y extensiones que pueden ser usadas en toda la aplicación.

- **Gestión de estado:** Se sigue un patrón MVI-like. Cada pantalla con lógica compleja tiene un `ViewModel` que expone un `StateFlow` de un `UiState` (data class). Este estado contiene campos como `isLoading`, `data` y `error`, permitiendo a la UI reaccionar de forma declarativa a los diferentes estados.

- **Navegación:** Se utiliza un `NavHost` centralizado en `AppNav.kt`. Las rutas están definidas en un objeto `Routes` para evitar errores de tipeo. El flujo principal es controlado por una pantalla `Splash` que verifica la existencia de un token de sesión para decidir si llevar al usuario al `Login` o a la pantalla `Main`.

## 4. Funcionalidades

- **Autenticación:** Formularios de Login y Registro con validación básica.
- **Gestión de Sesión:** Al hacer login, el `authToken` se guarda localmente usando `DataStore`. La app mantiene al usuario autenticado entre sesiones.
- **Navegación Protegida:** El `backstack` se gestiona para que el usuario no pueda volver a pantallas de autenticación una vez logueado, ni a la app al cerrar sesión.
- **Gestión de Estado Asíncrono:** Las pantallas que consumen datos (ej: Perfil) muestran estados de `Carga`, `Éxito` y `Error` de forma clara.
- **Perfil de Usuario:**
    - Se consume el endpoint `GET /auth/me` para obtener y mostrar los datos del usuario.
    - Se permite cambiar la foto de perfil.
- **Recursos Nativos (Cámara/Galería):**
    - El usuario puede elegir una foto de la **Galería** o tomar una nueva con la **Cámara**.
    - Se gestionan los **permisos de cámara** en tiempo de ejecución.
    - La imagen tomada se guarda en la caché de la app usando un `FileProvider` configurado correctamente para evitar crashes.
- **Persistencia de Imagen de Perfil:** Aunque la UI no se actualiza al instante (para mantener la lógica simple), la URI de la nueva imagen de perfil se guarda en el `SessionManager` para un uso futuro (ej: subida al servidor).

## 5. Endpoints
**Base URL:** `https://x8ki-letl-twmt.n7.xano.io/api:Rfm_61dW`

| Método | Ruta         | Body (Ejemplo)                    | Respuesta (Éxito)                |
|--------|--------------|-----------------------------------|------------------------------------|
| POST   | /auth/signup | `{ "email", "password", "name" }` | `200` con `{ authToken }`          |
| POST   | /auth/login  | `{ "email", "password" }`         | `200` con `{ authToken }`          |
| GET    | /auth/me     | - (Requiere header `Authorization`) | `200` con `{ id, email, name, ... }` |

## 6. User flows

- **Flujo de Usuario Nuevo:**
    1. El usuario abre la app y ve la pantalla `Splash`.
    2. Es redirigido a `Login`.
    3. Toca en "Registrarse" y navega a `SignUp`.
    4. Completa el formulario y se registra con éxito.
    5. Es redirigido de nuevo a `Login`.
    6. Inicia sesión y navega a la pantalla `Main`.

- **Flujo de Usuario Existente:**
    1. El usuario abre la app y ve la pantalla `Splash`.
    2. El `Splash` detecta el token guardado y lo redirige directamente a la pantalla `Main`.

- **Flujo de Cierre de Sesión:**
    1. Desde `Main` o `Profile`, el usuario toca en "Cerrar Sesión".
    2. El token local se borra.
    3. El usuario es redirigido a la pantalla de `Login`, y el historial de navegación se limpia.
