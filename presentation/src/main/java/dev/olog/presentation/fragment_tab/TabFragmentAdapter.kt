package dev.olog.presentation.fragment_tab

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseListAdapter
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_music.MusicController
import dev.olog.presentation.utils.extension.setOnClickListener
import dev.olog.presentation.utils.extension.setOnLongClickListener
import dev.olog.shared.MediaIdHelper
import javax.inject.Inject

class TabFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val navigator: Navigator,
        private val source: Int,
        private val musicController: MusicController

) : BaseListAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        if (viewType == R.layout.item_shuffle){
            viewHolder.itemView.setOnClickListener { musicController.playShuffle(MediaIdHelper.MEDIA_ID_BY_ALL) }
        } else {
            viewHolder.setOnClickListener(dataController) { item, position ->
                if (item.isPlayable){
                    musicController.playFromMediaId(item.mediaId)
                } else {
                    navigator.toDetailFragment(item.mediaId, position)
                }
            }
            viewHolder.setOnLongClickListener(dataController) { item, _ ->
                navigator.toDialog(item, viewHolder.itemView)
            }
            viewHolder.setOnClickListener(R.id.more, dataController) { item, _, view ->
                navigator.toDialog(item, view)
            }
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.source, source)
        binding.setVariable(BR.position, position)
    }

    override fun getItemViewType(position: Int): Int = dataController[position].type

    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }



}