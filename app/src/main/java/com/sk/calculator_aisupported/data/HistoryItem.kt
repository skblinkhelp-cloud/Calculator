package com.sk.calculator_aisupported.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class HistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val formulaExpression: String,
    val solvedResult: String,
    val timestamp: Long = System.currentTimeMillis()
)