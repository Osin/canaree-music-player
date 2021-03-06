package dev.olog.core.interactor.playlist

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class RemoveDuplicatesUseCaseTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val playlistGateway = mock<PlaylistGateway>()
    private val podcastGateway = mock<PodcastPlaylistGateway>()
    private val sut = RemoveDuplicatesUseCase(playlistGateway, podcastGateway)

    @Test
    fun testInvokePodcast() = coroutineRule.runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.PODCASTS_PLAYLIST, id.toString()
        )

        // when
        sut(mediaId)

        verify(podcastGateway).removeDuplicated(id)
        verifyZeroInteractions(playlistGateway)
    }

    @Test
    fun testInvokeTrack() = coroutineRule.runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.PLAYLISTS, id.toString()
        )

        // when
        sut(mediaId)

        verify(playlistGateway).removeDuplicated(id)
        verifyZeroInteractions(podcastGateway)
    }

    @Test
    fun testInvokeAuto() = coroutineRule.runBlocking {
        // given
        val allowed = listOf(
            MediaIdCategory.PODCASTS_PLAYLIST, MediaIdCategory.PLAYLISTS
        )

        for (value in MediaIdCategory.values()) {
            if (value in allowed) {
                continue
            }
            try {
                sut(MediaId.createCategoryValue(value, "1"))
                Assert.fail("invalid $value")
            } catch (ex: IllegalArgumentException) {
            }
        }
        verifyZeroInteractions(podcastGateway)
        verifyZeroInteractions(playlistGateway)
    }

}