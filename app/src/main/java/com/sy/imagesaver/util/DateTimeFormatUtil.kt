package com.sy.imagesaver.util

import kotlinx.datetime.*
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.format.*

@OptIn(ExperimentalTime::class)
fun Instant.toSeoulLocalDateTime(): LocalDateTime {
    return this.toLocalDateTime(TimeZone.of("Asia/Seoul"))
}


private val ymdHmFormat = LocalDateTime.Format {
    date(LocalDate.Formats.ISO)  // yyyy-MM-dd
    char(' ')
    hour(); char(':'); minute()
}

fun LocalDateTime.formatYmdHm(): String =
    this.format(ymdHmFormat)