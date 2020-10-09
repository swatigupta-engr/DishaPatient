package com.zuccessful.trueharmony.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zuccessful.trueharmony.DatabaseRoom;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.RoomEntity.LogEntity;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Log_Patient extends AppCompatActivity {

    EditText new_activity_name,hardships,experience;
    Button submitLog;
    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log__patient);
        new_activity_name= findViewById(R.id.new_activity_tried);
        hardships=findViewById(R.id.hardships);
        experience=findViewById(R.id.experience);
        submitLog= findViewById(R.id.submitLog);
        c= getApplicationContext();
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        submitLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name=new_activity_name.getText().toString();
                final String exp= experience.getText().toString();
                final String hard= hardships.getText().toString();
                //Log.d("Logg"," name is  "+name);
                if(name.equals(""))
                {
                    new_activity_name.setError("This field can not be blank");
                    Toast.makeText(getApplicationContext(),"Enter a valid activity name",Toast.LENGTH_LONG);
                }
                else {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseRoom database = DatabaseRoom.getInstance(getApplicationContext());
                            Log.d("Logg", " " + name + " " + exp + " " + hard);
                            SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                            String t_date = sdf.format(new Date());
                            // database.logRecords().deleteAll();
                            LogEntity ce = new LogEntity(t_date, name, exp, hard);
                            database.logRecords().addLogRecord(ce);
                            List<LogEntity> ret = database.logRecords().getLogRecords();
                            Log.d("Logg", " size of retrieved arraylist : " + ret.size());
                            //   writeToCSV(arr);

                        }
                    });
                    Toast.makeText(getBaseContext(), "Log Added ", Toast.LENGTH_LONG).show();
                    new_activity_name.setText("");
                    experience.setText("");
                    hardships.setText("");
                }
            }
        });
    }
}
