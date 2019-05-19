package dev.olog.msc.presentation.popup

import android.view.View
import android.widget.PopupMenu
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.interactor.item.*
import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class PopupMenuFactory @Inject constructor(
    private val getFolderUseCase: GetFolderUseCase,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getSongUseCase: GetSongUseCase,
    private val getAlbumUseCase: GetAlbumUseCase,
    private val getArtistUseCase: GetArtistUseCase,
    private val getGenreUseCase: GetGenreUseCase,
    private val getPodcastUseCase: GetPodcastUseCase,
    private val getPodcastPlaylistUseCase: GetPodcastPlaylistUseCase,
    private val getPodcastAlbumUseCase: GetPodcastAlbumUseCase,
    private val getPodcastArtistUseCase: GetPodcastArtistUseCase,
    private val listenerFactory: MenuListenerFactory

) {

    fun create(view: View, mediaId: MediaId): Single<PopupMenu> {
        val category = mediaId.category
        return when (category) {
            MediaIdCategory.FOLDERS -> getFolderPopup(view, mediaId)
            MediaIdCategory.PLAYLISTS -> getPlaylistPopup(view, mediaId)
            MediaIdCategory.SONGS -> getSongPopup(view, mediaId)
            MediaIdCategory.ALBUMS -> getAlbumPopup(view, mediaId)
            MediaIdCategory.ARTISTS -> getArtistPopup(view, mediaId)
            MediaIdCategory.GENRES -> getGenrePopup(view, mediaId)
            MediaIdCategory.PODCASTS -> getPodcastPopup(view, mediaId)
            MediaIdCategory.PODCASTS_PLAYLIST -> getPodcastPlaylistPopup(view, mediaId)
            MediaIdCategory.PODCASTS_ALBUMS -> getPodcastAlbumPopup(view, mediaId)
            MediaIdCategory.PODCASTS_ARTISTS -> getPodcastArtistPopup(view, mediaId)
            else -> throw IllegalArgumentException("invalid category $category")
        }
    }

    private fun getFolderPopup(view: View, mediaId: MediaId): Single<PopupMenu> = runBlocking {
        TODO()
//        val activity = view.context as FragmentActivity
//        getFolderUseCase.execute(mediaId).asObservable().firstOrError()
//            .flatMap { folder ->
//                if (mediaId.isLeaf) {
//                    runBlocking { getSongUseCase.execute(mediaId) }
//                        .asObservable().firstOrError()
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .map { FolderPopup(view, folder, it, listenerFactory.folder(activity, folder, it)) }
//                } else {
//                    Single.just(FolderPopup(view, folder, null, listenerFactory.folder(activity, folder, null)))
//                        .subscribeOn(AndroidSchedulers.mainThread())
//                }
//            } as Single<PopupMenu>
    }

    private fun getPlaylistPopup(view: View, mediaId: MediaId): Single<PopupMenu> = runBlocking {
        TODO()
//        val activity = view.context as FragmentActivity
//        getPlaylistUseCase.execute(mediaId).asObservable().firstOrError()
//            .flatMap { playlist ->
//                if (mediaId.isLeaf) {
//                    runBlocking { getSongUseCase.execute(mediaId) }.asObservable().firstOrError()
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .map { PlaylistPopup(view, playlist, it, listenerFactory.playlist(activity, playlist, it)) }
//                } else {
//                    Single.just(PlaylistPopup(view, playlist, null, listenerFactory.playlist(activity, playlist, null)))
//                        .subscribeOn(AndroidSchedulers.mainThread())
//                }
//            } as Single<PopupMenu>
    }

    private fun getSongPopup(view: View, mediaId: MediaId): Single<PopupMenu> = runBlocking {
        TODO()
//        val activity = view.context as FragmentActivity
//        getSongUseCase.execute(mediaId).asObservable().firstOrError()
//            .observeOn(AndroidSchedulers.mainThread())
//            .map { SongPopup(view, it, listenerFactory.song(activity, it)) } as Single<PopupMenu>

    }

    private fun getAlbumPopup(view: View, mediaId: MediaId): Single<PopupMenu> = runBlocking {
        TODO()
//        val activity = view.context as FragmentActivity
//        getAlbumUseCase.execute(mediaId).asObservable().firstOrError()
//            .flatMap { album ->
//                if (mediaId.isLeaf) {
//                    runBlocking { getSongUseCase.execute(mediaId) }.asObservable().firstOrError()
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .map { AlbumPopup(view, album, it, listenerFactory.album(activity, album, it)) }
//                } else {
//                    Single.just(AlbumPopup(view, album, null, listenerFactory.album(activity, album, null)))
//                        .subscribeOn(AndroidSchedulers.mainThread())
//                }
//            }as Single<PopupMenu>
    }

    private fun getArtistPopup(view: View, mediaId: MediaId): Single<PopupMenu> = runBlocking {
        TODO()
//        val activity = view.context as FragmentActivity
//        getArtistUseCase.execute(mediaId).asObservable().firstOrError()
//            .flatMap { artist ->
//                if (mediaId.isLeaf) {
//                    runBlocking { getSongUseCase.execute(mediaId) }.asObservable().firstOrError()
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .map { ArtistPopup(view, artist, it, listenerFactory.artist(activity, artist, it)) }
//                } else {
//                    Single.just(ArtistPopup(view, artist, null, listenerFactory.artist(activity, artist, null)))
//                        .subscribeOn(AndroidSchedulers.mainThread())
//                }
//            }as Single<PopupMenu>
    }

    private fun getGenrePopup(view: View, mediaId: MediaId): Single<PopupMenu> = runBlocking {
        TODO()
//        val activity = view.context as FragmentActivity
//        getGenreUseCase.execute(mediaId).asObservable().firstOrError()
//            .flatMap { genre ->
//                if (mediaId.isLeaf) {
//                    runBlocking { getSongUseCase.execute(mediaId) }.asObservable().firstOrError()
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .map { GenrePopup(view, genre, it, listenerFactory.genre(activity, genre, it)) }
//                } else {
//                    Single.just(GenrePopup(view, genre, null, listenerFactory.genre(activity, genre, null)))
//                        .subscribeOn(AndroidSchedulers.mainThread())
//                }
//            }as Single<PopupMenu>
    }

    private fun getPodcastPopup(view: View, mediaId: MediaId): Single<PopupMenu> = runBlocking {
        TODO()
//        val activity = view.context as FragmentActivity
//        runBlocking { getPodcastUseCase.execute(mediaId) }
//            .asObservable().firstOrError()
//            .observeOn(AndroidSchedulers.mainThread())
//            .map { PodcastPopup(view, it, listenerFactory.podcast(activity, it)) } as Single<PopupMenu>
    }

    private fun getPodcastPlaylistPopup(view: View, mediaId: MediaId): Single<PopupMenu> = runBlocking {
        TODO()
//        val activity = view.context as FragmentActivity
//        getPodcastPlaylistUseCase.execute(mediaId).asObservable().firstOrError()
//            .flatMap { playlist ->
//                if (mediaId.isLeaf) {
//                    runBlocking { getPodcastUseCase.execute(mediaId) }.asObservable().firstOrError()
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .map {
//                            PodcastPlaylistPopup(
//                                view,
//                                playlist,
//                                it,
//                                listenerFactory.podcastPlaylist(activity, playlist, it)
//                            )
//                        }
//                } else {
//                    Single.just(
//                        PodcastPlaylistPopup(
//                            view,
//                            playlist,
//                            null,
//                            listenerFactory.podcastPlaylist(activity, playlist, null)
//                        )
//                    )
//                        .subscribeOn(AndroidSchedulers.mainThread())
//                }
//            }as Single<PopupMenu>
    }

    private fun getPodcastAlbumPopup(view: View, mediaId: MediaId): Single<PopupMenu> = runBlocking {
        TODO()
//        val activity = view.context as FragmentActivity
//        getPodcastAlbumUseCase.execute(mediaId).asObservable().firstOrError()
//            .flatMap { album ->
//                if (mediaId.isLeaf) {
//                    runBlocking { getPodcastUseCase.execute(mediaId) }.asObservable().firstOrError()
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .map { PodcastAlbumPopup(view, album, it, listenerFactory.podcastAlbum(activity, album, it)) }
//                } else {
//                    Single.just(
//                        PodcastAlbumPopup(
//                            view,
//                            album,
//                            null,
//                            listenerFactory.podcastAlbum(activity, album, null)
//                        )
//                    )
//                        .subscribeOn(AndroidSchedulers.mainThread())
//                }
//            }as Single<PopupMenu>
    }

    private fun getPodcastArtistPopup(view: View, mediaId: MediaId): Single<PopupMenu> = runBlocking {
        TODO()
//        val activity = view.context as FragmentActivity
//        getPodcastArtistUseCase.execute(mediaId).asObservable().firstOrError()
//            .flatMap { artist ->
//                if (mediaId.isLeaf) {
//                    runBlocking { getPodcastUseCase.execute(mediaId) }.asObservable().firstOrError()
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .map {
//                            PodcastArtistPopup(
//                                view,
//                                artist,
//                                it,
//                                listenerFactory.podcastArtist(activity, artist, it)
//                            )
//                        }
//                } else {
//                    Single.just(
//                        PodcastArtistPopup(
//                            view,
//                            artist,
//                            null,
//                            listenerFactory.podcastArtist(activity, artist, null)
//                        )
//                    )
//                        .subscribeOn(AndroidSchedulers.mainThread())
//                }
//            }as Single<PopupMenu>
    }

}