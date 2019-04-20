package com.codeforgvl.trolleytrackerclient.di.modules.ui

import com.codeforgvl.trolleytrackerclient.ui.schedule.ScheduleContract
import com.codeforgvl.trolleytrackerclient.ui.schedule.SchedulePresenter
import dagger.Binds
import dagger.Module

@Module
abstract class SchedulePresenterModule {
    @Binds abstract
    fun bindsSchedulePresenter(schedulePresenter: SchedulePresenter): ScheduleContract.Presenter
}