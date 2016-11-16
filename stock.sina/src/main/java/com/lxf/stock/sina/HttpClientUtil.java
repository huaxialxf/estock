package com.lxf.stock.sina;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    /**
     * 内部对象 httpClient
     */
    private CloseableHttpClient httpClient = null;
    private Map<String, String> cookies = new HashMap<String, String>();

    public HttpClientUtil() {
        try {
            initHttpCient(null, null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public HttpClientUtil(String proxyHostInfo, String localIp) {
        try {
            initHttpCient(proxyHostInfo, localIp);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void initHttpCient(String proxyHostInfo, String localIp) throws Exception {
        HttpClientBuilder builder = HttpClients.custom();
        if (proxyHostInfo != null) {
            String[] arr = proxyHostInfo.split(";");
            String ip = arr[0];
            int port = Integer.parseInt(arr[1]);
            HttpHost proxyHost = new HttpHost(ip, port);
            builder.setProxy(proxyHost);
        }
        if (localIp != null) {
            InetAddress address;
            address = InetAddress.getByName(localIp);
            RequestConfig config = RequestConfig.custom().setLocalAddress(address).build();
            builder.setDefaultRequestConfig(config);
        }
        httpClient = builder.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.86 Safari/537.36").build();

    }

    public String exeGetMethodForString(Map<String, String> headers, String url) throws Exception {
        CloseableHttpResponse response = exeGetMethod(headers, url);
        handleRespone(response);
        String htmlResponse = EntityUtils.toString(response.getEntity());
        return htmlResponse;
    }

    public String exeGetMethodForString(String url) throws Exception {
        CloseableHttpResponse response = exeGetMethod(url);
        handleRespone(response);
        String htmlResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
        return htmlResponse;
    }

    private void handleRespone(CloseableHttpResponse response) {
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            if ("Set-Cookie".equals(header.getName())) {
                String value = header.getValue();
                String key = value.substring(0, value.indexOf("="));
                String cValue = value.substring(value.indexOf("=") + 1, value.indexOf(";"));
                cookies.put(key, cValue);
            }
        }
    }

    public String getCookies() {
        StringBuffer buffer = new StringBuffer();
        for (String key : cookies.keySet()) {
            buffer.append(key + "=" + cookies.get(key) + "; ");
        }
        return buffer.toString();
    }

    public CloseableHttpResponse exeGetMethod(String url) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        handleRespone(response);
        return response;
    }

    public CloseableHttpResponse exeGetMethod(Map<String, String> headers, String url) throws Exception {
        HttpGet httpGet = new HttpGet(url);

        if (headers != null) {
            for (String key : headers.keySet()) {
                String value = headers.get(key);
                httpGet.setHeader(key, value);
            }
        }
        CloseableHttpResponse response = httpClient.execute(httpGet);
        handleRespone(response);
        return response;
    }

    public String exePostMethodForString(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
        CloseableHttpResponse response = exePostMethod(url, headers, params);
        String htmlResponse = EntityUtils.toString(response.getEntity());
        return htmlResponse;
    }

    public String exePostMethodForString(String url, Map<String, String> headers, String body) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        if (headers != null) {
            for (String key : headers.keySet()) {
                String value = headers.get(key);
                httpPost.setHeader(key, value);
            }
        }
        httpPost.setEntity(new InputStreamEntity(new ByteArrayInputStream(body.getBytes())));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        handleRespone(response);
        String htmlResponse = EntityUtils.toString(response.getEntity());
        return htmlResponse;
    }

    public CloseableHttpResponse exePostMethod(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        if (headers != null) {
            for (String key : headers.keySet()) {
                String value = headers.get(key);
                httpPost.setHeader(key, value);
            }
        }
        if (params != null) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                String value = params.get(key);
                list.add(new BasicNameValuePair(key, value));
            }
            HttpEntity postBodyEnt = new UrlEncodedFormEntity(list, "UTF-8");
            httpPost.setEntity(postBodyEnt);
        }

        CloseableHttpResponse response = httpClient.execute(httpPost);
        handleRespone(response);

        return response;
    }

    public void close() {
        try {
            httpClient.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws Exception {

        HttpClientUtil clientUtil = new HttpClientUtil();
        // Map<String, String> params = new HashMap<String, String>();
        // params.put("q", "中文");
        // clientUtil.exePostMethod("http://127.0.0.1/", null, params);
        String ret = clientUtil.exeGetMethodForString("https://www.adndrc.org/mtsc/UDRP_Decisions.php?pageNum_decii=1");
        System.out.println(ret);
        // WebUtil.doPost("http://127.0.0.1/", params);
        // HttpClientBuilder builder = HttpClients.custom();
        //// builder.setConnectionTimeToLive(3, TimeUnit.SECONDS);
        // RequestConfig requestConfig =
        // RequestConfig.copy(RequestConfig.DEFAULT)
        // .setSocketTimeout(5000).setConnectTimeout(5000)
        // .setConnectionRequestTimeout(5000).build();
        //
        // builder.setDefaultRequestConfig(requestConfig);
        // final CloseableHttpClient httpClient = builder.build();
        // Thread t = new Thread() {
        // public void run() {
        //
        // };
        // };
        // t.start();

        // httpClient.close();
    }

}
