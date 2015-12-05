package com.example.chengcheng.network.cache;

/**
 * Author:  梁铖城
 * Email:   1038127753@qq.com
 * Date:  2015年12月4日15:37:37
 * Description: 请求缓存的接口，K 代表键 V 代表值
 */
public interface Cache<K,V> {

    //获取换粗
    V get(K key);

    void put(K key, V value);

    void remove(K key);
}
