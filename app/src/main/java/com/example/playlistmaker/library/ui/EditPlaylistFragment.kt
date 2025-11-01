package com.example.playlistmaker.library.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class EditPlaylistFragment : AddPlaylistFragment() {

    private val editPlaylistViewModel: EditPlaylistViewModel by viewModel()

    override val addPlaylistViewModel: AddPlaylistViewModel
        get() = editPlaylistViewModel

    private val playlistId: Int by lazy { requireArguments().getInt(ARG_PLAYLIST_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editPlaylistViewModel.initWithPlaylistId(playlistId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSave.text = getString(R.string.save)
        binding.toolbarButtonBack.title = getString(R.string.edit_button)
        binding.toolbarButtonBack.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.buttonSave.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                editPlaylistViewModel.editPlaylist(playlistId)
                editPlaylistViewModel.notifyPlaylistEdited(binding.root.context)
            }
        }

        editPlaylistViewModel.screenState.observe(viewLifecycleOwner) { state ->
            if (binding.playlistTitleInput.text.toString() != (state.name ?: "")) {
                binding.playlistTitleInput.setText(state.name ?: "")
            }
            if (binding.playlistDescriptionInput.text.toString() != (state.description ?: "")) {
                binding.playlistDescriptionInput.setText(state.description ?: "")
            }

            state.imageFile?.let { file ->
                Glide.with(requireContext()).load(file).centerCrop().into(binding.playlistImage)
            }
        }
    }

    override fun handleBackPressed() {
        findNavController().navigateUp()
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "playlist_id"

        fun createArgs(playlistId: Int) = bundleOf(ARG_PLAYLIST_ID to playlistId)
    }
}
