package com.jiangtj.micro.common.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PhoneNumberUtilsTest {

    @Test
    fun testToE164WithValidChinesePhoneNumber() {
        // 测试有效的中国手机号码
        val phoneNumber = "13800138000"
        val result = PhoneNumberUtils.toE164(phoneNumber)
        
        assertNotNull(result)
        assertEquals("+8613800138000", result)
    }

    @Test
    fun testToE164WithValidChinesePhoneNumberWithCountryCode() {
        // 测试已经包含国家代码的中国手机号码
        val phoneNumber = "+8613800138000"
        val result = PhoneNumberUtils.toE164(phoneNumber)
        
        assertNotNull(result)
        assertEquals("+8613800138000", result)
    }

    @Test
    fun testToE164WithInvalidPhoneNumber() {
        // 测试无效的电话号码
        val phoneNumber = "invalid-phone"
        val result = PhoneNumberUtils.toE164(phoneNumber)
        
        assertNull(result)
    }

    @Test
    fun testToE164WithEmptyPhoneNumber() {
        // 测试空的电话号码
        val phoneNumber = ""
        val result = PhoneNumberUtils.toE164(phoneNumber)
        
        assertNull(result)
    }

    @Test
    fun testToE164WithNullPhoneNumber() {
        // 测试null电话号码
        val phoneNumber = null
        val result = PhoneNumberUtils.toE164(phoneNumber)
        
        assertNull(result)
    }

    @Test
    fun testToE164WithUSPhoneNumber() {
        // 测试美国电话号码
        val phoneNumber = "(415) 555-2671"
        val result = PhoneNumberUtils.toE164(phoneNumber, "US")
        
        assertNotNull(result)
        assertEquals("+14155552671", result)
    }

    @Test
    fun testToE164WithLandlineNumber() {
        // 测试固定电话号码
        val phoneNumber = "010-12345678"
        val result = PhoneNumberUtils.toE164(phoneNumber)
        
        assertNotNull(result)
        assertEquals("+861012345678", result)
    }
}