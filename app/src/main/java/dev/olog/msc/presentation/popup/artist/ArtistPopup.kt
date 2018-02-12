package dev.olog.msc.presentation.popup.artist

import android.view.View
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener

class ArtistPopup (
        view: View,
        artist: Artist,
        song: Song?,
        listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        if (song == null){
            inflate(R.menu.dialog_artist)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        song?.let {
            menu.removeItem(R.id.viewArtist)

            if (it.album == AppConstants.UNKNOWN){
                menu.removeItem(R.id.viewAlbum)
            }
        }
    }

}