package com.example.chengcheng.network.config;

import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * Author:  梁铖城
 * Email:   1038127753@qq.com
 * Date: 2015年12月5日21:18:49
 * Description:
 */
public class HttpClientConfig extends HttpConfig {

    private static HttpClientConfig sConfig=new HttpClientConfig();
    SSLSocketFactory mSslSocketFactory;

    private HttpClientConfig(){

    }

    public  static HttpClientConfig getsConfig(){
        return sConfig;
    }

    /**
     * 配置https请求的SSLSocketFactory与HostnameVerifier
     * @param sslSocketFactory
     */
    public void setHttpsConfig(SSLSocketFactory sslSocketFactory){
        mSslSocketFactory=sslSocketFactory;
    }

    public SSLSocketFactory getSocketFactory() {
        return mSslSocketFactory;
    }
}
