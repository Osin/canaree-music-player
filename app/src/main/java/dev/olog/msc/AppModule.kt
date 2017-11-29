package dev.olog.msc

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dev.olog.shared.ApplicationContext

@Module
class AppModule(
        private val app: App

) {

    @Provides
    @ApplicationContext
    fun provideContext() : Context = app

    @Provides
    fun provideResources(): Resources = app.resources

    @Provides
    fun provideApplication(): Application = app

    @Provides
    fun provideContentResolver(): ContentResolver = app.contentResolver

}