package com.example.playlistmaker

import android.annotation.SuppressLint
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter

    private lateinit var noNetworkView: LinearLayout
    private lateinit var noResultsView: LinearLayout

    private lateinit var searchFieldValue: String

    private lateinit var history: SearchHistory
    private lateinit var rvHistory: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        history = SearchHistory(this)
        historyTracks.addAll(history.getTracks())

        noNetworkView = findViewById<LinearLayout>(R.id.llNoNetwork)
        noResultsView = findViewById<LinearLayout>(R.id.llNoResults)

        rvHistory = findViewById(R.id.rvTrackHistoryList)
        trackHistoryAdapter = TrackAdapter(historyTracks) {}
        rvHistory.adapter = trackHistoryAdapter

        val rvTrackList = findViewById<RecyclerView>(R.id.rvTrackList)
        trackAdapter = TrackAdapter(tracks) { track ->
            history.saveTrack(track)
            historyTracks.clear()
            historyTracks.addAll(history.getTracks())
            trackHistoryAdapter.notifyDataSetChanged()
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
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                historyView.visibility =
                    if (searchInput.hasFocus() && s?.isEmpty() == true && !historyTracks.isEmpty()) View.VISIBLE else View.GONE

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
                showSearchResults(searchInput.text.toString())
                true
            }
            false
        }
        searchInput.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && searchInput.text.isEmpty() && !historyTracks.isEmpty()) {
                historyTracks.clear()
                historyTracks.addAll(history.getTracks())
                trackHistoryAdapter.notifyDataSetChanged()
                historyView.visibility = View.VISIBLE
            } else {
                historyView.visibility = View.GONE
            }
        }

        clearHistoryButton.setOnClickListener {
            historyView.visibility = View.GONE
            history.clear()
            historyTracks.clear()
            trackHistoryAdapter.notifyDataSetChanged()
        }

        clearButton.setOnClickListener {
            searchInput.setText("")
            hideKeyboard(searchInput)
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            noNetworkView.visibility = View.GONE
            noResultsView.visibility = View.GONE
        }
    }

    private fun showSearchResults(searchQuery: String) {
        if (searchQuery.isEmpty()) {
            return
        }
        tracks.clear()
        noNetworkView.visibility = View.GONE
        noResultsView.visibility = View.GONE

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
                            tracks.addAll(
                                results.map { track ->
                                    Track(
                                        track.trackId,
                                        track.trackName,
                                        track.artistName,
                                        track.trackTimeMillis,
                                        track.artworkUrl100
                                    )
                                }
                            )
                        } else {
                            noResultsView.visibility = View.VISIBLE
                        }
                        trackAdapter.notifyDataSetChanged()
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    tracks.clear()
                    trackAdapter.notifyDataSetChanged()
                    noNetworkView.visibility = View.VISIBLE
                }
            })
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_FIELD_VALUE, searchFieldValue)
    }

    companion object {
        private const val SEARCH_FIELD_VALUE = "SEARCH_STRING"
    }
}
