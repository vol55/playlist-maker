package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val playlistsViewModel: PlaylistsViewModel by viewModel()
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private lateinit var playlistsAdapter: PlaylistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistsAdapter = PlaylistsAdapter(mutableListOf())
        binding.playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRecyclerView.adapter = playlistsAdapter

        playlistsViewModel.screenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistsScreenState.Content -> {
                    binding.playlistsRecyclerView.visibility = View.VISIBLE
                    binding.noPlaylistsImage.visibility = View.GONE
                    binding.noPlaylistsText.visibility = View.GONE

                    playlistsAdapter.playlists.clear()
                    playlistsAdapter.playlists.addAll(state.playlists)
                    playlistsAdapter.notifyDataSetChanged()
                }

                is PlaylistsScreenState.Empty -> {
                    binding.playlistsRecyclerView.visibility = View.GONE
                    binding.noPlaylistsImage.visibility = View.VISIBLE
                    binding.noPlaylistsText.visibility = View.VISIBLE
                }
            }
        }

        binding.addPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_addPlaylistFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}
