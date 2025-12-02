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
import com.green_solar.gs_app.ui.components.auth.SignupViewModel
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignupScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockAuthRepository: AuthRepository = mockk()

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `registro exitoso debe invocar el callback onRegistered`() {
        // ARRANGE
        val testUser = User("2", "New User", "new@test.com", "USER", null)
        var onRegisteredCalled = false
        coEvery { mockAuthRepository.signup(any(), any(), any()) } returns Result.success(testUser)

        composeTestRule.setContent {
            val viewModel: SignupViewModel = viewModel { SignupViewModel(mockAuthRepository) }
            SignupScreen(
                viewModel = viewModel,
                onRegistered = { onRegisteredCalled = true },
                onLoginClicked = { }
            )
        }

        // ACT
        composeTestRule.onNodeWithText("Nombre").performTextInput("New User")
        composeTestRule.onNodeWithText("Email").performTextInput("new@test.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Repite contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Registrarme").performClick()

        // ASSERT
        composeTestRule.waitUntil(5000) { onRegisteredCalled }
        assertTrue("El callback onRegistered debería haber sido llamado tras un registro exitoso", onRegisteredCalled)
    }

    @Test
    fun `registro fallido debe mostrar un mensaje de error`() {
        // ARRANGE
        val errorMessage = "El email ya está en uso"
        coEvery { mockAuthRepository.signup(any(), any(), any()) } returns Result.failure(Exception(errorMessage))

        composeTestRule.setContent {
            val viewModel: SignupViewModel = viewModel { SignupViewModel(mockAuthRepository) }
            SignupScreen(
                viewModel = viewModel,
                onRegistered = { },
                onLoginClicked = { }
            )
        }

        // ACT
        composeTestRule.onNodeWithText("Nombre").performTextInput("New User")
        composeTestRule.onNodeWithText("Email").performTextInput("existing@test.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Repite contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Registrarme").performClick()

        // ASSERT
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }
}
