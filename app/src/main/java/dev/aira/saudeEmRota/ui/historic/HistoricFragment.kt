package dev.aira.saudeEmRota.ui.historic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import dev.aira.saudeEmRota.databinding.FragmentHistoricBinding
import dev.aira.saudeEmRota.db.HistoricoManager

class HistoricFragment : Fragment() {

    private lateinit var binding: FragmentHistoricBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoricBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarTabs()
        carregarUsfConsultadas()
    }

    private fun configurarTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> carregarUsfConsultadas()
                    1 -> carregarQuestionarios() // futuro
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun carregarUsfConsultadas() {
        val historico = HistoricoManager.getHistorico(requireContext())

        if (historico.isEmpty()) {
            binding.tvVazio.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.tvVazio.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.recyclerView.adapter = HistoricoAdapter(historico) { item ->
                val action = HistoricFragmentDirections
                    .actionNavHistoricToUsfDetailFragment(item.usfId)
                findNavController().navigate(action)
            }
        }
    }

    private fun carregarQuestionarios() {
        // TODO implementar futuramente
        binding.tvVazio.text = "Nenhum questionário respondido ainda"
        binding.tvVazio.visibility = View.VISIBLE

        binding.recyclerView.visibility = View.GONE
    }
}