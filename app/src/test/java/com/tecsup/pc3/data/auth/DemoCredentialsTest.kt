package com.tecsup.pc3.data.auth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoCredentialsTest {
    @Test
    fun correctCredentialsAreAccepted() {
        assertTrue(DemoCredentials.areValid("wash", "123456"))
    }

    @Test
    fun incorrectCredentialsAreRejected() {
        assertFalse(DemoCredentials.areValid("wash", "incorrecta"))
        assertFalse(DemoCredentials.areValid("otro", "123456"))
        assertFalse(DemoCredentials.areValid("", ""))
    }
}
