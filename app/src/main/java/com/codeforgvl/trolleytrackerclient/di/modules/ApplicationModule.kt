package com.codeforgvl.trolleytrackerclient.di.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule {
    @Provides
    internal fun provideContext(application: Application): Context {
        return application.applicationContext
    }
}