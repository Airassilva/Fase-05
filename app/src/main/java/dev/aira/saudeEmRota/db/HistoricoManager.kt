package dev.aira.saudeEmRota.db

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.aira.saudeEmRota.model.HistoricoUsf
import dev.aira.saudeEmRota.model.Usf
import kotlin.collections.toMutableList
import androidx.core.content.edit

object HistoricoManager {

    private const val PREFS_NAME = "historico_prefs"
    private const val KEY_HISTORICO = "historico_usfs"

    fun salvarConsulta(context: Context, usf: Usf) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()

        val historico = getHistorico(context).toMutableList()

        historico.removeAll { it.usfId == usf.id }
        historico.add(
            0, HistoricoUsf(
                usfId = usf.id,
                nomeUsf = usf.nomeOficial,
                bairro = usf.bairro,
                consultadoEm = System.currentTimeMillis()
            )
        )

        val limitado = historico.take(20)
        prefs.edit { putString(KEY_HISTORICO, gson.toJson(limitado)) }
    }

    fun getHistorico(context: Context): List<HistoricoUsf> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString(KEY_HISTORICO, null) ?: return emptyList()
        val type = object : TypeToken<List<HistoricoUsf>>() {}.type
        return gson.fromJson(json, type)
    }
}