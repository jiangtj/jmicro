package com.jiangtj.micro.common.utils

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

object PhoneNumberUtils {

    /**
     * 将电话号码转换为E.164格式
     */
    fun toE164(phoneNumber: String?, defaultRegion: String = "CN"): String? {
        val phoneUtil = PhoneNumberUtil.getInstance();
        try {
            val parsedNumber = phoneUtil.parse(phoneNumber, defaultRegion);
            // Format the phone number in E.164 format
            return phoneUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (e: NumberParseException) {
            // Handle the exception if the phone number cannot be parsed
            logger.error(e) { "Error parsing phone number: $phoneNumber" }
            return null;
        }
    }

}