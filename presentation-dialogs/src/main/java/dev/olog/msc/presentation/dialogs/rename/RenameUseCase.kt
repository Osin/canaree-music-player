package dev.olog.msc.presentation.dialogs.rename

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.CompletableFlowWithParam
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class RenameUseCase @Inject constructor(
    scheduler: IoDispatcher,
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) : CompletableFlowWithParam<Pair<MediaId, String>>(scheduler) {


    override suspend fun buildUseCaseObservable(param: Pair<MediaId, String>) {
        val (mediaId, newTitle) = param
        when {
            mediaId.isPodcastPlaylist -> podcastPlaylistGateway.renamePlaylist(mediaId.categoryValue.toLong(), newTitle)
            mediaId.isPlaylist -> playlistGateway.renamePlaylist(mediaId.categoryValue.toLong(), newTitle)
            else -> throw IllegalArgumentException("not a folder nor a playlist, $mediaId")
        }
    }
}