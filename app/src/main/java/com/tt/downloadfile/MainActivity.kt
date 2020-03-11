package com.tt.downloadfile

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tt.downloadfile.databinding.ActivityMainBinding
import com.tt.downloadfile.viewmodel.DownloadViewModel
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val downloadViewModel: DownloadViewModel by viewModel()
    private var downloadJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val edtUserName = binding.username
        val progressBar = binding.progressHorizontal
        val btnDownload = binding.buttonDownload
        progressBar.max = 100

        val token =
            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1OTI0NjI4OTIsInVzZXJuYW1lIjoidGhhbmhiYXQ5NkBnbWFpbC5jb20ifQ.oT9Nu8CaHJWX4HdqeM9XA_x9LB_lMW_3Gv3y-CbrEOY"

        binding.buttonLogin.setOnClickListener {
            Log.d("dir", getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString())
        }

        btnDownload.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            btnDownload.isEnabled = false
            val fileName = edtUserName.text.toString()

            downloadJob = CoroutineScope(Dispatchers.IO).launch {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://handsomeman.herokuapp.com/api/jobs/downloadFile/$fileName")
                    .addHeader("Authorization", token)
                    .build()
                var response: Response? = null
                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null
                try {
                    withContext(Dispatchers.IO) {
                        response = client.newCall(request).execute()
                    }
                    // if don't find file available contentLength will return -1
                    if (response?.body?.contentLength()!! > 0) {
                        val file_size: Long? = response?.body?.contentLength()
                        inputStream =
                            BufferedInputStream(response?.body?.byteStream())
                        outputStream = FileOutputStream(
                            getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/$fileName"
                        )
                        val data = ByteArray(8192)
                        var total = 0f
                        var readBytes: Int

                        // below code in java:
                        // while ( (read_bytes = inputStream.read(data)) != -1 )
                        // Wow kotlin
                        withContext(Dispatchers.IO) {
                            while (inputStream.read(data).also { readBytes = it } != -1) {
                                total += readBytes
                                outputStream.write(data, 0, readBytes)
                                progressBar.progress = (total / file_size!! * 100).toInt()
                            }
                        }
                        progressBar.progress = 0
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                    this@MainActivity,
                                    "This file not available",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
                    withContext(Dispatchers.Main) {
                        btnDownload.isEnabled = true
                        progressBar.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    withContext(Dispatchers.IO) {
                        response?.body?.close()
                        inputStream?.close()
                        outputStream?.flush()
                        outputStream?.close()
                    }
                }
            }
        }
    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        return try { // todo change the file location/name according to your needs
            val futureStudioIconFile =
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "/Annotation 2020-03-10 021514.png"
                )
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.d(
                        "FragmentActivity.TAG",
                        "file download: $fileSizeDownloaded of $fileSize"
                    )
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            false
        }
    }

    override fun onDestroy() {
        downloadJob?.cancel()
        Log.d("Destroy", "It's destroy")
        super.onDestroy()
    }
}
