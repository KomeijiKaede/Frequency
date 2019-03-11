package net.teamfruit.frequency.util

import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchResult

class DataAPIAccess {
    private val httpTransport = NetHttpTransport.Builder().build()
    private val jsonFactory = JacksonFactory.getDefaultInstance()
    private val youtube = YouTube.Builder(httpTransport, jsonFactory, HttpRequestInitializer {})
            .setApplicationName("Frequency")
            .build()

    fun search(query: String): List<SearchResult> {
        val search = youtube.search().list("id,snippet").apply {
            key = YOUTUBE_API_KEY //Put your Api key.
            q = query
            type = "video"
            fields = "items(id/videoId,snippet/channelTitle,snippet/title,snippet/thumbnails/high/url)"
            maxResults = 25
        }
        return search.execute().items
    }
}