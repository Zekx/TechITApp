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
import android.widget.ProgressBar;
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
    private ProgressBar spinner;

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
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Button butn = (Button) findViewById(R.id.logonButton);
        butn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
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

        System.out.println("Now Authenticating...");

        //Makes a connection to the web application servlet.
        in = getAccess(username, password);

        if(inStr == null){

            output.setText("Hello, there was a problem connecting to the server");
            //Refreshes the window.
            getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
        }
        else{
            try{
                String result = convertStreamToString(in);
                responseData = new JSONObject(result);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                if(!responseData.isNull("firstLogin?")){
                    if(responseData.getBoolean("firstLogin?") == true){
                        spinner.setVisibility(View.GONE);
                        output.setText("The User is not a technician!");

                        //Refreshes the window.
                        getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
                    }
                    else{
                        spinner.setVisibility(View.GONE);
                        if(responseData.getString("position").equals("2")){
                            editor.putString("user", responseData.getString("user"));
                            editor.putString("unitID", responseData.getString("unit_id"));
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
                                editor.putString("tickets", responseData.getString("tickets"));
                                editor.commit();
                                //intent.putExtra("tickets", responseData.getString("tickets"));
                            }
                            startActivity(intent);
                            finish();
                        }
                        else{
                            output.setText("The User is not a technician!");

                            //Refreshes the window.
                            getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
                        }
                    }
                }
                else{
                    spinner.setVisibility(View.GONE);
                    output.setText(responseData.getString("error"));

                    //Refreshes the window.
                    getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
                }
                //System.out.println("Username is..." + responseData.get("user"));

            }catch(Exception e){
                spinner.setVisibility(View.GONE);
                output.setText("There was a problem connecting to the server");
                System.out.println("There was an error parsing the data!");

                //Refreshes the window.
                getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
                e.printStackTrace();
            }


        }
    }

    protected InputStream getAccess(String username, String password){
        //Check your localhost via cmd -> ipconfig
        //Using http://[localhost]:8080/springmvc/AndroidLogin for debugging purposes.
        //final String URL = "http://localhost:8080/springmvc/AndroidLogin?username="+username+"&password="+password;

        final String URL = "http://10.85.46.56:8080/techit/AndroidLogin?username="+username.trim()+"&password="+password.trim();
        //final String URL = "http://cs3.calstatela.edu:4046/techit/AndroidLogin?username="+username.trim()+"&password="+password.trim();
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
