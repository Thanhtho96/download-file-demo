package com.tt.downloadfile.network

import com.tt.downloadfile.request.UserLogin
import com.tt.downloadfile.response.DataBracketResponse
import com.tt.downloadfile.response.TokenState
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface DownloadService {
    @POST("api/user/login")
    suspend fun doLogin(@Body user: UserLogin, @Query("type") type: String?): Response<DataBracketResponse<TokenState>>

    @GET("api/jobs/downloadFile/{fileName}")
    suspend fun downloadFile(@Header("Authorization") token: String?, @Path("fileName") fileName: String): ResponseBody
}