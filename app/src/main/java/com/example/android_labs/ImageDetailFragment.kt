package com.example.android_labs

import android.content.Context
import android.net.Uri
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
import com.bumptech.glide.Glide
import com.example.android_labs.databinding.FragmentImageDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageDetailFragment : Fragment() {
    private var _binding: FragmentImageDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ImageDetailFragmentArgs by navArgs()

    private var isEditMode = false
    companion object {
        const val REQUEST_KEY = "imageDetailRequest"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isEditMode = args.isEditMode
        setupUI()
        setupMode()
    }

    private fun setupUI() {
        val uri = Uri.parse(args.mediaUri)
        Glide.with(this)
            .load(uri)
            .into(binding.imageView)

        binding.descriptionView.text = args.description
        binding.editDescription.setText(args.description)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.saveButton.setOnClickListener {
            saveChanges()
        }

        binding.editButton.setOnClickListener {
            switchToEditMode()
        }

        binding.cancelButton.setOnClickListener {
            exitWithoutSaving()
        }
    }

    private fun setupMode() {
        if (isEditMode) {
            switchToEditMode()
        } else {
            switchToViewMode()
        }
    }

    private fun switchToEditMode() {
        binding.descriptionView.visibility = View.GONE
        binding.editDescription.visibility = View.VISIBLE
        binding.editButton.visibility = View.GONE
        binding.saveButton.visibility = View.VISIBLE
        binding.cancelButton.visibility = View.VISIBLE

        binding.editDescription.requestFocus()
        showKeyboard()
    }

    private fun switchToViewMode() {
        binding.descriptionView.visibility = View.VISIBLE
        binding.editDescription.visibility = View.GONE
        binding.editButton.visibility = View.VISIBLE
        binding.saveButton.visibility = View.GONE
        binding.cancelButton.visibility = View.GONE

        hideKeyboard()
    }

    private fun saveChanges() {
        val newDescription = binding.editDescription.text.toString()

        setFragmentResult(
            REQUEST_KEY,
            bundleOf(
                "mediaId" to args.mediaId,
                "description" to newDescription
            )
        )

        binding.descriptionView.text = newDescription
        switchToViewMode()
    }

    private fun exitWithoutSaving() {
        binding.editDescription.setText(args.description)
        switchToViewMode()
    }

    private fun showKeyboard() {
        binding.editDescription.postDelayed({
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.editDescription, InputMethodManager.SHOW_IMPLICIT)
        }, 200)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editDescription.windowToken, 0)
        binding.editDescription.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}