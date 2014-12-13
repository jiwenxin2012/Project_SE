package com.example.first;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.WorkSource;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	private EditText field_user;
	private EditText field_password;
    private Button button_confirm;
    private String user, password;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.login);//�༭��ʱ��ʹ����һ��UI
        findViews();
        Work(savedInstanceState);
    }
	private void findViews() {
        field_user = (EditText) findViewById(R.id.user);
        field_password = (EditText) findViewById(R.id.password);
        button_confirm = (Button) findViewById(R.id.confirm);
    }
	private void Work(Bundle savedInstanceState) {
        button_confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	user = field_user.getText().toString();
            	password = field_password.getText().toString();
            	if (user == null || password == null)
            		setResult(RESULT_CANCELED);
            	else {
            		new Thread(new Runnable(){  
            			@Override  
            		     public void run() {  
            		    // TODO Auto-generated method stub  
            		    	myLogin();  
            		    }  
            		}).start(); 
                	
            	}
                
            }
        });
    }
	Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {   
             switch (msg.what) {   
                  case RESULT_OK:
                	  	Toast.makeText(Login.this,"��¼�ɹ�", Toast.LENGTH_SHORT).show(); 
          				setResult(RESULT_OK);
          				finish();
                       break;   
                  case RESULT_CANCELED:
                	  Toast.makeText(Login.this,"�û�����������������µ�¼", Toast.LENGTH_LONG).show();
                	  break;
             }   
             super.handleMessage(msg);   
        }   
   };  
	private void myLogin() {
		/* ���http����õ��Ľ�� */
		String result = "";
		String ss = null;
		/* ��Ҫ���͵����ݷ�� */
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("user", user));
		InputStream is = null;
		// http post
		try {
			/* ����һ��HttpClient��һ������ */
			HttpClient httpclient = new DefaultHttpClient();
			/* ����һ��HttpPost�Ķ��� */
			HttpPost httppost = new HttpPost("http://192.168.191.5/test.php");
			/* ������������� */
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			/* ����HttpResponse���� */
			HttpResponse response = httpclient.execute(httppost);
			/* ��ȡ��λ�Ӧ����Ϣʵ�� */
			HttpEntity entity = response.getEntity();
			/* ����һ��ָ�����ʵ��������� */
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
			/* ���ַ���result����һ��JSONArray���� */
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
            msg.what = RESULT_OK; 
        }
        else 
        {
            msg.what = RESULT_CANCELED; 
        }
        // ������Ϣ
        myHandler.sendMessage(msg); 
	}
}
