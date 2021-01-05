package cbfg.http

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author 李沐阳
 * @date：2020/5/5
 * @description:
 */

open class RequestAction<ResponseType, ResultType> {
    lateinit var api: (suspend () -> ResponseType)

    // 加载数据库缓存
    var mLoadCache: (() -> LiveData<ResultType>)? = null
        private set

    var mSaveCache: ((ResultType) -> Unit)? = null
        private set

    @Suppress("UNCHECKED_CAST")
    var transformer: ((ResponseType?) -> ResultType?) = { it as? ResultType }
        private set

    fun api(block: suspend () -> ResponseType) {
        this.api = block
    }

    fun loadCache(block: (() -> LiveData<ResultType>)?) {
        this.mLoadCache = block
    }

    fun saveCache(block: ((ResultType) -> Unit)?) {
        this.mSaveCache = block
    }

    // 将网络数据类型，转换为需要的数据类型
    fun transformer(block: (ResponseType?) -> ResultType?) {
        this.transformer = block
    }
}


/**
 * DSL网络请求
 * 针对 网络返回的数据类型 和 我们需要的数据类型是一致的情况
 */
inline fun <ResultType> CoroutineScope.simpleRequestLiveData(
    dsl: RequestAction<ResultType, ResultType>.() -> Unit
): LiveData<ResultData<ResultType>> {
    return requestLiveData(dsl)
}

/**
 * DSL网络请求
 *
 * ResponseType 为网络返回的数据类型
 * ResultType 为我们需要的数据类型
 *
 */
inline fun <ResponseType, ResultType> CoroutineScope.requestLiveData(
    dsl: RequestAction<ResponseType, ResultType>.() -> Unit
): LiveData<ResultData<ResultType>> {
    val action = RequestAction<ResponseType, ResultType>().apply(dsl)

    return liveData(this.coroutineContext) {
        // 加载数据库缓存
        action.mLoadCache?.invoke()?.let {
            val cacheResult = Transformations.map(it) { resultType ->
                ResultData.success(resultType, true)
            }
            emitSource(cacheResult)
        }

        // 通知网络请求开始
        emit(ResultData.start<ResultType>())

        val apiResponse = try {
            // 获取网络请求数据
            cbfg.http.ApiResponse.create(action.api())
        } catch (e: Throwable) {
            cbfg.http.ApiResponse.create(e)
        }

        // 根据 ApiResponse 类型，处理对于事物
        val result = when (apiResponse) {
            is ApiEmptyResponse -> null

            is ApiSuccessResponse -> {
                // 转换数据
                val result = action.transformer(apiResponse.body)
                if (result != null) {
                    // 缓存数据到数据库
                    action.mSaveCache?.let {
                        withContext(Dispatchers.IO) {
                            it.invoke(result)
                        }
                    }
                }
                emit(ResultData.success(result, false))
                result
            }

            is ApiErrorResponse -> {
                emit(ResultData.error<ResultType>(apiResponse.throwable))
                null
            }
        }

        emit(ResultData.complete(result))
    }
}