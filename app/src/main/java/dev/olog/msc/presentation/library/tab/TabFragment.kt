package dev.olog.msc.presentation.library.tab

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.GridLayoutManager
import android.view.View
import dagger.Lazy
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.widget.fast.scroller.WaveSideBarView
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.subscribe
import dev.olog.msc.utils.k.extension.toggleVisibility
import dev.olog.msc.utils.k.extension.withArguments
import kotlinx.android.synthetic.main.fragment_tab.*
import kotlinx.android.synthetic.main.fragment_tab.view.*
import javax.inject.Inject
import javax.inject.Provider

class TabFragment : BaseFragment() {

    companion object {

        private const val TAG = "TabFragment"
        const val ARGUMENTS_SOURCE = "$TAG.argument.dataSource"

        @JvmStatic
        fun newInstance(category: MediaIdCategory): TabFragment {
            return TabFragment().withArguments(ARGUMENTS_SOURCE to category.ordinal)
        }
    }

    @Inject lateinit var adapter: TabFragmentAdapter
    @Inject lateinit var viewModel: TabFragmentViewModel
    @Inject lateinit var category: MediaIdCategory
    @Inject lateinit var lastAlbumsAdapter : Lazy<TabFragmentLastPlayedAlbumsAdapter>
    @Inject lateinit var lastArtistsAdapter : Lazy<TabFragmentLastPlayedArtistsAdapter>
    @Inject lateinit var layoutManager: Provider<GridLayoutManager>
    @Inject lateinit var onAppBarScrollListener: TabOnAppBarScrollListener

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.observeData(category)
                .subscribe(this, { list ->
                    handleEmptyStateVisibility(list.isEmpty())
                    adapter.updateDataSet(list)
                    sidebar.onDataChanged(list)
                })

        when (category){
            MediaIdCategory.ALBUMS -> {
                viewModel.observeData(MediaIdCategory.RECENT_ALBUMS)
                        .subscribe(this, { lastAlbumsAdapter.get().updateDataSet(it) })
            }
            MediaIdCategory.ARTISTS -> {
                viewModel.observeData(MediaIdCategory.RECENT_ARTISTS)
                        .subscribe(this, { lastArtistsAdapter.get().updateDataSet(it) })
            }
        }
    }

    private fun handleEmptyStateVisibility(isEmpty: Boolean){
        view!!.emptyStateText.toggleVisibility(isEmpty)
        if (isEmpty){
            val emptyText = context!!.resources.getStringArray(R.array.tab_empty_state)
            view!!.emptyStateText.text = emptyText[category.ordinal]
        }
    }

    @CallSuper
    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.layoutManager = layoutManager.get()
        view.list.adapter = adapter
        view.list.setHasFixedSize(true)

        val scrollableLayoutId = when (category) {
            MediaIdCategory.SONGS -> R.layout.item_tab_song
            MediaIdCategory.ARTISTS -> R.layout.item_tab_artist
            else -> R.layout.item_tab_album
        }
        view.sidebar.scrollableLayoutId = scrollableLayoutId
    }

    override fun onResume() {
        super.onResume()
        activity!!.findViewById<AppBarLayout>(R.id.appBar).addOnOffsetChangedListener(onAppBarScrollListener)
        sidebar.setListener(letterTouchListener)
    }

    override fun onPause() {
        super.onPause()
        activity!!.findViewById<AppBarLayout>(R.id.appBar).removeOnOffsetChangedListener(onAppBarScrollListener)
        sidebar.setListener(null)
    }

    private val letterTouchListener = WaveSideBarView.OnTouchLetterChangeListener { letter ->
        list.stopScroll()

        val position = when (letter){
            TextUtils.MIDDLE_DOT -> -1
            "#" -> 0
            "?" -> adapter.itemCount - 1
            else -> adapter.indexOf {
                if (it.title.isBlank()) false
                else it.title[0].toUpperCase().toString() == letter
            }
        }
        if (position != -1){
            val layoutManager = list.layoutManager as GridLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_tab
}