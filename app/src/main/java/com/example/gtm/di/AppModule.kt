package com.example.gtm.di

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.example.gtm.BuildConfig
import com.example.gtm.data.remote.auth.AuthService
import com.example.gtm.data.remote.user.UserService
import com.example.gtm.utils.animations.UiAnimations
import com.example.gtm.utils.remote.Urls
import com.example.gtm.utils.token.AuthInterceptor
import com.example.gtm.utils.token.SessionManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module

@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context) = if (BuildConfig.DEBUG) {
        val authInterceptor = AuthInterceptor(context)
        val logIntercept = HttpLoggingInterceptor()
        logIntercept.setLevel(HttpLoggingInterceptor.Level.BODY)

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logIntercept)
            .build()
    } else {
        OkHttpClient
            .Builder()
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient):
            Retrofit = Retrofit.Builder()
        .baseUrl(Urls.baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()


    @Provides
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(@ApplicationContext context: Context): AuthInterceptor {
        return AuthInterceptor(context)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("GTM", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideUiAnimations(@ActivityContext activity: Activity):UiAnimations {
        return UiAnimations(activity)
    }

    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthService = retrofit.create(AuthService::class.java)


    @Provides
    fun provideUserService(retrofit: Retrofit): UserService = retrofit.create(UserService::class.java)
}