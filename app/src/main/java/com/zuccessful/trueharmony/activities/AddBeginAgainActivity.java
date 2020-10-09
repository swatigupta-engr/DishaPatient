package com.zuccessful.trueharmony.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.pojo.MedicineRecord_s;
import com.zuccessful.trueharmony.pojo.WeeklyTask;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ca.antonious.materialdaypicker.MaterialDayPicker;

import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;

public class AddBeginAgainActivity extends AppCompatActivity {
    private TextInputLayout nameLayout, descLayout;
    private TextInputEditText descEditText;
    private Spinner taskSpinner;
    private String  taskSlected;
    private LinearLayout medTimeLayout, medOtherLayout;
    private ArrayList<EditText> timesEditText;
    private MaterialDayPicker dayPicker;
    private AlarmManager alarmManager;
    private SakshamApp app;
    private FirebaseFirestore db;
    private PendingIntent pendingIntent;
    private ProgressBar progressBar;
    private TextView how_many_times;
    private Boolean injection_flag=false;
    EditText mon_task,tue_task,wed_task,thu_task,fri_task,sat_task,sun_task;
    ArrayList<WeeklyTask> added_tasks ;

    private String[] timesDef = {"08:00 AM", "02:00 PM", "08:00 PM"};
    HashMap<String,String>sub_tasks;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utilities.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.changeLanguage(this);
        setContentView(R.layout.add_weekly_task);
        progressBar = findViewById(R.id.progressbar);
        added_tasks=new ArrayList<>();

        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        medTimeLayout = findViewById(R.id.med_time_pref);
         how_many_times=findViewById(R.id.how_many_times);
//        nameLayout = findViewById(R.id.med_name_lay);
        descLayout = findViewById(R.id.med_desc_lay);
        taskSpinner = findViewById(R.id.med_name_et);

        descEditText = findViewById(R.id.med_desc_et);
        dayPicker = findViewById(R.id.repeat_days);
        mon_task=findViewById(R.id.task_monday);
        tue_task=findViewById(R.id.task_tuesday);
        wed_task=findViewById(R.id.task_wednesday);
        thu_task=findViewById(R.id.task_thursday);
        fri_task=findViewById(R.id.task_friday);
        sat_task=findViewById(R.id.task_saturday);
        sun_task=findViewById(R.id.task_sunday);


//        attachTimeListeners(1);

        taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                taskSlected = adapterView.getItemAtPosition(i).toString();
                Log.d("swati","task seelcted is"+taskSlected);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

     /*   if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().get(Constants.CALLED_FROM)!=null)
            {String a=getIntent().getExtras().get(Constants.CALLED_FROM).toString();
                Log.d("saumya","ONCREATE MED IN GetIntent() type is -"+a+" " +getIntent().getExtras().get(Constants.CALLED_FROM).getClass().getName());
                handleEditAlarm();

            }
        }*/
    }


/*
    public static void selectTime(Context context, final EditText editText, int h, int m)
    {
        // TODO: Fix this method
        Log.d("saumya","in selectTime ");
        Calendar calendar = Calendar.getInstance();
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMin = calendar.get(Calendar.MINUTE);

        if (h >= 0 && h < 24 && m >= 0 && m < 60) {
            mHour = h;
            mMin = m;
        }
        Locale.setDefault(Locale.ENGLISH);
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay,
                                  int minute) {
                DateFormat df = new SimpleDateFormat("HH:mm");
                //Date/time pattern of desired output date
                DateFormat outputformat = new SimpleDateFormat("hh:mm aa");
                Date date = null;
                String output = null;
                try{
                    //Conversion of input String to date
                    date= df.parse(hourOfDay+":"+minute);
                    //old date format to new date format
                    output = outputformat.format(date);
                    editText.setText(output);
                }catch(ParseException pe){
                    pe.printStackTrace();
                }
//                editText.setText(String.format(Locale.ENGLISH, "%02d:%02d", i, i1));


            }
        }, mHour, mMin, false);

        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok), timePickerDialog);
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel), timePickerDialog);

        timePickerDialog.show();
    }
*/

    public void submitMed(View view) {
        progressBar.setVisibility(View.VISIBLE);
        final ArrayList<String> reminders = new ArrayList<>();
        String mon,tue,wed,thu,fri,sat,sun;
        String name;
        HashMap<String, Boolean> days = new HashMap<>();
        HashMap<String,Boolean> subtask_status=new HashMap<>();
        sub_tasks=new HashMap<>();
        name = taskSlected;
        final ArrayList<Integer> weekdays = new ArrayList<>();
        if (name.equals(""))
        {
            Toast.makeText(AddBeginAgainActivity.this, getResources().getString(R.string.select_med), Toast.LENGTH_SHORT).show();
            return;
        }

        else {

            mon=mon_task.getText().toString();
            tue=tue_task.getText().toString();
            wed=wed_task.getText().toString();
            thu=thu_task.getText().toString();
            fri=fri_task.getText().toString();
            sat=sat_task.getText().toString();
            sun=sun_task.getText().toString();


          if(mon!=null){
              weekdays.add(Calendar.MONDAY);
              days.put("mon", true);
             // subtask_status.put(mon,false);
              sub_tasks.put("Monday",mon+"$"+"false");

          }
          else{
             // subtask_status.put("none",false);

              sub_tasks.put("Monday","none");
              days.put("mon", false);

          }
          if(tue!=null)
          { weekdays.add(Calendar.TUESDAY);
              days.put("tue", true);
              //subtask_status.put(tue,false);

              sub_tasks.put("Tuesday",tue+"$"+"false");

          }
          else{
              //subtask_status.put("none",false);

              sub_tasks.put("Tuesday","none");
              days.put("tue", false);

          }
            if(wed!=null){
                weekdays.add(Calendar.WEDNESDAY);
            //    subtask_status.put(wed,false);

                days.put("wed", true);
                sub_tasks.put("Wednesday",wed+"$"+"false");
            }
            else{
          //      subtask_status.put("none",false);

                sub_tasks.put("Wednesday","none");

                days.put("wed", false);

            }
            if(thu!=null)
            { weekdays.add(Calendar.THURSDAY);
                days.put("thu", true);
         //       subtask_status.put(thu,false);

                sub_tasks.put("Thursday",thu+"$"+"false");

            }
            else{
               // subtask_status.put("none",false);

                sub_tasks.put("Thursday","none");
                days.put("thu", false);


            }
            if(fri!=null){
                weekdays.add(Calendar.FRIDAY);
                //subtask_status.put(fri,false);

                days.put("fri", true);
                sub_tasks.put("Friday",fri+"$"+"false");

            }
            else {
               // subtask_status.put("none",false);

                sub_tasks.put("Friday", "none");
                days.put("fri", false);

            }

            if(sat!=null){
                weekdays.add(Calendar.SATURDAY);
               // subtask_status.put(sat,false);

                days.put("sat", true);
                sub_tasks.put("Saturday",sat+"$"+"false");

            }
            else{
              //  subtask_status.put("none",false);

                sub_tasks.put("Saturday","none");
                days.put("sat", false);


            }
            if(sun!=null)
            { weekdays.add(Calendar.SUNDAY);
                days.put("sun", true);
              //  subtask_status.put(sun,false);

                sub_tasks.put("Sunday",sun+"$"+"false");

            }
            else {
              //  subtask_status.put("none",false);

                sub_tasks.put("Sunday","none");
                days.put("sun", false);


            }

            setTaskData(name,days,sub_tasks,false);
            if(getIntent().getExtras()!=null){
                if(getIntent().getExtras().get(Constants.CALLED_FROM)!=null)
                {
                    Medication mItem = (Medication) getIntent().getSerializableExtra(Constants.MED_OBJ);
                    SakshamApp.getInstance().getFirebaseDatabaseInstance().collection("alarms/" +
                            SakshamApp.getInstance().getAppUser(null).getId() +
                            "/weekly_tasks").document(mItem.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            finish();
                        }

                    });
                }
            }else{
                progressBar.setVisibility(View.GONE);
                finish();
            }
        }



    }


    private void setTaskData( final String name, HashMap<String,Boolean> days,HashMap<String, String >subtasks,Boolean status ) {


        final SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
      Date today=new Date();
      String  datetoday  = sdf.format(today);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,  6);
        Date todate1 = cal.getTime();
        String post_date = sdf.format(todate1);
        final WeeklyTask taskObj = new WeeklyTask(name,  days, subtasks, datetoday,post_date,status);
       // Utilities.saveWeeklyTasksToList(taskObj);
       // added_tasks.add(taskObj);
       // Utilities.saveWeeklyTasksToList(taskObj);
        added_tasks= Utilities.getListOfWeeklytasks();
        if(added_tasks==null)
            added_tasks=new ArrayList<>();
        added_tasks.add(taskObj);

        Log.v("added tasks",added_tasks.size()+"");

         Utilities.saveListOfWeeklyTasks(added_tasks);

        ArrayList<WeeklyTask> tasks = Utilities.getListOfWeeklytasks();
        Log.d("Download_Data", " selected tasks are: " + tasks.size());
        if (tasks.size() > 0) {
            updateMedToFireBase(tasks);
        }

    }


    public void updateMedToFireBase(final ArrayList<WeeklyTask> tasks)
    {
        final ArrayList<MedicineRecord_s> medicationArrayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        final String current_date=new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()).toLowerCase();
        if (Utilities.isInternetOn(getApplicationContext()))
        {
            Log.d("firebase", "Internet present");
            for (final WeeklyTask m : tasks)
            {
                DocumentReference documentReference = db.collection("alarms/" + app.getAppUser(null).getId() + "/weekly_tasks").document(m.getName());
                documentReference.set(m).addOnFailureListener(new OnFailureListener()
                {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("firebase","failure in adding "+m.getName());
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("firebase","sucessss in adding  weekly tasks"+m.getName());
                    }
                });

            }
            for (int j = 0; j < tasks.size(); j++)
            {
                Map<String, Boolean> days = tasks.get(j).getDays();
                final String name=tasks.get(j).getName();
                final HashMap<String ,String >subtasks=   tasks.get(j).getSubtasks();

                if (days.containsKey(current_date))
                {
                     final SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                   // for (int k = 0; k < slots; k++)
                  //  {
                    //    final String time=slot_list.get(k);
                     //   final MedicineRecord_s med = new MedicineRecord_s(meds.get(j).getName(), slot_list.get(k), days);
                        DocumentReference dr= db.collection("tasks_weekly_reports/" + getApplicationContext().getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error") +
                                "/" + sdf.format(new Date())).document(tasks.get(j).getName() );
                        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) { if(task.isSuccessful()) {
                                DocumentSnapshot ds= task.getResult();
                                Log.d("logss",ds.getId()+" "+ds.exists());
                                if (ds.exists())
                                {
                                    Log.d("logss","ALREADY ENTRY PRESENT");
                                }
                                else
                                {   Log.d("logss","again setting data to firebase");
                                    db.collection("tasks_weekly_reports/" + getApplicationContext().getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error") +
                                            "/" + sdf.format(new Date())).document(name).set(subtasks, SetOptions.merge());
                                 }
                            }
                            }
                        });
                  //  }
                } else { Log.d("logss", "NO MED ON THIS DAY"); }
            }
        }

    }


     @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
