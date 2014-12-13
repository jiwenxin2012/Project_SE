package com.example.first;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.R.bool;
import android.R.string;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class FarDbAdapter {
	private TextView result;
	private String content;
	private Boolean ret;
	private final static int   REQUEST_SUCCESS = 1;
    private final static int   REQUEST_FALSE = 0;
	public FarDbAdapter() {
		// TODO Auto-generated constructor stub
	}
	Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {   
             switch (msg.what) {   
                  case REQUEST_SUCCESS:
                	   
                       break;   
                  case REQUEST_FALSE:
                	  break;
             }   
             super.handleMessage(msg);   
        }   
   };  
	public Boolean login(String user, String password) {
		final String user1=user, password1=password;
		ret=false;
		new Thread(new Runnable(){  
			@Override  
		     public void run() {  
		    // TODO Auto-generated method stub  
		    	Login(user1, password1);  
		    }  
		}).start(); 
		System.out.println("ret1 " +ret);
		return ret;
	} 
	private void Login(String user, String password) {
		/* 存放http请求得到的结果 */
		String result = "";
		String ss = null;
		/* 将要发送的数据封包 */
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("user", user));
		InputStream is = null;
		// http post
		try {
			/* 创建一个HttpClient的一个对象 */
			HttpClient httpclient = new DefaultHttpClient();
			/* 创建一个HttpPost的对象 */
			HttpPost httppost = new HttpPost("http://192.168.191.5/test.php");
			/* 设置请求的数据 */
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			/* 创建HttpResponse对象 */
			HttpResponse response = httpclient.execute(httppost);
			/* 获取这次回应的消息实体 */
			HttpEntity entity = response.getEntity();
			/* 创建一个指向对象实体的数据流 */
			is = entity.getContent();
		} catch (Exception e) {
			System.out.println("Connectiong Error");
			e.printStackTrace();
		}
		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			System.out.println("get = " + result);
		} catch (Exception e) {
			System.out.println("Error converting to String");
		}
		// parse json data
		try {
			/* 从字符串result创建一个JSONArray对象 */
			JSONArray jArray = new JSONArray(result);
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json_data = jArray.getJSONObject(i);
				System.out.println("Success");
				System.out.println("result " + json_data.toString());
				if (i == 0) {
					ss = json_data.getString("password");
				} else {
					ss += json_data.toString();
				}
			}
		} catch (JSONException e) {
			System.out.println("Error parsing json");
		}
		Message msg = new Message(); 
        if(ss.equals(password))
        {
            msg.what = REQUEST_SUCCESS; 
        }
        else 
        {
            msg.what = REQUEST_FALSE; 
        }
        // 发送消息
        myHandler.sendMessage(msg); 
	}
}