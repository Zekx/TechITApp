package io.cordova.techit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class FirstLogin extends AppCompatActivity {
    EditText firstName;
    EditText lastName;
    EditText email;
    EditText phoneNumber;
    Spinner department;
    TextView error;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private InputStream inStr = null;
    private String text = "main";
    private String responseText = "";
    private JSONObject responseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_first_login);

        firstName = (EditText) findViewById(R.id.FirstNameInput);
        lastName = (EditText) findViewById(R.id.LastNameInput);
        email = (EditText) findViewById(R.id.emailInput);
        phoneNumber = (EditText) findViewById(R.id.PhoneNumberInput);
        department = (Spinner) findViewById(R.id.department);
        error = (TextView) findViewById(R.id.ErrorView);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        firstName.setText(getIntent().getStringExtra("firstname"));
        lastName.setText(getIntent().getStringExtra("lastname"));
        email.setText(getIntent().getStringExtra("email"));

        Button butn = (Button) findViewById(R.id.SubmitButton);
        butn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyValue();
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

    protected void verifyValue(){
        InputStream ins = null;
        ins = checkCredentials();

        if(inStr == null){
            error.setText("There was a problem connecting! Try again!");
        }
        else{
            try{
                String result = convertStreamToString(inStr);
                responseData = new JSONObject(result);

                if(responseData.getBoolean("Valid")){
                    Intent intent = new Intent(this, HomePage.class);
                    intent.putExtra("firstname", responseData.getString("firstname"));
                    intent.putExtra("lastname", responseData.getString("lastname"));
                    intent.putExtra("phoneNumber", responseData.getString("phoneNumber"));
                    intent.putExtra("email", responseData.getString("email"));
                    if(!responseData.getString("department").isEmpty()){
                        intent.putExtra("department", responseData.getString("department"));
                    }
                    startActivity(intent);
                    finish();
                }
                else{
                    firstName.setText(responseData.getString("firstname"));
                    lastName.setText(responseData.getString("lastname"));
                    email.setText(responseData.getString("email"));
                    error.setText(responseData.getString("error"));
                }

            }catch(Exception e){
                error.setText("There was a problem with the data!");
                System.out.println("There was an error parsing the data!");
                e.printStackTrace();
            }
        }
    }

    protected InputStream checkCredentials(){
        String fn = firstName.getText().toString().trim();
        String ln = lastName.getText().toString().trim();
        String em = email.getText().toString().trim();
        String pn = phoneNumber.getText().toString().trim();
        String dp = department.getSelectedItem().toString();
        String encodedDP = "";
        try{
            encodedDP = URLEncoder.encode(dp, "UTF-8").trim();
        }catch(Exception e){
            System.out.println("Could not encode the department!");
        }

        //final String URL = "http://192.168.43.111:8080/springmvc/FirstAndroidLoginUpdate?username="+getIntent().getStringExtra("user")
        //        +"&firstName="+fn+"&lastName="+ln+"&email="+em+"&phoneNumber="+pn+"&department="+ encodedDP;
        final String URL = "http://cs3.calstatela.edu:4046/techit/FirstAndroidLoginUpdate?username="+getIntent().getStringExtra("user")
                +"&firstName="+fn+"&lastName="+ln+"&email="+em+"&phoneNumber="+pn+"&department="+ encodedDP;
        System.out.println("Accessing... " + URL);

        Thread log = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    java.net.URL url = new URL(URL);
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

        System.out.println("Connection to " + URL + " closing.");

        System.out.println(responseText);
        return inStr;
    }
}
