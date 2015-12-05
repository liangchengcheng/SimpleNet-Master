package com.example.chengcheng.network.core;

import android.os.Handler;
import android.os.Looper;

import com.example.chengcheng.network.base.Request;
import com.example.chengcheng.network.base.Response;

import java.util.concurrent.Executor;


/**
 * Author:  梁铖城
 * Email:   1038127753@qq.com
 * Date:  2015年12月5日21:36:39
 * Description: 请求结果投递类,将请求结果投递给UI线程
 */
public class ResponseDelivery implements Executor{

    /*主线程*/
    Handler mResponseHandler = new Handler(Looper.getMainLooper());

    /**
     * 处理请求结果,将其执行在UI线程
     * @param request  请求
     * @param response 返回
     */
    public void deliveryResponse(final Request<?> request, final Response response) {
        Runnable respRunnable = new Runnable() {

            @Override
            public void run() {
                request.deliveryResponse(response);
            }
        };

        execute(respRunnable);
    }
    @Override
    public void execute(Runnable command) {
        mResponseHandler.post(command);
    }
}
