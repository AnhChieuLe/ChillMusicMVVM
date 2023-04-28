package com.example.chillmusic.library

import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.enums.Sort
import com.example.chillmusic.enums.SortType
import com.example.chillmusic.model.Song

class SongComparator(
    var sortType: SortType = SortType.DECREASING,
    var sort: Sort = Sort.BY_DATE,
) : Comparator<Song> {
    override fun compare(song1: Song?, song2: Song?): Int {
        song1 ?: return 0
        song2 ?: return 0

        return if (sortType == SortType.INCREASING)
            when (sort) {
                Sort.BY_DATE -> (song1.id - song2.id).toInt()
                Sort.BY_NAME -> song1.title.compareTo(song2.title)
                Sort.BY_SIZE -> (song1.size - song2.size).toInt()
                Sort.BY_DURATION -> (song1.duration - song2.duration)
                Sort.BY_QUALITY -> (song1.bitrate - song2.bitrate).toInt()
            }
        else
            when (sort) {
                Sort.BY_DATE -> (song2.id - song1.id).toInt()
                Sort.BY_NAME -> song2.title.compareTo(song1.title)
                Sort.BY_SIZE -> (song2.size - song1.size).toInt()
                Sort.BY_DURATION -> (song2.duration - song1.duration)
                Sort.BY_QUALITY -> (song2.bitrate - song1.bitrate).toInt()
            }
    }
}