package dev.olog.msc.presentation.playlist.track.chooser

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.AnimationUtils
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.widget.fast.scroller.WaveSideBarView
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_playlist_track_chooser.*
import kotlinx.android.synthetic.main.fragment_playlist_track_chooser.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlaylistTracksChooserFragment : BaseFragment(){

    companion object {
        const val TAG = "PlaylistTracksChooserFragment"
    }

    @Inject lateinit var viewModel : PlaylistTracksChooserFragmentViewModel
    @Inject lateinit var adapter: PlaylistTracksChooserFragmentAdapter

    private var errorDisposable : Disposable? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.observeSelectedCount()
                .subscribe(this, { size ->
                    val text = when (size){
                        0 -> getString(R.string.playlist_tracks_chooser_no_tracks)
                        else -> resources.getQuantityString(R.plurals.playlist_tracks_chooser_count, size, size)
                    }
                    header.text = text

                    save.toggleVisibility(size > 0)
                })
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.layoutManager = LinearLayoutManager(context)
        view.list.adapter = adapter
        view.list.setHasFixedSize(true)

        viewModel.getAllSongs(filter(view))
                .subscribe(this, {
                    adapter.updateDataSet(it)
                    view.sidebar.onDataChanged(it)
                })

        RxView.clicks(view.back)
                .asLiveData()
                .subscribe(this, { act.onBackPressed() })

        RxView.clicks(view.save)
                .asLiveData()
                .subscribe(this, { showCreateDialog() })

        view.sidebar.scrollableLayoutId = R.layout.item_choose_track
    }

    override fun onResume() {
        super.onResume()
        sidebar.setListener(letterTouchListener)
        clear.setOnClickListener { filter.setText("") }
    }

    override fun onPause() {
        super.onPause()
        sidebar.setListener(null)
        clear.setOnClickListener(null)
    }

    private fun showCreateDialog(){
        val builder = AlertDialog.Builder(act)
                .setTitle(R.string.popup_new_playlist)
                .setView(R.layout.layout_edit_text)
                .setPositiveButton(R.string.popup_positive_ok, null)
                .setNegativeButton(R.string.popup_negative_cancel, null)

        val dialog = builder.makeDialog()

        val editText = dialog.findViewById<TextInputEditText>(R.id.editText)
        val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.editTextLayout)
        val clearButton = dialog.findViewById<View>(R.id.clear)
        clearButton.setOnClickListener { editText.setText("") }

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val editTextString = editText.text.toString()

            when {
                editTextString.isBlank() -> showError(editTextLayout, R.string.popup_playlist_name_not_valid)
                // todo title already exists
                else -> {
                    viewModel.savePlaylist(editTextString)
                            .subscribe({}, Throwable::printStackTrace)
                    dialog.dismiss()
                    act.onBackPressed()
                }
            }
        }

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    override fun onStop() {
        super.onStop()
        errorDisposable.unsubscribe()
    }

    private fun showError(editTextLayout: TextInputLayout, @StringRes errorStringId: Int){
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        editTextLayout.startAnimation(shake)
        editTextLayout.error = getString(errorStringId)
        editTextLayout.isErrorEnabled = true

        errorDisposable.unsubscribe()
        errorDisposable = Single.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ editTextLayout.isErrorEnabled = false }, Throwable::printStackTrace)
    }

    private fun filter(view: View): Observable<String> {
        return RxTextView.afterTextChangeEvents(view.filter)
                .map { it.editable().toString() }
                .filter { it.isBlank() || it.trim().length >= 2 }
                .debounce(250, TimeUnit.MILLISECONDS)
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
            val layoutManager = list.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playlist_track_chooser
}