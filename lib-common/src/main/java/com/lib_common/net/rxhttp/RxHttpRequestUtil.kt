package com.lib_common.net.rxhttp

import android.text.TextUtils
import androidx.lifecycle.LifecycleOwner
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.lib_common.net.rxhttp.callback.ErrorInfo
import com.lib_common.net.rxhttp.callback.OnError
import com.lib_common.net.rxhttp.callback.RxHttpCallBack
import com.lib_common.net.rxhttp.callback.RxHttpFile
import com.lib_common.net.rxhttp.response.PagerListBean
import com.rxjava.rxlife.ObservableLife
import com.rxjava.rxlife.RxLife
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.internal.functions.Functions
import rxhttp.wrapper.param.RxHttp.get
import rxhttp.wrapper.param.RxHttp.postForm
import rxhttp.wrapper.param.RxHttp.postJson
import rxhttp.wrapper.param.RxHttp.postJsonArray
import java.io.File


/**
 * 网络工具类
 * 注：
 * 泛型R尽量不要使用基本数据类型的包装类，如Boolean、Integer、Double等，如果接口返回data=null，会出现类型转换错误
 * 可以用String替代
 */
class RxHttpRequestUtil {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RxHttpRequestUtil()
        }
    }

    /**
     * get请求-获取单实体
     *
     * @param url            接口地址
     * @param map            请求参数
     * @param clazz 请求返回的数据对象
     * @param a
     * @param httpCallBack
     */
    fun <R> getData(url: String, paramMap: Map<String, *>?, clazz: Class<R>, owner: LifecycleOwner?, httpCallBack: RxHttpCallBack<R>): Disposable? {
        try {
            var req = paramMap
            if (req == null) {
                req = HashMap<String, String>()
            }
            get(url)
                    .addAll(req)
                    .toObservableResponseNet(clazz) //                    .observeOn(AndroidSchedulers.mainThread())
                    .`as`(RxLife.asOnMain(owner))
                    .subscribe({ success: R ->
                        //成功回调
                        httpCallBack.onSuccess(success)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, OnError { throwable: ErrorInfo ->
                        //失败回调
                        httpCallBack.onError(throwable)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, Functions.EMPTY_ACTION) {
                        // 开始回调
                        httpCallBack.onStart()
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * get请求-获取数组-不分页
     *
     * @param url            接口地址
     * @param map            请求参数
     * @param clazz 请求返回的数据对象
     * @param a
     * @param httpCallBack
     */
    fun <R> getDataList(url: String, paramMap: Map<String, *>?, clazz: Class<R>, owner: LifecycleOwner?, httpCallBack: RxHttpCallBack<List<R>?>): Disposable? {
        try {
            var req = paramMap
            if (req == null) {
                req = HashMap<String, String>()
            }
            return get(url)
                    .addAll(req)
                    .toObservableResponseNetList(clazz) //                    .observeOn(AndroidSchedulers.mainThread())
                    .`as`(RxLife.asOnMain(owner))
                    .subscribe({ success: List<R>? ->
                        //成功回调
                        httpCallBack.onSuccess(success)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, OnError { throwable: ErrorInfo ->
                        //失败回调
                        httpCallBack.onError(throwable)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, Functions.EMPTY_ACTION) {
                        // 开始回调
                        httpCallBack.onStart()
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * post请求-获取单实体
     *
     * @param url            接口地址
     * @param jsonReq            请求参数
     * @param clazz 请求返回的数据对象
     * @param a
     * @param httpCallBack
     */
    fun <R> postData(url: String, jsonReq: String?, clazz: Class<R>, owner: LifecycleOwner?, httpCallBack: RxHttpCallBack<R>): Disposable? {
        try {
            var jsonReq = jsonReq
            if (TextUtils.isEmpty(jsonReq)) {
                // RxHttp不接收Null或者空字符串入参
                jsonReq = JSONObject().toString()
            }
            return postJson(url)
                    .addAll(jsonReq!!)
                    .toObservableResponseNet(clazz) //                    .observeOn(AndroidSchedulers.mainThread())
                    .`as`(RxLife.asOnMain(owner))
                    .subscribe({ success: R ->
                        //成功回调
                        httpCallBack.onSuccess(success)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, OnError { throwable: ErrorInfo ->
                        //失败回调
                        httpCallBack.onError(throwable)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, Functions.EMPTY_ACTION) {
                        // 开始回调
                        httpCallBack.onStart()
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * post请求-获取单实体
     *
     * @param url            接口地址
     * @param map            请求参数
     * @param clazz 请求返回的数据对象
     * @param a
     * @param httpCallBack
     */
    fun <R> postData(url: String, paramMap: Map<String, *>?, clazz: Class<R>, owner: LifecycleOwner?, httpCallBack: RxHttpCallBack<R>): Disposable? {
        try {
            var req = paramMap
            if (req == null) {
                req = HashMap<String, String>()
            }
            return postJson(url)
                    .addAll(req)
                    .toObservableResponseNet(clazz) //                    .observeOn(AndroidSchedulers.mainThread())
                    .`as`<ObservableLife<R>>(RxLife.asOnMain<R>(owner))
                    .subscribe({ success: R ->
                        //成功回调
                        httpCallBack.onSuccess(success)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, OnError { throwable: ErrorInfo ->
                        //失败回调
                        httpCallBack.onError(throwable)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, Functions.EMPTY_ACTION, {
                        // 开始回调
                        httpCallBack.onStart()
                    })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * post请求-获取图片地址
     *
     * @param url            接口地址
     * @param map            请求参数
     * @param clazz 请求返回的数据对象
     * @param a
     * @param httpCallBack
     */
    fun <R> postPics(url: String, paramMap: Map<String, *>?, clazz: Class<R>, owner: LifecycleOwner?, httpCallBack: RxHttpCallBack<R>): Disposable? {
        try {
            var req = paramMap
            if (req == null) {
                req = HashMap<String, String>()
            }
            return postJson(url)
                    .setDomainToBaseFileServerUrlIfAbsent()
                    .addAll(req)
                    .toObservableResponseNet(clazz) //                    .observeOn(AndroidSchedulers.mainThread())
                    .`as`(RxLife.asOnMain(owner))
                    .subscribe({ success: R ->
                        //成功回调
                        httpCallBack.onSuccess(success)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, OnError { throwable: ErrorInfo ->
                        //失败回调
                        httpCallBack.onError(throwable)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, Functions.EMPTY_ACTION) {
                        // 开始回调
                        httpCallBack.onStart()
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * post请求-获取数组-不分页
     *
     * @param url            接口地址
     * @param map            请求参数
     * @param clazz 请求返回的数据对象
     * @param a
     * @param httpCallBack
     */
    fun <R> postDataList(url: String, paramMap: Map<String, *>?, clazz: Class<R>, owner: LifecycleOwner?, httpCallBack: RxHttpCallBack<List<R>?>): Disposable? {
        try {
            var req = paramMap
            if (req == null) {
                req = HashMap<String, String>()
            }
            return postJson(url)
                    .addAll(req)
                    .toObservableResponseNetList<R>(clazz) //                    .observeOn(AndroidSchedulers.mainThread())
                    .`as`<ObservableLife<List<R>>>(RxLife.asOnMain<List<R>>(owner))
                    .subscribe({ success: List<R> ->
                        //成功回调
                        httpCallBack.onSuccess(success)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, OnError { throwable: ErrorInfo ->
                        //失败回调
                        httpCallBack.onError(throwable)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, Functions.EMPTY_ACTION, {
                        // 开始回调
                        httpCallBack.onStart()
                    })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * post请求-获取数组-不分页
     *
     * @param url            接口地址
     * @param jsonReq            请求参数
     * @param clazz 请求返回的数据对象
     * @param a
     * @param httpCallBack
     */
    fun <R> postDataList(url: String, jsonReq: String?, clazz: Class<R>, owner: LifecycleOwner?, httpCallBack: RxHttpCallBack<List<R>?>): Disposable? {
        try {
            var jsonReq = jsonReq
            if (TextUtils.isEmpty(jsonReq)) {
                // RxHttp不接收Null或者空字符串入参
                jsonReq = JSONObject().toString()
            }
            return postJson(url)
                    .addAll(jsonReq!!)
                    .toObservableResponseNetList(clazz) //                    .observeOn(AndroidSchedulers.mainThread())
                    .`as`(RxLife.asOnMain(owner))
                    .subscribe({ success: List<R>? ->
                        //成功回调
                        httpCallBack.onSuccess(success)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, OnError { throwable: ErrorInfo ->
                        //失败回调
                        httpCallBack.onError(throwable)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, Functions.EMPTY_ACTION) {
                        // 开始回调
                        httpCallBack.onStart()
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * post请求-获取数组-分页
     *
     * @param url            接口地址
     * @param jsonReq            请求参数
     * @param clazz 请求返回的数据对象
     * @param a
     * @param httpCallBack
     */
    fun <R> postPageList(url: String, jsonReq: String?, clazz: Class<R>, owner: LifecycleOwner?, httpCallBack: RxHttpCallBack<PagerListBean<R>?>): Disposable? {
        try {
            var jsonReq = jsonReq
            if (TextUtils.isEmpty(jsonReq)) {
                // RxHttp不接收Null或者空字符串入参
                jsonReq = JSONObject().toString()
            }
            return postJson(url)
                    .addAll(jsonReq!!)
                    .toObservableResponseNetPagerListBean(clazz) //                    .observeOn(AndroidSchedulers.mainThread())
                    .`as`(RxLife.asOnMain(owner))
                    .subscribe({ success: PagerListBean<R> ->
                        //成功回调
                        httpCallBack.onSuccess(success)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, OnError { throwable: ErrorInfo ->
                        //失败回调
                        httpCallBack.onError(throwable)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, Functions.EMPTY_ACTION) {
                        // 开始回调
                        httpCallBack.onStart()
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * post请求-获取数组-分页
     *
     * @param url            接口地址
     * @param map            请求参数
     * @param clazz 请求返回的数据对象
     * @param a
     * @param httpCallBack
     */
    fun <R> postPageList(url: String, paramMap: Map<String, *>?, clazz: Class<R>, owner: LifecycleOwner?, httpCallBack: RxHttpCallBack<PagerListBean<R>?>): Disposable? {
        try {
            var req = paramMap
            if (req == null) {
                req = HashMap<String, String>()
            }
            return postJson(url)
                    .addAll(req)
                    .toObservableResponseNetPagerListBean(clazz) //                    .observeOn(AndroidSchedulers.mainThread())
                    .`as`(RxLife.asOnMain(owner))
                    .subscribe({ success: PagerListBean<R>? ->
                        //成功回调
                        httpCallBack.onSuccess(success)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, OnError { throwable: ErrorInfo ->
                        //失败回调
                        httpCallBack.onError(throwable)
                        // 完成回调
                        httpCallBack.onFinish()
                    }, Functions.EMPTY_ACTION) {
                        // 开始回调
                        httpCallBack.onStart()
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 上传文件
     *
     * @param url        上传地址
     * @param key        文件key
     * @param file   本地文件
     * @param rxHttpFile 上传回调
     */
    fun <T : Any> uploadFile(url: String, key: String, file: File, paramMap: Map<String, *>?, clazz: Class<T>, owner: LifecycleOwner?, rxHttpFile: RxHttpFile<T>) {
        var req = paramMap
        if (req == null) {
            req = HashMap<String, String>()
        }
        postForm(url) //发送Form表单形式的Post请求
                .setDomainToBaseFileServerUrlIfAbsent()
                .addAll(req)
                .addFile(key, file)
                .toObservableResponseNet(clazz)
                .onProgress(AndroidSchedulers.mainThread()) { progress -> rxHttpFile.onProgress(progress.progress, progress.currentSize, progress.totalSize) }
                .`as`(RxLife.asOnMain(owner))
                .subscribe(object : Observer<T> {
                    override fun onSubscribe(d: Disposable) {
                        rxHttpFile.onStart()
                    }

                    override fun onNext(s: T) {
                        rxHttpFile.onSuccess(s)
                    }

                    override fun onError(e: Throwable) {
                        rxHttpFile.onError(e.message)
                    }

                    override fun onComplete() {
                        rxHttpFile.onFinish()
                    }
                })
    }

    /**
     * 批量上传文件
     *
     * @param url        上传地址
     * @param key        文件key
     * @param files   本地文件
     * @param map   入参
     * @param clazz   返回结果
     * @param a   上下文
     * @param rxHttpFile 上传回调
     */
    fun <T : Any> uploadFileList(url: String, key: String, files: List<File>, paramMap: Map<String, *>?, clazz: Class<T>, owner: LifecycleOwner?, rxHttpFile: RxHttpFile<T>) {
        var req = paramMap
        if (req == null) {
            req = HashMap<String, String>()
        }
        postForm(url) //发送Form表单形式的Post请求
                .setDomainToBaseFileServerUrlIfAbsent()
                .addAll(req)
                .addFiles(key, files)
                .toObservableResponseNet(clazz)
                .onProgress(AndroidSchedulers.mainThread()) { progress -> rxHttpFile.onProgress(progress.progress, progress.currentSize, progress.totalSize) }
                .`as`(RxLife.asOnMain(owner))
                .subscribe(object : Observer<T> {
                    override fun onSubscribe(d: Disposable) {
                        rxHttpFile.onStart()
                    }

                    override fun onNext(s: T) {
                        rxHttpFile.onSuccess(s)
                    }

                    override fun onError(e: Throwable) {
                        rxHttpFile.onError(e.message)
                    }

                    override fun onComplete() {
                        rxHttpFile.onFinish()
                    }
                })
    }

    /**
     * 文件下载-暂未使用
     *
     * @param filePath  下载的文件网络位置
     * @param destPath 下载到本地的位置
     * @param a        activity
     * @param httpFile 下载回调
     */
    fun downloadFile(filePath: String, destPath: String, owner: LifecycleOwner, httpFile: RxHttpFile<String?>) {
        val length = File(destPath).length() //已下载的文件长度
        get(filePath)
                .setDomainToBaseFileServerUrlIfAbsent()
                .setRangeHeader(length) // 断点下载
                .toDownloadObservable(destPath)
                .onProgress(AndroidSchedulers.mainThread()) { progress -> httpFile.onProgress(progress.progress, progress.currentSize, progress.totalSize) }
                .`as`(RxLife.asOnMain(owner))
                .subscribe(object : Observer<String?> {
                    override fun onSubscribe(d: Disposable) {
                        httpFile.onStart()
                    }

                    override fun onNext(s: String) {
                        httpFile.onSuccess(s)
                    }

                    override fun onError(e: Throwable) {
                        httpFile.onError(e.message)
                    }

                    override fun onComplete() {
                        httpFile.onFinish()
                    }
                })
    }

    /**
     * postArray请求-获取单实体
     *
     * @param url            接口地址
     * @param map            请求参数
     * @param clazz 请求返回的数据对象
     * @param a
     * @param httpCallBack
     */
    fun <R> postArrayData(url: String, arrayReq: String?, clazz: Class<R>, owner: LifecycleOwner?, httpCallBack: RxHttpCallBack<R>): Disposable? {
        try {
            var jsonReq = arrayReq
            if (TextUtils.isEmpty(jsonReq)) {
                // RxHttp不接收Null或者空字符串入参
                jsonReq = JSONArray().toString()
            }
            return postJsonArray(url)
                .addAll(jsonReq!!)
                .toObservableResponseNet(clazz) //                    .observeOn(AndroidSchedulers.mainThread())
                .`as`<ObservableLife<R>>(RxLife.asOnMain(owner))
                .subscribe({ success: R ->
                    //成功回调
                    httpCallBack.onSuccess(success)
                    // 完成回调
                    httpCallBack.onFinish()
                }, OnError { throwable: ErrorInfo ->
                    //失败回调
                    httpCallBack.onError(throwable)
                    // 完成回调
                    httpCallBack.onFinish()
                }, Functions.EMPTY_ACTION, {
                    // 开始回调
                    httpCallBack.onStart()
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * postArray请求-获取数组
     *
     * @param url            接口地址
     * @param map            请求参数
     * @param clazz 请求返回的数据对象
     * @param a
     * @param httpCallBack
     */
    fun <R> postArrayDataList(url: String, arrayReq: String?, clazz: Class<R>, owner: LifecycleOwner?, httpCallBack: RxHttpCallBack<List<R>>): Disposable? {
        try {
            var jsonReq = arrayReq
            if (TextUtils.isEmpty(jsonReq)) {
                // RxHttp不接收Null或者空字符串入参
                jsonReq = JSONArray().toString()
            }
            return postJsonArray(url)
                .addAll(jsonReq!!)
                .toObservableResponseNetList(clazz) //                    .observeOn(AndroidSchedulers.mainThread())
                .`as`<ObservableLife<List<R>>>(RxLife.asOnMain(owner))
                .subscribe({ success: List<R> ->
                    //成功回调
                    httpCallBack.onSuccess(success)
                    // 完成回调
                    httpCallBack.onFinish()
                }, OnError { throwable: ErrorInfo ->
                    //失败回调
                    httpCallBack.onError(throwable)
                    // 完成回调
                    httpCallBack.onFinish()
                }, Functions.EMPTY_ACTION, {
                    // 开始回调
                    httpCallBack.onStart()
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}