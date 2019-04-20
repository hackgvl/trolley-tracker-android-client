package com.codeforgvl.trolleytrackerclient.di.modules.ui

import com.codeforgvl.trolleytrackerclient.ui.schedule.ScheduleFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentsModule {
    @ContributesAndroidInjector(modules = [(SchedulePresenterModule::class)])
    abstract fun bindScheduleFragment(): ScheduleFragment
}