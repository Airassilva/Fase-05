package dev.aira.saudeEmRota.ui.questions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.aira.saudeEmRota.R
import dev.aira.saudeEmRota.databinding.ItemQuestionarioBinding

class QuestionarioAdapter(
    private val medicamentos: List<String>,
    private val onResposta: (String, String) -> Unit
) : RecyclerView.Adapter<QuestionarioAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemQuestionarioBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuestionarioBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nome = medicamentos[position]
        holder.binding.tvNomeMedicamento.text = nome
        holder.binding.rgDisponibilidade.setOnCheckedChangeListener { _, checkedId ->
            val resp = when (checkedId) {
                R.id.rbTem -> "tem"
                R.id.rbNaoTem -> "nao_tem"
                else -> "nao_sei"
            }
            onResposta(nome, resp)
        }
    }

    override fun getItemCount() = medicamentos.size
}