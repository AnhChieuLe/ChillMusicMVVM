package com.example.chillmusic.network.crawl

import com.example.chillmusic.network.crawl.model.Track
import com.example.chillmusic.model.Song
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.TextNode
import java.net.URLEncoder
import java.util.*

abstract class LyricsResource {
    protected val setting: Document.OutputSettings = Document.OutputSettings().prettyPrint(false)
    private val agent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"


    abstract suspend fun search(name: String): List<Track>
    abstract suspend fun getLyrics(track: Track): String
    abstract val name: String

    fun getDocument(url: String) : Document{
        return  Jsoup
            .connect(url)
            .userAgent(agent)
            .get()
            .outputSettings(setting)
    }

    fun filter(tracks: List<Track>, song: Song): List<Track> {
        val sort = Comparator<Track> { t1, t2 -> t1.title.length - t2.title.length }.thenBy { it.artist.compareTo(song.artist) }
        return tracks.filter { it.title.compare(song.apiTitle) }.sortedWith(sort)
    }

    companion object {
        fun String.encode(): String {
            return URLEncoder.encode(this.lowercase(), "UTF-8")
        }

        fun String.compare(name: String): Boolean {
            val lowerTitle = this.lowercase(Locale.getDefault())
            val lowerName = name.lowercase(Locale.getDefault())
            return lowerTitle.contains(lowerName)
        }

        fun List<TextNode>.toText(): String{
            val lyrics = StringBuilder("")
            this.forEach {
                lyrics.append(it.text().trim() + "\n")
            }
            return lyrics.toString()
        }
    }
}

