# RetrofitCoroutinesLiveData
[![](https://jitpack.io/v/com.gitee.cbfg5210/RetrofitCoroutinesLiveData.svg)](https://jitpack.io/#com.gitee.cbfg5210/RetrofitCoroutinesLiveData)

基于 [limuyang2/RetrofitLivedataDemo](https://github.com/limuyang2/RetrofitLivedataDemo) 进行了一些小修改

### 引入依赖
#### Step 1. Add the JitPack repository to your build file
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
#### Step 2. Add the dependency
```gradle
dependencies {
    implementation 'com.gitee.cbfg5210:RetrofitCoroutinesLiveData:$version'
}
```

### 使用
#### Step 1. 初始化
```kotlin
 HttpManager.init { okHttpClientBuilder, retrofitBuilder ->
    okHttpClientBuilder.connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        ...
        //.addInterceptor(ApiInterceptor) //如果需要对请求进行加解密的话加上

    retrofitBuilder.baseUrl("https://api.apiopen.top")
        .addConverterFactory(MoshiConverterFactory.create())
        ...
 }
```
#### Step 2. 定义请求 interface
```kotlin
interface NewsApi {
    /**
     * 接口需要加上 [suspend] ！
     * 返回值，直接就是你的数据类型，不需要再包装其他的东西了，超级简介
     */
    @GET("/getWangYiNews")
    suspend fun getNews(): NewsBean
}
```
#### Step 3. 调用
```kotlin
    val liveData = viewModelScope.simpleRequestLiveData<NewsBean> {
        api { newsApi.getNews() }

        /**
         * 以下内容为可选实现
         */
        /*
        // 加载数据库缓存，直接返回 room 数据库的 LiveData
        loadCache {
            return@loadCache roomLiveData
        }
        */

        /*
        // 保存数据到 room 数据库
        saveCache {
        }
        */
    }
```

### 关于加解密
如果需要对请求进行解密或对请求结果进行解密，在初始化的时候加上这句：
```kotlin
okHttpClientBuilder.addInterceptor(ApiInterceptor)
```
然后调用：
```kotlin
 ApiInterceptor.addHandler(handler: IApiHandler, index: Int = -1)
```
添加加解密处理器 - IApiHandler，其中的接口内容如下：
```kotlin
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
```