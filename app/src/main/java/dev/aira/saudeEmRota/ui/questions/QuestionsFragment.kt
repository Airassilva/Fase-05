package dev.aira.saudeEmRota.ui.questions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dev.aira.saudeEmRota.databinding.FragmentQuestionsBinding
import dev.aira.saudeEmRota.ui.historic.HistoricFragment.TransformAdapter

class QuestionsFragment : Fragment() {

    private var _binding: FragmentQuestionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val questionsViewModel = ViewModelProvider(this).get(QuestionsViewModel::class.java)

        _binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val adapter = TransformAdapter()
        binding.recyclerviewTransform.adapter = adapter

        questionsViewModel.texts.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}