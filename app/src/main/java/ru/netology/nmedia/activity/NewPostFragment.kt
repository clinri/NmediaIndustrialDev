package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        binding.ok.isEnabled = true

        arguments?.textArg?.let(binding.edit::setText)

        binding.ok.setOnClickListener {
            binding.ok.isEnabled = false
            viewModel.changeContent(binding.edit.text.toString())
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
        }

        viewModel.data.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
        }

        viewModel.eventStateResId.observe(viewLifecycleOwner) {
            it?.let {
                binding.ok.isEnabled = true
                val toast = Toast.makeText(
                    activity,
                    activity?.getString(it),
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(0, 0, Gravity.CENTER)
                toast.show()
            }
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }
        return binding.root
    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}