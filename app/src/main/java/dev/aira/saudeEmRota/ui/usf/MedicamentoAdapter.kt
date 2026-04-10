package dev.aira.saudeEmRota.ui.usf

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.aira.saudeEmRota.R
import dev.aira.saudeEmRota.databinding.ItemMedicamentoBinding
import dev.aira.saudeEmRota.model.Medicamento

class MedicamentoAdapter(
    private val medicamentos: List<Medicamento>
) : RecyclerView.Adapter<MedicamentoAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMedicamentoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMedicamentoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val med = medicamentos[position]
        holder.binding.apply {
            tvProduto.text = med.produto
            tvApresentacao.text = med.apresentacao
            tvClasse.text = med.classe
            tvQuantidade.text = "Qtd: ${med.quantidade}"
            ivDisponivel.setImageResource(
                if (med.disponivel) R.drawable.ic_check
                else R.drawable.ic_close
            )
        }
    }

    override fun getItemCount() = medicamentos.size
}