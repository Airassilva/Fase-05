package dev.aira.saudeEmRota.db

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dev.aira.saudeEmRota.model.Medicamento
import kotlinx.coroutines.tasks.await

class EstoqueRepository {

    private val db = Firebase.firestore

//    suspend fun getMedicamentosByUSF(numeroUS: String): List<Medicamento> {
//        val result = mutableListOf<Medicamento>()
//
//        val snapshot = db.collection("estoque_por_usf")
//            .whereEqualTo("numeroUS", numeroUS)
//            .get()
//            .await()
//
//        for (doc in snapshot.documents) {
//
//            Log.d("DEBUG", snapshot.toString())
//
//            val medicamentosSnapshot = doc.reference
//                .collection("medicamentos")
//                .get()
//                .await()
//
//            val meds = medicamentosSnapshot.documents.map { medDoc ->
//                Medicamento(
//                    produto = medDoc.getString("produto") ?: "",
//                    apresentacao = medDoc.getString("apresentacao") ?: "",
//                    quantidade = medDoc.getLong("quantidade") ?: 0,
//                    classe = medDoc.getString("classe") ?: "",
//                    disponivel = medDoc.getBoolean("disponivel") ?: false
//                )
//            }
//
//            result.addAll(meds)
//        }
//
//        return result
//    }

    suspend fun getMedicamentosByUSF(numeroUS: String): List<Medicamento> {
        val snapshot = db.collection("medicamentos")
            .whereEqualTo("numeroUS", numeroUS)
            .get()
            .await()

        Log.d("TESTE", "docs encontrados: ${snapshot.size()}")

        val lista = mutableListOf<Medicamento>()

        for (doc in snapshot.documents) {
            Log.d("TESTE", "doc: ${doc.data}")

            val medicamento = Medicamento(
                produto = doc.getString("produto") ?: "",
                apresentacao = doc.getString("apresentacao") ?: "",
                quantidade = doc.getLong("quantidade")?.toInt() ?: 0,
                classe = doc.getString("classe") ?: "",
                disponivel = doc.getBoolean("disponivel") ?: false
            )

            Log.d("DEBUG", "Convertido: $medicamento")

            lista.add(medicamento)
        }

        return lista
    }
}