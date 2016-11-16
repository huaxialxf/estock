package com.lxf.stock.sina;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ws.WebSocket;
import com.ning.http.client.ws.WebSocketTextListener;
import com.ning.http.client.ws.WebSocketUpgradeHandler;

import sun.misc.BASE64Encoder;

public class SinaWebSocket {

    static String USER_NAME = "18606713600";
    static String PASSWORD = "Zaq12345678";
    static String WBCLIENT = "ssologin.js(v1.4.5)";
    static String THE_ONE = "current581ab683cfb2d";
    static String list = "2cn_sh603001,2cn_sh603001_orders,2cn_sh603001_0,2cn_sh603001_1,sh603001_i,sh603001";
    private static Logger logger = LoggerFactory.getLogger(SinaWebSocket.class);

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        String baseUserName = new BASE64Encoder().encode(USER_NAME.getBytes());
        String urlFormat = "http://login.sina.com.cn/sso/prelogin.php?entry=sso&callback=sinaSSOController.preloginCallBack&su=%s&rsakt=mod&client=%s";
        String url = String.format(urlFormat, baseUserName, WBCLIENT);
        logger.info("01 开始预登陆");
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        String ret = httpClientUtil.exeGetMethodForString(url);
        String json = ret.substring("sinaSSOController.preloginCallBack(".length(), ret.length() - 1);
        Map<String, String> map = JSONUtil.parseJavaBean(json, Map.class);
        logger.info("02 结束预登陆 {}", map);
        String servertime = String.valueOf(map.get("servertime"));
        Map<String, String> params = new HashMap<String, String>();
        params.put("entry", "finance");
        params.put("gateway", "1");
        params.put("from", "");
        params.put("savestate", "0");
        params.put("qrcode_flag", "false");
        params.put("useticket", "0");
        params.put("pagerefer", "");
        params.put("su", baseUserName);
        params.put("service", "finance");
        params.put("servertime", servertime);
        params.put("nonce", "JDNGYQ");
        params.put("pwencode", "rsa2");
        params.put("rsakv", "1330428213");
        String pubkey = map.get("pubkey");
        String nonce = map.get("nonce");

        params.put("sp", RSAUtils.getSpString(PASSWORD, pubkey, servertime, nonce));
        params.put("sr", "1600*900");
        params.put("encoding", "UTF-8");
        params.put("cdult", "3");
        params.put("domain", "sina.com.cn");
        params.put("prelt", "207");
        params.put("returntype", "TEXT");
        CloseableHttpResponse response = httpClientUtil.exePostMethod("http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)", null, params);
        String htmlResponse = EntityUtils.toString(response.getEntity());
        map = JSONUtil.parseJavaBean(htmlResponse, Map.class);
        logger.info("03 结束登陆 {} ", map);
        logger.info("03 结束登陆 Cookies={} ", httpClientUtil.getCookies());

        String queryType = "hq_pjb";
        String publicIp = httpClientUtil.exeGetMethodForString("http://ipinfo.io/ip").trim();

        logger.debug("queryType:" + queryType);
        logger.debug("publicIp:" + publicIp);
        logger.debug("list:" + list);
        String token = getToken(httpClientUtil, queryType, publicIp);
        url = "wss://ff.sinajs.cn/wskt?token=" + token + "&list=" + list;
        System.out.println("url>>>" + url);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        WebSocket websocket = asyncHttpClient.prepareGet(url).execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketTextListener() {
            @Override
            public void onMessage(String message) {
                System.out.println(new Date() + ">>" + message.trim());
            }
            @Override
            public void onOpen(WebSocket websocket) {
                System.err.println(">>>>onOpen " + new Date());
            }
            @Override
            public void onClose(WebSocket websocket) {
                System.err.println(">>>>close " + new Date());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println(">>>>onError");
            }
        }).build()).get();

        Thread t_heart = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000 * 60);
                    } catch (InterruptedException e) {
                    }
                    websocket.sendMessage("");
                    System.out.println("发送心跳");
                }
            };
        };
        t_heart.start();
        Thread tSendToken = new Thread() {
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(1000 * 60 * 3 - 3);
                    } catch (InterruptedException e) {
                    }
                    try {
                        String token = getToken(httpClientUtil, queryType, publicIp);
                        websocket.sendMessage("*" + token);
                        System.out.println("发送TOKEN=*" + token);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        };
        tSendToken.start();

    }

    public static String getToken(HttpClientUtil httpClientUtil, String queryType, String publicIp) throws Exception {
        logger.info("04 开始获取Token: ");
        String url = "https://current.sina.com.cn/auth/api/jsonp.php/var%20KKE_auth_0kvqmQSIj=/AuthSign_Service.getSignCode?kick=1&query=" + queryType + "&ip=" + publicIp + "&list=" + list;
        String ret = httpClientUtil.exeGetMethodForString(null, url);
        logger.info("04 结束获取Token: {} ", ret);
        String tokenJson = ret.substring("var KKE_auth_0kvqmQSIj=((".length(), ret.length() - ")); ".length() + 1);
        String token = (String) JSONUtil.parseJavaBean(tokenJson, Map.class).get("result");
        logger.info("04 结束获取Token =  {} ", token);
        return token;
    }
}
