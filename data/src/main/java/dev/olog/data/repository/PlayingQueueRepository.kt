package dev.olog.data.repository

import dev.olog.data.db.AppDatabase
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.PlayingQueueGateway
import dev.olog.domain.gateway.SongGateway
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlayingQueueRepository @Inject constructor(
        database: AppDatabase,
        private val songGateway: SongGateway

) : PlayingQueueGateway {

    private val publisher = BehaviorProcessor.create<List<Long>>()
    private val playingQueueDao = database.playingQueueDao()

    override fun getAll(): Single<List<Song>> {
        return Single.concat(
                playingQueueDao.getAllAsSongs(songGateway.getAll().firstOrError()),
                songGateway.getAll().firstOrError()
        ).filter { it.isNotEmpty() }
                .firstOrError()
    }

    override fun update(list: List<Long>): Completable {
        return Completable.fromCallable { playingQueueDao.insert(list) }
    }

    override fun updateMiniQueue(data: List<Long>) {
        publisher.onNext(data)
    }

    override fun observeMiniQueue(): Flowable<List<Song>> {
        return publisher
                .observeOn(Schedulers.computation())
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .flatMapSingle { it.toFlowable()
                        .flatMapMaybe { songId ->
                            songGateway.getAll().firstOrError()
                                    .flattenAsObservable { it }
                                    .filter { it.id == songId }
                                    .firstElement()
                        }.toList()
                }
    }
}
