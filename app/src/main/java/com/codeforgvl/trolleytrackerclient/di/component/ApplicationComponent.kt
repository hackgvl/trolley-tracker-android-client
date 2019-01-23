package com.codeforgvl.trolleytrackerclient.di.component

import com.codeforgvl.trolleytrackerclient.MyApplication
import com.codeforgvl.trolleytrackerclient.di.modules.ApplicationModule
import com.codeforgvl.trolleytrackerclient.di.modules.network.NetworkModule
import com.codeforgvl.trolleytrackerclient.di.modules.ui.FragmentsModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        FragmentsModule::class,
        NetworkModule::class
    ]
)
interface ApplicationComponent {

    fun inject(app: MyApplication)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: MyApplication): Builder

        fun build(): ApplicationComponent
    }
}