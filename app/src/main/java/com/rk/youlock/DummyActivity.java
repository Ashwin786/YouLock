package com.rk.youlock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;



import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class DummyActivity extends AppCompatActivity {

    private static ThreadSafeClientConnManager ccm;
    private HttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        call_http();
                    }
                }).start();

            }
        });

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ccm.shutdown();
            }
        });

    }

    private void call_http() {
//        LoginDto lr = new LoginDto("U2420161102207", "alluser", "adminss");
        String url = "http://192.168.1.35:9097/login/validateuser";
        String login = "{\"deviceId\":\"U2420161102207\",\"password\":\"alluser\",\"userName\":\"adminss\"}";
        Log.e("LoginActivity", "gson request online..." + login);
        try {
            StringEntity se = new StringEntity(login, HTTP.UTF_8);
            URI website = new URI(url);
            HttpResponse response = requestType(website, se);
            int statusCode = response.getStatusLine().getStatusCode();
            Log.e("HttpClientWrapper", "statusCode received" + statusCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String l;
            String nl = System.getProperty("line.separator");
            Log.e("HttpClientWrapper ", "getProperty :" + nl);
            while ((l = in.readLine()) != null) {
                Log.e("HttpClientWrapper ", "readLine" + l);
                sb.append(l + nl);
            }
            in.close();
            String responseData = sb.toString();
            Log.e("HttpClientWrapper ", "Response : " + responseData);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(DummyActivity.this, RetroActivity.class));
    }

    private HttpResponse requestType(URI uri, StringEntity entity) throws IOException {
            if (client == null)
                client = https_setup();
            HttpPost postRequest = new HttpPost();
            postRequest.setURI(uri);
            postRequest.setHeader("Content-Type", "application/json");
            postRequest.setHeader("Store_type", "fps");
            postRequest.setEntity(entity);
            return client.execute(postRequest);
    }

    public static HttpClient https_setup() {
        KeyStore trustStore = null;
        HttpClient client = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            HttpParams httpParameters = new BasicHttpParams();
            ccm = new ThreadSafeClientConnManager(httpParameters, registry);

            int timeoutConnection = 5000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            int timeoutSocket = 5000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
//            cm.setMaxTotal(500);
//            cm.setDefaultMaxPerRoute(200);
//            Log.e("max default connection",""+cm.getDefaultMaxPerRoute());
//            Log.e("max connection",""+cm.getMaxTotal());
            client = new DefaultHttpClient(ccm, httpParameters);
//            client = new DefaultHttpClient(httpParameters);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }
}
