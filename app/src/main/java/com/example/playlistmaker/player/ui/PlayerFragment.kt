package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.dpToPx
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val track: Track by lazy {
        val trackJson = requireArguments().getString(ARG_TRACK_JSON) ?: ""
        Gson().fromJson(trackJson, Track::class.java)
    }

    private val playerViewModel: PlayerViewModel by viewModel {
        parametersOf(track.previewUrl)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTrackInfo()

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
        }

        binding.ibPlayButton.setOnClickListener { playerViewModel.onPlayButtonClicked() }

        binding.toolbarButtonBack.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initTrackInfo() {
        binding.tvTrackTitle.text = track.trackName
        binding.tvArtistName.text = track.artistName
        binding.tvTrackDuration.text = track.trackDuration
        binding.tvCollectionName.text = track.collectionName
        binding.tvReleaseDate.text = track.trackReleaseYear
        binding.tvPrimaryGenreName.text = track.primaryGenreName
        binding.tvCountry.text = track.country

        Glide.with(requireContext()).load(track.coverArtworkUrl).apply(
            RequestOptions().placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(requireContext().dpToPx(8f)))
        ).into(binding.ivCoverArtwork)
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

    override fun onPause() {
        super.onPause()
        if (playerViewModel.screenState.value?.playerState == PlayerViewModel.PlayerState.PLAYING) {
            playerViewModel.onPlayButtonClicked()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_TRACK_JSON = "track_json"

        fun createArgs(track: Track): Bundle = bundleOf(ARG_TRACK_JSON to Gson().toJson(track))
    }
}
