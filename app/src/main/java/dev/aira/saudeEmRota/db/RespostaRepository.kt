package dev.aira.saudeEmRota.db

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dev.aira.saudeEmRota.model.Resposta
import kotlinx.coroutines.tasks.await

class RespostaRepository {

    private val db = Firebase.firestore

    suspend fun salvarResposta(resposta: Resposta): String {
        val doc = db.collection("respostas").document()
        val nova = resposta.copy(
            id = doc.id,
            timestamp = System.currentTimeMillis()
        )
        doc.set(nova).await()
        return doc.id
    }

    suspend fun getRespostasPorUSF(usfId: String): List<Resposta> {
        return db.collection("respostas")
            .whereEqualTo("usfId", usfId)
            .get()
            .await()
            .map { doc -> doc.toObject(Resposta::class.java).copy(id = doc.id) }
    }

    suspend fun buscarMedicamentoEmTodasUSFs(nomeMedicamento: String): List<Resposta> {
        return db.collection("respostas")
            .whereEqualTo("disponivel", "tem")
            .whereGreaterThanOrEqualTo("remedioNome", nomeMedicamento)
            .whereLessThanOrEqualTo("remedioNome", nomeMedicamento + "\uf8ff")
            .get()
            .await()
            .map { doc -> doc.toObject(Resposta::class.java).copy(id = doc.id) }
    }
}