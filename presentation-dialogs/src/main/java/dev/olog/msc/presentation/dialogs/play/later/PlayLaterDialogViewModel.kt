package dev.olog.msc.presentation.dialogs.play.later

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.interactor.GetSongListChunkByParamUseCase
import dev.olog.msc.shared.MusicConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayLaterDialogViewModel @Inject constructor(
    private val getSongListByParamUseCase: GetSongListChunkByParamUseCase
) : ViewModel() {

    fun execute(mediaId: MediaId, mediaController: MediaControllerCompat) = viewModelScope.launch(Dispatchers.Default) {
        //        return if (mediaId.isLeaf) {
//            Single.fromCallable { "${mediaId.leaf!!}" }.subscribeOn(Schedulers.io())
//        } else {
//            getSongListByParamUseCase.execute(mediaId)
//                .firstOrError()
//                .map { songList -> songList.asSequence().map { it.id }.joinToString() }
//        }.map { mediaController.addQueueItem(newMediaDescriptionItem(it)) }
//            .ignoreElement()
    }

    private fun newMediaDescriptionItem(mediaId: MediaId, songId: String): MediaDescriptionCompat {
        return MediaDescriptionCompat.Builder()
            .setMediaId(songId)
            .setExtras(bundleOf(MusicConstants.IS_PODCAST to mediaId.isAnyPodcast))
            .build()
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}