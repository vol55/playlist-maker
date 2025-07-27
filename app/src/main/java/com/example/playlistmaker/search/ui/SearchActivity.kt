package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val tracks = ArrayList<Track>()
    private val historyTracks = ArrayList<Track>()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter

    private lateinit var noNetworkView: LinearLayout
    private lateinit var noResultsView: LinearLayout
    private lateinit var progressBar: ProgressBar

    private lateinit var searchInput: EditText
    private lateinit var clearButton: ImageView
    private lateinit var historyView: LinearLayout

    private lateinit var rvHistory: RecyclerView
    private lateinit var rvTrackList: RecyclerView

    private val viewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressBar = findViewById(R.id.progressBar)
        noNetworkView = findViewById(R.id.llNoNetwork)
        noResultsView = findViewById(R.id.llNoResults)
        rvHistory = findViewById(R.id.rvTrackHistoryList)
        rvTrackList = findViewById(R.id.rvTrackList)
        searchInput = findViewById(R.id.input_field)
        clearButton = findViewById(R.id.clear_button)
        historyView = findViewById(R.id.llSearchHistory)

        trackAdapter = TrackAdapter(tracks) { track ->
            if (viewModel.clickDebounce()) {
                viewModel.saveTrackToHistory(track)
                viewModel.onTrackClicked(track)
            }
        }

        trackHistoryAdapter = TrackAdapter(historyTracks) { track ->
            if (viewModel.clickDebounce()) {
                viewModel.onTrackClicked(track)
            }
        }

        rvTrackList.adapter = trackAdapter
        rvHistory.adapter = trackHistoryAdapter

        val toolbar = findViewById<MaterialToolbar>(R.id.search_activity_toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (savedInstanceState != null) {
            val savedQuery = savedInstanceState.getString(SEARCH_FIELD_VALUE, "")
            searchInput.setText(savedQuery)
            viewModel.setSearchFieldValue(savedQuery)
        }

        viewModel.observeOpenPlayer().observe(this) { track ->
            startPlayerActivity(track)
        }

        viewModel.observeState().observe(this) { state ->
            when (state) {
                is SearchState.UpdateSearchHistory -> {
                    historyTracks.clear()
                    historyTracks.addAll(state.history)
                    trackHistoryAdapter.notifyDataSetChanged()
                    historyView.isVisible = historyTracks.isNotEmpty()
                }

                is SearchState.Content -> {
                    tracks.clear()
                    tracks.addAll(state.tracks)
                    trackAdapter.notifyDataSetChanged()
                    hideEverythingExcept(rvTrackList)
                }

                is SearchState.Loading -> {
                    hideEverythingExcept(progressBar)
                }

                is SearchState.NoResults -> {
                    hideEverythingExcept(noResultsView)
                }

                is SearchState.NotConnected -> {
                    hideEverythingExcept(noNetworkView)
                }

                else -> {
                    hideEverythingExcept(historyView)
                    historyView.isVisible = historyTracks.isNotEmpty()
                }
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                clearButton.isVisible = query.isNotEmpty()
                viewModel.onSearchQueryChanged(query)
                historyView.isVisible =
                    searchInput.hasFocus() && query.isEmpty() && historyTracks.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        searchInput.addTextChangedListener(textWatcher)

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onSearchQueryChanged(searchInput.text.toString(), 0)
                hideKeyboard(searchInput)
                true
            } else {
                false
            }
        }

        searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchInput.text.isEmpty() && historyTracks.isNotEmpty()) {
                viewModel.updateHistory()
                historyView.isVisible = true
            } else {
                historyView.isVisible = false
            }
        }

        findViewById<Button>(R.id.bvClearTrackHistory).setOnClickListener {
            historyView.isVisible = false
            viewModel.clearHistory()
        }

        clearButton.setOnClickListener {
            searchInput.setText("")
            hideKeyboard(searchInput)
            viewModel.onSearchQueryChanged("")
        }

        findViewById<Button>(R.id.bvNoNetworkRetry).setOnClickListener {
            viewModel.onSearchQueryChanged(searchInput.text.toString(), 0)
        }

        viewModel.updateHistory()
        searchInput.requestFocus()
    }

    private fun hideEverythingExcept(viewToShow: View) {
        val views = listOf(rvTrackList, progressBar, noNetworkView, noResultsView, historyView)
        views.forEach { it.isVisible = (it == viewToShow) }
    }

    private fun startPlayerActivity(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        val trackJson = Gson().toJson(track)
        intent.putExtra("TRACK_JSON_KEY", trackJson)
        startActivity(intent)
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_FIELD_VALUE, searchInput.text.toString())
    }

    companion object {
        private const val SEARCH_FIELD_VALUE = ""
    }
}
