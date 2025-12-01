package com.green_solar.gs_app.data.repository

import android.content.Context
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.data.remote.RetrofitClient
import com.green_solar.gs_app.data.remote.dto.AuthResponse
import com.green_solar.gs_app.data.remote.dto.LoginResponseDto
import com.green_solar.gs_app.data.remote.dto.SignupResponseDto
import com.green_solar.gs_app.data.remote.dto.UserDto
import io.mockk.*

import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

class AuthRepositoryImplTest {

    private lateinit var mockApiService: ApiService
    private lateinit var mockContext: Context
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
        mockApiService = mockk()

        val mockRetrofit: Retrofit = mockk()
        mockkObject(RetrofitClient)
        every { RetrofitClient.create(mockContext) } returns mockRetrofit
        every { mockRetrofit.create(ApiService::class.java) } returns mockApiService

        mockkConstructor(SessionManager::class)
        every { anyConstructed<SessionManager>().saveToken(any()) } just Runs
        every { anyConstructed<SessionManager>().clear() } just Runs

        repository = AuthRepositoryImpl(mockContext)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `login exitoso debe retornar Success con User y guardar el token`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val mockToken = "mock_auth_token_123"
        val userDto = UserDto(id = 1, name = "Test User", email = email, role = "USER", avatar = null)
        val mockApiResponse = LoginResponseDto(access_token = mockToken, message = "Success", user = userDto)

        coEvery { mockApiService.login(any()) } returns mockApiResponse

        // ACT
        val result = repository.login(email, "password123")

        // ASSERT
        assertTrue("El resultado del login debería ser Success", result.isSuccess)
        val user = result.getOrNull()
        assertEquals("1", user?.id)
        assertEquals("Test User", user?.name)

        verify(exactly = 1) { anyConstructed<SessionManager>().saveToken(mockToken) }
    }

    @Test
    fun `login con credenciales inválidas debe retornar Failure`() = runTest {
        // ARRANGE
        val mockHttpException = HttpException(Response.error<LoginResponseDto>(401, mockk(relaxed = true)))
        coEvery { mockApiService.login(any()) } throws mockHttpException

        // ACT
        val result = repository.login("wrong@example.com", "wrongpassword")

        // ASSERT
        assertTrue("El resultado debería ser Failure para credenciales inválidas", result.isFailure)
        assertTrue(result.exceptionOrNull() is HttpException)
    }

    @Test
    fun `signup exitoso debe retornar Success con User y guardar el token`() = runTest {
        // ARRANGE
        val name = "New User"
        val email = "new@example.com"
        val mockToken = "new_mock_token_456"
        val userDto = UserDto(user_id = "1", name = name, email = email, role = "USER", img_url = null)
        val mockApiResponse = AuthResponse(id, token = mockToken, username = "name", imgUrl = null)

        coEvery { mockApiService.signup(any()) } returns mockApiResponse

        // ACT
        val result = repository.signup(name, email, "password123")

        // ASSERT
        assertTrue("El resultado del signup debería ser Success", result.isSuccess)
        val user = result.getOrNull()
        assertEquals("2", user?.id)
        assertEquals(name, user?.name)

        verify(exactly = 1) { anyConstructed<SessionManager>().saveToken(mockToken) }
    }

    @Test
    fun `logout debe llamar al método clear del SessionManager`() = runTest {
        // ACT
        repository.logout()

        // ASSERT
        verify(exactly = 1) { anyConstructed<SessionManager>().clear() }
    }
}
