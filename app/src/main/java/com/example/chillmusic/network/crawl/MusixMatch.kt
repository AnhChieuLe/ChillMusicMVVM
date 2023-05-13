package com.example.chillmusic.network.crawl

import com.example.chillmusic.constant.log
import com.example.chillmusic.network.crawl.model.Track
import com.example.chillmusic.model.Song
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*

object MusixMatch : LyricsResource() {
    private const val baseUrl = "https://www.musixmatch.com"
    private const val search = "/search"
    private const val tracks = "/tracks"


    override val name: String get() = "MusixMatch"

    override suspend fun search(name: String): List<Track> {
        val url = baseUrl + search + "/" + name.encode() + tracks
        val document = Jsoup
            .connect(url)
            .userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
            .get()
            .outputSettings(setting)

        val trackList = document.getElementsByClass("track-card")

        val list = trackList.map {
            val albumArts = it.getElementsByClass("media-card-picture").select("img").attr("srcset").split(",")

            val maps = albumArts.map {str ->
                str.trim().split(" ")[0]
            }

            Track (
                title = it.getElementsByClass("title").tagName("span").text(),
                artist = it.getElementsByClass("artist").text(),
                url = baseUrl + it.getElementsByClass("title").attr("href"),
                albumArtUrl = maps[0]
            )
        }
        return list
    }

    override suspend fun getLyrics(track: Track): String{
        val document = Jsoup.connect(track.url).get().outputSettings(setting)

        val elements = document.getElementsByClass("lyrics__content__ok")
        elements.addAll(document.getElementsByClass("lyrics__content__error"))
        elements.addAll(document.getElementsByClass("lyrics__content__warning"))

        val lyrics = elements.textNodes().joinToString()
        if(lyrics.isEmpty())    throw IOException("Lyrics not found")
        return lyrics
    }
}