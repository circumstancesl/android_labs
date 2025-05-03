package com.example.android_labs.ui

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
import com.example.android_labs.data.ImageItem
import com.example.android_labs.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment() {
    private val viewModel: GalleryViewModel by viewModels()
    private lateinit var binding: FragmentGalleryBinding
    private val imageAdapter by lazy { createImageAdapter() }
    private val permissionRequestCode = 100

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentGalleryBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        checkPermissionsAndLoad()
        observeViewModel()
    }

    private fun createImageAdapter() = ImageAdapter(
        onLongClick = { position -> viewModel.images.value?.get(position)?.let(::navigateToEdit) },
        onClick = ::navigateToDetail
    )

    private fun setupRecyclerView() {
        binding.rView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = imageAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.images.observe(viewLifecycleOwner) { items ->
            imageAdapter.updateItems(items)
            binding.rView.post { binding.rView.requestLayout() }
        }
    }

    private fun checkPermissionsAndLoad() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (hasPermission(permission)) {
            viewModel.loadImages()
        } else {
            requestPermissions(arrayOf(permission), permissionRequestCode)
        }
    }

    private fun hasPermission(permission: String) =
        ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED

    @Deprecated("Deprecated in Fragment")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == permissionRequestCode && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            viewModel.loadImages()
        }
    }

    private fun navigateToEdit(item: ImageItem) {
        findNavController().navigate(
            GalleryFragmentDirections.actionToEdit(item.mediaId, item.description)
        )
        setupResultListener()
    }

    private fun navigateToDetail(item: ImageItem) {
        findNavController().navigate(
            GalleryFragmentDirections.actionGalleryToDetail(item.uri.toString(), item.description)
        )
    }

    private fun setupResultListener() {
        setFragmentResultListener(EditDescriptionFragment.REQUEST_KEY) { _, bundle ->
            bundle.getLong("mediaId").takeIf { it != -1L }?.let { mediaId ->
                bundle.getString(EditDescriptionFragment.DESCRIPTION_KEY)?.let { description ->
                    viewModel.updateDescription(mediaId, description)
                }
            }
        }
    }
}