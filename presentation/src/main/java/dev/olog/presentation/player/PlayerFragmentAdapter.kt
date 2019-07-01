package dev.olog.presentation.player

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.core.MediaId
import dev.olog.media.*
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation.base.*
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.utils.isCollapsed
import dev.olog.presentation.utils.isExpanded
import dev.olog.presentation.widgets.SwipeableView
import dev.olog.presentation.widgets.audiowave.AudioWaveViewWrapper
import dev.olog.shared.MusicConstants
import dev.olog.shared.extensions.*
import dev.olog.shared.theme.HasImageShape
import dev.olog.shared.theme.ImageShape
import dev.olog.shared.theme.hasPlayerAppearance
import dev.olog.shared.utils.TextUtils
import dev.olog.shared.widgets.AnimatedImageView
import dev.olog.shared.widgets.playpause.AnimatedPlayPauseImageView
import kotlinx.android.synthetic.main.player_layout_default.view.*
import kotlinx.android.synthetic.main.player_toolbar_default.view.*
import kotlinx.android.synthetic.main.player_controls_default.view.*

class PlayerFragmentAdapter(
    @FragmentLifecycle lifecycle: Lifecycle,
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val viewModel: PlayerFragmentViewModel,
    private val presenter: PlayerFragmentPresenter

) : ObservableAdapter<DisplayableItem>(lifecycle, DiffCallbackDisplayableItem) {

    private val playerViewTypes = listOf(
        R.layout.player_layout_default,
        R.layout.player_layout_spotify,
        R.layout.player_layout_flat,
        R.layout.player_layout_big_image,
        R.layout.player_layout_fullscreen,
        R.layout.player_layout_clean,
        R.layout.player_layout_mini
    )

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_mini_queue -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    mediaProvider.skipToQueueItem(item.trackNumber.toLong())
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    navigator.toDialog(item, view)
                }
                viewHolder.elevateAlbumOnTouch()

//                viewHolder.setOnMoveListener(controller, touchHelper!!) TODO
            }
            R.layout.player_layout_default,
            R.layout.player_layout_spotify,
            R.layout.player_layout_fullscreen,
            R.layout.player_layout_flat,
            R.layout.player_layout_big_image,
            R.layout.player_layout_clean,
            R.layout.player_layout_mini -> {
                setupListeners(viewHolder)

                viewHolder.setOnClickListener(R.id.more, this) { _, _, view ->
                    val mediaId = MediaId.songId(viewModel.getCurrentTrackId())
                    navigator.toDialog(mediaId, view)
                }

                val view = viewHolder.itemView
                val hasImageShape = view.context.applicationContext as HasImageShape
                val imageShape = hasImageShape.getImageShape()
                if (imageShape == ImageShape.RECTANGLE) {
                    view.coverWrapper.radius = 0f
                }
            }
        }

    }

    @SuppressLint("RxLeakedSubscription")
    override fun onViewAttachedToWindow(holder: DataBoundViewHolder) {
        super.onViewAttachedToWindow(holder)

        val viewType = holder.itemViewType

        if (viewType in playerViewTypes) {

            val view = holder.itemView
            view.bigCover.observeProcessorColors()
                .asLiveData()
                .subscribe(holder, viewModel::updateProcessorColors)
            view.bigCover.observePaletteColors()
                .asLiveData()
                .subscribe(holder, viewModel::updatePaletteColors)

            bindPlayerControls(holder, view)
        }

        val view = holder.itemView

        when (viewType) {
            R.layout.player_layout_default,
            R.layout.player_layout_spotify,
            R.layout.player_layout_big_image,
            R.layout.player_layout_clean -> {

                viewModel.observePaletteColors()
                    .map { it.accent }
                    .asLiveData()
                    .subscribe(holder) { accent ->
                        view.artist.apply { animateTextColor(accent) }
                        view.shuffle.updateSelectedColor(accent)
                        view.repeat.updateSelectedColor(accent)
                        view.seekBar.apply {
                            thumbTintList = ColorStateList.valueOf(accent)
                            progressTintList = ColorStateList.valueOf(accent)
                        }
                    }

            }
            R.layout.player_layout_flat -> {
                viewModel.observeProcessorColors()
                    .asLiveData()
                    .subscribe(holder) { colors ->
                        view.title.apply {
                            animateTextColor(colors.primaryText)
                            animateBackgroundColor(colors.background)
                        }
                        view.artist.apply {
                            animateTextColor(colors.secondaryText)
                            animateBackgroundColor(colors.background)
                        }
                    }

                viewModel.observePaletteColors()
                    .map { it.accent }
                    .asLiveData()
                    .subscribe(holder) { accent ->
                        view.seekBar.apply {
                            thumbTintList = ColorStateList.valueOf(accent)
                            progressTintList = ColorStateList.valueOf(accent)
                        }
                        view.shuffle.updateSelectedColor(accent)
                        view.repeat.updateSelectedColor(accent)
                    }
            }
            R.layout.player_layout_fullscreen -> {
                view.playPause.useLightImage()
                view.next.useLightImage()
                view.previous.useLightImage()

                viewModel.observePaletteColors()
                    .map { it.accent }
                    .asLiveData()
                    .subscribe(holder) { accent ->
                        view.seekBar.apply {
                            thumbTintList = ColorStateList.valueOf(accent)
                            progressTintList = ColorStateList.valueOf(accent)
                        }
                        view.artist.animateTextColor(accent)
                        view.playPause.backgroundTintList = ColorStateList.valueOf(accent)
                        view.shuffle.updateSelectedColor(accent)
                        view.repeat.updateSelectedColor(accent)
                    }
            }
            R.layout.player_layout_mini -> {
                viewModel.observePaletteColors()
                    .map { it.accent }
                    .asLiveData()
                    .subscribe(holder) { accent ->
                        view.artist.apply { animateTextColor(accent) }
                        view.shuffle.updateSelectedColor(accent)
                        view.repeat.updateSelectedColor(accent)
                        view.seekBar.apply {
                            thumbTintList = ColorStateList.valueOf(accent)
                            progressTintList = ColorStateList.valueOf(accent)
                        }
                        view.more.imageTintList = ColorStateList.valueOf(accent)
                        view.lyrics.imageTintList = ColorStateList.valueOf(accent)
                    }
            }
        }
    }

    private fun setupListeners(holder: DataBoundViewHolder) {
        val view = holder.itemView
        view.repeat.setOnClickListener { mediaProvider.toggleRepeatMode() }
        view.shuffle.setOnClickListener { mediaProvider.toggleShuffleMode() }
        view.favorite.setOnClickListener { mediaProvider.togglePlayerFavorite() }
        view.lyrics.setOnClickListener { navigator.toOfflineLyrics() }
        view.next.setOnClickListener { mediaProvider.skipToNext() }
        view.playPause.setOnClickListener { mediaProvider.playPause() }
        view.previous.setOnClickListener { mediaProvider.skipToPrevious() }

        view.replay.setOnClickListener {
            it.rotate(-30f)
            mediaProvider.replayTenSeconds()
        }

        view.replay30.setOnClickListener {
            it.rotate(-50f)
            mediaProvider.replayThirtySeconds()
        }

        view.forward.setOnClickListener {
            it.rotate(30f)
            mediaProvider.forwardTenSeconds()
        }

        view.forward30.setOnClickListener {
            it.rotate(50f)
            mediaProvider.forwardThirtySeconds()
        }

        view.playbackSpeed.setOnClickListener { openPlaybackSpeedPopup(it) }

        view.seekBar.setListener(
            onProgressChanged = {
                view.bookmark.text = TextUtils.formatMillis(it)
            }, onStartTouch = {

            }, onStopTouch = {
                mediaProvider.seekTo(it.toLong())
            }
        )
    }

    @SuppressLint("RxLeakedSubscription", "CheckResult")
    private fun bindPlayerControls(holder: DataBoundViewHolder, view: View) {

        val waveWrapper: AudioWaveViewWrapper? = view.findViewById(R.id.waveWrapper)

        view.findViewById<AnimatedImageView>(R.id.next)?.setDefaultColor()
        view.findViewById<AnimatedImageView>(R.id.previous)?.setDefaultColor()
        view.findViewById<AnimatedPlayPauseImageView>(R.id.playPause)?.setDefaultColor()

        mediaProvider.observeMetadata()
            .subscribe(holder) {
                viewModel.updateCurrentTrackId(it.getId())
                waveWrapper?.onTrackChanged(it.getString(MusicConstants.PATH))
                waveWrapper?.updateMax(it.getDuration())

                updateMetadata(view, it)
                updateImage(view, it)
            }

        mediaProvider.observePlaybackState()
            .subscribe(holder) { onPlaybackStateChanged(view, it) }

        viewModel.observeProgress
            .asLiveData()
            .subscribe(holder) {
                view.seekBar.setProgress(it)
                waveWrapper?.updateProgress(it)
            }

        mediaProvider.observeRepeat()
            .subscribe(holder, view.repeat::cycle)

        mediaProvider.observeShuffle()
            .subscribe(holder, view.shuffle::cycle)

        view.swipeableView?.setOnSwipeListener(object : SwipeableView.SwipeListener {
            override fun onSwipedLeft() {
                mediaProvider.skipToNext()
            }

            override fun onSwipedRight() {
                mediaProvider.skipToPrevious()
            }

            override fun onClick() {
                mediaProvider.playPause()
            }

            override fun onLeftEdgeClick() {
                mediaProvider.skipToPrevious()
            }

            override fun onRightEdgeClick() {
                mediaProvider.skipToNext()
            }
        })

        viewModel.onFavoriteStateChanged
            .asLiveData()
            .subscribe(holder, view.favorite::onNextState)

        viewModel.skipToNextVisibility
            .asLiveData()
            .subscribe(holder, view.next::updateVisibility)

        viewModel.skipToPreviousVisibility
            .asLiveData()
            .subscribe(holder, view.previous::updateVisibility)

        val playerAppearance = view.context.hasPlayerAppearance()

        presenter.observePlayerControlsVisibility()
            .filter { !playerAppearance.isFullscreen() && !playerAppearance.isMini() }
            .asLiveData()
            .subscribe(holder) { visible ->
                view.previous.toggleVisibility(visible, true)
                view.playPause.toggleVisibility(visible, true)
                view.next.toggleVisibility(visible, true)
            }


        if (playerAppearance.isFullscreen() || playerAppearance.isMini()) {

            mediaProvider.observePlaybackState()
                .map { it.state }
                .filter { state ->
                    state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT ||
                            state == PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS
                }
                .map { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT }
                .subscribe(holder) { animateSkipTo(view, it) }

            mediaProvider.observePlaybackState()
                .map { it.state }
                .filter {
                    it == PlaybackStateCompat.STATE_PLAYING ||
                            it == PlaybackStateCompat.STATE_PAUSED
                }.distinctUntilChanged()
                .subscribe(holder) { state ->

                    if (state == PlaybackStateCompat.STATE_PLAYING) {
                        playAnimation(view, true)
                    } else {
                        pauseAnimation(view, true)
                    }
                }
        }
    }

    private fun updateMetadata(view: View, metadata: MediaMetadataCompat) {
        view.title.text = metadata.getTitle()
        view.artist.text = metadata.getArtist()

        val duration = metadata.getDuration()

        val readableDuration = metadata.getDurationReadable()
        view.duration.text = readableDuration
        view.seekBar.max = duration.toInt()

        val isPodcast = metadata.isPodcast()
        val playerControlsRoot: ConstraintLayout = view.findViewById(R.id.playerControls)
            ?: view.findViewById(R.id.playerRoot) as ConstraintLayout
        playerControlsRoot.findViewById<View>(R.id.replay).toggleVisibility(isPodcast, true)
        playerControlsRoot.findViewById<View>(R.id.forward).toggleVisibility(isPodcast, true)
        playerControlsRoot.findViewById<View>(R.id.replay30).toggleVisibility(isPodcast, true)
        playerControlsRoot.findViewById<View>(R.id.forward30).toggleVisibility(isPodcast, true)
    }

    private fun updateImage(view: View, metadata: MediaMetadataCompat) {
        view.bigCover.loadImage(metadata) ?: return
    }

    private fun openPlaybackSpeedPopup(view: View) {
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.dialog_playback_speed)
        popup.menu.getItem(viewModel.getPlaybackSpeed()).isChecked = true
        popup.setOnMenuItemClickListener {
            viewModel.setPlaybackSpeed(it.itemId)
            true
        }
        popup.show()
    }

    private fun onPlaybackStateChanged(view: View, playbackState: PlaybackStateCompat) {
        val isPlaying = playbackState.isPlaying()
        if (isPlaying || playbackState.isPaused()) {
            view.nowPlaying.isActivated = isPlaying
            val playerAppearance = view.context.hasPlayerAppearance()
            if (playerAppearance.isClean()) {
                view.bigCover.isActivated = isPlaying
            } else {
                view.coverWrapper.isActivated = isPlaying
            }

        }
    }

    private fun animateSkipTo(view: View, toNext: Boolean) {
        val hasSlidingPanel = (view.context) as HasSlidingPanel
        if (hasSlidingPanel.getSlidingPanel().isCollapsed()) return

        if (toNext) {
            view.next.playAnimation()
        } else {
            view.previous.playAnimation()
        }
    }

    private fun playAnimation(view: View, animate: Boolean) {
        val hasSlidingPanel = (view.context) as HasSlidingPanel
        val isPanelExpanded = hasSlidingPanel.getSlidingPanel().isExpanded()
        view.playPause.animationPlay(isPanelExpanded && animate)
    }

    private fun pauseAnimation(view: View, animate: Boolean) {
        val hasSlidingPanel = (view.context) as HasSlidingPanel
        val isPanelExpanded = hasSlidingPanel.getSlidingPanel().isExpanded()
        view.playPause.animationPause(isPanelExpanded && animate)
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

//    override val onDragAction = { from: Int, to: Int -> mediaProvider.swapRelative(from, to) } TODO
//
//    override val onSwipeRightAction = { position: Int -> mediaProvider.removeRelative(position) }
//
//    override fun canInteractWithViewHolder(viewType: Int): Boolean? {
//        return viewType == R.layout.item_mini_queue
//    }
}