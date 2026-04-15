package dev.aira.saudeEmRota.ui.questions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.aira.saudeEmRota.databinding.ItemResultadoBuscaBinding
import dev.aira.saudeEmRota.model.Resposta

class ResultadoBuscaAdapter(
    private val resultados: List<Resposta>,
    private val onItemClick: (Resposta) -> Unit
) : RecyclerView.Adapter<ResultadoBuscaAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemResultadoBuscaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemResultadoBuscaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = resultados[position]
        holder.binding.apply {
            tvNomeMedicamento.text = item.remedioNome
            tvNomeUsf.text = item.usfNome.replaceFirstChar { it.uppercase() }
            tvDisponivel.text = when (item.disponivel) {
                "tem" -> "✅ Disponível"
                "nao_tem" -> "❌ Não disponível"
                else -> "🤷 Não confirmado"
            }
            root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun getItemCount() = resultados.size
}