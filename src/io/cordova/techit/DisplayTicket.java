package io.cordova.techit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DisplayTicket extends AppCompatActivity {

    LinearLayout layoutVert;
    LinearLayout layoutHori;

    Button takeBtn;
    Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ticket);

        layoutVert = (LinearLayout) findViewById(R.id.ticketInfo);
        layoutHori = (LinearLayout) findViewById(R.id.buttonMap);

        TextView txt = new TextView(this);
        txt.setText("Ticket ID#: " + getIntent().getIntExtra("id", 0));
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
            txt.setText("Technicians: " + getIntent().getStringExtra("technicians"));
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
}
