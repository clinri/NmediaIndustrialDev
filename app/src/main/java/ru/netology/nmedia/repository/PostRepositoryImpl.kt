package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post


class PostRepositoryImpl : PostRepository {

    override fun getAllAsync(callback: PostRepository.PostCallback<List<Post>>) {
        PostApi.service.getAllPosts()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    val posts = response.body() ?: run {
                        callback.onError(RuntimeException("Body is null"))
                        return
                    }
                    callback.onSuccess(posts)
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.PostCallback<Post>) {
        PostApi.service.likePostById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    val posts = response.body() ?: run {
                        callback.onError(Exception("Body is null"))
                        return
                    }
                    callback.onSuccess(posts)
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }


    override fun dislikeByIdAsync(id: Long, callback: PostRepository.PostCallback<Post>) {
        PostApi.service.dislikePostById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message()))
                        return
                    }
                    val posts = response.body() ?: run {
                        callback.onError(RuntimeException("Body is null"))
                        return
                    }
                    callback.onSuccess(posts)
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }

    override fun saveAsync(post: Post, callback: PostRepository.PostCallback<Unit>) {
        PostApi.service.savePost(post)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message()))
                        return
                    }
                    response.body() ?: run {
                        callback.onError(RuntimeException("Body is null"))
                        return
                    }
                    callback.onSuccess(Unit)
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.PostCallback<Unit>) {
        PostApi.service.removePostById(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    response.body() ?: run {
                        callback.onError(Exception("Body is null"))
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(Exception(t))
                }

            })
    }
}

