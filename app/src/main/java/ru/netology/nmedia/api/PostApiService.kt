package ru.netology.nmedia.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"

private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

private val client = OkHttpClient.Builder()
    .let{
        if (BuildConfig.DEBUG){
            it.addInterceptor(logging)
        } else {
            it
        }
    }
    .connectTimeout(30, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .baseUrl(BASE_URL)
    .build()

interface PostApiService {

    @GET("posts")
    fun getAllPosts(): Call<List<Post>>

    @POST("posts")
    fun savePost(@Body body: Post): Call<Post>

    @POST("posts/{id}/likes")
    fun likePostById(@Path("id") postId: Long): Call<Post>

    @DELETE("posts/{id}/likes")
    fun dislikePostById(@Path("id") postId: Long): Call<Post>

    @DELETE("posts/{id}")
    fun removePostById(@Path("id") postId: Long): Call<Unit>
}

object PostApi {
    val service: PostApiService by lazy {
        retrofit.create()
    }
}