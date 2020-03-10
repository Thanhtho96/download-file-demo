package com.tt.downloadfile.di

import com.tt.downloadfile.network.DownloadService
import com.tt.downloadfile.viewmodel.DownloadViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appComponent = module {
    factory { getHttpLoggingInterceptor() }
    factory { provideOkHttpClient(get()) }
    single { provideRetrofit() }
    single { provideDownloadService(get()) }
    viewModel { DownloadViewModel(get()) }
}

fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    return httpLoggingInterceptor
}

fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .connectTimeout(1, TimeUnit.MINUTES)
        .callTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
}

fun provideRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://handsomeman.herokuapp.com/")
        .client(provideOkHttpClient(getHttpLoggingInterceptor()))
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideDownloadService(retrofit: Retrofit): DownloadService {
    return retrofit.create<DownloadService>(
        DownloadService::class.java)
}
