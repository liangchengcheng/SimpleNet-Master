package com.example.chengcheng.network.httpstacks;

import android.net.http.AndroidHttpClient;
import com.example.chengcheng.network.base.Request;
import com.example.chengcheng.network.base.Response;
import com.example.chengcheng.network.config.HttpClientConfig;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import java.util.Map;
import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * Author:  梁铖城
 * Email:   1038127753@qq.com
 * Date:    2015年12月5日22:01:49
 * Description:
 */
public class HttpClientStack implements HttpStack {

    /**
     * 使用HttpClient执行网络请求时的Https配置
     */
    HttpClientConfig mConfig = HttpClientConfig.getsConfig();

    /**
     * HttpClient
     */
    HttpClient mHttpClient = AndroidHttpClient.newInstance(mConfig.userAgent);

    @Override
    public Response performRequest(Request<?> request) {
        try {
            HttpUriRequest httpRequest = createHttpRequest(request);
            // 添加连接参数
            setConnectionParams(httpRequest);
            // 添加header
            addHeaders(httpRequest, request.getmHeaders());
            // https配置
            configHttps(request);
            // 执行请求
            HttpResponse response = mHttpClient.execute(httpRequest);
            // 构建Response
            Response rawResponse = new Response(response.getStatusLine());
            // 设置Entity
            rawResponse.setEntity(response.getEntity());
            return rawResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 如果是https请求,则使用用户配置的SSLSocketFactory进行配置.
     * @param request 请求
     */
    private void configHttps(Request<?> request) {
        SSLSocketFactory sslSocketFactory = mConfig.getSocketFactory();
        if (request.isHttps() && sslSocketFactory != null) {
            Scheme sch = new Scheme("https", sslSocketFactory, 443);
            mHttpClient.getConnectionManager().getSchemeRegistry().register(sch);
        }
    }

    /**
     * 设置连接参数,这里比较简单啊.一些优化设置就没有写了.
     * @param httpUriRequest uri请求
     */
    private void setConnectionParams(HttpUriRequest httpUriRequest) {
        HttpParams httpParams = httpUriRequest.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, mConfig.connTimeOut);
        HttpConnectionParams.setSoTimeout(httpParams, mConfig.soTimeOut);
    }

    /**
     * 根据请求类型创建不同的Http请求
     * @param request 不同的Http请求
     * @return  HttpUriRequest
     */
    static HttpUriRequest createHttpRequest(Request<?> request) {
        HttpUriRequest httpUriRequest = null;
        switch (request.getmHttpMethod()) {
            case GET:
                httpUriRequest = new HttpGet(request.getmUrl());
                break;
            case DELETE:
                httpUriRequest = new HttpDelete(request.getmUrl());
                break;
            case POST: {
                httpUriRequest = new HttpPost(request.getmUrl());
                httpUriRequest.addHeader(Request.HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody((HttpPost) httpUriRequest, request);
            }
            break;
            case PUT: {
                httpUriRequest = new HttpPut(request.getmUrl());
                httpUriRequest.addHeader(Request.HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody((HttpPut) httpUriRequest, request);
            }
            break;
            default:
                throw new IllegalStateException("Unknown request method.");
        }

        return httpUriRequest;
    }

    private static void addHeaders(HttpUriRequest httpRequest, Map<String, String> headers) {
        for (String key : headers.keySet()) {
            httpRequest.setHeader(key, headers.get(key));
        }
    }

    /**
     * 将请求参数设置到HttpEntity中
     * @param httpRequest 请求参数
     * @param request HttpEntity请求
     */
    private static void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest,
                                                Request<?> request) {
        byte[] body = request.getBody();
        if (body != null) {
            HttpEntity entity = new ByteArrayEntity(body);
            httpRequest.setEntity(entity);
        }
    }
}
