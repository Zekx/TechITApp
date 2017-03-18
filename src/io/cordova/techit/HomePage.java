package io.cordova.techit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuInflater;
import android.support.v4.app.ActionBarDrawerToggle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HomePage extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private LinearLayout layout;
    private Button createTicket;
    private ListView navList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean[] sortBy = {true, false, false};

    String id;
    String technicians;
    String username;
    String userFirstName = "";
    String userLastName = "";
    String phone;
    String email;
    String currentProgress;
    String priority;
    int unitId;
    String details;
    Date startDate;
    Date endDate;
    Date lastUpdated;
    String lastUpdatedTime;
    String ticketLocation;
    String completionDetails;
    String updates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        createHomepageLayout();
    }

    public void createHomepageLayout(){
        layout = (LinearLayout) findViewById(R.id.ticketView);
        navList = (ListView) findViewById(R.id.navList);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        llp.setMargins(0, 0, 0, 15); // llp.setMargins(left, top, right, bottom);

        if(getIntent().getBooleanArrayExtra("sort") != null){
            sortBy = getIntent().getBooleanArrayExtra("sort");
        }
        String ticket = sharedpreferences.getString("tickets" , "");

        JSONArray ticketJson = new JSONArray();
        try{
            if(ticket != null ){
                ticketJson = new JSONArray(ticket);
            }

            for(int i = 0; i < ticketJson.length(); i++){
                JSONObject obj = (JSONObject) ticketJson.get(i);

                /*Iterator<String> iter = obj.keys();
                while(iter.hasNext()){
                    String key = iter.next();
                    System.out.println("item: " + key);
                }*/

                id = obj.getString("id");
                JSONArray techList = new JSONArray(obj.getString("technicians"));
                try{
                    for(int j = 0; j < techList.length(); j++){
                        JSONObject techies = (JSONObject) techList.get(i);

                        //System.out.println(techies.toString());
                    }
                }catch(Exception e2){

                }

                username = obj.getString("username");
                userFirstName = obj.getString("userFirstName");
                userLastName = obj.getString("userLastName");
                phone = obj.getString("phone");
                email = obj.getString("email");
                currentProgress = obj.getString("currentProgress");
                priority = obj.getString("currentPriority");
                unitId = obj.getInt("unitId");
                details = obj.getString("details");
                technicians = obj.getString("technicians");

                String string = obj.getString("startDate");
                DateFormat format = new SimpleDateFormat("MMMM d, yyyy");
                startDate = format.parse(string);

                string = obj.getString("startDate");
                endDate = format.parse(string);

                string = obj.getString("startDate");
                lastUpdated = format.parse(string);

                lastUpdatedTime = obj.getString("lastUpdated");
                ticketLocation = obj.getString("ticketLocation");
                completionDetails = obj.getString("completionDetails");
                updates = obj.getString("updates");

                if(sortBy[1]){
                    System.out.println("ACTIVE");
                    if(!currentProgress.equals("COMPLETED")){
                        TextView nBut = new TextView(this);
                        nBut.setText("Ticket ID: " + id + "\n" + details);
                        nBut.setTextSize(16f);
                        nBut.setBackgroundColor(0xffffdf00);
                        nBut.setTextColor(0xff000000);
                        nBut.setPadding(30, 20, 30, 80);
                        createTicketButton(nBut, llp);
                    }
                }
                else if(sortBy[2]){
                    System.out.println("COMPLETED");
                    if(currentProgress.equals("COMPLETED")){
                        TextView nBut = new TextView(this);
                        nBut.setText("Ticket ID: " + id + "\n" + details);
                        nBut.setTextSize(16f);
                        nBut.setBackgroundColor(0xffffdf00);
                        nBut.setTextColor(0xff000000);
                        nBut.setPadding(30, 20, 30, 80);
                        createTicketButton(nBut, llp);
                    }
                }
                else{
                    System.out.println("RECENT");
                    TextView nBut = new TextView(this);
                    nBut.setText("Ticket ID: " + id + "\n" + details);
                    nBut.setTextSize(16f);
                    nBut.setBackgroundColor(0xffffdf00);
                    nBut.setTextColor(0xff000000);
                    nBut.setPadding(30, 20, 30, 80);
                    createTicketButton(nBut, llp);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        this.setUpDrawer();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(HomePage.this)
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HomePage.this.finish();

                        sharedpreferences.edit().clear().commit();

                        Intent intent = new Intent(HomePage.this, Login.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    protected void setUpDrawer(){
        String[] choices = {"Logout","______________________", "Create Ticket", "Search"};
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, choices);
        navList.setAdapter(mAdapter);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    new AlertDialog.Builder(HomePage.this)
                            .setMessage("Are you sure you want to logout?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    HomePage.this.finish();

                                    sharedpreferences.edit().clear().commit();

                                    Intent intent = new Intent(HomePage.this, Login.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                else if(position == 2){
                    Intent createPage = new Intent(HomePage.this, CreateTicket.class);
                    createPage.putExtra("firstname", getIntent().getStringExtra("firstname"));
                    createPage.putExtra("lastname", getIntent().getStringExtra("lastname"));
                    createPage.putExtra("phoneNumber", getIntent().getStringExtra("phoneNumber"));
                    createPage.putExtra("email", getIntent().getStringExtra("email"));
                    if(getIntent().getStringExtra("department") != null){
                        createPage.putExtra("department", getIntent().getStringExtra("department"));
                    }

                    startActivity(createPage);
                }
                else if (position == 3){
                    System.out.println("Option "+3);
                }
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.nav_icon, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("TECHIT Navigation");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("Welcome " + getIntent().getStringExtra("firstname") + " " + getIntent().getStringExtra("lastname"));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        setTitle("Welcome " + getIntent().getStringExtra("firstname") + " " + getIntent().getStringExtra("lastname"));
    }

    protected void createTicketButton(TextView nBut, LinearLayout.LayoutParams llp){
        nBut.setOnClickListener(new View.OnClickListener(){
            private String idInner = id;
            private String techInner = technicians;
            private String usernameInner = username;
            private String userFirstNameInner = userFirstName;
            private String userLastNameInner = userLastName;
            private String phoneNumber = phone;
            private String emailInner = email;
            private String currentProgessInner = currentProgress;
            private String priorityInner = priority;
            private int unitIdInner = unitId;
            private String detailsInner = details;
            private Date sdInner = startDate;
            private Date edInner = endDate;
            private Date ldInner = lastUpdated;
            private String ludInner = lastUpdatedTime;
            private String ticketLocationInner = ticketLocation;
            private String comDetails = completionDetails;
            private String upd = updates;

            @Override
            public void onClick(View v){
                //System.out.println(userFirstName + " " + userLastName + " \nDate:" + startDate + " " + lastUpdatedTime + " \n" + "Prority: " + priority);
                Intent intent = new Intent(HomePage.this, DisplayTicket.class);

                intent.putExtra("id", idInner);
                if(techInner != null){
                    intent.putExtra("technicians", techInner);
                }
                intent.putExtra("username", usernameInner);
                intent.putExtra("userFirstName", userFirstNameInner);
                intent.putExtra("userLastName", userLastNameInner);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("email", emailInner);
                intent.putExtra("currentProgress", currentProgessInner);
                intent.putExtra("priority", priorityInner);
                intent.putExtra("unit_id", unitIdInner);
                intent.putExtra("details", detailsInner);
                intent.putExtra("startDate", sdInner.toString());
                if(getIntent().hasExtra("department")) {
                    intent.putExtra("department", getIntent().getStringExtra("department"));
                }
                else{
                    intent.putExtra("department", "");
                }
                intent.putExtra("endDate", edInner.toString());
                intent.putExtra("lastUpdated", ldInner.toString());
                intent.putExtra("lastUpdatedTime", ludInner.toString());
                intent.putExtra("ticketLocation", ticketLocationInner);
                intent.putExtra("completionDetails", comDetails);
                intent.putExtra("updates", upd);

                //Used to determine what the user can do in the next page.
                intent.putExtra("position", getIntent().getStringExtra("position"));

                startActivity(intent);
            }
        });

        layout.addView(nBut, llp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sortRecent:
                sortBy[0] = true;
                sortBy[1] = false;
                sortBy[2] = false;

                System.out.println(sortBy[0] + " " + sortBy[1] + " " + sortBy[2]);
                getIntent().putExtra("sort", sortBy);
                finish();
                getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(getIntent());
                break;
            case R.id.sortActive:
                sortBy[0] = false;
                sortBy[1] = true;
                sortBy[2] = false;

                System.out.println(sortBy[0] + " " + sortBy[1] + " " + sortBy[2]);
                getIntent().putExtra("sort", sortBy);
                finish();
                getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(getIntent());
                break;
            case R.id.sortCompleted:
                sortBy[0] = false;
                sortBy[1] = false;
                sortBy[2] = true;

                System.out.println(sortBy[0] + " " + sortBy[1] + " " + sortBy[2]);
                getIntent().putExtra("sort", sortBy);
                finish();
                getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(getIntent());
                break;
        }
        return true;
    }

    @Override
    public void onRestart()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
        finish();
        startActivity(getIntent());
    }
}
