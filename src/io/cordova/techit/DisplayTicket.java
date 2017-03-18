package io.cordova.techit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DisplayTicket extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    LinearLayout layoutVert;
    LinearLayout layoutHori;

    Button takeBtn;
    Button updateBtn;

    private InputStream inStr = null;
    private String text = "main";
    private String responseText = "";
    private JSONObject responseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ticket);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        layoutVert = (LinearLayout) findViewById(R.id.ticketInfo);
        layoutHori = (LinearLayout) findViewById(R.id.buttonMap);

        TextView txt = new TextView(this);
        txt.setText("Ticket ID#: " + getIntent().getStringExtra("id"));
        txt.setBackgroundColor(0xffAAAAAA);
        txt.setTextColor(0xff000000);
        txt.setTextSize(18f);
        layoutVert.addView(txt);

        txt = new TextView(this);
        txt.setText("Firstname: " + getIntent().getStringExtra("userFirstName"));
        txt.setBackgroundColor(0xffffffff);
        txt.setTextColor(0xff000000);
        txt.setTextSize(18f);
        layoutVert.addView(txt);

        txt = new TextView(this);
        txt.setText("Lastname: " + getIntent().getStringExtra("userLastName"));
        txt.setBackgroundColor(0xffAAAAAA);
        txt.setTextColor(0xff000000);
        txt.setTextSize(18f);
        layoutVert.addView(txt);

        txt = new TextView(this);
        txt.setText("Progress: " + getIntent().getStringExtra("currentProgress"));
        txt.setBackgroundColor(0xffffffff);
        txt.setTextColor(0xff000000);
        txt.setTextSize(18f);
        layoutVert.addView(txt);

        txt = new TextView(this);
        txt.setText("Phone: " + getIntent().getStringExtra("phoneNumber"));
        txt.setBackgroundColor(0xffAAAAAA);
        txt.setTextColor(0xff000000);
        txt.setTextSize(18f);
        layoutVert.addView(txt);

        txt = new TextView(this);
        txt.setText("Email: " + getIntent().getStringExtra("email"));
        txt.setBackgroundColor(0xffffffff);
        txt.setTextColor(0xff000000);
        txt.setTextSize(18f);
        layoutVert.addView(txt);

        txt = new TextView(this);
        txt.setText("Date Submitted: " + getIntent().getStringExtra("startDate"));
        txt.setBackgroundColor(0xffAAAAAA);
        txt.setTextColor(0xff000000);
        txt.setTextSize(18f);
        layoutVert.addView(txt);

        txt = new TextView(this);
        txt.setText("Department: " + getIntent().getStringExtra("department"));
        txt.setBackgroundColor(0xffffffff);
        txt.setTextColor(0xff000000);
        txt.setTextSize(18f);
        layoutVert.addView(txt);

        txt = new TextView(this);
        txt.setText("Location of Problem: " + getIntent().getStringExtra("ticketLocation"));
        txt.setBackgroundColor(0xffAAAAAA);
        txt.setTextColor(0xff000000);
        txt.setTextSize(18f);
        layoutVert.addView(txt);

        txt = new TextView(this);
        txt.setText("Priority: " + getIntent().getStringExtra("priority"));
        txt.setBackgroundColor(0xffffffff);
        txt.setTextColor(0xff000000);
        txt.setTextSize(18f);
        layoutVert.addView(txt);

        txt = new TextView(this);
        if(getIntent().getStringExtra("technicians") != null){
            String list = "";
            try{
                JSONArray techniciansOnTicket = new JSONArray(getIntent().getStringExtra("technicians"));

                for(int i = 0; i < techniciansOnTicket.length(); i++){
                    JSONObject obj = techniciansOnTicket.getJSONObject(i);

                    list = list + obj.getString("firstName") + " " + obj.getString("lastName");
                    if(i != techniciansOnTicket.length() - 1){
                        list = list + ", ";
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            txt.setText("Technicians: " + list);
        }
        else{
            txt.setText("Technicians: " + " currently none...");
        }
        txt.setBackgroundColor(0xffAAAAAA);
        txt.setTextColor(0xff000000);
        txt.setTextSize(18f);
        layoutVert.addView(txt);

        txt = new TextView(this);
        txt.setText("Details: " + getIntent().getStringExtra("details"));
        txt.setBackgroundColor(0xffffffff);
        txt.setTextColor(0xff000000);
        txt.setTextSize(18f);
        layoutVert.addView(txt);

        String updates = getIntent().getStringExtra("updates");

        JSONArray updateJSON = new JSONArray();
        try {
            TableLayout table = new TableLayout(this);
            table.setColumnShrinkable(1, true);
            TableRow row = new TableRow(this);

            TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, 0, 2, 0);//2px right-margin

            //New Cell
            LinearLayout cell = new LinearLayout(this);
            cell.setBackgroundColor(Color.LTGRAY);
            cell.setLayoutParams(llp);//2px border on the right for the cell

            TextView dateDescription = new TextView(this);
            dateDescription.setText("Date Modified");
            dateDescription.setTextSize(16);
            dateDescription.setTextColor(Color.BLACK);
            dateDescription.setBackgroundResource(R.drawable.cell_shape);

            TextView updateDescription = new TextView(this);
            updateDescription.setText("Update Description");
            updateDescription.setTextSize(16);
            updateDescription.setTextColor(Color.BLACK);
            updateDescription.setBackgroundResource(R.drawable.cell_shape);

            row.addView(dateDescription);
            row.addView(updateDescription);
            table.addView(row);

            if (updates != null) {
                updateJSON = new JSONArray(updates);
            }

            for (int i = 0; i < updateJSON.length(); i++) {
                JSONObject obj = (JSONObject) updateJSON.get(i);

                TableRow newRow = new TableRow(this);

                int id = obj.getInt("id");
                int ticketId = obj.getInt("ticketId");
                String modifier = obj.getString("modifier");
                String updateDetails = obj.getString("updateDetails");

                String string = obj.getString("modifiedDate");
                DateFormat format = new SimpleDateFormat("MMMMM dd,yyyy");
                //Date modifiedDate = format.parse(string);

                TextView date = new TextView(this);
                date.setText(string);
                date.setTextColor(Color.BLACK);
                TextView update = new TextView(this);
                update.setText(updateDetails);

                update.setTextColor(Color.BLACK);

                newRow.addView(date);
                newRow.addView(update);

                table.addView(newRow);
            }

            //System.out.println("wassup");
            layoutVert.addView(table);
        } catch (Exception e) {
            e.printStackTrace();
        }

        takeBtn = (Button) findViewById(R.id.takeTicket);
        updateBtn = (Button) findViewById(R.id.updateTicket);

        takeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean alreadyTaken = false;
                    if(getIntent().getStringExtra("technicians") != null){
                        String list = "";
                        try{
                            JSONArray techniciansOnTicket = new JSONArray(getIntent().getStringExtra("technicians"));

                            for(int i = 0; i < techniciansOnTicket.length(); i++){
                                JSONObject obj = techniciansOnTicket.getJSONObject(i);
                                Iterator<String> iter = obj.keys();
                                /*while(iter.hasNext()){
                                    String key = iter.next();
                                    System.out.println("item: " + key);
                                }*/

                                if(obj.getString("userName").equals(sharedpreferences.getString("user", getIntent().getStringExtra("username")))){
                                    alreadyTaken = true;
                                    break;
                                }
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        if(!alreadyTaken){
                            new AlertDialog.Builder(DisplayTicket.this)
                                    .setMessage("Do you want to accept the ticket?")
                                    .setCancelable(false)
                                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            InputStream in = getAccess(sharedpreferences.getString("user", getIntent().getStringExtra("username")), sharedpreferences.getString("unitID", "0"), getIntent().getStringExtra("id"));

                                            if (in == null){
                                                System.out.println("There was a problem accessing the database!");
                                            }
                                            else{
                                                try{
                                                    String result = convertStreamToString(in);
                                                    responseData = new JSONObject(result);

                                                    getIntent().putExtra("id", responseData.getString("id"));
                                                    getIntent().putExtra("technicians", responseData.getString("technicians"));
                                                    getIntent().putExtra("username", responseData.getString("username"));
                                                    getIntent().putExtra("userFirstName", responseData.getString("firstname"));
                                                    getIntent().putExtra("userLastName", responseData.getString("lastname"));
                                                    getIntent().putExtra("phoneNumber", responseData.getString("phoneNumber"));
                                                    getIntent().putExtra("email", responseData.getString("email"));
                                                    getIntent().putExtra("currentProgess", responseData.getString("currentProgress"));
                                                    getIntent().putExtra("priority", responseData.getString("priority"));
                                                    getIntent().putExtra("unit_id", responseData.getString("unitID"));
                                                    getIntent().putExtra("details", responseData.getString("details"));
                                                    getIntent().putExtra("startDate", responseData.getString("startDate"));
                                                    getIntent().putExtra("department", responseData.getString("department"));
                                                    getIntent().putExtra("endDate", responseData.getString("endDate"));
                                                    getIntent().putExtra("userFirstName", responseData.getString("firstname"));
                                                    getIntent().putExtra("lastUpdated", responseData.getString("lastUpdated"));
                                                    getIntent().putExtra("lastUpdatedTime", responseData.getString("lastUpdatedTime"));
                                                    getIntent().putExtra("ticketLocation", responseData.getString("ticketLocation"));
                                                    getIntent().putExtra("updates", responseData.getString("updates"));
                                                    //Used to determine what the user can do in the next page.
                                                    getIntent().putExtra("position", responseData.getString("position"));

                                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                                    editor.putString("tickets", responseData.getString("updatedTickets"));
                                                    editor.commit();

                                                    //Refreshes the window.
                                                    finish();
                                                    startActivity(getIntent());

                                                }catch (Exception e){
                                                    System.out.println("There was an error parsing the data!");
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    })
                                    .setNegativeButton("Decline", null)
                                    .show();
                        }
                        else{
                            AlertDialog alertDialog = new AlertDialog.Builder(DisplayTicket.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("This ticket has already been assigned to you!");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    }
                }
            });

        updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent updatePage = new Intent(DisplayTicket.this, UpdateTicket.class);
                    startActivity(updatePage);
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

    protected InputStream getAccess(String username, String unitID, String ticketID){
        final String URL = "http://192.168.42.173:8080/springmvc/AndroidAssign?username="+username.trim()+"&androidTechAssign="+ticketID+"&unitID="+unitID;
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
}
