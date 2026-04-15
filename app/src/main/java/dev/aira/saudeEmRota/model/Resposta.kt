package dev.aira.saudeEmRota.model

data class Resposta(
    val id: String = "",
    val usfId: String = "",
    val usfNome: String = "",
    val remedioNome: String = "",
    val disponivel: String = "",
    val adicionadoPeloUsuario: Boolean = false,
    val timestamp: Long = 0L
)