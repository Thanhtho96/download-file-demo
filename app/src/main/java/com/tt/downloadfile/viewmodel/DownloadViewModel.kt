package com.tt.downloadfile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.tt.downloadfile.network.DownloadService
import com.tt.downloadfile.request.UserLogin
import com.tt.downloadfile.response.TokenState
import okhttp3.ResponseBody

class DownloadViewModel(private val downloadService: DownloadService) : ViewModel() {

    var byteArray: LiveData<ResponseBody> = MutableLiveData()
    var tokenState: LiveData<TokenState?> = MutableLiveData()

    fun downloadFile(token: String?, fileName: String) {
        byteArray = liveData { emit(downloadService.downloadFile(token, fileName)) }
    }

    fun login(userLogin: UserLogin){
        val role: String = "ROLE_HANDYMAN"
        tokenState = liveData { emit(downloadService.doLogin(userLogin, role).body()?.data) }
    }
}