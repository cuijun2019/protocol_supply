package com.etone.protocolsupply.cas;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;

/**
  * 
  * User: JiangWei
  * Date: Apr 29, 2020
  * Time: 11:48:17 AM
  */
 public class HttpClientUtil {

     /**
      * http post 公共方法
      * @param url
      * @param headers
      * @param params
      * @return
      * @throws Exception
      */
     public static String post(String url, Map<String, String> headers, Map<String, String> params) throws Exception{
         HttpClient client = new DefaultHttpClient();
         client.getParams().setParameter("http.connection.tiomeot",5000);
         client.getParams().setParameter("http.socket.tiomeot",5000);

         HttpPost post = new HttpPost(url);

         // 请求头设置
         for(String key : headers.keySet()){
             post.setHeader(key, headers.get(key));
         }

         // 请求参数设置
         StringEntity entityParam = new StringEntity(new JSONObject(params).toString(),
					Charset.forName("UTF-8").toString());
         post.setEntity(entityParam);

         // 执行http post请求
         HttpResponse r = client.execute(post);

         // 返回数据处理
         HttpEntity entity = r.getEntity();
         byte[] bytes = EntityUtils.toByteArray(entity);
         return new String(bytes);
     }

     /**
      * http get 公共方法
      * @param url
      * @param headers
      * @return
      * @throws Exception
      */
     public static String get(String url, Map<String, String> headers) throws Exception{
         HttpClient client = new DefaultHttpClient();
         client.getParams().setParameter("http.connection.tiomeot",5000);
         client.getParams().setParameter("http.socket.tiomeot",5000);

         HttpGet get = new HttpGet(new URI(url));

         // 请求头设置
         for(String key : headers.keySet()){
             get.setHeader(key, headers.get(key));
         }

         // 执行http get请求
         HttpResponse r = client.execute(get);

         // 返回数据处理
         HttpEntity entity = r.getEntity();
         byte[] bytes = EntityUtils.toByteArray(entity);
         return new String(bytes, "utf-8");
     }
}
