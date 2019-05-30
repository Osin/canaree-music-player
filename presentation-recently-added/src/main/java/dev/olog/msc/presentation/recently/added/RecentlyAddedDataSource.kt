package dev.olog.msc.presentation.recently.added

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.interactor.played.GetRecentlyAddedSongsUseCase
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.list.paging.BaseDataSource
import dev.olog.msc.presentation.base.list.paging.BaseDataSourceFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class RecentlyAddedDataSource @Inject constructor(
    private val recentlyAddedUseCase: GetRecentlyAddedSongsUseCase,
    private val mediaId: MediaId
) : BaseDataSource<DisplayableItem>() {

    private val chunked by lazy { recentlyAddedUseCase.get(mediaId) }

    override fun onAttach() {
        launch {
            chunked.observeNotification()
                .take(1)
                .collect {
                    invalidate()
                }
        }
    }

    override suspend fun getMainDataSize(): Int {
        return chunked.getCount(Filter.NO_FILTER)
    }

    override suspend fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override suspend fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        return chunked.getPage(request)
            .map { it.toRecentDetailDisplayableItem(mediaId) }
    }

    private fun Song.toRecentDetailDisplayableItem(parentId: MediaId): DisplayableItem {
        return DisplayableItem(
            R.layout.item_recently_added,
            MediaId.playableItem(parentId, id),
            title,
            artist,
            true
        )
    }


}

internal class RecentlyAddedDataSourceFactory @Inject constructor(
    dataSourceProvider: Provider<RecentlyAddedDataSource>
) : BaseDataSourceFactory<DisplayableItem, RecentlyAddedDataSource>(dataSourceProvider)