@file:JvmName("DatetimeFormatters")
package com.jiangtj.micro.common

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

@JvmField
val DateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")!!
@JvmField
val Date = DateTimeFormatter.ofPattern("yyyy-MM-dd")!!
@JvmField
val Time = DateTimeFormatter.ofPattern("HH:mm:ss")!!
// RFC3339 for wechat pay v3
@JvmField
val RFC3339 = DateTimeFormatterBuilder()
    .parseCaseInsensitive()
    .append(Date)
    .appendLiteral("T")
    .append(Time)
    .parseLenient()
    .appendOffsetId()
    .parseStrict()
    .toFormatter()!!