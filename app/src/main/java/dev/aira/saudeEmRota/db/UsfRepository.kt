package dev.aira.saudeEmRota.db

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dev.aira.saudeEmRota.model.Usf
import kotlinx.coroutines.tasks.await

class UsfRepository {

    private val db = Firebase.firestore

    suspend fun getUsfs(): List<Usf> {
        return db.collection("usfs")
            .whereEqualTo("ativa", true)
            .get()
            .await()
            .map { doc ->
                Usf(
                    id = doc.id,
                    nomeOficial = doc.getString("nomeOficial") ?: "",
                    numeroUS = doc.getString("numeroUS") ?: "",
                    latitude = doc.getDouble("latitude") ?: 0.0,
                    longitude = doc.getDouble("longitude") ?: 0.0,
                    endereco = doc.getString("endereco") ?: "",
                    bairro = doc.getString("bairro") ?: "",
                    fone = doc.getString("fone") ?: "",
                    horario = doc.getString("horario") ?: ""
                )
            }
    }

    suspend fun getUsfById(id: String): Usf? {
        return db.collection("usfs")
            .document(id)
            .get()
            .await()
            .let { doc ->
                if (doc.exists()) {
                    Usf(
                        id = doc.id,
                        nomeOficial = doc.getString("nomeOficial") ?: "",
                        numeroUS = doc.getString("numeroUS") ?: "",
                        distrito = doc.getLong("distrito")?.toInt() ?: 0,
                        endereco = doc.getString("endereco") ?: "",
                        bairro = doc.getString("bairro") ?: "",
                        fone = doc.getString("fone") ?: "",
                        especialidade = doc.getString("especialidade") ?: "",
                        horario = doc.getString("horario") ?: "",
                        latitude = doc.getDouble("latitude") ?: 0.0,
                        longitude = doc.getDouble("longitude") ?: 0.0,
                    )
                } else null
            }
    }

    suspend fun criarUsf(usf: Usf): String {
        val doc = db.collection("usfs").document()
        val novaUsf = usf.copy(
            id = doc.id,
            criadaPorUsuario = true,
            aprovada = false,
            criadaEm = System.currentTimeMillis()
        )
        doc.set(novaUsf).await()
        return doc.id
    }
}