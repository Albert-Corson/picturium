package com.example.picturium.repositories

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.picturium.api.ImgurAPI
import com.example.picturium.api.response.GalleryResponse
import com.example.picturium.models.Submission
import retrofit2.HttpException
import java.io.IOException

private const val IMGUR_STARTING_PAGE_INDEX = 1

class GalleryRepository {
    fun getGallery(section: String, sort: String, window: String): LiveData<PagingData<Submission>> {

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GalleryPagingSource { position ->
                    ImgurAPI.instance.getGallery(section, sort, window, position)
                }
            }
        ).liveData
    }

    fun getSearchGallery(sort: String, window: String, query: String): LiveData<PagingData<Submission>> {

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GalleryPagingSource { position ->
                    ImgurAPI.instance.getSearchGallery(sort, window, position, query)
                }
            }
        ).liveData
    }

    class GalleryPagingSource(
        private val apiRequest: suspend (position: Int) -> GalleryResponse
    ) : PagingSource<Int, Submission>() {

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Submission> {
            val position = params.key ?: IMGUR_STARTING_PAGE_INDEX
            return try {
                val response = apiRequest(position).data

                LoadResult.Page(
                    data = response,
                    prevKey = if (position == IMGUR_STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = if (response.isEmpty()) null else position + 1,
                )
            } catch (exception: IOException) {
                LoadResult.Error(exception)
            } catch (exception: HttpException) {
                LoadResult.Error(exception)
            }
        }
    }
}