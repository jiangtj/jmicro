package com.jiangtj.micro.common

import org.junit.jupiter.api.Assertions
import java.time.*
import kotlin.test.Test

class DatetimeFormattersKTest {

    @Test
    fun test() {
        val date = LocalDate.of(2024, 2, 3)
        val time = LocalTime.of(8, 30, 40)
        val datetime = LocalDateTime.of(date, time)
        val zonedDatetime = ZonedDateTime.of(datetime, ZoneId.of("Asia/Shanghai"))

        Assertions.assertEquals("2024-02-03", date.format(Date))
        Assertions.assertEquals("2024-02-03", datetime.format(Date))
        Assertions.assertEquals("2024-02-03", zonedDatetime.format(Date))
        Assertions.assertEquals("08:30:40", time.format(Time))
        Assertions.assertEquals("08:30:40", datetime.format(Time))
        Assertions.assertEquals("08:30:40", zonedDatetime.format(Time))
        Assertions.assertEquals("2024-02-03 08:30:40", datetime.format(DateTime))
        Assertions.assertEquals("2024-02-03 08:30:40", zonedDatetime.format(DateTime))
        Assertions.assertEquals("2024-02-03T08:30:40+08:00", zonedDatetime.format(RFC3339))
    }
}