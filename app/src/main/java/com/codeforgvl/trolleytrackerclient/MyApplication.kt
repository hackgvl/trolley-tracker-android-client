package com.codeforgvl.trolleytrackerclient

import android.app.Activity
import android.app.Application
import android.support.v4.app.Fragment
import com.codeforgvl.trolleytrackerclient.di.component.ApplicationComponent
import com.codeforgvl.trolleytrackerclient.di.component.DaggerApplicationComponent
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.FontAwesomeModule
import com.joanzapata.iconify.fonts.MaterialModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import io.fabric.sdk.android.Fabric

import net.danlew.android.joda.JodaTimeAndroid
import javax.inject.Inject

/**
 * Created by ahodges on 8/25/2015.
 */
class MyApplication : Application(), HasActivityInjector, HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var dispatchingAndroidActivityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        Fabric.with(
            this, Crashlytics.Builder().core(
                CrashlyticsCore.Builder()
                    .disabled(BuildConfig.DEBUG)
                    .build()
            ).build()
        )
        Iconify.with(FontAwesomeModule()).with(MaterialModule())
        JodaTimeAndroid.init(this)

        DaggerApplicationComponent
            .builder()
            .application(this)
            .build()
            .inject(this)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity>? {
        return dispatchingAndroidActivityInjector
    }

    fun setTestComponenet(component: ApplicationComponent) {
        component.inject(this)
    }
}
