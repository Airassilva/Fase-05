package dev.aira.saudeEmRota.model

data class Usf(
    val id: String = "",
    val nomeOficial: String = "",
    val numeroUS: String = "",
    val distrito: Int = 0,
    val cnes: String = "",
    val endereco: String = "",
    val bairro: String = "",
    val fone: String = "",
    val especialidade: String = "",
    val horario: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val ativa: Boolean = true,
    val chaveRelacao: String = "",

    val criadaPorUsuario: Boolean = false,  // distingue do banco oficial
    val usuarioId: String = "",             // quem criou
    val aprovada: Boolean = false,          // moderação futura
    val criadaEm: Long = 0L                 // timestamp
)
