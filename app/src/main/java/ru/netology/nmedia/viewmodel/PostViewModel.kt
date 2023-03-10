package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = "",
    attachment = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    private val edited = MutableLiveData(empty)
    val navigateToNewPostFragment = SingleLiveEvent<String?>()
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    val eventStateResId = SingleLiveEvent<Int?>()

    init {
        loadPosts()
    }

    fun loadPosts() {
        // Начинаем загрузку
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.PostCallback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.value = FeedModel(posts = posts, empty = posts.isEmpty())
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun refreshPosts() {
        // Начинаем обновление
        _data.value = _data.value?.copy(loading = true)
        repository.getAllAsync(object : PostRepository.PostCallback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.value = FeedModel(posts = posts, empty = posts.isEmpty())
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let {
            _data.value = _data.value?.copy(loading = true)
            repository.saveAsync(it, object : PostRepository.PostCallback<Unit> {
                override fun onSuccess(data: Unit) {
                    _postCreated.value = data
                    eventStateResId.value = R.string.save_success
                }

                override fun onError(e: Exception) {
                    _data.value = FeedModel(error = true)
                    eventStateResId.value = R.string.save_error
                }
            })
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
        navigateToNewPostFragment.value = post.content
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    // возвращает измененную FeedModel со списком Post's, в котором один из
    // постов имеет измененный Like
    fun getListPostsWithChangedLikeInPost(id: Long, data: Post): FeedModel? =
        _data.value?.copy(posts = _data.value?.posts.orEmpty()
            .map
            { post ->
                if (post.id != id)
                    post
                else data
            })

    fun likeById(id: Long) {
        repository.likeByIdAsync(id, object : PostRepository.PostCallback<Post> {
            override fun onSuccess(data: Post) {
                eventStateResId.value = R.string.like_success
                _data.value = getListPostsWithChangedLikeInPost(id, data)
            }

            override fun onError(e: Exception) {
                eventStateResId.value = R.string.like_error
                _data.value = FeedModel(error = true)
            }
        })
    }

    fun dislikeById(id: Long) {
        repository.dislikeByIdAsync(id, object : PostRepository.PostCallback<Post> {
            override fun onSuccess(data: Post) {
                eventStateResId.value = R.string.dislike_success
                _data.value = getListPostsWithChangedLikeInPost(id, data)
            }

            override fun onError(e: Exception) {
                eventStateResId.value = R.string.dislike_error
                _data.value = FeedModel(error = true)
            }
        })
    }

    fun removeById(id: Long) {
        // Оптимистичная модель
        val old = _data.value?.posts.orEmpty()
        _data.value = _data.value?.copy(
            posts = _data.value?.posts.orEmpty()
                .filter { it.id != id }
        )
        repository.removeByIdAsync(id, object : PostRepository.PostCallback<Unit> {
            override fun onSuccess(data: Unit) {
                eventStateResId.value = R.string.remove_success
            }

            override fun onError(e: Exception) {
                eventStateResId.value = R.string.remove_error
                _data.value = _data.value?.copy(posts = old)
            }
        })
    }
}















