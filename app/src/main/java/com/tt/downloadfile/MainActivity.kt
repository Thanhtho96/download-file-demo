package com.tt.downloadfile

import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.tt.downloadfile.databinding.ActivityMainBinding
import com.tt.downloadfile.viewmodel.DownloadViewModel
import okhttp3.ResponseBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.*
import java.net.URL
import java.net.URLConnection


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val downloadViewModel: DownloadViewModel by viewModel()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var token: String? =
            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1OTI0NjI4OTIsInVzZXJuYW1lIjoidGhhbmhiYXQ5NkBnbWFpbC5jb20ifQ.oT9Nu8CaHJWX4HdqeM9XA_x9LB_lMW_3Gv3y-CbrEOY"

        binding.buttonLogin.setOnClickListener {
            Log.d("dir", getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString())
        }

        binding.buttonDownload.setOnClickListener {
            object : AsyncTask<String?, Int?, String?>() {
                override fun onPreExecute() {
                    progressBar = binding.progressHorizontal
                    progressBar.max = 100
                }

                override fun doInBackground(vararg params: String?): String? {
                    var url: URL = URL(params[0])
                    var con: URLConnection = url.openConnection()
                    con.connect()
                    var fileLeng = con.contentLength
                    var filePath =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path

                    var inputStream = BufferedInputStream(url.openStream())
                    var outputStream =
                            FileOutputStream("/storage/emulated/0/Android/data/com.tt.downloadfile/files/Download" + "/Annotation 2020-03-10 021514.png")
                    val data = ByteArray(1024)
                    var total = 0
                    var count: Int
                    while (inputStream.read(data).also { count = it } !== -1) {
                        total += count
                        publishProgress(total * 100 / fileLeng)
                        outputStream.write(data, 0, count)
                    }

                    outputStream.flush()
                    outputStream.close()
                    inputStream.close()
                    return null
                }

                override fun onProgressUpdate(vararg values: Int?) {
                    super.onProgressUpdate(*values)
                    values[0]?.let { it1 -> progressBar.setProgress(it1) }
                }
            }.execute("https://handsomeman.herokuapp.com/api/jobs/downloadFile/Annotation%202020-03-10%20021514.png")
//                val writtenToDisk: Boolean = writeResponseBodyToDisk(response)
//                Toast.makeText(this, "Download", Toast.LENGTH_SHORT).show()
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
}
