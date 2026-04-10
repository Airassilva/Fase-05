package dev.aira.saudeEmRota.db

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dev.aira.saudeEmRota.model.Medicamento
import kotlinx.coroutines.tasks.await

class EstoqueRepository {

    private val db = Firebase.firestore

    suspend fun getMedicamentosByUSF(numeroUS: String): List<Medicamento> {
        return db.collection("estoque_por_usf")
            .whereEqualTo("numeroUS", numeroUS)
            .get()
            .await()
            .flatMap { doc ->
                (doc.get("medicamentos") as? List<Map<String, Any>>)
                    ?.map { med ->
                        Medicamento(
                            produto = med["produto"] as? String ?: "",
                            apresentacao = med["apresentacao"] as? String ?: "",
                            quantidade = (med["quantidade"] as? Long)?.toInt() ?: 0,
                            classe = med["classe"] as? String ?: "",
                            disponivel = med["disponivel"] as? Boolean ?: false
                        )
                    } ?: emptyList()
            }
    }
}