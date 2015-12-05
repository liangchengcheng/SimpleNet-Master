package com.example.chengcheng.network.base;

/**
 * Author:  梁铖城
 * Email:   1038127753@qq.com
 * Date:    2015年12月4日16:16:15
 * Description:   网络请求的回调
 */
public interface RequestListener<T> {

    void onComplete(int stCode,T response, String errMsg);
}
