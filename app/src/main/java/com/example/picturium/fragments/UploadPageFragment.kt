package com.example.picturium.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picturium.R
import com.example.picturium.adapters.UploadAdapter
import com.example.picturium.api.ImgurAPI
import kotlinx.android.synthetic.main.fragment_upload_page.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.io.InputStream


class UploadPageFragment : Fragment(R.layout.fragment_upload_page) {

    lateinit var adapter: UploadAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        upload_imageButton.setOnClickListener { choosePicture() }
        upload_ibDone.setOnClickListener { GlobalScope.launch(Dispatchers.Main) { uploadAlbum() } }

        val list: RecyclerView = upload_rvImage
        adapter = UploadAdapter()
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            data?.data?.let {
                adapter.addUri(it)
            }
        }
    }

    private fun choosePicture() {
        val intent = Intent()

        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    @Throws(IOException::class)
    private suspend fun uploadAlbum() {
        val imagesId = mutableListOf<String>()
        val list = adapter.getUris()
        var inputStream: InputStream?
        var binary: ByteArray?
        var requestImage: MultipartBody.Part
        val context = this.requireContext()
        val titleAlbum: RequestBody = upload_etTitle.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionAlbum: RequestBody = upload_etDescription.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val idsAlbum: MutableList<RequestBody> = mutableListOf()
        val coverAlbum: RequestBody

        list.forEach { uri ->
            inputStream = context.contentResolver.openInputStream(uri)
            binary = inputStream?.readBytes()
            binary?.toRequestBody("multipart/form-data".toMediaTypeOrNull(), 0, binary!!.size)?.let { bin ->
                requestImage = MultipartBody.Part.createFormData("image", "my_image", bin)
                val imageInfo = ImgurAPI.safeCall {
                    ImgurAPI.instance.newImage(requestImage)
                }
                if (imageInfo is ImgurAPI.CallResult.SuccessResponse) {
                    imageInfo.data.id?.let { imagesId.add(it) }
                }
            }
        }
        if (imagesId.isEmpty())
            return
        imagesId.forEach { value ->
            idsAlbum.add(value.toRequestBody("text/plain".toMediaTypeOrNull()))
        }
        coverAlbum = imagesId.first().toRequestBody("text/plain".toMediaTypeOrNull())

        val tmp = ImgurAPI.safeCall {
            ImgurAPI.instance.newAlbum(titleAlbum, descriptionAlbum, idsAlbum, coverAlbum)
        }
    }
}