package com.example.playlistmaker.search.ui

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val tracks = ArrayList<Track>()
    private val historyTracks = ArrayList<Track>()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter

    private val viewModel: SearchViewModel by viewModel()

    private var currentQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onTrackClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) { track ->
            viewModel.saveTrackToHistory(track)
            openPlayerFragment(track)
        }

        trackAdapter = TrackAdapter(tracks) { track -> onTrackClickDebounce(track) }
        trackHistoryAdapter = TrackAdapter(historyTracks) { track -> onTrackClickDebounce(track) }

        binding.rvTrackList.adapter = trackAdapter
        binding.llSearchHistory.rvTrackHistoryList.adapter = trackHistoryAdapter

        savedInstanceState?.getString(SEARCH_FIELD_VALUE)?.let { savedQuery ->
            binding.inputField.setText(savedQuery)
            currentQuery = savedQuery
            viewModel.setSearchFieldValue(savedQuery)
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchState.History -> {
                    historyTracks.clear()
                    historyTracks.addAll(state.history)
                    trackHistoryAdapter.notifyDataSetChanged()
                    binding.llSearchHistory.root.isVisible = historyTracks.isNotEmpty()
                }

                is SearchState.Results -> {
                    tracks.clear()
                    tracks.addAll(state.tracks)
                    trackAdapter.notifyDataSetChanged()
                    hideEverythingExcept(binding.rvTrackList)
                }

                is SearchState.Loading -> hideEverythingExcept(binding.progressBar)
                is SearchState.NoResults -> hideEverythingExcept(binding.llNoResults.root)
                is SearchState.NotConnected -> hideEverythingExcept(binding.llNoNetwork.root)
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                currentQuery = query
                binding.clearButton.isVisible = query.isNotEmpty()
                viewModel.onSearchQueryChanged(query)
                binding.llSearchHistory.root.isVisible =
                    binding.inputField.hasFocus() && query.isEmpty() && historyTracks.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.inputField.addTextChangedListener(textWatcher)

        binding.inputField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.performSearch(binding.inputField.text.toString())
                hideKeyboard()
                true
            } else false
        }

        binding.inputField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.inputField.text.isEmpty() && historyTracks.isNotEmpty()) {
                viewModel.updateHistory()
                binding.llSearchHistory.root.isVisible = true
            } else {
                binding.llSearchHistory.root.isVisible = false
            }
        }

        binding.llSearchHistory.bvClearTrackHistory.setOnClickListener {
            binding.llSearchHistory.root.isVisible = false
            viewModel.clearHistory()
        }

        binding.clearButton.setOnClickListener {
            binding.inputField.setText("")
            hideKeyboard()
            viewModel.onSearchQueryChanged("")
        }

        binding.llNoNetwork.bvNoNetworkRetry.setOnClickListener {
            viewModel.performSearch(binding.inputField.text.toString())
        }

        viewModel.updateHistory()
        binding.inputField.requestFocus()
    }

    private fun hideEverythingExcept(viewToShow: View) {
        val views = listOf(
            binding.rvTrackList,
            binding.progressBar,
            binding.llNoNetwork.root,
            binding.llNoResults.root,
            binding.llSearchHistory.root
        )
        views.forEach { it.isVisible = (it == viewToShow) }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun openPlayerFragment(track: Track) {
        val args = PlayerFragment.createArgs(track.toUi())
        findNavController().navigate(R.id.action_searchFragment_to_playerFragment, args)
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputField.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_FIELD_VALUE, currentQuery)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val SEARCH_FIELD_VALUE = "search_field_value"
        private const val CLICK_DEBOUNCE_DELAY = 0L
    }
}
