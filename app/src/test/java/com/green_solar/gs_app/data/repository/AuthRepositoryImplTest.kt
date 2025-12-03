package com.green_solar.gs_app.data.repository

import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.data.remote.dto.AuthResponse
import com.green_solar.gs_app.data.remote.dto.MeResponse
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para AuthRepositoryImpl, siguiendo la guía de buenas prácticas.
 *
 * Cubre:
 * - Login exitoso
 * - Login fallido (simulado)
 * - Logout
 */
class AuthRepositoryImplTest {

    // Mocks para las dependencias
    private lateinit var mockApiService: ApiService
    private lateinit var mockSessionManager: SessionManager

    // SUT (System Under Test)
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        // Crear mocks. Relaxed = true para no tener que definir el comportamiento de todas las funciones.
        mockApiService = mockk(relaxed = true)
        mockSessionManager = mockk(relaxed = true)

        // Inyectar mocks en la clase a probar
        repository = AuthRepositoryImpl(mockApiService, mockSessionManager)
    }

    @After
    fun teardown() {
        // Limpiar todos los mocks después de cada test
        unmockkAll()
    }

    @Test
    fun `login exitoso debe guardar token y retornar usuario`() = runTest {
        // ARRANGE (GIVEN) - Preparar el escenario
        val fakeToken = "fake_auth_token_123"
        val fakeUserId = 1L

        val mockAuthResponse = AuthResponse(
            id = fakeUserId,
            username = "Test User",
            imgUrl = "",
            token = fakeToken
        )
        coEvery { mockApiService.login(any()) } returns mockAuthResponse

        val mockMeResponse = MeResponse(
            role = "USER",
            email = "test@example.com",
            username = "Test User", // Added username to MeResponse mock based on DTO
            imgUrl = ""             // Added imgUrl to MeResponse mock based on DTO
        )
        coEvery { mockApiService.getCurrentUser(any()) } returns mockMeResponse

        //  Ejecutar la acción a probar
        val result = repository.login("test@example.com", "password123")

        //  Verificar los resultados
        assertTrue("El resultado debería ser exitoso", result.isSuccess)
        val user = result.getOrNull()
        assertEquals(fakeUserId.toString(), user?.user_id)
        assertEquals("Test User", user?.username)

        // Verificar que los métodos del SessionManager fueron llamados
        coVerify(exactly = 1) { mockSessionManager.saveToken(fakeToken) }
        coVerify(exactly = 1) { mockSessionManager.saveUserId(fakeUserId) }
    }

    @Test
    fun `login con error de API debe retornar Failure`() = runTest {
        // Simular que la API lanza una excepción
        coEvery { mockApiService.login(any()) } throws RuntimeException("Error de red")

        val result = repository.login("test@example.com", "password123")


        assertTrue("El resultado debería ser un fallo", result.isFailure)
        assertTrue("La excepción debería ser la que lanzamos", result.exceptionOrNull() is RuntimeException)
        
        // Verificar que NUNCA se intentó guardar en la sesión
        coVerify(exactly = 0) { mockSessionManager.saveToken(any()) }
        coVerify(exactly = 0) { mockSessionManager.saveUserId(any()) }
    }

    @Test
    fun `logout debe llamar a clear en SessionManager`() = runTest {
        // No se necesita preparación para este test

        repository.logout()

        // Verificar que la función `clear` del session manager fue llamada exactamente una vez
        coVerify(exactly = 1) { mockSessionManager.clear() }
    }
}
