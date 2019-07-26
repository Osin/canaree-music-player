package dev.olog.core.gateway

import android.content.Context
import dev.olog.core.MediaId

interface ImageVersionGateway {

    fun getCurrentVersion(mediaId: MediaId): Int
    fun increaseCurrentVersion(mediaId: MediaId)

}

interface HasImageVersionGateway {
    fun getImageVersionGateway(): ImageVersionGateway
}

fun Context.getImageVersionGateway() = (applicationContext as HasImageVersionGateway).getImageVersionGateway()