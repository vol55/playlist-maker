package com.example.playlistmaker.library.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.library.data.db.PlaylistDao
import com.example.playlistmaker.library.data.db.PlaylistTracksDao
import com.example.playlistmaker.library.data.db.PlaylistWithTracks
import com.example.playlistmaker.library.data.db.mappers.toDomain
import com.example.playlistmaker.library.data.db.mappers.toEntity
import com.example.playlistmaker.library.data.db.mappers.toPlaylistTrackEntity
import com.example.playlistmaker.library.domain.api.PlaylistsRepository
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream


class PlaylistsRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTracksDao: PlaylistTracksDao,
    private val context: Context
) : PlaylistsRepository {

    override suspend fun addPlaylist(playlist: Playlist): Int {
        val id = playlistDao.insertPlaylist(playlist.toEntity())
        return id.toInt()
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addTrack(track: Track, playlistId: Int) {
        playlistTracksDao.insertTrack(track.toPlaylistTrackEntity(playlistId))
        val count = playlistTracksDao.getTrackCountForPlaylist(playlistId)
        playlistDao.updateTrackCount(playlistId, count)
    }

    override suspend fun isTrackInPlaylist(playlistId: Int, trackId: Int): Boolean {
        return playlistTracksDao.isTrackInPlaylist(playlistId, trackId)
    }

    override fun saveCover(uri: Any): File? {
        val filePath = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlists"
        )
        if (!filePath.exists()) filePath.mkdirs()

        val file = File(filePath, "playlist_cover_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri as Uri).use { input ->
            FileOutputStream(file).use { output ->
                BitmapFactory.decodeStream(input).compress(Bitmap.CompressFormat.JPEG, 90, output)
            }
        }
        return file
    }

    override fun getPlaylistsWithTracks(): Flow<List<PlaylistWithTracks>> {
        return playlistDao.getPlaylistsWithTracks()
    }
}