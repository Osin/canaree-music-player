package dev.olog.msc.presentation.popup.podcastplaylist

import android.view.MenuItem
import dev.olog.msc.core.AppShortcuts
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.entity.podcast.toSong
import dev.olog.msc.core.interactor.GetPlaylistsBlockingUseCase
import dev.olog.msc.presentation.base.interfaces.MediaProvider
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.presentation.popup.R
import dev.olog.msc.presentation.popup.domain.AddToPlaylistUseCase
import dev.olog.msc.shared.extensions.toast
import javax.inject.Inject

class PodcastPlaylistPopupListener @Inject constructor(
    private val navigator: Navigator,
    getPlaylistBlockingUseCase: GetPlaylistsBlockingUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase,
    private val appShortcuts: AppShortcuts

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, true) {

    private lateinit var playlist: PodcastPlaylist
    private var podcast: Podcast? = null

    fun setData(playlist: PodcastPlaylist, podcast: Podcast?): PodcastPlaylistPopupListener {
        this.playlist = playlist
        this.podcast = podcast
        return this
    }

    private fun getMediaId(): MediaId {
        if (podcast != null) {
            return MediaId.playableItem(MediaId.podcastPlaylistId(playlist.id), podcast!!.id)
        } else {
            return MediaId.podcastPlaylistId(playlist.id)
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), playlist.size, playlist.title)

        when (itemId) {
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.rename -> rename()
            R.id.clear -> clearPlaylist()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, MediaId.podcastAlbumId(podcast!!.albumId))
            R.id.viewArtist -> viewArtist(navigator, MediaId.podcastArtistId(podcast!!.artistId))
            R.id.share -> share(activity, podcast!!.toSong())
            R.id.addHomeScreen -> appShortcuts.addDetailShortcut(getMediaId(), playlist.title)
            R.id.removeDuplicates -> removeDuplicates()
        }


        return true
    }

    private fun removeDuplicates() {
        navigator.toRemoveDuplicatesDialog(activity, MediaId.podcastPlaylistId(playlist.id), playlist.title)
    }

    private fun toCreatePlaylist() {
        if (podcast == null) {
            navigator.toCreatePlaylistDialog(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toCreatePlaylistDialog(activity, getMediaId(), -1, podcast!!.title)
        }
    }

    private fun playFromMediaId() {
        if (playlist.size == 0) {
            activity.toast(R.string.common_empty_list)
        } else {
            (activity as MediaProvider).playFromMediaId(getMediaId())
        }
    }

    private fun playShuffle() {
        if (playlist.size == 0) {
            activity.toast(R.string.common_empty_list)
        } else {
            (activity as MediaProvider).shuffle(getMediaId())
        }
    }

    private fun playLater() {
        if (podcast == null) {
            navigator.toPlayLater(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toPlayLater(activity, getMediaId(), -1, podcast!!.title)
        }
    }

    private fun playNext() {
        if (podcast == null) {
            navigator.toPlayNext(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toPlayNext(activity, getMediaId(), -1, podcast!!.title)
        }
    }


    private fun addToFavorite() {
        if (podcast == null) {
            navigator.toAddToFavoriteDialog(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toAddToFavoriteDialog(activity, getMediaId(), -1, podcast!!.title)
        }
    }

    private fun delete() {
        if (podcast == null) {
            navigator.toDeleteDialog(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toDeleteDialog(activity, getMediaId(), -1, podcast!!.title)
        }
    }

    private fun rename() {
        navigator.toRenameDialog(activity, getMediaId(), playlist.title)
    }

    private fun clearPlaylist() {
        navigator.toClearPlaylistDialog(activity, getMediaId(), playlist.title)
    }


}