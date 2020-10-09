package com.zuccessful.trueharmony.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Activity_Alarm;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.receivers.AlarmReceiver;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class AlarmSetActivity extends AppCompatActivity {

    private Spinner activitySpinner;
    private String activitySelected;
    private LinearLayout act_time;
    private ArrayList<EditText> timesEditText;
    private MaterialDayPicker dayPicker;
    private AlarmManager alarmManager;
    private SakshamApp app;
    private FirebaseFirestore db;
    private ProgressBar progressBar;

    private String[] timesDef = {"08:00 AM", "02:00 PM", "08:00 PM"};


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utilities.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.changeLanguage(this);
        setContentView(R.layout.activity_alarm_set);
        progressBar = findViewById(R.id.progressbar);

        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        act_time = findViewById(R.id.act_time_pref);
        activitySpinner = findViewById(R.id.med_name_et);
        dayPicker = findViewById(R.id.repeat_days);
        attachTimeListeners(1);
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
                activitySelected = adapterView.getItemAtPosition(i).toString();
                Log.d("saumya"," " +activitySelected+ " selected");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().get(Constants.CALLED_FROM)!=null) {

                handleEditAlarm();

            }
        }
    }

    private void handleEditAlarm() {
        Medication medication = (Medication) getIntent().getSerializableExtra(Constants.MED_OBJ);

        Toast.makeText(getApplicationContext(), "medication received : " + medication.getName(), Toast.LENGTH_SHORT).show();

        Map<String,Boolean> daysMap = medication.getDays();
        List<MaterialDayPicker.Weekday> weekdayList = new ArrayList<>();

        {
            weekdayList.add(MaterialDayPicker.Weekday.FRIDAY);
            weekdayList.add(MaterialDayPicker.Weekday.TUESDAY);
        }
        dayPicker.setSelectedDays(weekdayList);

        switch (medication.getReminders().size()){
            case 1:{
                ((RadioButton)findViewById(R.id.med_reminder_1)).setChecked(true);
                break;
            }
            case 2:{
                ((RadioButton)findViewById(R.id.med_reminder_2)).setChecked(true);
                break;
            }
            case 3:{
                ((RadioButton)findViewById(R.id.med_reminder_3)).setChecked(true);
                break;
            }
        }
    }

    public static void selectTime(Context context, final EditText editText, int h, int m) {
        // TODO: Fix this method
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

    public void submitMed(View view)
    {
        final ArrayList<String> reminders = new ArrayList<>();
        final String name;
        Map<String, Boolean> days = new HashMap<>();
        name = activitySelected;

        List<MaterialDayPicker.Weekday> daysSelected = dayPicker.getSelectedDays();
        if (daysSelected.size() < 1)
        {
            Toast.makeText(this, getResources().getString(R.string.select_days), Toast.LENGTH_SHORT).show();
            return;
        }
        setMedAlarm(reminders, name, days, daysSelected);
    }

    private void setMedAlarm(final ArrayList<String> reminders, final String name,Map<String, Boolean> days,
                             List<MaterialDayPicker.Weekday> daysSelected) {
        final ArrayList<Integer> alarm_ids = new ArrayList<>();
        final ArrayList<Integer> weekdays = new ArrayList<>();
        for (EditText editText : timesEditText) {
            String time = editText.getText().toString();
            if (!time.equals("")) {
                reminders.add(time);
            }
        }

        for (MaterialDayPicker.Weekday day : daysSelected) {
            for (int i = 0; i < reminders.size(); i++) {
                alarm_ids.add(Utilities.getNextAlarmId(this));
            }

            switch (day) {
                case MONDAY:
                    weekdays.add(Calendar.MONDAY);
                    days.put("mon", true);
                    break;
                case TUESDAY:
                    weekdays.add(Calendar.TUESDAY);
                    days.put("tue", true);
                    break;
                case WEDNESDAY:
                    weekdays.add(Calendar.WEDNESDAY);
                    days.put("wed", true);
                    break;
                case THURSDAY:
                    weekdays.add(Calendar.THURSDAY);
                    days.put("thu", true);
                    break;
                case FRIDAY:
                    weekdays.add(Calendar.FRIDAY);
                    days.put("fri", true);
                    break;
                case SATURDAY:
                    weekdays.add(Calendar.SATURDAY);
                    days.put("sat", true);
                    break;
                case SUNDAY:
                    weekdays.add(Calendar.SUNDAY);
                    days.put("sun", true);
                    break;
            }
        }
        final Activity_Alarm actObj = new Activity_Alarm(name, days, reminders);
        if (weekdays.size() * actObj.getReminders().size() == alarm_ids.size()) {      // Check
            for (int slot = 0; slot < actObj.getReminders().size(); slot++) {
                for (int i = 0; i < weekdays.size(); i++) {
                    Intent mIntent = new Intent(AlarmSetActivity.this, AlarmReceiver.class);
                    mIntent.putExtra("alarm_id", alarm_ids.get(slot * weekdays.size() + i));
                    mIntent.putExtra("medSlot", slot);
                    DateFormat df = new SimpleDateFormat("hh:mm aa");
                    DateFormat outputformat = new SimpleDateFormat("HH:mm");
                    Date date = null;
                    String output = null;
                    try {
                        //Conversion of input String to date
                        date = df.parse(reminders.get(slot));
                        //old date format to new date format
                        output = outputformat.format(date);
                    } catch (ParseException pe) {
                        pe.printStackTrace();
                    }
                    int h = Integer.parseInt(output.split(":")[0]);
                    int m = Integer.parseInt(output.split(":")[1]);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, h);
                    calendar.set(Calendar.MINUTE, m);
                    calendar.set(Calendar.SECOND, 0);
                    scheduleAlarm(alarmManager, calendar, mIntent, alarm_ids.get(slot * weekdays.size() + i), weekdays.get(i));
                    Log.d("saumya", "Setting alarm, Alarm Id: " + alarm_ids.get(slot * weekdays.size() + i) + " Slot: " + slot + " : weekday: " + weekdays.get(i));
                }
            }
            Toast.makeText(AlarmSetActivity.this, "Medicine: " + name + " added successfully.", Toast.LENGTH_SHORT).show();

        }

    }
    private void scheduleAlarm(AlarmManager alarmManager, Calendar calendar, Intent intent, int request_code, int dayOfWeek) {
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

        // Check we aren't setting it in the past which would trigger it to fire instantly
        if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }

        // Set this to whatever you were planning to do at the given time
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmSetActivity.this, request_code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        finish();
    }


    public void attachTimeListeners(int count) {
        timesEditText = new ArrayList<>();
        act_time.removeAllViews();
        for (int i = 0; i < count; i++) {
            final EditText et = new EditText(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            et.setLayoutParams(layoutParams);
            et.setText(timesDef[i]);
            et.setFocusable(false);
            final int h = Integer.parseInt(timesDef[i].split(" ")[0].split(":")[0]);
            final int m = Integer.parseInt(timesDef[i].split(" ")[0].split(":")[1]);
            et.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    selectTime(AlarmSetActivity.this, et, h, m);
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

            act_time.addView(et);
            timesEditText.add(et);
        }
    }

    public void updateTimes(View view) {
        int times = 1;
        switch (view.getId()) {
            case R.id.med_reminder_1:
                times = 1;
                break;
            case R.id.med_reminder_2:
                times = 2;
                break;
            case R.id.med_reminder_3:
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
