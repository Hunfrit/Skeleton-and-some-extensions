package homecare.android.com.api

import homecare.android.com.BuildConfig
import homecare.android.com.api.RestClient.api
import homecare.android.com.preferences.AppPreferences
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Class provides access to do requests.
 * Use [api] value to send one of the requests described in [Api] class.
 * In this representation you're not allowed to change end point on the fly
 */
object RestClient {

    var END_POINT = ""//TODO: put here your server endPoint.
    private const val TIMEOUT = 40L
    const val MAX_REQUEST_COUNT = 30

    var tokenForChangePassword: String? = null

    var api: Api = provideRetrofit(END_POINT)
        .create(Api::class.java)

    //Used to send several requests in parallel
    private val dispatcher: Dispatcher
        get() {
            val dispatcher = Dispatcher()
            dispatcher.maxRequests = MAX_REQUEST_COUNT
            return dispatcher
        }

    private val authInterceptor: Interceptor
        get() = Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                    .header("Content-type", "application/json")
                    .header("Accept", "application/json")
                    .header("Version", "1")
                    .header("X-EL-API-KEY", "Y7E1WDo5JP4Hy7TvMM3bc8Ege8KN3bV1")
                    .header("Authorization", "Bearer ${tokenForChangePassword ?: AppPreferences.token ?: ""}")
                    .method(original.method(), original.body())
                    .build()
            chain.proceed(request)
        }

    private val loggingInterceptor: HttpLoggingInterceptor
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            return interceptor
        }

    private fun provideRetrofit(endPoint: String): Retrofit {
        val builder = OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(authInterceptor)
                .dispatcher(dispatcher)
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(loggingInterceptor)
        }
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(CoroutineCallAdapterFactory()) //used only with coroutines
                .baseUrl(endPoint)
                .client(builder.build())
                .build()
    }
}
