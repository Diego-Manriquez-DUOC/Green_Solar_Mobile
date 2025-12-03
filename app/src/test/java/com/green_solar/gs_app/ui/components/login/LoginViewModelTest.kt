package com.green_solar.gs_app.ui.components.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.green_solar.gs_app.domain.model.User
import com.green_solar.gs_app.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockAuthRepository = mockk()

        // Inyectamos un validador simple para evitar cualquier dependencia Android en JVM
        viewModel = LoginViewModel(
            mockAuthRepository,
            emailValidator = { email -> email.contains("@") && email.contains(".") }
        )
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login con campos vacios no llama repo`() = runTest {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("")

        viewModel.login()
        advanceUntilIdle()

        val state = viewModel.ui.value

        assertEquals("Introduzca su email", state.emailError)
        assertEquals("Introduzca su contraseña", state.passwordError)

        coVerify(exactly = 0) { mockAuthRepository.login(any(), any()) }
    }

    @Test
    fun `login exitoso cambia estado a done`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val user = User("1", "Test", email, "USER", null)

        coEvery { mockAuthRepository.login(email, password) } returns Result.success(user)

        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)

        viewModel.login()

        // estado intermedio
        assertTrue(viewModel.ui.value.isLoading)

        advanceUntilIdle()

        val final = viewModel.ui.value
        assertFalse(final.isLoading)
        assertTrue(final.done)

        coVerify(exactly = 1) { mockAuthRepository.login(email, password) }
    }

    @Test
    fun `login con credenciales invalidas muestra error`() = runTest {
        val email = "test@gmail.com"
        val password = "contraseña123"

        coEvery { mockAuthRepository.login(email, password) } returns Result.failure(Exception("Credenciales inválidas"))

        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)

        viewModel.login()
        assertTrue(viewModel.ui.value.isLoading)

        advanceUntilIdle()

        val final = viewModel.ui.value
        assertFalse(final.isLoading)
        assertEquals("El email o la contraseña son incorrectos.", final.error)

        coVerify(exactly = 1) { mockAuthRepository.login(email, password) }
    }
}
