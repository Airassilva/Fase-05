package dev.aira.saudeEmRota.model

data class Medicamento(
    val produto: String = "",
    val apresentacao: String = "",
    val quantidade: Int = 0,
    val classe: String = "",
    val disponivel: Boolean = false
)