# Green Solar App
## 1. Caso elegido y alcance
- **Caso:** Green-Solar
- **Alcance EP3:** Diseño/UI, validaciones, navegación, estado, persistencia, recursos nativ
## 2. Requisitos y ejecución
- **Stack:** <framework, librerías>
- **Instalación:** comandos
- **Ejecución:** comandos y perfiles
## 3. Arquitectura y flujo
- **Estructura carpetas** (pantallas, servicios, state, data/repos, navigation, components)
- **Gestión de estado**: estrategia (local/global), flujo de datos
- **Navegación**: stack/tabs/deep link
## 4. Funcionalidades
- Formulario validado (registro/otra entidad)
- Navegación y backstack
- Gestión de estado (carga/éxito/error)
- **Persistencia local** (CRUD) y **almacenamiento de imagen de perfil**
- **Recursos nativos**: cámara/galería (permisos y fallback)
- **Animaciones** con propósito
- **Consumo de API** (incluye `/me`)
## 5. Endpoints
**Base URL:** `https://x8ki-letl-twmt.n7.xano.io/api:Rfm_61dW`
| Método | Ruta | Body | Respuesta
| ------ | ------------ | --------------------------------- | ------------------------------
| POST | /auth/signup | { email, password, name?, ... } | 201 { authToken, user: { id, e
| POST | /auth/login | { email, password } | 200 { authToken, user: { id, e
| GET | /auth/me | - (requiere header Authorization) | 200 { id, email, name, avatarU
## 6. User flows
- Diagramas/Descripción del flujo principal y casos de error
App ejecuta sin bloqueos.
README documenta funcionalidades, user flows, arquitectura, estado, navegación,
persistencia, recursos nativos (cámara/galería), animaciones, endpoints y manejo de
errores/red.
Validaciones campo a campo y mensajes claros; evita envío inválido.
Flujos y backstack probados; estados vacío/error.
Estados carga/éxito/error sincronizados.
CRUD local consistente tras reinicio; imagen de perfil almacenada localmente y render
offline.
2 recursos nativos integrados con permisos y fallback.
Animaciones breves, fluidas y con sentido.
API funcional (incluye /me), manejo de errores/timeout, sin secretos en código.
