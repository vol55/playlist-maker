package com.example.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.library.ui.AddPlaylistFragment
import com.example.playlistmaker.search.ui.TrackUi
import com.example.playlistmaker.utils.showCustomToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val track: TrackUi? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(ARG_TRACK, TrackUi::class.java)
        } else {
            @Suppress("DEPRECATION") requireArguments().getParcelable(ARG_TRACK) as? TrackUi
        }
    }

    private val playerViewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    private lateinit var playlistsAdapter: PlaylistsAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (track == null) {
            findNavController().navigateUp()
            return
        }

        binding.tvTrackTitle.text = track?.trackName
        binding.tvArtistName.text = track?.artistName
        binding.tvTrackDuration.text = track?.trackDuration
        binding.tvCollectionName.text = track?.collectionName
        binding.tvReleaseDate.text = track?.trackReleaseYear
        binding.tvPrimaryGenreName.text = track?.primaryGenreName
        binding.tvCountry.text = track?.country

        Glide.with(requireContext()).load(track?.coverArtworkUrl).apply(
            RequestOptions().placeholder(R.drawable.placeholder)
        ).into(binding.ivCoverArtwork)

        initBottomSheet()

        binding.ibPlayButton.setOnClickListener { playerViewModel.onPlayButtonClicked() }
        binding.likeButton.setOnClickListener { playerViewModel.onFavoriteClicked() }
        binding.toolbarButtonBack.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.leftButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        playerViewModel.screenState.observe(viewLifecycleOwner) { state ->
            when (state.playerState) {
                PlayerViewModel.PlayerState.DEFAULT -> {
                    binding.ibPlayButton.isEnabled = false
                    setPlayIcon()
                }

                PlayerViewModel.PlayerState.PREPARED -> {
                    binding.ibPlayButton.isEnabled = true
                    setPlayIcon()
                }

                PlayerViewModel.PlayerState.PLAYING -> {
                    binding.ibPlayButton.isEnabled = true
                    setPauseIcon()
                }

                PlayerViewModel.PlayerState.PAUSED -> {
                    binding.ibPlayButton.isEnabled = true
                    setPlayIcon()
                }
            }

            binding.tvCurrentTime.text = state.progressTime
            setLikeIcon(state.isFavorite)

            playlistsAdapter.updatePlaylists(state.playlists)

        }
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        playlistsAdapter = PlaylistsAdapter { playlist ->
            track?.let { currentTrack ->
                playerViewModel.addTrackToPlaylist(playlist.id) { added ->
                    val message: String
                    if (added) {
                        message = getString(R.string.player_track_added, playlist.title)
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    } else {
                        message = getString(R.string.player_track_exists, playlist.title)
                    }
                    showCustomToast(requireContext(), message)
                }
            }
        }

        binding.playlists.adapter = playlistsAdapter

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.visibility =
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.newPlaylistButton.setOnClickListener {
            track?.let { currentTrack ->
                findNavController().navigate(
                    R.id.action_playerFragment_to_addPlaylistFragment,
                    AddPlaylistFragment.createArgs(currentTrack)
                )
            }
        }
    }

    private fun setPlayIcon() {
        val value = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.play, value, true)
        binding.ibPlayButton.setImageResource(value.resourceId)
    }

    private fun setPauseIcon() {
        val value = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.pause, value, true)
        binding.ibPlayButton.setImageResource(value.resourceId)
    }

    private fun setLikeIcon(isFavorite: Boolean) {
        val drawableRes = if (isFavorite) R.drawable.liked else R.drawable.like
        binding.likeButton.setImageResource(drawableRes)
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.pausePlayerIfNeeded()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_TRACK = "track"
        fun createArgs(track: TrackUi): Bundle = bundleOf(ARG_TRACK to track)
    }
}
