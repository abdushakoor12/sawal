package com.abdushakoor12.sawal

import com.abdushakoor12.sawal.core.ServiceLocator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test

class ServiceLocatorTest {

    @Test
    fun `singleton returns same instance`() {
        val instance = TestClass()
        val serviceLocator = ServiceLocator.builder()
            .addSingleton(instance)
            .build()

        val result1 = serviceLocator.get<TestClass>()
        val result2 = serviceLocator.get<TestClass>()

        assertSame(instance, result1)
        assertSame(result1, result2)
    }

    @Test
    fun `factory creates new instance each time`() {
        var counter = 0
        val serviceLocator = ServiceLocator.builder()
            .addFactory { TestClass().also { counter++ } }
            .build()

        val result1 = serviceLocator.get<TestClass>()
        val result2 = serviceLocator.get<TestClass>()

        assertNotSame(result1, result2)
        assertEquals(2, counter)
    }

    @Test
    fun `get throws exception when no provider found`() {
        val serviceLocator = ServiceLocator.builder().build()

        val exception = assertThrows(IllegalArgumentException::class.java) {
            serviceLocator.get<TestClass>()
        }

        assertEquals("No provider found for TestClass", exception.message)
    }

    @Test
    fun `can register multiple different types`() {
        val testClass = TestClass()

        val serviceLocator = ServiceLocator.builder()
            .addSingleton(testClass)
            .addFactory { OtherTestClass() }
            .build()

        val result1 = serviceLocator.get<TestClass>()
        val result2 = serviceLocator.get<OtherTestClass>()

        assertSame(testClass, result1)
        assertNotNull(result2)
    }

    // Helper classes for testing
    private class TestClass
    private class OtherTestClass
}