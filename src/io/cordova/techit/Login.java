package io.cordova.techit;

import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    private static final String TAG = "Login";
    private TextView output;
    private EditText username;
    private EditText password;

    private InputStream inStr = null;
    private String text = "main";
    private String responseText = "";
    private JSONObject responseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        output = null;
        username = (EditText) findViewById(R.id.usernameField);
        password = (EditText) findViewById(R.id.passwordField);

        Button butn = (Button) findViewById(R.id.logonButton);
        butn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(username.getText().toString(), password.getText().toString());
                //System.out.println(username.getText() + " " + password.getText());
            }
        });
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    protected void login(String username, String password){
        Log.d(TAG, "Login");
        InputStream in = null;
        String text = "";

        final ProgressDialog progressDialog = new ProgressDialog(Login.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        System.out.println("Now Authenticating...");

        //Makes a connection to the web application servlet.
        in = getAccess(username, password);

        if(inStr == null){
            progressDialog.setIndeterminate(false);
            progressDialog.hide();
        }
        else{
            String result = convertStreamToString(in);
            if(validate()){
                try{
                    responseData = new JSONObject(result);
                    System.out.println("Username is..." + responseData.get("user"));
                    System.out.println("Password is..." + responseData.get("pass"));

                }catch(Exception e){
                    System.out.println("There was an error parsing the data!");
                    e.printStackTrace();
                }

            }
            else{

            }

        }
        progressDialog.setIndeterminate(false);
        progressDialog.hide();
    }

    protected InputStream getAccess(String username, String password){
        //Check your localhost via cmd -> ipconfig
        //Using http://[localhost]:8080/springmvc/AndroidLogin for debugging purposes.
        //final String URL = "http://localhost:8080/springmvc/AndroidLogin?username="+username+"&password="+password;
        final String URL = "http://192.168.1.213:8080/springmvc/AndroidLogin?username="+username+"&password="+password;
        System.out.println("Accessing... " + URL);

        Thread log = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL(URL);
                    URLConnection conn = url.openConnection();

                    HttpURLConnection httpConn = (HttpURLConnection) conn;
                    httpConn.setRequestMethod("POST");
                    httpConn.setDoInput(true);
                    httpConn.setDoOutput(true);
                    httpConn.connect();

                    DataOutputStream dataStream = new DataOutputStream(conn
                            .getOutputStream());

                    dataStream.writeBytes(text);
                    dataStream.flush();
                    dataStream.close();

                    int responseCode = httpConn.getResponseCode();
                    responseText = "Response code is..." + responseCode + "; OK Code is..." + HttpURLConnection.HTTP_OK;
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        System.out.println("RESPONSE CODE: OK");
                        inStr = httpConn.getInputStream();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    System.out.println("Connection Failed!");
            }
        }
        });

        try{
            log.start();
            log.join();
        }catch(Exception e){
            e.printStackTrace();
        }

        /*try{
            URL url = new URL(URL);
            URLConnection conn = url.openConnection();

            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("POST");
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            httpConn.connect();

            DataOutputStream dataStream = new DataOutputStream(conn
                    .getOutputStream());

            dataStream.writeBytes(text);
            dataStream.flush();
            dataStream.close();

            int responseCode = httpConn.getResponseCode();
            responseText = "Response code is..." + responseCode + "; OK Code is..." + HttpURLConnection.HTTP_OK;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("RESPONSE CODE: OK");
                inStr = httpConn.getInputStream();
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Connection Failed!");
        }*/

        System.out.println("Connection to " + URL + " closing.");

        System.out.println(responseText);
        return inStr;
    }

    protected boolean validate(){
        return true;
    }
}