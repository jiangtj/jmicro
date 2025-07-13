package com.jiangtj.micro.common;

import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatetimeFormattersTest {

    @Test
    public void testFormatters() {
        LocalDate date = LocalDate.of(2024, 2, 3);
        LocalTime time = LocalTime.of(8, 30, 40);
        LocalDateTime datetime = LocalDateTime.of(date, time);
        ZonedDateTime zonedDatetime = ZonedDateTime.of(datetime, ZoneId.of("Asia/Shanghai"));

        assertEquals("2024-02-03", date.format(DatetimeFormatters.DATE));
        assertEquals("2024-02-03", datetime.format(DatetimeFormatters.DATE));
        assertEquals("2024-02-03", zonedDatetime.format(DatetimeFormatters.DATE));
        assertEquals("08:30:40", time.format(DatetimeFormatters.TIME));
        assertEquals("08:30:40", datetime.format(DatetimeFormatters.TIME));
        assertEquals("08:30:40", zonedDatetime.format(DatetimeFormatters.TIME));
        assertEquals("2024-02-03 08:30:40", datetime.format(DatetimeFormatters.DATE_TIME));
        assertEquals("2024-02-03 08:30:40", zonedDatetime.format(DatetimeFormatters.DATE_TIME));
        assertEquals("2024-02-03T08:30:40+08:00", zonedDatetime.format(DatetimeFormatters.RFC3339));
    }

}