package dev.olog.msc.core.interactor.sort

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.coroutines.CompletableFlowWithParam
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway
import javax.inject.Inject

class SetSortOrderUseCase @Inject constructor(
    schedulers: IoDispatcher,
    private val gateway: SortPreferencesGateway

) : CompletableFlowWithParam<SetSortOrderRequestModel>(schedulers) {

    override suspend fun buildUseCaseObservable(param: SetSortOrderRequestModel) {
        val category = param.mediaId.category
        return when (category) {
            MediaIdCategory.FOLDERS -> gateway.setFolderSortOrder(param.sortType)
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> gateway.setPlaylistSortOrder(param.sortType)
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> gateway.setAlbumSortOrder(param.sortType)
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> gateway.setArtistSortOrder(param.sortType)
            MediaIdCategory.GENRES -> gateway.setGenreSortOrder(param.sortType)
            else -> throw IllegalArgumentException("invalid param $param")
        }
    }
}

class SetSortOrderRequestModel(
    val mediaId: MediaId,
    val sortType: SortType
)