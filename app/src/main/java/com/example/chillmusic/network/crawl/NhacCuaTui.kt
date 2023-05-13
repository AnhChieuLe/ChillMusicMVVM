package com.example.chillmusic.network.crawl

import com.example.chillmusic.constant.log
import com.example.chillmusic.network.crawl.model.Track
import org.jsoup.Jsoup
import java.io.IOException


object NhacCuaTui : LyricsResource() {
    private const val baseUrl = "https://www.nhaccuatui.com"
    private const val search = "/tim-kiem"
    private const val track = "/bai-hat?"

    override val name: String get() = "Nhạc của tui"

    override suspend fun search(name: String): List<Track> {
        val q = "q=" + name.encode()
        val url = baseUrl + search + track + q

        log(url)

        val document = getDocument(url)
        val trackList = document.getElementsByClass("sn_search_single_song")

        return trackList.map {
            val imageCard = it.getElementsByClass("thumb").select("img")
            val albumArt = imageCard.attr("data-src")
            val title = it.getElementsByClass("title_song").select("a").attr("title")
            val artist = it.getElementsByClass("name_singer").textNodes().joinToString()
            val trackUrl = it.select("a").attr("href")

            Track(
                title = title,
                artist =  artist,
                url = trackUrl,
                albumArtUrl = albumArt
            )
        }.take(6)
    }

    override suspend fun getLyrics(track: Track): String {
        val document = getDocument(track.url)

        val container = document.getElementById("divLyric") ?: return ""

        val empty = container.select("a")
        if(empty.isNotEmpty()) throw IOException("Lyrics not found")

        return container.textNodes().toText()
    }
}