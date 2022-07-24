package com.ssong_develop.retrofitcallbackconvertproject

import android.webkit.WebStorage
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Characters(
    var page: Int = 0,
    /** The id of the character */
    @SerializedName("id")
    val id: Int = -1,
    /** The name of the character */
    @SerializedName("name")
    val name: String = "",
    /** The status of the character('Alive','Dead','unknown') */
    @SerializedName("status")
    val status: String = "",
    /** The species of the character */
    @SerializedName("species")
    val species: String = "",
    /** The type or subspecies of the character */
    @SerializedName("type")
    val type: String = "",
    /** The gender of the character */
    @SerializedName("gender")
    val gender: String = "",
    /** Name and link to the character's origin location */
    @SerializedName("origin")
    var origin: WebStorage.Origin? = null,
    /** Last Known location */
    @SerializedName("location")
    var location: Location? = null,
    /** Link to the character's image. */
    @SerializedName("image")
    val image: String = "",
    /** List of episodes in which this character appeared*/
    @SerializedName("episode")
    val episode: List<String> = emptyList(),
    /** Link to the character's own URL endpoint */
    @SerializedName("url")
    val url: String = "",
    /** Time at which the character was created in the database */
    @SerializedName("created")
    val created: String = ""
) {

    data class Origin(
        @SerializedName("name")
        val originName: String,
        @SerializedName("url")
        val originUrl: String
    )

    data class Location(
        @SerializedName("name")
        val locationName: String,
        @SerializedName("url")
        val locationUrl: String
    )
}

data class Wrapper<T, V>(
    var isNetworkSuccessTag: String = "false",
    @SerializedName("info")
    val info: T,
    @SerializedName("results")
    val results: List<V>
)

data class Info(
    @SerializedName("count")
    val count: Int,
    @SerializedName("pages")
    val pages: Int,
    @SerializedName("next")
    val next: String,
    @SerializedName("prev")
    val prev: String?
)