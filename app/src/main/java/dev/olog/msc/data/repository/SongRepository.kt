package dev.olog.msc.data.repository

import android.content.ContentResolver
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns.IS_MUSIC
import android.provider.MediaStore.Audio.Media.DURATION
import android.provider.MediaStore.Audio.Media.TITLE
import com.squareup.sqlbrite3.BriteContentResolver
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toSong
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

private val PROJECTION = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.YEAR,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.DATE_ADDED
)

private const val SELECTION = "$IS_MUSIC <> 0 AND $TITLE NOT LIKE ? AND $DURATION > ?"

private val SELECTION_ARGS = arrayOf("AUD%", "20000")

private const val SORT_ORDER = "lower(${MediaStore.Audio.Media.TITLE})"

class SongRepository @Inject constructor(
        private val contentResolver: ContentResolver,
        private val rxContentResolver: BriteContentResolver,
        private val appPrefsUseCase: AppPreferencesUseCase,
        appDatabase: AppDatabase

) : BaseRepository<Song, Long>(), SongGateway {

    private val lastFmDao = appDatabase.lastFmDao()

    override fun queryAllData(): Observable<List<Song>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, PROJECTION, SELECTION,
                SELECTION_ARGS, SORT_ORDER, true
        ).mapToList { it.toSong() }
                .map {
                    val blackListed = appPrefsUseCase.getBlackList()
                    if (blackListed.isEmpty()){
                        it
                    } else {
                        it.filter { !blackListed.contains(it.folderPath) }
                    }
                }
                .map {
                    val images = lastFmDao.getAllTrackImagesBlocking()
                    it.map { song ->
                        val image = images.asSequence()
                                .filter { it.use }
                                .firstOrNull { it.id == song.id }?.image

                        if (image != null && image.isNotBlank()){
                            song.copy(image = image)
                        } else song
                    }
                }
                .onErrorReturn { listOf() }
    }

    override fun getByParamImpl(list: List<Song>, param: Long): Song {
        return list.first { it.id == param }
    }

    override fun getAllUnfiltered(): Observable<List<Song>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI,
                PROJECTION,
                SELECTION,
                SELECTION_ARGS,
                SORT_ORDER,
                false
        ).mapToList { it.toSong() }
                .onErrorReturn { listOf() }
    }

    override fun deleteSingle(songId: Long): Completable {
        return Single.fromCallable {
            contentResolver.delete(MEDIA_STORE_URI, "${BaseColumns._ID} = ?", arrayOf("$songId"))
        }
                .filter { it > 0 }
                .flatMapSingle { getByParam(songId).firstOrError() }
                .map { File(it.path) }
                .filter { it.exists() }
                .map { it.delete() }
                .toSingle()
                .toCompletable()

    }

    override fun deleteGroup(songList: List<Song>): Completable {
        return Flowable.fromIterable(songList)
                .map { it.id }
                .flatMapCompletable { deleteSingle(it).subscribeOn(Schedulers.io()) }
    }

}
