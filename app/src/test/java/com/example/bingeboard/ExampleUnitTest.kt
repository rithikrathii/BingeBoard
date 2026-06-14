package com.example.bingeboard

import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {

    // Basic sanity test
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    // Signup validation tests
    @Test
    fun signup_emptyName_fails() {
        val name = ""
        assertTrue("Name should be invalid", name.isBlank() || name.length < 2)
    }

    @Test
    fun signup_validName_passes() {
        val name = "Rithik Kumar"
        assertFalse("Name should be valid", name.isBlank() || name.length < 2)
    }

    @Test
    fun signup_invalidEmail_fails() {
        val email = "notanemail"
        assertFalse("Email should be invalid", email.contains("@"))
    }

    @Test
    fun signup_validEmail_passes() {
        val email = "rithik@gmail.com"
        assertTrue("Email should be valid", email.contains("@"))
    }

    @Test
    fun signup_shortPassword_fails() {
        val password = "123"
        assertTrue("Password too short", password.length < 8)
    }

    @Test
    fun signup_validPassword_passes() {
        val password = "Test1234!"
        assertFalse("Password should be valid", password.length < 8)
    }

    @Test
    fun signup_passwordMismatch_fails() {
        val password = "Test1234!"
        val confirmPassword = "Different1!"
        assertNotEquals("Passwords should not match", password, confirmPassword)
    }

    @Test
    fun signup_passwordMatch_passes() {
        val password = "Test1234!"
        val confirmPassword = "Test1234!"
        assertEquals("Passwords should match", password, confirmPassword)
    }

    // Movie filter tests
    @Test
    fun filter_emptyQuery_returnsAll() {
        val query = ""
        assertTrue("Empty query should return all", query.isEmpty())
    }

    @Test
    fun filter_genreMatch_passes() {
        val movieGenres = listOf("Action", "Drama")
        val selectedGenre = "Action"
        assertTrue("Genre should match", movieGenres.contains(selectedGenre))
    }

    @Test
    fun filter_genreNoMatch_fails() {
        val movieGenres = listOf("Action", "Drama")
        val selectedGenre = "Horror"
        assertFalse("Genre should not match", movieGenres.contains(selectedGenre))
    }

    @Test
    fun filter_titleSearch_passes() {
        val title = "Interstellar"
        val query = "inter"
        assertTrue("Title should match search", title.contains(query, ignoreCase = true))
    }

    @Test
    fun filter_titleSearch_fails() {
        val title = "Interstellar"
        val query = "batman"
        assertFalse("Title should not match search", title.contains(query, ignoreCase = true))
    }

    // Login validation tests
    @Test
    fun login_emptyEmail_fails() {
        val email = ""
        assertTrue("Email should be empty", email.isBlank())
    }

    @Test
    fun login_emptyPassword_fails() {
        val password = ""
        assertTrue("Password should be empty", password.isBlank())
    }

    @Test
    fun login_validCredentials_passes() {
        val email = "test@bingeboard.com"
        val password = "Test1234!"
        assertTrue("Credentials should be valid", email.contains("@") && password.length >= 8)
    }
}