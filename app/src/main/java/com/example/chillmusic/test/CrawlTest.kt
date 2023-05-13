package com.example.chillmusic.test

import org.jsoup.Jsoup
import java.lang.StringBuilder

fun main() {
    val url = "https://www.musixmatch.com/lyrics/B%C3%B9i-Anh-Tu%E1%BA%A5n/Thu%E1%BA%ADn-Theo-%C3%9D-Tr%E1%BB%9Di?utm_source=application&utm_campaign=api&utm_medium=H%E1%BB%8Dc+vi%E1%BB%87n+C%C3%B4ng+ngh%E1%BB%87+B%C6%B0u+ch%C3%ADnh+Vi%E1%BB%85n+Th%C3%B4ng%3A1409622981503"
    getLyrics(url)
}

fun getLyrics(url: String){
    val documents = Jsoup.connect(url).get()
    val elements = documents.getElementsByClass("lyrics__content__ok")
    val lyrics: StringBuilder = StringBuilder("")
    elements.forEach {
        lyrics.append(it.text())
    }
    println(lyrics)
}