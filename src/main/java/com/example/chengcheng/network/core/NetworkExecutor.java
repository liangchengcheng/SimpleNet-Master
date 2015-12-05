package com.example.chengcheng.network.core;

import android.util.Log;

import com.example.chengcheng.network.base.Request;
import com.example.chengcheng.network.base.Response;
import com.example.chengcheng.network.cache.Cache;
import com.example.chengcheng.network.cache.LruMemCache;
import com.example.chengcheng.network.httpstacks.HttpStack;

import java.util.concurrent.BlockingQueue;

/**
 * Author:  梁铖城
 * Email:   1038127753@qq.com
 * Date:    2015年12月5日21:31:10
 * Description: 网络请求Executor,继承自Thread,从网络请求队列中循环读取请求并且执行
 */
final class NetworkExecutor extends Thread{
    /**
     * 网络请求队列
     */
    private BlockingQueue<Request<?>> mRequestQueue;

    /**
     * 网络请求栈
     */
    private HttpStack mHttpStack;

    /**
     * 结果分发器,将结果投递到主线程
     */
    private static ResponseDelivery mResponseDelivery = new ResponseDelivery();

    /**
     * 请求缓存
     */
    private static Cache<String, Response> mReqCache = new LruMemCache();

    /**
     * 是否停止
     */
    private boolean isStop = false;

    public NetworkExecutor(BlockingQueue<Request<?>> queue, HttpStack httpStack) {
        mRequestQueue = queue;
        mHttpStack = httpStack;
    }

    @Override
    public void run() {
        try {
            while (!isStop) {
                final Request<?> request = mRequestQueue.take();
                if (request.isCanceled()) {
                    Log.d("### ", "### 取消执行了");
                    continue;
                }
                Response response = null;
                if (isUseCache(request)) {
                    // 从缓存中取
                    response = mReqCache.get(request.getmUrl());
                } else {
                    // 从网络上获取数据
                    response = mHttpStack.performRequest(request);
                    // 如果该请求需要缓存,那么请求成功则缓存到mResponseCache中
                    if (request.shouldCache() && isSuccess(response)) {
                        mReqCache.put(request.getmUrl(), response);
                    }
                }
                // 分发请求结果
                mResponseDelivery.deliveryResponse(request, response);
            }
        } catch (InterruptedException e) {
            Log.i("", "### 请求分发器退出");
        }
    }

    private boolean isSuccess(Response response) {
        return response != null && response.getStatusCode() == 200;
    }

    private boolean isUseCache(Request<?> request) {
        return request.shouldCache() && mReqCache.get(request.getmUrl()) != null;
    }

    public void quit() {
        isStop = true;
        interrupt();
    }
}
