package com.example.playlistmaker.library.ui

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAddPlaylistBinding
import com.example.playlistmaker.search.ui.TrackUi
import com.example.playlistmaker.search.ui.toDomain
import com.example.playlistmaker.utils.showCustomToast
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class AddPlaylistFragment : Fragment() {

    private val addPlaylistViewModel: AddPlaylistViewModel by viewModel()

    private var _binding: FragmentAddPlaylistBinding? = null
    private val binding get() = _binding!!

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                addPlaylistViewModel.onImageSelected(uri)
                Glide.with(requireContext()).load(uri).into(binding.playlistImage)
                addPlaylistViewModel.saveImageToPrivateStorage(requireContext())
            }
        }

    private val track: TrackUi? by lazy {
        arguments?.getParcelable(ARG_TRACK, TrackUi::class.java)
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
            val playlistName = addPlaylistViewModel.name

            viewLifecycleOwner.lifecycleScope.launch {
                val playlistId = addPlaylistViewModel.createPlaylist(requireContext())

                track?.let { trackUi ->
                    val track = trackUi.toDomain()
                    addPlaylistViewModel.addTrackToPlaylist(track, playlistId)
                }

                val message = if (track != null) {
                    "Добавлено в плейлист \"$playlistName\""
                } else {
                    "Плейлист \"$playlistName\" создан"
                }

                showCustomToast(binding.root.context, message)
                findNavController().navigateUp()
            }
        }
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

    companion object {
        const val ARG_TRACK = "track"
        fun createArgs(track: TrackUi) = bundleOf(ARG_TRACK to track)
    }
}
