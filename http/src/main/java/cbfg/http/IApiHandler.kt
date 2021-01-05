package cbfg.http

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

interface IApiHandler {
    /**
     * @return true: 对此请求进行处理
     */
    fun isApi(request: Request): Boolean

    /**
     * 对 request 进行处理
     *
     * @param request Request
     * @return Request
     */
    fun handleRequest(request: Request): Request

    /**
     * 对 response 进行处理
     *
     * @param response Response
     * @param chain Interceptor.Chain
     * @return Response
     * @throws IOException
     */
    @Throws(IOException::class)
    fun handleResponse(response: Response, chain: Interceptor.Chain): Response
}