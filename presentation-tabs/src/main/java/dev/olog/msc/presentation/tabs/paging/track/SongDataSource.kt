package dev.olog.msc.presentation.tabs.paging.track

import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.list.paging.BaseDataSource
import dev.olog.msc.presentation.base.list.paging.BaseDataSourceFactory
import dev.olog.msc.presentation.tabs.TabFragmentHeaders
import dev.olog.msc.presentation.tabs.mapper.toTabDisplayableItem
import dev.olog.msc.shared.core.flow.merge
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class SongDataSource @Inject constructor(
    gateway: SongGateway,
    private val prefsGateway: SortPreferencesGateway,
    private val displayableHeaders: TabFragmentHeaders
) : BaseDataSource<DisplayableItem>() {

    private val pageRequest = gateway.getAll()

    override fun onAttach() {
        launch {
            pageRequest.observeNotification()
                .merge(prefsGateway.observeAllTracksSortOrder().drop(1))
                .take(1)
                .collect {
                    invalidate()
                }
        }
    }

    override suspend fun getMainDataSize(): Int {
        return pageRequest.getCount(Filter.NO_FILTER)
    }

    override suspend fun getHeaders(mainListSize: Int): List<DisplayableItem> {
        if (mainListSize == 0) {
            return listOf()
        }
        return listOf(displayableHeaders.shuffleHeader)
    }

    override suspend fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        return pageRequest.getPage(request)
            .map { it.toTabDisplayableItem() }
    }

}

internal class SongDataSourceFactory @Inject constructor(
    dataSourceProvider: Provider<SongDataSource>
) : BaseDataSourceFactory<DisplayableItem, SongDataSource>(dataSourceProvider)