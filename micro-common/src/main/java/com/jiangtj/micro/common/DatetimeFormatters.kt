@file:JvmName("DatetimeFormatters")
package com.jiangtj.micro.common

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

@JvmField
val YYYY_MM_DD: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
@JvmField
val YYYY_MM_DD_HH_MM: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
@JvmField
val YYYY_MM_DD_HH_MM_SS: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
@JvmField
val HH_MM: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
@JvmField
val HH_MM_SS: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

@JvmField
val DATE_TIME: DateTimeFormatter = YYYY_MM_DD_HH_MM_SS
@JvmField
val DATE: DateTimeFormatter = YYYY_MM_DD
@JvmField
val TIME: DateTimeFormatter = HH_MM_SS
// RFC3339 for wechat pay v3
@JvmField
val RFC3339: DateTimeFormatter = DateTimeFormatterBuilder()
    .parseCaseInsensitive()
    .append(DATE)
    .appendLiteral("T")
    .append(TIME)
    .parseLenient()
    .appendOffsetId()
    .parseStrict()
    .toFormatter()
