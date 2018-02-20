package dev.olog.msc.presentation.detail.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.MediaIdCategoryKey
import dev.olog.msc.domain.entity.*
import dev.olog.msc.domain.interactor.detail.item.*
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.TextUtils
import io.reactivex.Observable

@Module
class DetailFragmentModuleItem {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.FOLDERS)
    internal fun provideFolderItem(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetFolderUseCase) : Observable<DisplayableItem> {

        return useCase.execute(mediaId).map { it.toHeaderItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PLAYLISTS)
    internal fun providePlaylistItem(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetPlaylistUseCase) : Observable<DisplayableItem> {

        return useCase.execute(mediaId).map { it.toHeaderItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ALBUMS)
    internal fun provideAlbumItem(
            mediaId: MediaId,
            useCase: GetAlbumUseCase,
            artistUseCase: GetArtistUseCase) : Observable<DisplayableItem> {

        return useCase.execute(mediaId)
                .flatMap {album ->
                    artistUseCase.execute(MediaId.artistId(album.artistId))
                            .map { album.toHeaderItem(it.image) }
                }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ARTISTS)
    internal fun provideArtistItem(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetArtistUseCase) : Observable<DisplayableItem> {

        return useCase.execute(mediaId).map { it.toHeaderItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.GENRES)
    internal fun provideGenreItem(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetGenreUseCase) : Observable<DisplayableItem> {

        return useCase.execute(mediaId).map { it.toHeaderItem(resources) }
    }
}


private fun Folder.toHeaderItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_item_info,
            MediaId.folderId(path),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            image
    )
}

private fun Playlist.toHeaderItem(resources: Resources): DisplayableItem {
    val listSize = if (this.size == -1){ "" } else {
        resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase()
    }

    return DisplayableItem(
            R.layout.item_detail_item_info,
            MediaId.playlistId(this.id),
            title.capitalize(),
            listSize,
            image
    )
}

private fun Album.toHeaderItem(artistImage: String): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_item_info,
            MediaId.albumId(this.id),
            title,
            artist,
            image,
            trackNumber = artistImage
    )
}

private fun Artist.toHeaderItem(resources: Resources): DisplayableItem {
    val songs = resources.getQuantityString(R.plurals.song_count, this.songs, this.songs)
    val albums = if (this.albums == 0) "" else {
        "${resources.getQuantityString(R.plurals.album_count, this.albums, this.albums)}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return DisplayableItem(
            R.layout.item_detail_item_info,
            MediaId.artistId(this.id),
            name,
            "$albums$songs".toLowerCase(),
            image
    )
}

private fun Genre.toHeaderItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_item_info,
            MediaId.genreId(this.id),
            name,
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            image
    )
}