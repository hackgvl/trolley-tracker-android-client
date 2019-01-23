package com.codeforgvl.trolleytrackerclient.di.modules.ui

import com.codeforgvl.trolleytrackerclient.fragments.ScheduleFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentsModule {
    @ContributesAndroidInjector()
    abstract fun bindScheduleFragment(): ScheduleFragment
}