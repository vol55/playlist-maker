package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {
    private val iTunesBaseUrl = " https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private val tracks = ArrayList<Track>()
    private val historyTracks = ArrayList<Track>()

    private var isClickAllowed = true

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter

    private lateinit var noNetworkView: LinearLayout
    private lateinit var noResultsView: LinearLayout

    private var searchFieldValue: String = ""

    private lateinit var history: SearchHistory
    private lateinit var rvHistory: RecyclerView

    private lateinit var rvTrackList: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        progressBar = findViewById(R.id.progressBar)

        history = SearchHistory(this)
        historyTracks.addAll(history.getTracks())

        noNetworkView = findViewById(R.id.llNoNetwork)
        noResultsView = findViewById(R.id.llNoResults)

        rvHistory = findViewById(R.id.rvTrackHistoryList)
        trackHistoryAdapter = TrackAdapter(historyTracks) { track ->
            if (clickDebounce()) {
                startPlayerActivity(track)
            }
        }
        rvHistory.adapter = trackHistoryAdapter

        rvTrackList = findViewById(R.id.rvTrackList)
        trackAdapter = TrackAdapter(tracks) { track ->
            if (clickDebounce()) {
                history.saveTrack(track)
                historyTracks.clear()
                historyTracks.addAll(history.getTracks())
                trackHistoryAdapter.notifyDataSetChanged()
                startPlayerActivity(track)
            }
        }
        rvTrackList.adapter = trackAdapter

        val toolbar = findViewById<MaterialToolbar>(R.id.search_activity_toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val historyView = findViewById<LinearLayout>(R.id.llSearchHistory)

        val clearButton = findViewById<ImageView>(R.id.clear_button)
        val clearHistoryButton = findViewById<Button>(R.id.bvClearTrackHistory)


        val searchInput = findViewById<EditText>(R.id.input_field)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchFieldValue = s.toString()
                clearButton.isVisible = !s.isNullOrEmpty()
                historyView.isVisible =
                    searchInput.hasFocus() && s?.isEmpty() == true && historyTracks.isNotEmpty()
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        if (savedInstanceState != null) {
            searchFieldValue = savedInstanceState.getString(SEARCH_FIELD_VALUE, "")
            searchInput.setText(searchFieldValue)
        }

        val retryButton = findViewById<Button>(R.id.bvNoNetworkRetry)
        retryButton.setOnClickListener { showSearchResults(searchInput.text.toString()) }

        searchInput.addTextChangedListener(textWatcher)
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handler.removeCallbacks(searchRunnable)
                showSearchResults(searchInput.text.toString())
            }
            false
        }
        searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchInput.text.isEmpty() && historyTracks.isNotEmpty()) {
                historyTracks.clear()
                historyTracks.addAll(history.getTracks())
                trackHistoryAdapter.notifyDataSetChanged()
                historyView.isVisible = true
            } else {
                historyView.isVisible = false
            }
        }

        clearHistoryButton.setOnClickListener {
            historyView.isVisible = false
            history.clear()
            historyTracks.clear()
            trackHistoryAdapter.notifyDataSetChanged()
        }

        clearButton.setOnClickListener {
            searchInput.setText("")
            hideKeyboard(searchInput)
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            noNetworkView.isVisible = false
            noResultsView.isVisible = false
        }

        searchInput.requestFocus()
    }

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { showSearchResults(searchFieldValue) }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun showSearchResults(searchQuery: String) {
        if (searchQuery.isEmpty()) {
            return
        }
        tracks.clear()
        noNetworkView.isVisible = false
        noResultsView.isVisible = false
        progressBar.isVisible = true

        iTunesService.search(searchQuery)
            .enqueue(object : Callback<TracksResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    tracks.clear()
                    if (response.code() == 200) {
                        val results = response.body()?.results
                        if (!results.isNullOrEmpty()) {
                            Log.d("DEBUG_TAG", results[0].toString())
                            tracks.addAll(
                                results.map { track ->
                                    Track(
                                        track.trackId,
                                        track.trackName,
                                        track.artistName,
                                        track.trackTimeMillis,
                                        track.artworkUrl100,
                                        track.collectionName,
                                        track.releaseDate,
                                        track.primaryGenreName,
                                        track.country,
                                        track.previewUrl,
                                    )
                                }
                            )
                        } else {
                            progressBar.isVisible = false
                            noResultsView.isVisible = true
                        }
                        progressBar.isVisible = false
                        trackAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    tracks.clear()
                    progressBar.isVisible = false
                    noNetworkView.isVisible = true
                }
            })
    }

    private fun startPlayerActivity(track: Track) {
        val playerIntent = Intent(this, PlayerActivity::class.java)
        playerIntent.putExtra("track", track)
        startActivity(playerIntent)
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_FIELD_VALUE, searchFieldValue)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    companion object {
        private const val SEARCH_FIELD_VALUE = "SEARCH_STRING"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
