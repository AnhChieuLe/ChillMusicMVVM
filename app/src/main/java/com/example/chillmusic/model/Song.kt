package com.example.chillmusic.model

import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class Song(
    val id: Long = 0L,
    val path: String = "",
    var title: String = "",
    var artist: String = "",
    var artistId: Long = 0L,
    var date: Long = 0,
    var duration: Int = 0,
    var genre: String = "",
    var album: String = "",
    var albumId: Long = 0L,
    var bitrate: Long = 0,
    var tracks: Int = 0,
    var composer: String = "",
    var writer: String = "",
    var size: Long = 0,
) : Parcelable{
    val uri: Uri get() = ContentUris.withAppendedId(
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
        id
    )

    @IgnoredOnParcel
    var liveAlbumArt : MutableLiveData<Bitmap> = MutableLiveData()

    val largeAlbumArt: Bitmap?
        get() {
            val metadata = MediaMetadataRetriever()
            try{
                metadata.setDataSource(path)
                val byteArray = metadata.embeddedPicture ?: return null
                return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            }catch (e: IllegalArgumentException){
                e.printStackTrace()
                return null
            }
        }

    fun extractMetaData() {
        val metadata = MediaMetadataRetriever()
        try {
            metadata.setDataSource(path)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return
        }

        val DURATION = MediaMetadataRetriever.METADATA_KEY_DURATION
        val TITLE = MediaMetadataRetriever.METADATA_KEY_TITLE
        val ALBUM = MediaMetadataRetriever.METADATA_KEY_ALBUM
        val ARTIST = MediaMetadataRetriever.METADATA_KEY_ARTIST
        val GENRE = MediaMetadataRetriever.METADATA_KEY_GENRE
        val BITRATE = MediaMetadataRetriever.METADATA_KEY_BITRATE
        val DATE = MediaMetadataRetriever.METADATA_KEY_DATE
        val TRACKS = MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS
        val COMPOSER = MediaMetadataRetriever.METADATA_KEY_COMPOSER
        val WRITER = MediaMetadataRetriever.METADATA_KEY_WRITER
        val file = File(path)

        duration = metadata.extractMetadata(DURATION)?.toInt() ?: 0
        title = metadata.extractMetadata(TITLE).toString()
        artist = metadata.extractMetadata(ARTIST).toString()
        album = metadata.extractMetadata(ALBUM).toString()
        genre = metadata.extractMetadata(GENRE).toString()
        bitrate = metadata.extractMetadata(BITRATE)?.toLong() ?: 0L
        date = file.lastModified()
        size = file.length()
        tracks = metadata.extractMetadata(TRACKS)?.toInt() ?: 0
        composer = metadata.extractMetadata(COMPOSER).toString()
        writer = metadata.extractMetadata(WRITER).toString()
    }
    override fun equals(other: Any?) = (other as Song?)?.id == id
    override fun hashCode() = id.hashCode()
}
