package com.codeforgvl.trolleytrackerclient.di.modules.network

import android.content.Context
import com.codeforgvl.trolleytrackerclient.BuildConfig
import com.codeforgvl.trolleytrackerclient.network.ApiClient
import com.codeforgvl.trolleytrackerclient.network.ApiService
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {
    private val BASE_URL = if (BuildConfig.DEBUG) "http://yeahthattrolley.azurewebsites.net" else "http://api.yeahthattrolley.com"

    @Provides
    @Singleton
    fun provideHttpCache(context: Context): Cache {
        val cacheSize = 10L * 1024L * 1024L
        return Cache(context.cacheDir, cacheSize)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor().setLevel(
                        HttpLoggingInterceptor.Level.BODY
                    )
                } else {
                    HttpLoggingInterceptor().setLevel(
                        HttpLoggingInterceptor.Level.NONE
                    )
                }
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideApiClient(
        apiService: ApiService
    ): ApiClient {
        return ApiClient(apiService)
    }
}