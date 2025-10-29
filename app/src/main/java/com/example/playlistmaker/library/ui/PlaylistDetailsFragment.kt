package com.example.playlistmaker.library.ui

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.toUi
import com.example.playlistmaker.utils.debounce
import com.example.playlistmaker.utils.showCustomToast
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailsFragment : Fragment() {

    private val viewModel: PlaylistDetailsViewModel by viewModel()
    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private val trackList = ArrayList<Track>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var onTrackClick: (Track) -> Unit
    private lateinit var onTrackLongClick: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val playlistBottomSheetBinding = binding.playlistBottomsheet

        val playlistId = arguments?.getInt(ARG_PLAYLIST_ID) ?: return

        onTrackClick = debounce(
            CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) { track ->
            val args = PlayerFragment.createArgs(track.toUi())
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_playerFragment, args
            )

        }

        onTrackLongClick = { track ->
            showDeleteConfirmationDialog(playlistId, track)
        }

        trackAdapter = TrackAdapter(
            trackList,
            onItemClick = { track -> onTrackClick(track) },
            onItemLongClick = { track -> onTrackLongClick(track) })
        binding.trackList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }

        viewModel.loadPlaylist(playlistId)

        viewModel.screenState.observe(viewLifecycleOwner) { screenState ->
            binding.title.text = screenState.playlistName ?: ""
            binding.description.text = screenState.description ?: ""
            screenState.imageFile?.let { binding.playlistImage.setImageURI(Uri.fromFile(it)) }

            screenState.imageFile?.let {
                playlistBottomSheetBinding.cover.setImageURI(
                    Uri.fromFile(
                        it
                    )
                )
            }
            playlistBottomSheetBinding.title.text = screenState.playlistName ?: ""
            playlistBottomSheetBinding.count.text = resources.getQuantityString(
                R.plurals.track_count, screenState.trackCount ?: 0, screenState.trackCount ?: 0
            )

            val minutes = resources.getQuantityString(
                R.plurals.minutes_count, screenState.minutes ?: 0, screenState.minutes ?: 0
            )
            val trackCount = resources.getQuantityString(
                R.plurals.track_count, screenState.trackCount ?: 0, screenState.trackCount ?: 0
            )
            val duration = "$minutes • $trackCount"
            binding.misc.text = duration

            trackList.clear()
            trackList.addAll(screenState.tracks)
            trackAdapter.notifyDataSetChanged()
        }

        viewModel.observeShareIntent().observe(viewLifecycleOwner) { intent ->
            startActivity(intent)
        }

        binding.toolbarButtonBack.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonShare.setOnClickListener { onShareClick() }

        binding.buttonOptions.setOnClickListener {
            toggleOptionsBottomSheet()
        }

        binding.shareTextView.setOnClickListener { onShareClick() }
        binding.editPlaylist.setOnClickListener { onEditPlaylistClick(playlistId) }
        binding.removePlaylist.setOnClickListener { onRemovePlaylistClick(playlistId) }
    }

    private fun showDeleteConfirmationDialog(playlistId: Int, track: Track) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.delete_track_dialogue))
            .setNegativeButton(getString(R.string.no_capitals)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.yes_capitals)) { dialog, _ ->
                lifecycleScope.launch {
                    viewModel.removeTrack(playlistId, track)
                    viewModel.loadPlaylist(playlistId)
                }
                dialog.dismiss()
            }.show()
    }

    private fun onShareClick() {
        if (trackList.isEmpty()) {
            showCustomToast(binding.root.context, getString(R.string.empty_tracklist_message))
            return
        }
        val currentState = viewModel.screenState.value ?: return
        val trackListText = currentState.tracks.mapIndexed { index, track ->
            "${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackDuration})"
        }.joinToString("\n")

        val trackCount = currentState.trackCount ?: 0
        val tracksCountText = resources.getQuantityString(
            R.plurals.track_count, trackCount, trackCount
        )

        val shareText = buildString {
            appendLine(currentState.playlistName ?: "")
            appendLine(currentState.description ?: "")
            appendLine(tracksCountText)
            appendLine()
            append(trackListText)
        }

        viewModel.sharePlaylist(shareText)
    }

    private fun toggleOptionsBottomSheet() {
        if (binding.trackList.isVisible) {
            binding.trackList.isVisible = false
            binding.optionsBottomsheet.isVisible = true
        } else {
            binding.trackList.isVisible = true
            binding.optionsBottomsheet.isVisible = false
        }
    }

    private fun onRemovePlaylistClick(playlistId: Int) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString((R.string.delete_playlist_dialogue)))
            .setNegativeButton(getString(R.string.no_capitals)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.yes_capitals)) { dialog, _ ->
                lifecycleScope.launch {
                    viewModel.removePlaylist(playlistId)
                    findNavController().navigateUp()
                }
            }.show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun onEditPlaylistClick(playlistId: Int) {
        val args = EditPlaylistFragment.createArgs(playlistId)
        findNavController().navigate(
            R.id.action_playlistDetailsFragment_to_editPlaylistFragment, args
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 200L
        private const val ARG_PLAYLIST_ID = "playlist_id"
    }
}
