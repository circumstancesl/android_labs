package com.example.android_labs

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android_labs.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment() {
    private val viewModel: ImagesViewModel by viewModels()
    private lateinit var binding: FragmentGalleryBinding
    private val permissionRequestCode = 100

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupPermissions()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = ImagesAdapter(
                onLongClick = { position -> showDetailScreen(position, true) },
                onClick = { item -> showDetailScreen(item, false) }
            )
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.images.observe(viewLifecycleOwner) { items ->
            (binding.rView.adapter as? ImagesAdapter)?.updateItems(items)
        }
    }

    private fun setupPermissions() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.loadImages()
        } else {
            requestPermissions(arrayOf(permission), permissionRequestCode)
        }
    }

    @Deprecated("Deprecated in Fragment")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionRequestCode &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewModel.loadImages()
        }
    }

    private fun showDetailScreen(position: Int, editMode: Boolean) {
        val currentItem = viewModel.images.value?.get(position) ?: return
        showDetailScreen(currentItem, editMode)
    }

    private fun showDetailScreen(item: ImageItem, editMode: Boolean) {
        val direction = GalleryFragmentDirections.actionGalleryToDetail(
            mediaUri = item.uri.toString(),
            description = item.description,
            mediaId = item.mediaId,
            isEditMode = editMode
        )
        findNavController().navigate(direction)

        if (editMode) {
            setupResultListener()
        }
    }

    private fun setupResultListener() {
        setFragmentResultListener(ImageDetailFragment.REQUEST_KEY) { _, bundle ->
            val mediaId = bundle.getLong("mediaId")
            val newDescription = bundle.getString("description") ?: ""
            if (mediaId != -1L) {
                viewModel.updateDescription(mediaId, newDescription)
            }
        }
    }
}