package com.green_solar.gs_app.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.green_solar.gs_app.domain.model.User
import com.green_solar.gs_app.domain.repository.AuthRepository
import com.green_solar.gs_app.ui.components.login.LoginViewModel
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var mockAuthRepository: AuthRepository

    @Test
    fun `login exitoso debe invocar el callback onLoggedIn`() {
        // ARRANGE
        val testUser = User("1", "Test User", "test@test.com", "USER", null)
        var onLoggedInCalled = false

        coEvery { mockAuthRepository.login(any(), any()) } returns Result.success(testUser)

        composeTestRule.setContent {
            val viewModel: LoginViewModel = viewModel { LoginViewModel(mockAuthRepository) }
            LoginScreen(
                vm = viewModel,
                onLoggedIn = { onLoggedInCalled = true },
                onRegisterClick = { }
            )
        }

        // ACT
        composeTestRule.onNodeWithText("Email").performTextInput("test@test.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Entrar").performClick()

        // ASSERT
        composeTestRule.waitUntil(5000) { onLoggedInCalled }
        assertTrue(onLoggedInCalled)
    }

    @Test
    fun `login fallido debe mostrar un mensaje de error`() {
        // ARRANGE
        val errorMessage = "Credenciales incorrectas"

        coEvery { mockAuthRepository.login(any(), any()) } returns
                Result.failure(Exception(errorMessage))

        composeTestRule.setContent {
            val viewModel: LoginViewModel = viewModel { LoginViewModel(mockAuthRepository) }
            LoginScreen(
                vm = viewModel,
                onLoggedIn = { },
                onRegisterClick = { }
            )
        }

        // ACT
        composeTestRule.onNodeWithText("Email").performTextInput("test@test.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("wrongpassword")
        composeTestRule.onNodeWithText("Entrar").performClick()

        // ASSERT
        composeTestRule.onNodeWithText(errorMessage, substring = true).assertIsDisplayed()
    }
}
