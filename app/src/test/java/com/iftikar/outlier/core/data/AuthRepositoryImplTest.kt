package com.iftikar.outlier.core.data

import com.iftikar.outlier.core.data.repository.AuthRepositoryImpl
import com.iftikar.outlier.core.models.Session
import com.iftikar.outlier.core.network.api.AuthApiService
import com.iftikar.outlier.core.network.model.ApiResponse
import com.iftikar.outlier.core.network.model.AuthResponse
import com.iftikar.outlier.core.network.model.RegisterResponseDto
import com.iftikar.outlier.core.network.model.UserRequestDto
import com.iftikar.outlier.core.network.model.VerifyEmailRequestDto
import com.iftikar.outlier.core.result.EmailVerificationError
import com.iftikar.outlier.core.result.GenericError
import com.iftikar.outlier.core.result.Result
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import java.io.IOException

@ExtendWith(MockitoExtension::class)
class AuthRepositoryImplTest {

    @Mock
    lateinit var authApiService: AuthApiService
    private lateinit var repository: AuthRepositoryImpl

    private val testDispatcher =
        StandardTestDispatcher()

    @BeforeEach
    fun setUp() {

        repository = AuthRepositoryImpl(
            authApiService = authApiService,
            io = testDispatcher
        )
    }

    @Test
    fun checkIfUsernameExists_whenApiReturnsTrue_returnsSuccessTrue() = runTest(context = testDispatcher) {
//        val testDispatcher =
//            StandardTestDispatcher(testScheduler)
//
//        val repository = AuthRepositoryImpl(
//            authApiService = authApiService,
//            io = testDispatcher
//        )
        // Arrange
        `when`(
            authApiService.checkUsernameExists("ryu")
        ).thenReturn(
            ApiResponse(
                code = "SUCCESS",
                message = "Username exists",
                data = true
            )
        )

        // Act
        val result = repository.checkIfUsernameExists("ryu")

        // Assert
        assertEquals(
            Result.Success(true),
            result
        )
    }

    @Test
    fun checkIfUsernameExists_whenApiReturnsFalse_returnsSuccessFalse() = runTest {
        val testDispatcher =
            StandardTestDispatcher(testScheduler)

        val repository = AuthRepositoryImpl(
            authApiService = authApiService,
            io = testDispatcher
        )

        `when`(authApiService.checkUsernameExists("ryu"))
            .thenReturn(
                ApiResponse(
                    "SUCCESS",
                    "Username does not exist",
                    false
                )
            )

        val result = repository.checkIfUsernameExists("ryu")
        assertEquals(
            Result.Success(false),
            result
        )
    }

    @Test
    fun checkIfUsernameExist_whenDataIsNull_returnsUnknownError() = runTest {
        val testDispatcher =
            StandardTestDispatcher(testScheduler)

        val repository = AuthRepositoryImpl(
            authApiService = authApiService,
            io = testDispatcher
        )

        `when`(authApiService.checkUsernameExists("ryu"))
            .thenReturn(
                ApiResponse(
                    "UNKNOWN",
                    "OOps",
                    null
                )
            )
        val result = repository.checkIfUsernameExists("ryu")
        assertEquals(
            Result.Error(GenericError.UNKNOWN),
            result
        )
    }

    @Test
    fun checkIfUsernameExists_whenIOException_returnsNoInternet() = runTest {
        val testDispatcher =
            StandardTestDispatcher(testScheduler)

        val repository = AuthRepositoryImpl(
            authApiService = authApiService,
            io = testDispatcher
        )

        `when`(authApiService.checkUsernameExists("ryu"))
            .thenAnswer { throw IOException() }
        val result = repository.checkIfUsernameExists("ryu")

        assertEquals(
            Result.Error(GenericError.NO_INTERNET),
            result
        )
    }

    @Test
    fun verifyOtp_whenApiReturnsTokens_returnsSuccessSession() = runTest(testDispatcher) {
        `when`(authApiService.verifyEmail(VerifyEmailRequestDto("email", "code")))
            .thenReturn(
                ApiResponse(
                    "SUCCESS",
                    "suucess",
                    AuthResponse(
                        accessToken = "access",
                        refreshToken = "refresh"
                    )
                )
            )
        val result = repository.verifyOtp("email", "code")
        assertEquals(
            Result.Success(Session("access", "refresh")),
            result
        )
    }

    @Test
    fun verifyOtp_whenApiReturnsInvalidOtp_returnsInvalid() = runTest(testDispatcher) {
        `when`(authApiService.verifyEmail(VerifyEmailRequestDto("email", "code")))
            .thenReturn(
                ApiResponse(
                    "INVALID_OTP",
                    "suucess",
                    null
                )
            )
        val result = repository.verifyOtp("email", "code")
        assertEquals(
            Result.Error(EmailVerificationError.INVALID_OTP),
            result
        )
    }

    @Test
    fun register_whenApiReturnsEmail_returnsSuccess() = runTest(testDispatcher) {
        `when`(authApiService.registerUser(
            UserRequestDto(
                username = "username",
                email = "email",
                password = "password",
                name = "name",
                role = "role"
            )
        )).thenReturn(
            ApiResponse(
                "SUCCESS",
                "yahoo",
                RegisterResponseDto("em")
            )
        )

        val result = repository.register(
            username = "username",
            email = "email",
            password = "password",
            name = "name",
            role = "role"
        )

        assertEquals(
            Result.Success("em"),
            result
        )
    }
}


















