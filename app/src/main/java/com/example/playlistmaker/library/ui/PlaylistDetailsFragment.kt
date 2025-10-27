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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.toUi
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailsFragment : Fragment() {

    private val viewModel: PlaylistDetailsViewModel by viewModel()
    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private val trackList = ArrayList<Track>()
    private lateinit var trackAdapter: PlaylistDetailsTrackAdapter
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

        val playlistId = arguments?.getInt("playlistId") ?: return

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

        trackAdapter = PlaylistDetailsTrackAdapter(
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

        binding.toolbarButtonBack.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showDeleteConfirmationDialog(playlistId: Int, track: Track) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.delete_treck_dialogue))
            .setNegativeButton(getString(R.string.no_capitals)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.yes_capitals)) { dialog, _ ->
                lifecycleScope.launch {
                    viewModel.removeTrack(playlistId, track)
                }
                viewModel.loadPlaylist(playlistId)
                dialog.dismiss()
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 200L
    }
}
