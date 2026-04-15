package dev.aira.saudeEmRota.ui.questions

import androidx.appcompat.widget.SearchView
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dev.aira.saudeEmRota.R
import dev.aira.saudeEmRota.databinding.FragmentQuestionsBinding
import dev.aira.saudeEmRota.db.EstoqueRepository
import dev.aira.saudeEmRota.db.RespostaRepository
import dev.aira.saudeEmRota.model.Resposta
import kotlinx.coroutines.launch

class QuestionsFragment : Fragment() {

    private lateinit var binding: FragmentQuestionsBinding
    private val args: QuestionsFragmentArgs by navArgs()
    private val estoqueRepository = EstoqueRepository()
    private val respostaRepository = RespostaRepository()

    private val respostas = mutableMapOf<String, String>()
    private val medicamentosCustom = mutableListOf<String>()

    private val modoUsf get() = args.usfId != null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (modoUsf) configurarModoUsf()
        else configurarModoBusca()

    }

    private fun configurarModoUsf() {
        binding.headerUsf?.visibility = View.VISIBLE
        binding.tvNomeUsf?.text = args.usfNome ?: ""
        binding.fabAddMedicamento.visibility = View.VISIBLE
        binding.btnEnviarRespostas.visibility = View.VISIBLE
        binding.searchMedicamento.visibility = View.GONE

        val params = binding.rvMedicamentosQuestionario.layoutParams
                as ConstraintLayout.LayoutParams
        params.topToBottom = R.id.headerUsf
        binding.rvMedicamentosQuestionario.layoutParams = params

        carregarMedicamentosUsf()

        binding.fabAddMedicamento.setOnClickListener { mostrarDialogAddMedicamento() }
        binding.btnEnviarRespostas.setOnClickListener { enviarRespostas() }
    }

    private fun carregarMedicamentosUsf(extras: List<String> = emptyList()) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val meds = estoqueRepository
                    .getMedicamentosByUSF(args.usfId!!)
                    .map { it.produto }
                    .plus(extras)
                    .distinct()

                exibirQuestionario(meds as List<String>)
            } catch (e: Exception) {
                Log.e("QuestionsFragment", "Erro ao carregar medicamentos", e)
            }
        }
    }

    private fun exibirQuestionario(medicamentos: List<String>) {
        if (medicamentos.isEmpty()) {
            binding.tvVazio.visibility = View.VISIBLE
            binding.rvMedicamentosQuestionario.visibility = View.GONE
            return
        }
        binding.tvVazio.visibility = View.GONE
        binding.rvMedicamentosQuestionario.visibility = View.VISIBLE
        binding.rvMedicamentosQuestionario.adapter = QuestionarioAdapter(medicamentos) { nome, resp ->
            respostas[nome] = resp
        }
    }

    private fun mostrarDialogAddMedicamento() {
        val input = android.widget.EditText(requireContext()).apply {
            hint = "Ex: Amoxicilina 500mg"
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Adicionar medicamento")
            .setMessage("Digite o nome do medicamento:")
            .setView(input)
            .setPositiveButton("Adicionar") { _, _ ->
                val nome = input.text.toString().trim()
                if (nome.isNotEmpty()) {
                    medicamentosCustom.add(nome)
                    carregarMedicamentosUsf(medicamentosCustom)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun enviarRespostas() {
        if (respostas.isEmpty()) {
            Toast.makeText(requireContext(),
                "Responda pelo menos um medicamento", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                respostas.forEach { (remedioNome, disponivel) ->
                    val resposta = Resposta(
                        usfId = args.usfId!!,
                        usfNome = args.usfNome ?: "",
                        remedioNome = remedioNome,
                        disponivel = disponivel,
                        adicionadoPeloUsuario = medicamentosCustom.contains(remedioNome)
                    )
                    respostaRepository.salvarResposta(resposta)
                }
                Toast.makeText(requireContext(),
                    "Respostas enviadas! Obrigado 🙏", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.nav_map)
            } catch (e: Exception) {
                Toast.makeText(requireContext(),
                    "Erro ao enviar respostas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarModoBusca() {
        binding.searchMedicamento.visibility = View.VISIBLE
        binding.headerUsf?.visibility = View.GONE
        binding.fabAddMedicamento.visibility = View.GONE
        binding.btnEnviarRespostas.visibility = View.GONE

        val params = binding.rvMedicamentosQuestionario.layoutParams
                as ConstraintLayout.LayoutParams
        params.topToBottom = R.id.searchMedicamento
        binding.rvMedicamentosQuestionario.layoutParams = params

        binding.searchMedicamento.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { buscarMedicamento(it) }
                    return true
                }
                override fun onQueryTextChange(newText: String?) = false
            }
        )
    }

    private fun buscarMedicamento(nome: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resultados = respostaRepository.buscarMedicamentoEmTodasUSFs(nome)
                if (resultados.isEmpty()) {
                    binding.tvVazio.visibility = View.VISIBLE
                    binding.rvMedicamentosQuestionario.visibility = View.GONE
                } else {
                    binding.tvVazio.visibility = View.GONE
                    binding.rvMedicamentosQuestionario.visibility = View.VISIBLE
                    binding.rvMedicamentosQuestionario.adapter =
                        ResultadoBuscaAdapter(resultados) { resposta ->
                            val action = QuestionsFragmentDirections
                                .actionNavQuestionsToUsfDetailFragment(resposta.usfId)
                            findNavController().navigate(action)
                        }
                }
            } catch (e: Exception) {
                Log.e("QuestionsFragment", "Erro na busca", e)
            }
        }
    }
}
