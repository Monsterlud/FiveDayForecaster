package com.monsalud.fivedayforecaster.di

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.google.gson.Gson
import com.monsalud.fivedayforecaster.data.LocalDataSource
import com.monsalud.fivedayforecaster.data.RemoteDataSource
import com.monsalud.fivedayforecaster.data.WeatherListRepositoryImpl
import com.monsalud.fivedayforecaster.data.datasource.local.LocalDataSourceImpl
import com.monsalud.fivedayforecaster.data.datasource.local.room.WeatherDAO
import com.monsalud.fivedayforecaster.data.datasource.local.room.WeatherDatabase
import com.monsalud.fivedayforecaster.data.datasource.remote.RemoteDataSourceImpl
import com.monsalud.fivedayforecaster.data.utils.NetworkUtils
import com.monsalud.fivedayforecaster.data.utils.WeatherMappers
import com.monsalud.fivedayforecaster.domain.WeatherRepository
import com.monsalud.fivedayforecaster.presentation.WeatherViewModel
import com.monsalud.fivedayforecaster.presentation.utils.LocationUtils
import com.monsalud.fivedayforecaster.presentation.utils.PermissionsHandler
import com.monsalud.fivedayforecaster.presentation.utils.WeatherConstants.TIMEOUT
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.observer.ResponseObserver
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    val moduleInstance = AppModule()

    fun provideDatabase(application: Application): WeatherDatabase {
        return Room.databaseBuilder(
            application,
            WeatherDatabase::class.java,
            AppModule.WEATHER_DATABASE
        ).build()
    }

    fun provideDao(database: WeatherDatabase): WeatherDAO {
        return database.weatherDao()
    }

    single { LocalDataSourceImpl(get(), get(), get()) } bind LocalDataSource::class
    single { RemoteDataSourceImpl(get()) } bind RemoteDataSource::class
    single { WeatherListRepositoryImpl(get(), get(), get()) } bind (WeatherRepository::class)
    viewModel { WeatherViewModel(get(), get()) }

    single(qualifier = null) { moduleInstance.ktorClient() }
    single { NetworkUtils() }
    single { WeatherMappers() }
    single { Gson() }

    single { provideDatabase(androidApplication()) } bind WeatherDatabase::class
    single { provideDao(get()) }
    single { LocationUtils(get()) }
    single { PermissionsHandler() }
}


class AppModule {

    fun ktorClient() = HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                }
            )
        }
        install(ResponseObserver) {
            onResponse { response ->
                Log.i("KOIN", "${response.status.value}")
            }
        }
        engine {
            connectTimeout = TIMEOUT
            socketTimeout = TIMEOUT
        }
    }

    companion object {
        const val WEATHER_DATABASE = "weather_database"
    }
}