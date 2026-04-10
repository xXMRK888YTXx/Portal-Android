package com.xxmrk888ytxx.coreandroid

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultValidatorTest {

    @Test
    fun isHostValid_returnsTrueForValidIPv4() {
        assertTrue(DefaultValidator.isHostValid("192.168.1.1"))
        assertTrue(DefaultValidator.isHostValid("127.0.0.1"))
        assertTrue(DefaultValidator.isHostValid("255.255.255.255"))
        assertTrue(DefaultValidator.isHostValid("0.0.0.0"))
    }

    @Test
    fun isHostValid_returnsFalseForInvalidIPv4() {
        assertFalse(DefaultValidator.isHostValid("256.256.256.256"))
        assertFalse(DefaultValidator.isHostValid("192.168.1"))
        assertFalse(DefaultValidator.isHostValid("abc.def.ghi.jkl"))
        assertFalse(DefaultValidator.isHostValid(""))
        assertFalse(DefaultValidator.isHostValid("192.168.1.1.1"))
    }
}
