package com.example.playlistmaker.library.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.toUi
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {
    private val favoritesViewModel: FavoritesViewModel by viewModel()
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var onTrackClick: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClick = debounce(
            CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) { track ->
            openPlayerFragment(track)
        }

        favoritesViewModel.favoriteTracks.observe(viewLifecycleOwner) { tracks ->
            binding.rvTrackList.isVisible = tracks.isNotEmpty()
            binding.llEmptyFavorites.isVisible = tracks.isEmpty()

            val adapter = TrackAdapter(tracks) { track -> onTrackClick(track) }
            binding.rvTrackList.adapter = adapter
        }

        favoritesViewModel.loadFavoriteTracks()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun openPlayerFragment(track: Track) {
        val args = PlayerFragment.createArgs(track.toUi())
        findNavController().navigate(R.id.action_libraryFragment_to_playerFragment, args)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoritesFragment()
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}
