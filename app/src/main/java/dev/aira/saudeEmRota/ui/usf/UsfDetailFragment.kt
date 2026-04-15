package dev.aira.saudeEmRota.ui.usf

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import dev.aira.saudeEmRota.databinding.FragmentUsfDetailBinding
import dev.aira.saudeEmRota.db.EstoqueRepository
import dev.aira.saudeEmRota.db.UsfRepository
import dev.aira.saudeEmRota.model.Usf
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import dev.aira.saudeEmRota.db.HistoricoManager

class UsfDetailFragment : Fragment() {

    private lateinit var binding: FragmentUsfDetailBinding
    private val args: UsfDetailFragmentArgs by navArgs()
    private val usfRepository = UsfRepository()
    private val estoqueRepository = EstoqueRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsfDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carregarDados()
    }

    private fun carregarDados() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val usf = usfRepository.getUsfById(args.usfId)
                usf?.let { exibirUsf(it) }
            } catch (e: Exception) {
                Log.e("UsfDetail", "Erro ao carregar USF", e)
            }
        }
    }

    private fun exibirUsf(usf: Usf) {
        HistoricoManager.salvarConsulta(requireContext(), usf)
        binding.tvNome.text = usf.nomeOficial.replaceFirstChar { it.uppercase() }
        binding.tvDistrito.text = "Distrito Sanitário ${usf.distrito}"
        binding.tvEndereco.text = "📍 ${usf.endereco}"
        binding.tvBairro.text = "🏘️ ${usf.bairro.replaceFirstChar { it.uppercase() }}"
        binding.tvFone.text = "📞 ${usf.fone.ifEmpty { "Não informado" }}"
        binding.tvHorario.text = "🕐 ${usf.horario}"
        binding.tvEspecialidade.text = "🩺 ${usf.especialidade}"

        binding.btnRota.setOnClickListener {
            val uri = "google.navigation:q=${usf.latitude},${usf.longitude}".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
            }
            startActivity(intent)
        }
        binding.btnQuestionario.setOnClickListener {
            val action = UsfDetailFragmentDirections
                .actionUsfDetailToQuestions(
                    usfId = usf.id,
                    usfNome = usf.nomeOficial
                )
            findNavController().navigate(action)
        }
        carregarMedicamentos(usf.numeroUS)
    }

    private fun carregarMedicamentos(numeroUS: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val medicamentos = estoqueRepository.getMedicamentosByUSF(numeroUS)
                Log.d("TESTE", "numeroUS enviado: $numeroUS")
                Log.d("Medicamentos", "medicamentos achados: $medicamentos")
                val adapter = MedicamentoAdapter(medicamentos)
                binding.rvMedicamentos.adapter = adapter
            } catch (e: Exception) {
                Log.e("UsfDetail", "Erro ao carregar medicamentos", e)
            }
        }
    }
}