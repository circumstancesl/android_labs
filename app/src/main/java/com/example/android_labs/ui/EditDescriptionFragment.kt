package com.example.android_labs.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android_labs.databinding.FragmentEditBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditDescriptionFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private val args: EditDescriptionFragmentArgs by navArgs()

    companion object {
        const val REQUEST_KEY = "editDescriptionRequest"
        const val DESCRIPTION_KEY = "description"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentEditBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            etDescription.apply {
                setText(args.description)
                requestFocus()
                showKeyboardDelayed()
            }

            btnSave.setOnClickListener { saveAndExit() }
            btnCancel.setOnClickListener { exitWithoutSaving() }
        }
    }

    private fun saveAndExit() {
        hideKeyboard()
        setFragmentResult(
            REQUEST_KEY,
            bundleOf("mediaId" to args.mediaId, DESCRIPTION_KEY to binding.etDescription.text.toString())
        )
        findNavController().navigateUp()
    }

    private fun exitWithoutSaving() {
        hideKeyboard()
        findNavController().navigateUp()
    }

    private fun showKeyboardDelayed() = binding.etDescription.postDelayed({
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(binding.etDescription, InputMethodManager.SHOW_IMPLICIT)
    }, 200)

    private fun hideKeyboard() {
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.etDescription.windowToken, 0)
        binding.etDescription.clearFocus()
    }
}