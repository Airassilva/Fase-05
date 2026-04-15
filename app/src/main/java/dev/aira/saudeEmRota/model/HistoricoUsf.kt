package dev.aira.saudeEmRota.model

data class HistoricoUsf(
    val usfId: String = "",
    val nomeUsf: String = "",
    val bairro: String = "",
    val consultadoEm: Long = 0L
)