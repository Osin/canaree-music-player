package dev.olog.msc.presentation.library.folder.tree

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import dev.olog.msc.R
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.io.File
import java.text.Collator

class FolderTreeFragmentViewModel(
        private val context: Context,
        private val appPreferencesUseCase: AppPreferencesUseCase,
        private val collator: Collator

) : ViewModel() {

    companion object {
        val BACK_HEADER_ID = MediaId.folderId("back header")
    }

    private val observer = object : ContentObserver(Handler(Looper.getMainLooper())){
        override fun onChange(selfChange: Boolean) {
            currentFile.onNext(currentFile.value!!)
        }
    }

    private val currentFile = BehaviorSubject.createDefault(Environment.getExternalStorageDirectory())


    init {
        context.contentResolver.registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, observer)
    }

    override fun onCleared() {
        context.contentResolver.unregisterContentObserver(observer)
    }

    fun observeFileName(): LiveData<File> = currentFile.asLiveData()

    fun observeChildrens(): LiveData<List<DisplayableFile>> = currentFile.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map {
                val blackList = appPreferencesUseCase.getBlackList()
                val childrens = it.listFiles()
                        .filter { if (it.isDirectory) !blackList.contains(it.path) else !blackList.contains(it.parentFile.path) }

                var start = System.currentTimeMillis()
                val (directories, files) = childrens.partition { it.isDirectory }
                println("1 ${System.currentTimeMillis() - start}")
                start = System.currentTimeMillis()
                val sortedDirectory = filterFolders(directories)
                println("2 ${System.currentTimeMillis() - start}")
                start = System.currentTimeMillis()
                val sortedFiles = filterTracks(files)
                println("3 ${System.currentTimeMillis() - start}")

                val displayableItems = sortedDirectory.plus(sortedFiles)

                if (it == Environment.getExternalStorageDirectory()){
                    displayableItems
                } else {
                    displayableItems.startWith(backDisplableItem)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .asLiveData()

    private fun filterFolders(files: List<File>): List<DisplayableFile> {
        return files.asSequence()
                .filter { it.isDirectory }
                .sortedWith(Comparator { o1, o2 -> collator.compare(o1.name, o2.name) })
                .map { it.toDisplayableItem() }
                .toList()
                .startWithIfNotEmpty(foldersHeader)
    }

    private fun filterTracks(files: List<File>): List<DisplayableFile> {
        return files.asSequence()
                .filter { it.isAudioFile() }
                .sortedWith(Comparator { o1, o2 -> collator.compare(o1.name, o2.name) })
                .map { it.toDisplayableItem() }
                .toList()
                .startWithIfNotEmpty(tracksHeader)
    }

    fun popFolder(): Boolean{
        val current = currentFile.value!!
        if (current.isStorageDir()){
            return false
        }
        currentFile.onNext(current.parentFile)
        return true
    }

    fun goBack(){
        if (!currentFile.value!!.isStorageDir()){
            currentFile.onNext(currentFile.value!!.parentFile)
        }
    }

    fun nextFolder(file: File){
        currentFile.onNext(file)
    }

    private val backDisplableItem: List<DisplayableFile> = listOf(
            DisplayableFile(R.layout.item_folder_tree_directory, BACK_HEADER_ID, "...", null, null)
    )

    private val foldersHeader = DisplayableFile(
            R.layout.item_folder_tree_header, MediaId.headerId("folder header"), context.getString(R.string.common_folders), null, null)

    private val tracksHeader = DisplayableFile(
            R.layout.item_folder_tree_header, MediaId.headerId("track header"), context.getString(R.string.common_tracks), null, null)

    private fun File.toDisplayableItem(): DisplayableFile {
        val isDirectory = this.isDirectory
        val id = if (isDirectory) R.layout.item_folder_tree_directory else R.layout.item_folder_tree_track

        return DisplayableFile(
                type = id,
                mediaId = MediaId.folderId(this.path),
                title = this.name,
                subtitle = null,
                path =  this.path
        )
    }

}