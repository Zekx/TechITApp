package io.cordova.techit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class UpdateTicket extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private String oldprogress;
    private String newprogress;
    private String updateMessage;
    private Spinner outputSpinner;
    private EditText details;
   // private EditText prompt;
    private ProgressBar spinner;
    private JSONObject responseData;


    private String text = "main";
    private String responseText = "";
    private InputStream inStr = null;
    private boolean change = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ticket);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Log.d("tag", "DiD i got here" + getIntent().getStringExtra("currentProgress"));

        oldprogress = getIntent().getStringExtra("currentProgress");

        outputSpinner = (Spinner) findViewById(R.id.statusSpinner);
        details = (EditText) findViewById(R.id.detailsText);
        //prompt = (EditText) findViewById(R.id.prompt);
        //prompt.setTextColor(Color.RED);

        outputSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                newprogress = adapterView.getItemAtPosition(pos).toString();
                newprogress = newprogress.replaceAll("\\s+","");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                newprogress = adapterView.getItemAtPosition(0).toString();
                newprogress = newprogress.replaceAll("\\s+","");

            }


        });

        Button submit = (Button) findViewById(R.id.submitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // spinner.setVisibility(View.VISIBLE);

                updateMessage = details.getText().toString();
                if(updateMessage.isEmpty() ||  updateMessage.trim() == "")
                {
                    Toast.makeText(getApplicationContext(), "Please Enter Details", Toast.LENGTH_LONG).show();
                    return;
                }
                submitProc(details.getText().toString());


                Intent displayTicket = new Intent(UpdateTicket.this, DisplayTicket.class);

                displayTicket.putExtra("user", getIntent().getStringExtra("user"));

                displayTicket.putExtra("id", getIntent().getStringExtra("id"));
                displayTicket.putExtra("technicians", getIntent().getStringExtra("technicians"));
                displayTicket.putExtra("username", getIntent().getStringExtra("username"));
                displayTicket.putExtra("userFirstName", getIntent().getStringExtra("userFirstName"));
                displayTicket.putExtra("userLastName", getIntent().getStringExtra("userLastName"));
                displayTicket.putExtra("phoneNumber", getIntent().getStringExtra("phoneNumber"));
                displayTicket.putExtra("email", getIntent().getStringExtra("email"));
                displayTicket.putExtra("currentProgress", newprogress);
                displayTicket.putExtra("priority", getIntent().getStringExtra("priority"));
                displayTicket.putExtra("unit_id", getIntent().getStringExtra("unit_id"));
                displayTicket.putExtra("details", getIntent().getStringExtra("details"));
                displayTicket.putExtra("startDate", getIntent().getStringExtra("startDate"));
                displayTicket.putExtra("department",getIntent().getStringExtra("department"));
                displayTicket.putExtra("endDate", getIntent().getStringExtra("endDate"));
                displayTicket.putExtra("userFirstName", getIntent().getStringExtra("userFirstName"));
                displayTicket.putExtra("lastUpdated", getIntent().getStringExtra("lastUpdated"));
                displayTicket.putExtra("lastUpdatedTime", getIntent().getStringExtra("lastUpdatedTime"));
                displayTicket.putExtra("ticketLocation", getIntent().getStringExtra("ticketLocation"));
                displayTicket.putExtra("updates", getIntent().getStringExtra("updates"));
                //Used to determine what the user can do in the next page.
                displayTicket.putExtra("position", getIntent().getStringExtra("position"));

                startActivity(displayTicket);


            }
        });
    }

    protected void submitProc(String details)

    {
        Log.d("tag", "new: " +newprogress);
        Log.d("tag", "old: " +oldprogress);
        Log.d("tag", "old: " + getIntent().getStringExtra("unit_id"));
        details = details.replace(" ", "_");
        if(oldprogress.equalsIgnoreCase(newprogress))
        {
            change = false;
        }

        InputStream in = getAccess(sharedpreferences.getString("id", getIntent().getStringExtra("id")),sharedpreferences.getString("username", getIntent().getStringExtra("user")), details , newprogress, change, getIntent().getStringExtra("unit_id"),getIntent().getStringExtra("username"));
        if (in == null){
            System.out.println("There was a problem accessing the database!");
        }
        else{
            try{
                String result = convertStreamToString(in);
                responseData = new JSONObject(result);
                getIntent().putExtra("updates", responseData.getString("updates"));
                //Used to determine what the user can do in the next page.

                System.out.println("This is the output" + responseData.getString("updates"));


            }catch (Exception e){
                System.out.println("There was an error parsing the data!");
                e.printStackTrace();
            }
        }
    }

    protected InputStream getAccess(String ticketID, String modifier, String updateDetails, String progress, boolean change, String unitID, String username){
        System.out.println("This first" + ticketID + modifier + updateDetails + progress + change + unitID + username);


        final String URL = "http://10.85.46.56:8080/techit/AndroidModify?TicketID="+ticketID+"&Modifier="+modifier+"&NewProgress="+newprogress+"&Change="+change+"&UnitID="+unitID +"&Username="+username+"&UpdateDetails="+updateDetails;
        //final String URL = "http://cs3.calstatela.edu:4046/techit/AndroidLogin?username="+username.trim()+"&password="+password.trim();
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
}
