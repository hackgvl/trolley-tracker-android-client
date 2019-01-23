package com.codeforgvl.trolleytrackerclient.di.modules

import android.content.Context
import com.codeforgvl.trolleytrackerclient.MyApplication
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule {
    @Provides
    internal fun provideContext(myApplication: MyApplication): Context {
        return myApplication.applicationContext
    }
}