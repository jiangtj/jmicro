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

        Assertions.assertEquals("2024-02-03", date.format(DATE))
        Assertions.assertEquals("2024-02-03", datetime.format(DATE))
        Assertions.assertEquals("2024-02-03", zonedDatetime.format(DATE))
        Assertions.assertEquals("08:30:40", time.format(TIME))
        Assertions.assertEquals("08:30:40", datetime.format(TIME))
        Assertions.assertEquals("08:30:40", zonedDatetime.format(TIME))
        Assertions.assertEquals("2024-02-03 08:30:40", datetime.format(DATE_TIME))
        Assertions.assertEquals("2024-02-03 08:30:40", zonedDatetime.format(DATE_TIME))
        Assertions.assertEquals("2024-02-03T08:30:40+08:00", zonedDatetime.format(RFC3339))
    }
}