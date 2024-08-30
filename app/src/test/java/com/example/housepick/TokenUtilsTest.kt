package com.example.housepick

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.jwt.JWT
import com.example.housepick.data.utils.TokenUtils
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class TokenUtilsTest {

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockJwt: JWT

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        // Set up the Application context to use the mock SharedPreferences
        Application.appContext = mock(Application::class.java)
        `when`(
            Application.appContext?.getSharedPreferences(
                "MySharedPref", AppCompatActivity.MODE_PRIVATE
            )
        ).thenReturn(mockSharedPreferences)
    }

    @Test
    fun `test checkIfExpired returns true when token is null`() {
        `when`(mockSharedPreferences.getString("jwt", null)).thenReturn(null)

        val result = TokenUtils.checkIfExpired()

        assertTrue(result)  // Expecting true because the token is null
    }

    @Test
    fun `test getId returns null when token is null`() {
        `when`(mockSharedPreferences.getString("jwt", null)).thenReturn(null)

        val result = TokenUtils.getId()

        assertNull(result)  // Expecting null because the token is null
    }

}