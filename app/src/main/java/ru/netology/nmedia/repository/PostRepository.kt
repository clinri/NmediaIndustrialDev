package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAllAsync(callback: PostCallback<List<Post>>)
    fun likeByIdAsync(id: Long, callback: PostCallback<Post>)
    fun dislikeByIdAsync(id: Long, callback: PostCallback<Post>)
    fun saveAsync(post: Post, callback: PostCallback<Unit>)
    fun removeByIdAsync(id: Long, callback: PostCallback<Unit>)

    interface PostCallback <T> {
        fun onSuccess(data: T)
        fun onError(e: Exception)
    }
}
