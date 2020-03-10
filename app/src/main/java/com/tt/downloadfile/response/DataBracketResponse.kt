package com.tt.downloadfile.response

class DataBracketResponse<T : Any?> : StandardResponse() {
    var data: T? = null
        private set

    fun setData(data: T) {
        this.data = data
    }
}

