package com.example.playlistmaker.library.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAddPlaylistBinding
import com.example.playlistmaker.utils.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddPlaylistFragment : Fragment() {

    private val addPlaylistViewModel: AddPlaylistViewModel by viewModel()

    private var _binding: FragmentAddPlaylistBinding? = null
    private val binding get() = _binding!!

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                addPlaylistViewModel.onImageSelected(uri)
                showImage(uri)
                addPlaylistViewModel.saveImageToPrivateStorage(requireContext())
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.playlistImage.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.toolbarButtonBack.setNavigationOnClickListener { handleBackPressed() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { handleBackPressed() }

        addPlaylistViewModel.screenState.observe(viewLifecycleOwner) { state ->
            val isEnabled = state is AddPlaylistScreenState.ValidName
            binding.buttonSave.isEnabled = isEnabled
            binding.buttonSave.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(), if (isEnabled) R.color.yandex_blue else R.color.yandex_gray
            )
        }

        binding.playlistTitleInput.addTextChangedListener {
            addPlaylistViewModel.onNameChanged(it.toString())
        }

        binding.playlistDescriptionInput.addTextChangedListener {
            addPlaylistViewModel.onDescriptionChanged(it.toString())
        }

        binding.buttonSave.setOnClickListener {
            val playlistName = addPlaylistViewModel.getName()
            Toast.makeText(
                requireContext(), "Плейлист \"$playlistName\" создан", Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }
    }

    private fun showImage(uri: Uri) {
        Glide.with(requireContext()).load(uri)
            .transform(RoundedCorners(requireContext().dpToPx(8f))).into(binding.playlistImage)
    }

    private fun handleBackPressed() {
        if (addPlaylistViewModel.hasUnsavedChanges()) {
            showExitConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showExitConfirmationDialog() {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Завершить") { dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
