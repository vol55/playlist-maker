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

    private val track: TrackUi? by lazy {
        arguments?.getParcelable(ARG_TRACK, TrackUi::class.java)
    }

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { addPlaylistViewModel.onImageSelected(it) }
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
            binding.buttonSave.isEnabled = state.isNameValid
            binding.buttonSave.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (state.isNameValid) R.color.yandex_blue else R.color.yandex_gray
            )

            state.imageFile?.let {
                Glide.with(requireContext()).load(it).centerCrop().into(binding.playlistImage)
                binding.playlistFrame.foreground = null
            }

            state.toastMessage?.let {
                showCustomToast(binding.root.context, it)
            }

            if (state.navigateUp) findNavController().navigateUp()
        }

        binding.playlistTitleInput.addTextChangedListener {
            addPlaylistViewModel.onNameChanged(it.toString())
        }

        binding.playlistDescriptionInput.addTextChangedListener {
            addPlaylistViewModel.onDescriptionChanged(it.toString())
        }

        binding.buttonSave.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val playlistId = addPlaylistViewModel.createPlaylist()
                track?.let { addPlaylistViewModel.addTrackToPlaylist(it.toDomain(), playlistId) }
                addPlaylistViewModel.notifyPlaylistCreated(binding.root.context, track)
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
            .setTitle(R.string.add_playlist_exit_title)
            .setMessage(R.string.add_playlist_exit_message)
            .setNegativeButton(R.string.add_playlist_exit_cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.add_playlist_exit_confirm) { dialog, _ ->
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
