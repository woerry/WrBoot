package com.github.woerry.Service;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.*;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.*;



public class WrWebHandle {
    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }
   private CookieStore cookieStore = new BasicCookieStore();

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CloseableHttpResponse getResponse() {
        return response;
    }

    public void setResponse(CloseableHttpResponse response) {
        this.response = response;
    }

    public static String getProtocol() {
        return protocol;
    }

    public  void setProtocol(String protocol) {
        WrWebHandle.protocol = protocol;
    }

    public String getUserAgent() {
        return UserAgent;
    }

    public void setUserAgent(String userAgent) {
        UserAgent = userAgent;
    }

    public String getAcceptLanguage() {
        return AcceptLanguage;
    }

    public void setAcceptLanguage(String acceptLanguage) {
        AcceptLanguage = acceptLanguage;
    }

    public String getAcceptEncoding() {
        return AcceptEncoding;
    }

    public void setAcceptEncoding(String acceptEncoding) {
        AcceptEncoding = acceptEncoding;
    }

    public String getAccept() {
        return Accept;
    }

    public void setAccept(String accept) {
        Accept = accept;
    }

    public String getContenttype() {
        return Contenttype;
    }

    public void setContenttype(String contenttype) {
        Contenttype = contenttype;
    }

    public String getDNT() {
        return DNT;
    }

    public void setDNT(String DNT) {
        this.DNT = DNT;
    }

    public void setConnection(String connection) {
        Connection = connection;
    }

    public String getPostEntityEncode() {
        return postEntityEncode;
    }

    public void setPostEntityEncode(String postEntityEncode) {
        this.postEntityEncode = postEntityEncode;
    }

    public HttpHost getTargetHost() {
        return targetHost;
    }

    public void setTargetHost(HttpHost targetHost) {
        this.targetHost = targetHost;
    }

    public HttpUriRequest getRealRequest() {
        return realRequest;
    }

    public void setRealRequest(HttpUriRequest realRequest) {
        this.realRequest = realRequest;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }


    private Log log = LogFactory.getLog(WrWebHandle.class);
    // 池化管理
    private static PoolingHttpClientConnectionManager poolConnManager = null;

    private HttpContext httpContext = new BasicHttpContext();

    public HttpContext getHttpContext() {
        return httpContext;
    }

    private CloseableHttpClient httpClient;
    //请求器的配置
    private static RequestConfig requestConfig;

    private CloseableHttpResponse response = null;
    private static String protocol="TLSv1";
    private  String UserAgent="Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0";
    private  String AcceptLanguage="zh-CN,zh;q=0.8";
    private  String AcceptEncoding="gzip, deflate,sdch";
    private  String Accept= "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    private  String Contenttype="application/x-www-form-urlencoded";
    private  String DNT="1";
    //    private  String Connection="Keep-Alive";
    private  String Connection= HTTP.CONN_CLOSE;
    private String postEntityEncode="UTF-8";
    private HttpHost targetHost=null;
    private HttpUriRequest realRequest=null;

    private String encode="utf-8";
    private String FiddlerIP="127.0.0.1";
    private Integer FiddlerPort=8888;
    private Boolean useFiddlerProxy=false;
    public void setFiddlerProxy(String ip,Integer port){
        useFiddlerProxy=true;

        this.FiddlerIP=ip;
        this.FiddlerPort=port;
    }
    public void setFiddlerProxy(){
        useFiddlerProxy=true;

    }
    public Boolean getFiddlerProxy(){
      return   useFiddlerProxy;

    }
    public WrWebHandle(){
    struts();
    }
    public WrWebHandle(Boolean useFiddlerProxy){
        this.useFiddlerProxy=true;
        struts();
    }
    private void struts(){
        try {
//            log.info("初始化HttpClient~~~开始");
            //采用绕过验证的方式处理https请求
            SSLContext sslcontext = createIgnoreVerifySSL();

            // 配置同时支持 HTTP 和 HTPPS
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();
            // 初始化连接管理器
            poolConnManager = new PoolingHttpClientConnectionManager(
                    socketFactoryRegistry);
            // 将最大连接数增加到200，实际项目最好从配置文件中读取这个值
            poolConnManager.setMaxTotal(200);
            // 设置最大路由
            poolConnManager.setDefaultMaxPerRoute(20);
            // 根据默认超时限制初始化requestConfig
            Integer socketTimeout=12000;
            Integer connectTimeout=12000;
            Integer connectionRequestTimeout=12000;
            if(useFiddlerProxy){
                HttpHost proxy = new HttpHost(this.FiddlerIP,this.FiddlerPort);
                requestConfig = RequestConfig.custom()
                        .setProxy(proxy)
                        .setConnectionRequestTimeout(
                                connectionRequestTimeout).setSocketTimeout(socketTimeout).setConnectTimeout(
                                connectTimeout).build();
            }else{
                requestConfig = RequestConfig.custom().setConnectionRequestTimeout(
                        connectionRequestTimeout).setSocketTimeout(socketTimeout).setConnectTimeout(
                        connectTimeout).build();
            }


            // 初始化httpClient
            httpClient = getConnection();

//            log.info("初始化HttpClient~~~结束");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }  catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    static  {

    }

    /**
     * 绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static   SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance(protocol);

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
    }


    public CloseableHttpClient getConnection() {
        CloseableHttpClient httpClient = HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(poolConnManager)
                // 设置请求配置
                .setDefaultRequestConfig(requestConfig)
                // 设置重试次数
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, false))
                .setDefaultCookieStore(cookieStore)
                .build();

        if (poolConnManager != null && poolConnManager.getTotalStats() != null)
        {
            log.info("now client pool "
                    + poolConnManager.getTotalStats().toString());
        }
        if(requestConfig.getProxy()!=null){
            System.out.println(requestConfig.getProxy().getAddress()+":"+
                    requestConfig.getProxy().getPort()
            +"代理执行");
        }else{
            System.out.println("没有代理");
        }

        return httpClient;
    }

    public String Get(String url,String encode){
        this.encode=encode;
        return Get(url);
    }

    public HttpGet createHttpGet(String url){
        HttpGet post=new HttpGet(url);
        post.addHeader("User-Agent",UserAgent);
        post.addHeader("Accept-Language", AcceptLanguage);
        post.addHeader("Accept-Encoding", AcceptEncoding);
        post.addHeader("Accept", Accept);
        post.addHeader("Content-Type", Contenttype);
        post.addHeader("DNT", DNT="1");
        post.addHeader("Connection", Connection);

        return post;
    }

    private Map<String,String> postHeaders=new HashMap<>();

    public void setPostHeaders(Map<String, String> postHeaders) {
        this.postHeaders = postHeaders;
    }

    public HttpPost createHttpPost(String url){
        HttpPost post=new HttpPost(url);
        post.addHeader("User-Agent",UserAgent);
        post.addHeader("Accept-Language", AcceptLanguage);
        post.addHeader("Accept-Encoding", AcceptEncoding);
        post.addHeader("Accept", Accept);
        post.addHeader("Content-Type", Contenttype);
        post.addHeader("DNT", DNT="1");
        post.addHeader("Connection", Connection);
        for (Map.Entry<String, String> entry : postHeaders.entrySet()) {
            System.out.println("postHeaders:Key = " + entry.getKey() + ", Value = " + entry.getValue());
            post.addHeader(entry.getKey(),entry.getValue());
        }
        return post;
    }

    public String Get(String url){
        HttpGet httpGet =createHttpGet(url);
        String result=null;
        try {
            response = httpClient.execute(httpGet,httpContext);
            HttpEntity entity = response.getEntity();
            targetHost = (HttpHost)httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
            if(!this.useFiddlerProxy){
                realRequest = (HttpUriRequest)httpContext.getAttribute(HttpCoreContext.HTTP_REQUEST);

            }else{

            }




            result = EntityUtils.toString(entity, this.encode);
            EntityUtils.consume(entity);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return result;

    }

    public void close(){
        try {
            if (response != null)
                response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String Post(HttpPost post, String url){
        String resp=null;
        try {
            response = httpClient.execute(post,httpContext);
        } catch (IOException e) {
            log.error("post失败！");
        }
        targetHost = (HttpHost)httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
        if(!useFiddlerProxy){
            realRequest = (HttpUriRequest)httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);

        }


        try {
            inputStream=response.getEntity().getContent();
            resp= IOUtils.toString(inputStream, this.encode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;
    }

    public String Post(String url, Map<String, String> params){
        String resp=null;
        HttpPost post=createHttpPost(url);
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            UrlEncodedFormEntity entity = null;
            try {
                entity = new UrlEncodedFormEntity(formParams, postEntityEncode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            post.setEntity(entity);
        }
       resp=Post(post,url);
        return resp;
    }

    /**
     * 传入raw数据，进行post
     * @param url
     * @param raw 必须是类似"{\"action_name\":\"QR_LIMIT_SCENE\"}的JSON格式，不能直接用map的toString()
     * @return
     * @throws Exception
     */
    public String Post(String url, String raw){
        HttpPost post =createHttpPost(url);

        if (raw != null && !raw.isEmpty()) {

                post.setEntity(new StringEntity(raw,this.postEntityEncode));

        }
        return Post(post,url);
    }

    public String PostXML(String url,String xml){
        HttpPost post =createHttpPost(url);

        if (xml != null && !xml.equals("")) {
            StringEntity stringEntity = new StringEntity(xml,this.postEntityEncode);
            stringEntity.setContentEncoding(this.postEntityEncode);
            post.setEntity(stringEntity);
        }
        return Post(post,url);
    }

    private InputStream inputStream=null;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void closeInputStream(){
        try {
            inputStream.close();
        } catch (IOException e) {
            System.out.println("线程池线程关闭错误！");
            e.printStackTrace();
        }
    }



}
