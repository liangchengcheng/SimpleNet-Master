package com.example.chengcheng.network.base;

import android.util.Property;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Author:  梁铖城
 * Email:   1038127753@qq.com
 * Date:    2015年12月4日15:56:33
 * Description:网络请求类. 注意GET和DELETE不能传递参数,因为其请求的性质所致,用户可以将参数构建到url后传递进来到Request中.
 */
public abstract class Request<T> implements Comparable<Request<T>>{

    /*请求的方式*/
    public static enum HttpMethod{
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");

        private String mHttpMethod="";

        private HttpMethod(String method){
            mHttpMethod=method;
        }

        @Override
        public String toString() {
            return mHttpMethod;
        }
    }

    /*优先级枚举*/
    public static enum Priority{
        LOW,NORMAL,HIGH,IMMEDIATE
    }
    /*默认的编码的格式为utf-8*/
    public static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    /*Content-type*/
    public final static String HEADER_CONTENT_TYPE = "Content-Type";

    /*请求序列号*/
    protected int mSerialNum = 0;

    /*优先级设置为Normal*/
    protected Priority mPriority= Priority.NORMAL;

    /*是否取消请求*/
    protected boolean isCancel=false;

    /** 该请求是否应该缓存 */
    private boolean mSholdCache=true;

    /*请求的Listener*/
    protected RequestListener<T> mRequestListener;

    /*请求的地址*/
    private String mUrl="";

    /*请求的方式*/
    HttpMethod mHttpMethod=HttpMethod.GET;

    /*请求的header*/
    private Map<String,String> mHeaders=new HashMap<>();

    /*请求的参数*/
    private Map<String,String> mBodyParams=new HashMap<>();

    public Request(HttpMethod method,String url,RequestListener<T> listener){
        mHttpMethod=method;
        mUrl=url;
        mRequestListener=listener;
    }

    /*设置头文件*/
    public void addHeader(String name,String value){
        mHeaders.put(name,value);
    }

    /*从原生的网络总解析结果*/
    public abstract T parseResponse(Response response);

    public final void deliveryResponse(Response response){
        T result=parseResponse(response);
        if (mRequestListener!=null){
            int stCode=response!=null? response.getStatusCode():-1;
            String msg=response!=null?response.getMessage():"unknow error";
            mRequestListener.onComplete(stCode,result,msg);
        }
    }

    public String getmUrl(){
        return mUrl;
    }

    public RequestListener<T> getmRequestListener(){
        return mRequestListener;
    }

    public int getmSerialNumber(){
        return mSerialNum;
    }

    public void setmSerialNumber(int mSerialNum){
        this.mSerialNum=mSerialNum;
    }

    public Priority getmPriority(){
        return mPriority;
    }

    public void setmPriority(Priority mPriority){
        this.mPriority=mPriority;
    }

    /*获取编码格式*/
    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public String getBodyContentType(){
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    public HttpMethod getmHttpMethod(){
        return mHttpMethod;
    }

    public Map<String,String> getmHeaders(){
        return mHeaders;
    }

    public Map<String,String> getParams(){
        return mBodyParams;
    }

    public boolean isHttps(){
        return mUrl.startsWith("https");
    }

    /*请求是否应该缓存*/
    public  void setShouldCache(boolean shouldCache){
        this.mSholdCache=shouldCache;
    }

    public  boolean shouldCache(){
        return mSholdCache;
    }

    public void cancel(){
        isCancel=true;
    }

    public boolean isCanceled(){
        return isCancel;
    }

    /*Returns the raw POST or PUT body to be sent.*/
    public byte[] getBody(){
        Map<String,String> params=getParams();
        if (params!=null&&params.size()>0){
            return encodeParameters(params,getParamsEncoding());
        }
        return null;
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded
     * encoded string.
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    @Override
    public int compareTo(Request<T> another) {
        Priority myPriority=this.getmPriority();
        Priority anotherPriority=another.getmPriority();
        //要是优先级相同的话，就按照添加到队列的序列号的顺序执行
        return myPriority.equals(anotherPriority)?this.getmSerialNumber()-another.getmSerialNumber():myPriority.ordinal()-anotherPriority.ordinal();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mHeaders == null) ? 0 : mHeaders.hashCode());
        result = prime * result + ((mHttpMethod == null) ? 0 : mHttpMethod.hashCode());
        result = prime * result + ((mBodyParams == null) ? 0 : mBodyParams.hashCode());
        result = prime * result + ((mPriority == null) ? 0 : mPriority.hashCode());
        result = prime * result + (mSholdCache ? 1231 : 1237);
        result = prime * result + ((mUrl == null) ? 0 : mUrl.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Request<?> other = (Request<?>) obj;
        if (mHeaders == null) {
            if (other.mHeaders != null)
                return false;
        } else if (!mHeaders.equals(other.mHeaders))
            return false;
        if (mHttpMethod != other.mHttpMethod)
            return false;
        if (mBodyParams == null) {
            if (other.mBodyParams != null)
                return false;
        } else if (!mBodyParams.equals(other.mBodyParams))
            return false;
        if (mPriority != other.mPriority)
            return false;
        if (mSholdCache != other.mSholdCache)
            return false;
        if (mUrl == null) {
            if (other.mUrl != null)
                return false;
        } else if (!mUrl.equals(other.mUrl))
            return false;
        return true;
    }

}
