package dev.aira.saudeEmRota.ui.historic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.aira.saudeEmRota.databinding.ItemHistoricUsfBinding
import dev.aira.saudeEmRota.model.HistoricoUsf

class HistoricoAdapter(
    private val historico: List<HistoricoUsf>,
    private val onItemClick: (HistoricoUsf) -> Unit
) : RecyclerView.Adapter<HistoricoAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHistoricUsfBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoricUsfBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = historico[position]
        holder.binding.apply {
            tvNomeUsf.text = item.nomeUsf.replaceFirstChar { it.uppercase() }
            tvBairro.text = "🏘️ ${item.bairro.replaceFirstChar { it.uppercase() }}"
            tvDataConsulta.text = "Consultado em: ${formatarData(item.consultadoEm)}"
            root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun getItemCount() = historico.size

    private fun formatarData(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}