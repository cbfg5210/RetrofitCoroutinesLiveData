package com.example.retrofitdemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cbfg.http.HttpManager
import cbfg.http.ResultData
import cbfg.http.simpleRequestLiveData

/**
 * @author 李沐阳
 * @date：2020/5/5
 * @description:
 */
class MainViewModel : ViewModel() {

    private val newsApi = HttpManager.retrofit.create(NewsApi::class.java)

    private val _newsLiveData = MediatorLiveData<ResultData<NewsBean>>()

    // 对外暴露的只是抽象的LiveData，防止外部随意更改数据
    val newsLiveData: LiveData<ResultData<NewsBean>>
        get() = _newsLiveData

    fun getNews() {
        val liveData = viewModelScope.simpleRequestLiveData<NewsBean> {
            api { newsApi.getNews() }

            /**
             * 以下内容为可选实现
             */

            // 加载数据库缓存，直接返回 room 数据库的 LiveData
//            loadCache {
//                return@loadCache roomLiveData
//            }

            // 保存数据到 room 数据库
//            saveCache {
//            }
        }

        // 监听数据变化
        _newsLiveData.addSource(liveData)
    }
}