package com.zuccessful.trueharmony.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
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
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.pojo.DailyRoutineRecord;
import com.zuccessful.trueharmony.receivers.AlarmReceiver;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;


public class AddDailyRoutActivity extends AppCompatActivity {
    private LinearLayout dailyRoutTimeLayout;
    private Spinner activitySpinner;
    private String selected;
    private ArrayList<EditText> timesEditText;
    private AlarmManager alarmManager;
    private SakshamApp app;
    private FirebaseFirestore db;
    private PendingIntent pendingIntent;
    private String[] timesDef = {"08:00", "14:00", "20:00"};

    // todo set daily rout task name, get from intent

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utilities.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.changeLanguage(this);
        setContentView(R.layout.activity_add_daily_rout);
        activitySpinner = findViewById(R.id.med_name_et);
        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        dailyRoutTimeLayout = findViewById(R.id.daily_rout_time_pref);

        attachTimeListeners(Constants.DEFAULT_ACTIVITY_REPETITIONS);
        ArrayList<String>phyActList = Utilities.getListFromSharedPref(Constants.KEY_PHY_ACT_LIST);
        ArrayList<String>leisureActList = Utilities.getListFromSharedPref(Constants.KEY_LEISURE_ACT_LIST);
        ArrayList<String> both = new ArrayList<>();
        both.addAll(phyActList);
        both.addAll(leisureActList);
        String[] arraySpinner = both.toArray(new String[both.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, arraySpinner);
        activitySpinner.setAdapter(adapter);
        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected = adapterView.getItemAtPosition(i).toString();
                Log.d("saumya"," " +selected+ " selected");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(getResources().getString(R.string.daily_routine));
        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().get(Constants.CALLED_FROM)!=null) {
                handleEditAlarm();

            }
        }
    }


    private void handleEditAlarm() {
        DailyRoutine dailyRoutine = (DailyRoutine) getIntent().getSerializableExtra(Constants.DAILY_ROUT_OBJ);
        switch (dailyRoutine.getReminders().size()){
            case 1:{
                ((RadioButton)findViewById(R.id.daily_rout_reminder_1)).setChecked(true);
                break;
            }
            case 2:{
                ((RadioButton)findViewById(R.id.daily_rout_reminder_2)).setChecked(true);
                break;
            }
            case 3:{
                ((RadioButton)findViewById(R.id.daily_rout_reminder_3)).setChecked(true);
                break;
            }
        }



    }

    public static void selectTime(Context context, final EditText editText, int h, int m)
    {
        Calendar calendar = Calendar.getInstance();
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMin = calendar.get(Calendar.MINUTE);

        if (h >= 0 && h < 24 && m >= 0 && m < 60) {
            mHour = h;
            mMin = m;
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) { editText.setText(String.format(Locale.ENGLISH, "%02d:%02d", i, i1));
            }
        }, mHour, mMin, false);

        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok), timePickerDialog);
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel), timePickerDialog);

        timePickerDialog.show();
    }

    public void submitDailyRout(View view) {
        final ArrayList<String> reminders = new ArrayList<>();
        if (selected==null) {
            Toast.makeText(this,"Please add activities first",Toast.LENGTH_LONG).show();
        } else {
            int to_be_added=0;
            int size=0;
            try {
                size = Utilities.getListOfDailyRoutineAlarms().size();
            }catch (Exception e){}
            if(size>0)
            { for(int i=0;i<size;i++){


                if(Utilities.getListOfDailyRoutineAlarms().get(i).getName().equalsIgnoreCase(selected)){
                    Toast.makeText(this,"Activity already added!",Toast.LENGTH_LONG).show();
                     to_be_added=0;
                     break;
                }
                else{
                         to_be_added=1;
                }
            }
            if(to_be_added==1){
                setDailyRoutAlarm(reminders, selected, "1");

            }}
            else{
                setDailyRoutAlarm(reminders, selected, "1");

            }
            finish();
        }
    }



    private void setDailyRoutAlarm(final ArrayList<String> reminders, final String name, final String id)
    {
        final ArrayList<Integer> alarm_ids = new ArrayList<>();
        final ArrayList<Integer> weekdays = new ArrayList<>();
        Map<String, Boolean> days = new HashMap<>();

        weekdays.add(Calendar.SUNDAY);
        days.put("sun", true);

        weekdays.add(Calendar.MONDAY);
        days.put("mon", true);

        weekdays.add(Calendar.TUESDAY);
        days.put("tue", true);

        weekdays.add(Calendar.WEDNESDAY);
        days.put("wed", true);

        weekdays.add(Calendar.THURSDAY);
        days.put("thu", true);

        weekdays.add(Calendar.FRIDAY);
        days.put("fri", true);

        weekdays.add(Calendar.SATURDAY);
        days.put("sat", true);



//Put data in database




//These plus the corresponding get methods are all included
        for (EditText editText : timesEditText) {
            String time = editText.getText().toString();
            if (!time.equals("")) {
                reminders.add(time);
            }
        }
        Log.d("saumya","size of rem: "+reminders.size());


        for(int j=0;j<7;j++)   // as alarm set for all 7 days
        {
            for (int i = 0; i < reminders.size(); i++)
            {
                alarm_ids.add(Utilities.getNextAlarmId(this));
            }
        }


        Log.d("saumya","alarm id arraylist:"+alarm_ids);

        final DailyRoutine dailyRoutineObj = new DailyRoutine(name, reminders,id,days);
        dailyRoutineObj.setAlarmIds(alarm_ids);
        Utilities.saveDailyRoutineAlarms(dailyRoutineObj);
        Log.d("saumya","saving alarm");


        if (weekdays.size() * dailyRoutineObj.getReminders().size() == alarm_ids.size())
        {      // Check
            for (int slot = 0; slot < dailyRoutineObj.getReminders().size(); slot++)
            {
                for (int i = 0; i < weekdays.size(); i++) {
                    Intent mIntent = new Intent(AddDailyRoutActivity.this, AlarmReceiver.class);
                    Utilities.setExtraForIntent(mIntent, "dailyRoutRaw", dailyRoutineObj);
                    Log.d("saumya"," setting in intent id: ----"+ alarm_ids.get(slot* weekdays.size() + i));
                    mIntent.putExtra("alarm_id", alarm_ids.get(slot * weekdays.size() + i));
                    mIntent.putExtra("dailyRoutSlot", slot);
                    int h = Integer.parseInt(reminders.get(slot).split(":")[0]);
                    int m = Integer.parseInt(reminders.get(slot).split(":")[1]);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, h);
                    calendar.set(Calendar.MINUTE, m);
                    calendar.set(Calendar.SECOND, 0);
                    scheduleAlarm(alarmManager, calendar, mIntent, alarm_ids.get(slot * weekdays.size() + i), weekdays.get(i));
                    Log.d("saumya", "Setting alarm, Alarm Id: " + alarm_ids.get(slot * weekdays.size() + i) + " Slot: " + slot + " : weekday: " + weekdays.get(i));
                }
            }
        }

        Log.d("saumya---->>",alarm_ids.size()+"");
        /////////////////////////////////////////////////////
        ArrayList<DailyRoutine> alarms = Utilities.getListOfDailyRoutineAlarms();
        Log.d("Download_Data", " selected alarms are: " + alarms.size());
        if (alarms.size() > 0) {
            updateAlarmToFireBase(alarms);
        }

    }
    public void updateAlarmToFireBase(final ArrayList<DailyRoutine> alarms)
    {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        final String current_date=new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()).toLowerCase();
        if (Utilities.isInternetOn(getApplicationContext()))
        {
            Log.d("firebase", "Internet present");
            for (final DailyRoutine m : alarms)
            {
                DocumentReference documentReference = db.collection("alarms/" + app.getAppUser(null).getId() + "/daily_routine").document(m.getName());
                documentReference.set(m).addOnFailureListener(new OnFailureListener()
                {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("firebase","failure in adding "+m.getName());
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("firebase","sucessss in adding "+m.getName());
                    }
                });

            }
            for (int j = 0; j < alarms.size(); j++)
            {
                Map<String, Boolean> days = alarms.get(j).getDays();
                if (days.containsKey(current_date))
                {
                    ArrayList<String> slot_list = alarms.get(j).getReminders();
                    int slots = slot_list.size();
                    final SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                    for (int k = 0; k < slots; k++)
                    {
                        final String time=slot_list.get(k);
                        final DailyRoutineRecord data = new DailyRoutineRecord(alarms.get(j).getName(), slot_list.get(k), days);
                        DocumentReference dr= db.collection("patient_daily_routine_reports/" + getApplicationContext().getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error") +
                                "/" + sdf.format(new Date())).document(data.getName()+" "+slot_list.get(k));
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
                                    db.collection("patient_daily_routine_reports/" + getApplicationContext().getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error") +
                                            "/" + sdf.format(new Date())).document(data.getName()+" "+time).set(data, SetOptions.merge());
                                    Log.d("logss","settt to false-----------");
                                }
                            }
                            }
                        });
                    }
                } else { Log.d("logss", "NO MED ON THIS DAY"); }
            }
        }

    }

    private void scheduleAlarm(AlarmManager alarmManager, Calendar calendar, Intent intent, int request_code, Integer day) {
        Log.d("saumya", " In ScheduleAlarm  ");
        calendar.set(Calendar.DAY_OF_WEEK, day);
        // Check we aren't setting it in the past which would trigger it to fire instantly
        if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            Log.d("saumya--"," correct time...");
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }

        // Set this to whatever you were planning to do at the given time
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddDailyRoutActivity.this, request_code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        finish();
    }

    public void attachTimeListeners(int count) {
        timesEditText = new ArrayList<>();
        dailyRoutTimeLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            final EditText et = new EditText(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            et.setLayoutParams(layoutParams);
            et.setText(timesDef[i]);
            et.setFocusable(false);
            final int h = Integer.parseInt(timesDef[i].split(":")[0]);
            final int m = Integer.parseInt(timesDef[i].split(":")[1]);
            et.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    selectTime(AddDailyRoutActivity.this, et, h, m);
                }
            });

            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (timesEditText.contains(et)) {
                        timesDef[timesEditText.indexOf(et)] = et.getText().toString();
                    }
                }
            });

            dailyRoutTimeLayout.addView(et);
            timesEditText.add(et);
        }
    }

    public void updateTimes(View view) {
        int times = 1;
        switch (view.getId()) {
            case R.id.daily_rout_reminder_1:
                times = 1;
                break;
            case R.id.daily_rout_reminder_2:
                times = 2;
                break;
            case R.id.daily_rout_reminder_3:
                times = 3;
                break;
        }
        attachTimeListeners(times);
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
