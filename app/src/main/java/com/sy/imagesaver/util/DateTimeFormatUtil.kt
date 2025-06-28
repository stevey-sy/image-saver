package com.sy.imagesaver.util

import kotlinx.datetime.*
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.format.*
import kotlin.time.Clock

@OptIn(ExperimentalTime::class)
fun Instant.toSeoulLocalDateTime(): LocalDateTime {
    return this.toLocalDateTime(TimeZone.of("Asia/Seoul"))
}

@OptIn(ExperimentalTime::class)
fun Instant.toLong(): Long {
    return this.toEpochMilliseconds()
}

@OptIn(ExperimentalTime::class)
fun Long.toInstant(): Instant {
    return Instant.fromEpochMilliseconds(this)
}

// 문자열 형식의 datetime을 Instant로 파싱
@OptIn(ExperimentalTime::class)
fun String.parseToInstant(): Instant {
    return try {
        // "2025-06-22 12:00" 형식을 파싱
        val parts = this.split(" ")
        val dateParts = parts[0].split("-")
        val timeParts = parts[1].split(":")
        
        val year = dateParts[0].toInt()
        val month = dateParts[1].toInt()
        val day = dateParts[2].toInt()
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()
        
        // LocalDateTime 생성 후 Instant로 변환
        val localDateTime = LocalDateTime(year, month, day, hour, minute)
        localDateTime.toInstant(TimeZone.of("Asia/Seoul"))
    } catch (e: Exception) {
        // 파싱 실패 시 현재 시간 반환
        Clock.System.now()
    }
}

private val ymdHmFormat = LocalDateTime.Format {
    date(LocalDate.Formats.ISO)  // yyyy-MM-dd
    char(' ')
    hour(); char(':'); minute()
}

fun LocalDateTime.formatYmdHm(): String =
    this.format(ymdHmFormat)