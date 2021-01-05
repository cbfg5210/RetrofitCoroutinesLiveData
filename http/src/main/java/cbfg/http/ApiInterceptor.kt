package cbfg.http

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

object ApiInterceptor : Interceptor {
    private val apiHandlers = ArrayList<IApiHandler>()

    fun addHandler(handler: IApiHandler, index: Int = -1) {
        if (index == -1 || index >= apiHandlers.size) {
            apiHandlers.add(handler)
        } else {
            apiHandlers.add(index, handler)
        }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        var targetHandler: IApiHandler? = null

        /*
         * 寻找该接口对应的 ApiHandler
         */
        for (handler in apiHandlers) {
            if (handler.isApi(request)) {
                targetHandler = handler
                break
            }
        }

        /*
         * 该接口不需要进行处理，直接返回请求结果
         */
        if (targetHandler == null) {
            return chain.proceed(request)
        }

        //对请求进行处理
        request = targetHandler.handleRequest(request)

        //对返回结果进行处理
        var response: Response = chain.proceed(request)
        response = targetHandler.handleResponse(response, chain)

        return response
    }
}