package com.example.gesport.ui.backend.ges_reservation

import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class TimeSlot(
    val start: String,   // "HH:mm"
    val end: String,     // "HH:mm"
    val label: String    // "08:00 - 09:00"
)

private val timeFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun generateSlots(
    dayStart: String = "08:00",
    dayEnd: String = "22:00",
    slotMinutes: Long = 60L
): List<TimeSlot> {
    require(slotMinutes > 0) { "slotMinutes debe ser mayor que 0" }

    val start = LocalTime.parse(dayStart, timeFmt)
    val end = LocalTime.parse(dayEnd, timeFmt)

    val slots = mutableListOf<TimeSlot>()
    var cursor = start

    while (cursor.plusMinutes(slotMinutes) <= end) {
        val next = cursor.plusMinutes(slotMinutes)
        val s = cursor.format(timeFmt)
        val e = next.format(timeFmt)
        slots.add(TimeSlot(start = s, end = e, label = "$s - $e"))
        cursor = next
    }

    return slots
}

fun overlaps(
    slotStart: String,
    slotEnd: String,
    resStart: String,
    resEnd: String
): Boolean {
    val aStart = LocalTime.parse(slotStart, timeFmt)
    val aEnd = LocalTime.parse(slotEnd, timeFmt)
    val bStart = LocalTime.parse(resStart, timeFmt)
    val bEnd = LocalTime.parse(resEnd, timeFmt)

    // solape: empieza antes de que acabe el otro y acaba después de que empiece
    return aStart < bEnd && aEnd > bStart
}