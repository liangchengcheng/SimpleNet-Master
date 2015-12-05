package com.example.chengcheng.network.core;

import com.example.chengcheng.network.httpstacks.HttpStack;

/**
 * Author:  梁铖城
 * Email:   1038127753@qq.com
 * Date:    2015年12月5日21:53:35
 * Description: SimpleNet入口
 */
public final class SimpleNet {
    /**
     * 创建一个请求队列,NetworkExecutor数量为默认的数量
     * @return RequestQueue
     */
    public static RequestQueue newRequestQueue() {
        return newRequestQueue(RequestQueue.DEFAULT_CORE_NUMS);
    }

    /**
     * 创建一个请求队列,NetworkExecutor数量为coreNums
     * @param coreNums coreNums
     * @return RequestQueue
     */
    public static RequestQueue newRequestQueue(int coreNums) {
        return newRequestQueue(coreNums, null);
    }

    /**
     * 创建一个请求队列,NetworkExecutor数量为coreNums
     * @param coreNums 线程数量
     * @param httpStack 网络执行者
     * @return RequestQueue
     */
    public static RequestQueue newRequestQueue(int coreNums, HttpStack httpStack) {
        RequestQueue queue = new RequestQueue(Math.max(0, coreNums), httpStack);
        queue.start();
        return queue;
    }
}
