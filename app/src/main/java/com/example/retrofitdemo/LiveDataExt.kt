package com.example.retrofitdemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import cbfg.http.RequestStatus
import cbfg.http.ResultData

/**
 * @author 李沐阳
 * @date：2020/5/6
 * @description:
 */

fun <T> MediatorLiveData<ResultData<T>>.addSource(liveData: LiveData<ResultData<T>>) {
    this.addSource(liveData) {
        if (it.requestStatus == RequestStatus.COMPLETE) {
            this.removeSource(liveData)
        }
        this.value = it
    }
}