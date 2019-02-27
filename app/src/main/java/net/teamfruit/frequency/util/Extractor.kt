package net.teamfruit.frequency.util

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import net.teamfruit.frequency.database.Base
import java.net.URLDecoder

data class AdaptiveFormat (
    val url: String,
    val mimeType: String,
    val itag: Int,
    val averageBitrate: Int,
    val audioSampleRate: String,
    val contentsLength: String
)

data class PlayabilityStatus (
    val status: String
)

data class StreamingData (
    val adaptiveFormats: List<AdaptiveFormat>
)

data class PlayerResponse (
    val streamingData: StreamingData,
    val playabilityStatus: PlayabilityStatus,
    val videoDetails: VideoDetails
)

data class VideoDetails (
        val title: String,
        val author: String,
        val thumbnail: Thumbnails
)

data class Thumbnails (
        val thumbnails: List<Thumbnail>
)

data class Thumbnail (
        val url: String,
        val width: String,
        val height: String
)

object Extractor {
    private fun getYoutubeUriByVid(youtubeUri: String): String {
        return when {
            Regex("""^https?://(www\.)?(m\.)?youtube\.com""").containsMatchIn(youtubeUri) -> {
                val videoIdResult = Regex("""v=[\w\-]{11}""").find(youtubeUri)
                if (videoIdResult === null) throw java.lang.IllegalArgumentException("Are you kidding?")
                videoIdResult.value.substring(2)
            }
            Regex("""^https?://(www\.)?(m\.)?youtu.be/""").containsMatchIn(youtubeUri) -> {
                youtubeUri.substring(youtubeUri.length - 11, youtubeUri.length)
            }
            else -> throw java.lang.IllegalArgumentException("Are you kidding?")
        }
    }

    fun getStringValueByUrlParameters(urlParametersStr: String, parameterName: String) : String {
        val parameters : List<String> = urlParametersStr.split('&')

        for(parameter in parameters) {
            val parsedParameter = parameter.split('=')

            if(parsedParameter[0] == parameterName)
                return parsedParameter[1]
        }

        throw IndexOutOfBoundsException("Are you kidding?")
    }

    fun getBestQualityAudioFormatByAdaptiveFormats(adaptiveFormats: List<AdaptiveFormat>) : AdaptiveFormat? {
        var maxItag : Int = 0
        var bestQualityAudioFormat : AdaptiveFormat? = null

        for (adaptiveFormat in adaptiveFormats) {
            if(!adaptiveFormat.mimeType.startsWith("audio"))
                continue

            if(adaptiveFormat.itag < maxItag)
                continue

            maxItag = adaptiveFormat.itag
            bestQualityAudioFormat = adaptiveFormat
        }

        return bestQualityAudioFormat
    }

    private fun getThumbnail(res: PlayerResponse) : String {
        var index = 0
        for ((x, item) in res.videoDetails.thumbnail.thumbnails.withIndex()) {
            if (Integer.parseInt(item.width) < index)
                continue
            index = x
        }
        return res.videoDetails.thumbnail.thumbnails[index].url
    }

    fun addInfo(url: String, base: Base) {
        val gson = Gson()
        val id = getYoutubeUriByVid(url)
        Fuel.get("https://www.youtube.com/get_video_info?video_id=$id")
                .response { _, response, result ->
                    when (result) {
                        is Result.Success -> {
                            val parsedJson: PlayerResponse
                            try {
                                parsedJson = gson.fromJson(
                                        URLDecoder.decode(getStringValueByUrlParameters(
                                                response.toString(),
                                                "player_response"),
                                                "UTF-8"),
                                        PlayerResponse::class.java
                                )
                            }catch (e: JsonSyntaxException) { return@response }

                            if (parsedJson.playabilityStatus.status == "UNPLAYABLE") return@response

                            val format = getBestQualityAudioFormatByAdaptiveFormats(
                                    parsedJson.streamingData.adaptiveFormats) ?: return@response

                            AddAsyncTask(base).execute(Base.create(parsedJson.videoDetails.title, id, getThumbnail(parsedJson)))
                        }

                        is Result.Failure -> return@response
                    }
                }
    }
}