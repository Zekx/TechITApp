package io.cordova.techit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
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

    private TextView output;
    private EditText username;
    private EditText password;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private InputStream inStr = null;
    private String text = "main";
    private String responseText = "";
    private JSONObject responseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        output = (TextView) findViewById(R.id.output);
        username = (EditText) findViewById(R.id.usernameField);
        password = (EditText) findViewById(R.id.passwordField);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

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
            progressDialog.dismiss();


            output.setText("Hello, there was a problem connecting to the server");
            //Refreshes the window.
            getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
        }
        else{
            try{
                String result = convertStreamToString(in);
                responseData = new JSONObject(result);

                progressDialog.setIndeterminate(false);
                progressDialog.dismiss();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                if(!responseData.isNull("firstLogin?")){
                    if(responseData.getBoolean("firstLogin?") == true){
                        editor.putString("user", responseData.getString("user"));
                        editor.commit();

                        Intent intent = new Intent(this, FirstLogin.class);
                        intent.putExtra("user", responseData.getString("user"));
                        intent.putExtra("firstname", responseData.getString("firstname"));
                        intent.putExtra("lastname", responseData.getString("lastname"));
                        intent.putExtra("email", responseData.getString("email"));
                        intent.putExtra("unit_id", responseData.getString("unit_id"));
                        intent.putExtra("position", responseData.getString("position"));
                        if(!responseData.isNull("tickets")){
                            System.out.print("Step 1");
                            intent.putExtra("tickets", responseData.getString("tickets"));
                        }
                        progressDialog.setIndeterminate(false);
                        progressDialog.dismiss();
                        startActivity(intent);
                        finish();
                    }
                    else{
                        editor.putString("user", responseData.getString("user"));
                        editor.commit();

                        Intent intent = new Intent(this, HomePage.class);
                        intent.putExtra("user", responseData.getString("user"));
                        intent.putExtra("firstname", responseData.getString("firstname"));
                        intent.putExtra("lastname", responseData.getString("lastname"));
                        intent.putExtra("phoneNumber", responseData.getString("phoneNumber"));
                        intent.putExtra("email", responseData.getString("email"));
                        intent.putExtra("unit_id", responseData.getString("unit_id"));
                        intent.putExtra("position", responseData.getString("position"));
                        if(!responseData.isNull("department")){
                            intent.putExtra("department", responseData.getString("department"));
                        }
                        if(!responseData.isNull("tickets")){
                            intent.putExtra("tickets", responseData.getString("tickets"));
                        }
                        progressDialog.setIndeterminate(false);
                        progressDialog.dismiss();
                        startActivity(intent);
                        finish();
                    }
                }
                else{
                    output.setText(responseData.getString("error"));

                    //Refreshes the window.
                    getWindow().getDecorView().findViewById(android.R.id.content).invalidate();

                    progressDialog.setIndeterminate(false);
                    progressDialog.dismiss();
                }
                //System.out.println("Username is..." + responseData.get("user"));

            }catch(Exception e){
                output.setText("There was a problem connecting to the server");
                System.out.println("There was an error parsing the data!");

                //Refreshes the window.
                getWindow().getDecorView().findViewById(android.R.id.content).invalidate();

                progressDialog.setIndeterminate(false);
                progressDialog.dismiss();
                e.printStackTrace();
            }


        }
    }

    protected InputStream getAccess(String username, String password){
        //Check your localhost via cmd -> ipconfig
        //Using http://[localhost]:8080/springmvc/AndroidLogin for debugging purposes.
        //final String URL = "http://localhost:8080/springmvc/AndroidLogin?username="+username+"&password="+password;

        //final String URL = "http://192.168.43.111:8080/springmvc/AndroidLogin?username="+username.trim()+"&password="+password.trim();
        final String URL = "http://cs3.calstatela.edu:4046/techit/AndroidLogin?username="+username.trim()+"&password="+password.trim();
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

                    httpConn.disconnect();
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
}
