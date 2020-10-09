package com.zuccessful.trueharmony.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.pojo.WeeklyTask;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class WeeklyReport extends AppCompatActivity {
    private ArrayList<WeeklyTask> tasks=new ArrayList<>();
    private ArrayList<WeeklyTask> tasks_filtered_by_subtasks=new ArrayList<>();

   Spinner spinner;
   RelativeLayout rel_mon,rel_tue,rel_wed,rel_thu,rel_fri,rel_sat,rel_sun;
   TextView tasks_mon,tasks_tue,tasks_wed,tasks_thu,tasks_fri,tasks_sat,tasks_sun;
   ImageView tasks_mon_img,tasks_tue_img,tasks_wed_img,tasks_thu_img,tasks_fri_img,tasks_sat_img,tasks_sun_img;
   TextView current_week_dates;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weekly_report);

        spinner=(Spinner)findViewById(R.id.tasks_spinner);
        current_week_dates =(TextView)findViewById(R.id.current_week_dates);
        tasks = Utilities.getListOfWeeklytasks();

        rel_mon=(RelativeLayout) findViewById(R.id.rel_mon);
        rel_tue=(RelativeLayout) findViewById(R.id.rel_tue);
        rel_wed=(RelativeLayout) findViewById(R.id.rel_wed);
        rel_thu=(RelativeLayout) findViewById(R.id.rel_thu);
        rel_fri=(RelativeLayout) findViewById(R.id.rel_fri);
        rel_sat=(RelativeLayout) findViewById(R.id.rel_sat);
        rel_sun=(RelativeLayout) findViewById(R.id.rel_sun);

        tasks_mon=(TextView)findViewById(R.id.task_mon);
        tasks_tue=(TextView)findViewById(R.id.task_tue);
        tasks_wed=(TextView)findViewById(R.id.task_wed);
        tasks_thu=(TextView)findViewById(R.id.task_thu);
        tasks_fri=(TextView)findViewById(R.id.task_fri);
        tasks_sat=(TextView)findViewById(R.id.task_sat);
        tasks_sun=(TextView)findViewById(R.id.task_sun);



        tasks_mon_img=(ImageView) findViewById(R.id.img_mon);
        tasks_tue_img=(ImageView) findViewById(R.id.img_tue);
        tasks_wed_img=(ImageView) findViewById(R.id.img_wed);
        tasks_thu_img=(ImageView) findViewById(R.id.img_thu);
        tasks_fri_img=(ImageView) findViewById(R.id.img_fri);
        tasks_sat_img=(ImageView) findViewById(R.id.img_sat);
        tasks_sun_img=(ImageView) findViewById(R.id.img_sun);

        Log.v("Tasks,,,,,",tasks+"\n"+"tasks size___"+tasks.size());

        final String current_date_cap;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

        Date date = calendar.getTime();
        String date_today= sdf.format(new Date());
        String date_7_days_before;

        Log.v("date today",date_today);

       // date_six_days_before=getCalculatedDate(date_today,"MM-dd-yy", -6); // It will gives you date before 10 days from current date
      //  Date newDate = new Date(date.getTime() - 604800000L); // 7 * 24 * 60 * 60 * 1000
        Calendar calendar2 = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date newDate = calendar.getTime();

        date_7_days_before=  sdf.format(newDate);

        Log.v("date 6 days befor",date_7_days_before);

        current_date_cap  =capitalize(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime()).toLowerCase());



        ArrayList<String> all_tasks=new ArrayList<>();
        int in_range=0;
        Date start=null,end=null;
        for(int i=0;i<tasks.size();i++){
            try{
                   start= sdf.parse( tasks.get(i).getDate_start());}
            catch (Exception e){}

            try{
                end= sdf.parse(tasks.get(i).getDate_end());
            }
            catch (Exception e){}
            if(start!=null ){
                if(start.after(newDate)|| end.before(date)|| end.equals(date)|| start.equals(date)){
                    in_range=1;
                }
            }
            if(in_range==1){
          if(Utilities.getListofSubtasks(tasks.get(i)).size()>0)
                all_tasks.add(tasks.get(i).getName());

            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, all_tasks);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(Utilities.getListofSubtasks(tasks.get(i)).size()>0){

                    current_week_dates.setText(tasks.get(i).getDate_start()+" to "+tasks.get(i).getDate_end());

                    tasks_filtered_by_subtasks.add(tasks.get(i));
              HashMap<String,String> subtasks;
                    subtasks=tasks_filtered_by_subtasks.get(i).getSubtasks();
                    String status_Monday= (subtasks.get("Monday").split("\\$")[1]);
                    String status_Tuesday= (subtasks.get("Tuesday").split("\\$")[1]);
                    String status_Wed= (subtasks.get("Wednesday").split("\\$")[1]);
                    String status_Thu= (subtasks.get("Thursday").split("\\$")[1]);
                    String status_Fri= (subtasks.get("Friday").split("\\$")[1]);
                    String status_Sat= (subtasks.get("Saturday").split("\\$")[1]);
                    String status_Sun= (subtasks.get("Sunday").split("\\$")[1]);


                    String task_mon= (subtasks.get("Monday").split("\\$")[0]);
                    String task_tue= (subtasks.get("Tuesday").split("\\$")[0]);
                    String task_wed= (subtasks.get("Wednesday").split("\\$")[0]);
                    String task_thu= (subtasks.get("Thursday").split("\\$")[0]);
                    String task_fri= (subtasks.get("Friday").split("\\$")[0]);
                    String task_sat= (subtasks.get("Saturday").split("\\$")[0]);
                    String task_sun= (subtasks.get("Sunday").split("\\$")[0]);


                    Log.v("Task name",tasks.get(i).getName()+"\n");
                    Log.v("__ subtasks_mon_",task_mon+"________"+status_Monday);
                    Log.v("__ subtasks_tue_",task_tue+"________"+status_Tuesday);
                    Log.v("__ subtasks_wed_",task_wed+"________"+status_Wed);
                    Log.v("__ subtasks_thu_",task_thu+"________"+status_Thu);
                    Log.v("__ subtasks_fri_",task_fri+"________"+status_Fri);
                    Log.v("__ subtasks_sat_",task_sat+"________"+status_Sat);
                    Log.v("__ subtasks_sun_",task_sun+"________"+status_Sun);


                    tasks_mon.setText(task_mon);
                    tasks_tue.setText(task_tue);
                    tasks_wed.setText(task_wed);
                    tasks_thu.setText(task_thu);
                    tasks_fri.setText(task_fri);
                    tasks_sat.setText(task_sat);
                    tasks_sun.setText(task_sun);

                    if(status_Monday.equalsIgnoreCase("true"))
                        tasks_mon_img.setImageResource(R.drawable.smiley);

                    else tasks_mon_img.setImageResource(R.drawable.sad_face);



                    if(status_Tuesday.equalsIgnoreCase("true"))
                        tasks_tue_img.setImageResource(R.drawable.smiley);

                    else tasks_tue_img.setImageResource(R.drawable.sad_face);



                    if(status_Wed.equalsIgnoreCase("true"))
                        tasks_wed_img.setImageResource(R.drawable.smiley);

                    else tasks_wed_img.setImageResource(R.drawable.sad_face);


                    if(status_Thu.equalsIgnoreCase("true"))
                        tasks_thu_img.setImageResource(R.drawable.smiley);

                    else tasks_thu_img.setImageResource(R.drawable.sad_face);


                    if(status_Fri.equalsIgnoreCase("true"))
                        tasks_fri_img.setImageResource(R.drawable.smiley);

                    else tasks_fri_img.setImageResource(R.drawable.sad_face);



                    if(status_Sat.equalsIgnoreCase("true"))
                        tasks_sat_img.setImageResource(R.drawable.smiley);

                    else tasks_sat_img.setImageResource(R.drawable.sad_face);


                    if(status_Sun.equalsIgnoreCase("true"))
                        tasks_sun_img.setImageResource(R.drawable.smiley);

                    else tasks_sun_img.setImageResource(R.drawable.sad_face);

                    if(task_mon.length()==0)
                        rel_mon.setVisibility(View.GONE);

                    if(task_tue.length()==0)
                        rel_tue.setVisibility(View.GONE);


                    if(task_wed.length()==0)
                        rel_wed.setVisibility(View.GONE);

                    if(task_thu.length()==0)
                        rel_thu.setVisibility(View.GONE);

                    if(task_fri.length()==0)
                        rel_fri.setVisibility(View.GONE);

                    if(task_sat.length()==0)
                        rel_sat.setVisibility(View.GONE);


                    if(task_sun.length()==0)
                        rel_sun.setVisibility(View.GONE);





                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


     /*   for(int i=0;i<tasks.size();i++){

            if(Utilities.getListofSubtasks(tasks.get(i)).size()>0){
                tasks_filtered_by_subtasks.add(tasks.get(i));

               subtasks=tasks_filtered_by_subtasks.get(i).getSubtasks();
                 String status_Monday= (subtasks.get("Monday").split("\\$")[1]);
                String status_Tuesday= (subtasks.get("Tuesday").split("\\$")[1]);
                String status_Wed= (subtasks.get("Wednesday").split("\\$")[1]);
                String status_Thu= (subtasks.get("Thursday").split("\\$")[1]);
                String status_Fri= (subtasks.get("Friday").split("\\$")[1]);
                String status_Sat= (subtasks.get("Saturday").split("\\$")[1]);
                String status_Sun= (subtasks.get("Sunday").split("\\$")[1]);


                String task_mon= (subtasks.get("Monday").split("\\$")[0]);
                String task_tue= (subtasks.get("Tuesday").split("\\$")[0]);
                String task_wed= (subtasks.get("Wednesday").split("\\$")[0]);
                String task_thu= (subtasks.get("Thursday").split("\\$")[0]);
                String task_fri= (subtasks.get("Friday").split("\\$")[0]);
                String task_sat= (subtasks.get("Saturday").split("\\$")[0]);
                String task_sun= (subtasks.get("Sunday").split("\\$")[0]);


                Log.v("Task name",tasks.get(i).getName()+"\n");
                Log.v("__ subtasks_mon_",task_mon+"________"+status_Monday);
                Log.v("__ subtasks_tue_",task_tue+"________"+status_Tuesday);
                Log.v("__ subtasks_wed_",task_wed+"________"+status_Wed);
                Log.v("__ subtasks_thu_",task_thu+"________"+status_Thu);
                Log.v("__ subtasks_fri_",task_fri+"________"+status_Fri);
                Log.v("__ subtasks_sat_",task_sat+"________"+status_Sat);
                Log.v("__ subtasks_sun_",task_sun+"________"+status_Sun);


                tasks_mon.setText(task_mon);
                tasks_tue.setText(task_tue);
                tasks_wed.setText(task_wed);
                tasks_thu.setText(task_thu);
                tasks_fri.setText(task_fri);
                tasks_sat.setText(task_sat);
                tasks_sun.setText(task_sun);

                if(status_Monday.equalsIgnoreCase("true"))
                tasks_mon_img.setImageResource(R.drawable.almonds);

                else tasks_mon_img.setImageResource(R.drawable.brown_bread);



                if(status_Tuesday.equalsIgnoreCase("true"))
                    tasks_tue_img.setImageResource(R.drawable.almonds);

                else tasks_tue_img.setImageResource(R.drawable.brown_bread);



                if(status_Wed.equalsIgnoreCase("true"))
                    tasks_wed_img.setImageResource(R.drawable.almonds);

                else tasks_wed_img.setImageResource(R.drawable.brown_bread);


                if(status_Thu.equalsIgnoreCase("true"))
                    tasks_thu_img.setImageResource(R.drawable.almonds);

                else tasks_thu_img.setImageResource(R.drawable.brown_bread);


                if(status_Fri.equalsIgnoreCase("true"))
                    tasks_fri_img.setImageResource(R.drawable.almonds);

                else tasks_fri_img.setImageResource(R.drawable.brown_bread);



                if(status_Sat.equalsIgnoreCase("true"))
                    tasks_sat_img.setImageResource(R.drawable.almonds);

                else tasks_sat_img.setImageResource(R.drawable.brown_bread);


                if(status_Sun.equalsIgnoreCase("true"))
                    tasks_sun_img.setImageResource(R.drawable.almonds);

                else tasks_sun_img.setImageResource(R.drawable.brown_bread);

                if(task_mon.length()==0)
                    rel_mon.setVisibility(View.GONE);

                if(task_tue.length()==0)
                    rel_tue.setVisibility(View.GONE);


                if(task_wed.length()==0)
                    rel_wed.setVisibility(View.GONE);

                if(task_thu.length()==0)
                    rel_thu.setVisibility(View.GONE);

                if(task_fri.length()==0)
                    rel_fri.setVisibility(View.GONE);

                if(task_sat.length()==0)
                    rel_sat.setVisibility(View.GONE);


                if(task_sun.length()==0)
                    rel_sun.setVisibility(View.GONE);





            }
        }*/

    }

    public static String capitalize(String str)
    {
        if(str == null) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    public static String getCalculatedDate(String date, String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        cal.add(Calendar.DAY_OF_YEAR, days);
        try {
            return s.format(new Date(s.parse(date).getTime()));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            Log.e("TAG", "Error in Parsing Date : " + e.getMessage());
        }
        return null;
    }
}
