package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.GenreGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllGenresUseCase @Inject constructor(
        gateway: GenreGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Genre>(gateway, schedulers)