package com.zuccessful.trueharmony.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.DatabaseRoom;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.RoomEntity.BasicMeasurementsEntity;
import com.zuccessful.trueharmony.RoomEntity.BloodMeasurementsEntity;
import com.zuccessful.trueharmony.RoomEntity.DailyProgressEntity;
import com.zuccessful.trueharmony.RoomEntity.LogEntity;
import com.zuccessful.trueharmony.RoomEntity.MedicineEntity;
import com.zuccessful.trueharmony.RoomEntity.MedicineProgressEntity;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.fragments.HomeFragment;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.pojo.Patient;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;

import static com.zuccessful.trueharmony.utilities.Utilities.changeLanguage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TITLE = "TITLE";
    private static final String FRAGMENT_TAG = "FRAGMENT_TAG";
    private String selectedFragmentId;
    private String selectedFragmentTitle;
    FirebaseUser user;
    private TextView userNameText, userEmailText;
    private ImageView userProfPic;
    private FirebaseAuth mAuth;
    private SakshamApp app;
    private FirebaseFirestore db;
    private Patient patient;
    File file = null;
    CSVWriter cw;
    FileWriter fw;
    Boolean internetOnFlag=false;

    StringBuffer sb = new StringBuffer();

    DateFormat outputformat = new SimpleDateFormat("HH:mm");
    String output = null;

    //    private DrawerLayout mDrawer;
//    GoogleSignInClient mGoogleSignInClient;
    private final int WRITE_EXTERNAL_STORAGE_CODE = 1;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utilities.onAttach(newBase));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        changeLanguage(getApplicationContext());
        setContentView(R.layout.activity_main);

        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        patient = app.getAppUser(null);

        // askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_CODE);
        mAuth = FirebaseAuth.getInstance();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_launcher_white_bg));
        toolbar.setPadding(10, 2, 2, 0);

        toolbar.setTitle(getResources().getString(R.string.app_name) + " - " + SakshamApp.getInstance().getPatientID());


//        Intent gintent = new Intent(MainActivity.this, GyroscopeService.class);
//        startService(gintent);


        Calendar cur_cal = new GregorianCalendar();
        cur_cal.setTimeInMillis(System.currentTimeMillis());//set the current time and date for this calendar

        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, cur_cal.get(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cur_cal.get(Calendar.MILLISECOND));
        cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
        cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));

        Calendar cal2 = new GregorianCalendar();
        cal2.add(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
        cal2.set(Calendar.HOUR_OF_DAY, 23);
        cal2.set(Calendar.MINUTE, 30);
        cal2.set(Calendar.SECOND, cur_cal.get(Calendar.SECOND));
        cal2.set(Calendar.MILLISECOND, cur_cal.get(Calendar.MILLISECOND));
        cal2.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
        cal2.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));

//        Intent intent = new Intent(this.getApplicationContext(), ServiceReceiver.class);
//        intent.setAction("START_TEST_SERVICE");
//
//        PendingIntent pintent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
//
//        AlarmManager alarm1 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//        alarm1.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pintent);
//
//        Intent intent2 = new Intent(this.getApplicationContext(), ServiceReceiver.class);
//        intent2.setAction("STOP_TEST_SERVICE");
//
//        PendingIntent pintent2 = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent2, 0);
//
//        AlarmManager alarm2 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//        alarm2.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pintent2);


        Fragment fragment = null;
        fragment = new HomeFragment();
//        if (fragment != null && !fragment.getClass().getSimpleName().equals(getSelectedFragmentId())) {
        replaceAndSetFragment(fragment);
//            item.setChecked(true);
        setTitle(getResources().getString(R.string.app_name));
        setSelectedFragmentTitle(getResources().getString(R.string.app_name));
//        }

        if (Utilities.isInternetOn(getApplicationContext()))
        {
            internetOnFlag=true;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void askPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            // we dont have permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        } else {
            //we have permission

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    downloadData();
                }
                break;


        }
    }


    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
        if (doubleBackToExitPressedOnce) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        } else {
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }


//            super.onBackPressed();
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add(SakshamApp.getInstance().getPatientID());
//        menu.add("About me");
//        menu.add("Alarm preferences");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean res = false;
        switch (item.getItemId()) {
            case R.id.action_about_me:
                // about me
               /* Intent intent = new Intent(this, AboutMe.class);
                startActivity(intent);*/
                String FILENAME = "userbooklet_patient.pdf";
                String FILENAME_HINDI = "user_booklet_hindi_patient.pdf";
                Intent pIntent = new Intent(MainActivity.this, PDFRenderActivity.class);
                pIntent.putExtra("filename",FILENAME);
                pIntent.putExtra("filename_hindi",FILENAME_HINDI);

                startActivity(pIntent);
                break;
            case R.id.action_alarm_pref:
                // alarm preferences;
                Intent intent2 = new Intent(this, AlarmPref.class);
                startActivity(intent2);
                break;

            case R.id.user_profile:
                // alarm preferences;
                Intent userProfileIntent = new Intent(this, UserProfileActivity.class);
                startActivity(userProfileIntent);
                break;
            case R.id.download_data:
                // alarm preferences;
               // Toast.makeText(getApplicationContext(),"Files downloaded",Toast.LENGTH_LONG).show();
                downloadData();
                break;


            default:
                res = super.onOptionsItemSelected(item);
        }
        return res;

    }


    private void replaceAndSetFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName())
                .commit();
        setSelectedFragmentId(fragment.getClass().getSimpleName());
    }

    //
    public String getSelectedFragmentId() {
        return selectedFragmentId;
    }

    public void setSelectedFragmentId(String selectedFragmentId) {
        this.selectedFragmentId = selectedFragmentId;
    }

    //
//    public String getSelectedFragmentTitle() {
//        return selectedFragmentTitle;
//    }
//
    public void setSelectedFragmentTitle(String selectedFragmentTitle) {
        this.selectedFragmentTitle = selectedFragmentTitle;
    }

    public void downloadData() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseRoom database = DatabaseRoom.getInstance(getApplicationContext());
                int res_size;
                try {
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Patient_log.csv");
                    if (file.exists()) {
                        file.delete();
                    }

                    boolean b = file.createNewFile();
                    Log.d("Download_Data", b + " file created " + file.getAbsoluteFile());
                    fw = new FileWriter(file.getAbsolutePath(), true);
                    cw = new CSVWriter(fw);
                    cw.writeNext(new String[]{"S.No", "Date", "New Activity", "Experience", "Hardships"});
                    List<LogEntity> log_list = database.logRecords().getLogRecords();
                    res_size = log_list.size();
                    Log.d("Download_Data", res_size + " entries present");
                    for (int i = 0; i < res_size; i++) {
                        Log.d("Download_Data", "Getting entry number: " + i);
                        LogEntity temp = log_list.get(i);
                        String arr[] = new String[6];
                        arr[0] = String.valueOf(temp.getSerialNo());
                        arr[1] = temp.getDate();
                        arr[2] = temp.getActivity_name();
                        arr[3] = temp.getExperience();
                        arr[4] = temp.getHardships();
                        cw.writeNext(arr);
                    }
                    Log.d("Download_Data", "DONE");
                    cw.flush();
                    cw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SelectedMedicine_log.csv");
                    if (file.exists()) {
                        file.delete();
                    }
                    ArrayList<Medication> medications = Utilities.getListOfMedication();
                    try {
                        Log.d("Download_Data", " selectedMeds are: " + medications.size());
                        if (medications.size() > 0) {
                            updateMedToFireBase(medications);
                        }
                        database.selectedMedicineRecords().deleteAll();
                        for (Medication m : medications) {
                            Map<String, Boolean> days = m.getDays();
                            String dayInt = "";
                            for (Map.Entry entry : days.entrySet()) {
                                if (entry.getValue().equals(true)) {
                                    dayInt = dayInt + (String) entry.getKey() + ", ";
                                }
                            }
                            Log.d("Download_Data", " days are: " + dayInt);
                            ArrayList<String> rem = m.getReminders();
                            String time = "";
                            for (String s : rem) {
                                time = time + s + ", ";
                            }
                            MedicineEntity me = new MedicineEntity(m.getName(), m.getDescription(), dayInt, time);
                            database.selectedMedicineRecords().addSelectedMedicineRecord(me);
                        }
                    }catch (Exception e){}
                    boolean b = file.createNewFile();
                    Log.d("Download_Data", b + " file created " + file.getAbsoluteFile());
                    fw = new FileWriter(file.getAbsolutePath(), true);
                    cw = new CSVWriter(fw);
                    cw.writeNext(new String[]{"Medicine", "Description", "Days", "Time"});
                    List<MedicineEntity> selectedMedicine_list = database.selectedMedicineRecords().getSelectedMedicineRecords();
                    res_size = selectedMedicine_list.size();
                    Log.d("Download_Data", res_size + " entries present");
                    for (int i = 0; i < res_size; i++) {
                        Log.d("Download_Data", "Getting entry number: " + i);
                        MedicineEntity temp = selectedMedicine_list.get(i);
                        String arr[] = new String[6];
                        arr[0] = temp.getMedicineName();
                        arr[1] = temp.getDescription();
                        arr[2] = temp.getDays();
                        arr[3] = temp.getTime();
                        cw.writeNext(arr);
                    }
                    Log.d("Download_Data", "DONE");
                    cw.flush();
                    cw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {
                    file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"MedicineProgress_log.csv");
                    if(file.exists())
                    { file.delete(); }

                    boolean b=file.createNewFile();
                    Log.d("Download_Data",b+" file created "+file.getAbsoluteFile());
                    fw = new FileWriter(file.getAbsolutePath(), true);
                    cw = new CSVWriter(fw);
                    cw.writeNext(new String[]{"S.No", "Date", "Med Name","Time", "Status"});
                    List<MedicineProgressEntity> list= database.medicineProgressRecords().getMedicineProgressRecords();
                    res_size=list.size();
                    Log.d("Download_Data",res_size+" entries present");
                    for(int i=0;i<res_size;i++)
                    {
                        Log.d("Download_Data","Getting entry number: "+i);
                        MedicineProgressEntity temp=list.get(i);
                        String arr[]=new String[5];
                        arr[0]= String.valueOf(temp.getSerialNo());
                        arr[1]= temp.getDate();
                        arr[2]= temp.getMedicineName();
                        arr[3]= temp.getTime();
                        arr[4]= String.valueOf(temp.getTaken());
                        cw.writeNext(arr);
                    }
                    Log.d("Download_Data","DONE");
                    cw.flush();
                    cw.close();
                } catch (IOException e) { e.printStackTrace(); }
/*
                try {
                    file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"DailyProgress_log.csv");
                    if(file.exists())
                    { file.delete(); }

                    boolean b=file.createNewFile();
                    Log.d("Download_Data",b+" file created "+file.getAbsoluteFile());
                    fw = new FileWriter(file.getAbsolutePath(), true);
                    cw = new CSVWriter(fw);
                    cw.writeNext(new String[]{"S.No", "Date", "Activity Name","Status" });
                    List<DailyProgressEntity> list= database.dailyProgressRecords().getDailyProgressRecords();
                    res_size=list.size();
                    Log.d("Download_Data",res_size+" entries present");
                    for(int i=0;i<res_size;i++)
                    {
                        Log.d("Download_Data","Getting entry number: "+i);
                        DailyProgressEntity temp=list.get(i);
                        String arr[]=new String[5];
                        arr[0]= String.valueOf(temp.getSerialNo());
                        arr[1]= temp.getDate();
                        arr[2]= temp.getActivityName();
                        arr[3]= String.valueOf(temp.getStatus());
                         cw.writeNext(arr);
                    }
                    Log.d("Download_Data","DONE");
                    cw.flush();
                    cw.close();
                } catch (IOException e) { e.printStackTrace(); }*/

                try {
                    file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"BasicMeasurements_log.csv");
                    if(file.exists())
                    { file.delete(); }

                    boolean b=file.createNewFile();
                    Log.d("Download_Data",b+" file created "+file.getAbsoluteFile());
                    fw = new FileWriter(file.getAbsolutePath(), true);
                    cw = new CSVWriter(fw);
                    cw.writeNext(new String[]{"S.No", "Date", "height","weight","waist","bp_low","bp_high"});
                    List<BasicMeasurementsEntity> list= database.BasicMeasurementsRecords().getBasicMeasurementsRecords();
                    res_size=list.size();
                    Log.d("Download_Data",res_size+" entries present");
                    for(int i=0;i<res_size;i++)
                    {
                        Log.d("Download_Data","Getting entry number: "+i);
                        BasicMeasurementsEntity temp=list.get(i);
                        String arr[]=new String[7];
                        arr[0]= String.valueOf(temp.getSerialNo());
                        arr[1]= temp.getDate();
                        arr[2]= temp.getHeight();
                        arr[3]= temp.getWeight();
                        arr[4]= temp.getWaist();
                        arr[5]= temp.getBp_low();
                        arr[6]= temp.getBp_high();
                        cw.writeNext(arr);
                    }
                    Log.d("Download_Data","DONE");
                    cw.flush();
                    cw.close();
                } catch (IOException e) { e.printStackTrace(); }

                try {
                    file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"BloodMeasurements_log.csv");
                    if(file.exists())
                    { file.delete(); }

                    boolean b=file.createNewFile();
                    Log.d("Download_Data",b+" file created "+file.getAbsoluteFile());
                    fw = new FileWriter(file.getAbsolutePath(), true);
                    cw = new CSVWriter(fw);
                    cw.writeNext(new String[]{"S.No", "Date", "Cholesterol","TSH","Sugar"});
                    List<BloodMeasurementsEntity> list= database.bloodMeasurementsRecords().getBloodMeasurementsRecords();
                    res_size=list.size();
                    Log.d("Download_Data",res_size+" entries present");
                    for(int i=0;i<res_size;i++)
                    {
                        Log.d("Download_Data","Getting entry number: "+i);
                        BloodMeasurementsEntity temp=list.get(i);
                        String arr[]=new String[7];
                        arr[0]= String.valueOf(temp.getSerialNo());
                        arr[1]= temp.getDate();
                        arr[2]= temp.getCholesterol();
                        arr[3]= temp.getTsh();
                        arr[4]= temp.getSugar();
                        cw.writeNext(arr);
                    }
                    Log.d("Download_Data","DONE");
                    cw.flush();
                    cw.close();
                } catch (IOException e) { e.printStackTrace(); }

                try {
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Alarm_Medicine_log.csv");
                    if (file.exists()) {
                        file.delete();
                    }
                    ArrayList<Medication> medications = Utilities.getListOfMedication();
                    try {
                        Log.d("Download_Data", " selectedMeds are: " + medications.size());
                        if (medications.size() > 0) {
                            updateMedToFireBase(medications);
                        }
                        database.selectedMedicineRecords().deleteAll();
                        for (Medication m : medications) {
                            Map<String, Boolean> days = m.getDays();
                            String dayInt = "";
                            for (Map.Entry entry : days.entrySet()) {
                                if (entry.getValue().equals(true)) {
                                    dayInt = dayInt + (String) entry.getKey() + ", ";
                                }
                            }
                            Log.d("Download_Data", " days are: " + dayInt);
                            ArrayList<String> rem = m.getReminders();
                            String time = "";
                            for (String s : rem) {
                                time = time + s + ", ";
                            }
                            MedicineEntity me = new MedicineEntity(m.getName(), m.getDescription(), dayInt, time);
                            database.selectedMedicineRecords().addSelectedMedicineRecord(me);
                        }
                    }catch (Exception e){}

                    Boolean b=false;

                    if(isStoragePermissionGranted())

                     b = file.createNewFile();

                    Log.d("Download_Data", b + " file created " + file.getAbsoluteFile());
                    fw = new FileWriter(file.getAbsolutePath(), true);
                    cw = new CSVWriter(fw);
                    cw.writeNext(new String[]{"Medicine", "Description", "Days", "Time"});
                    List<MedicineEntity> selectedMedicine_list = database.selectedMedicineRecords().getSelectedMedicineRecords();
                    res_size = selectedMedicine_list.size();
                    Log.d("Download_Data", res_size + " entries present");
                    for (int i = 0; i < res_size; i++) {
                        Log.d("Download_Data", "Getting entry number: " + i);
                        MedicineEntity temp = selectedMedicine_list.get(i);
                        String arr[] = new String[5];
                        arr[0] = temp.getMedicineName();
                        arr[1] = temp.getDescription();
                        arr[2] = temp.getDays();
                        arr[3] = temp.getTime();

                        cw.writeNext(arr);
                    }
                    Log.d("Download_Data", "DONE");
                    cw.flush();
                    cw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {
                    file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Alarms_DailyProgress_log.csv");
                    if(file.exists())
                    { file.delete(); }

                    boolean b=file.createNewFile();
                    Log.d("Download_Data",b+" file created "+file.getAbsoluteFile());
                    fw = new FileWriter(file.getAbsolutePath(), true);
                    cw = new CSVWriter(fw);
                    cw.writeNext(new String[]{"S.No", "Date", "Activity Name","Time" });
                    List<DailyProgressEntity> list= database.dailyProgressRecords().getDailyProgressRecords();
                    res_size=list.size();
                    Log.d("progress records size:",res_size+"");
                    Log.d("Download_Data",res_size+" entries present");
                    Log.d("size of alarms:", Utilities.getListOfDailyRoutineAlarms().size()+"");
                    int size= Utilities.getListOfDailyRoutineAlarms().size();
                    if(Utilities.getListOfDailyRoutineAlarms().size()<res_size)
                        res_size= Utilities.getListOfDailyRoutineAlarms().size();

                      HashMap<String,ArrayList<Integer>> map;

                   // for(int i=0;i<res_size;i++)
                    for(int i=0;i<size;i++)
                    {
                      Log.d("progress:alarm name", Utilities.getListOfDailyRoutineAlarms().get(i).getName()+"..alarm size."+ Utilities.getListOfDailyRoutineAlarms().get(i).getReminders().size());

                       //  DailyProgressEntity temp=list.get(i);
                        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                        String t_date=sdf.format(new Date());
                        String arr[]=new String[5];
                        arr[0]= String.valueOf(i+1);
                        arr[1]= t_date;
                        try{
                        map = Utilities.getDailyLog().get(t_date);}
                        catch (Exception e){
                            map=null;
                        }

                         //int value=0;

                        ArrayList<Integer> value=new ArrayList<>();
                        if(map!=null){
                            Iterator myVeryOwnIterator = map.keySet().iterator();
                            while (myVeryOwnIterator.hasNext()) {
                                String key = (String) myVeryOwnIterator.next();
                                 // value =  map.get(key);

                            }}

                        //          arr[2]= temp.getActivityName();
                        arr[2]= Utilities.getListOfDailyRoutineAlarms().get(i).getName();
                        try{
                        arr[3]= Utilities.getListOfDailyRoutineAlarms().get(i).getReminders().toString();}
                        catch (Exception e){}

                      /*  try{
                            if(value==1)
                        arr[4]="True";
                        else
                        arr[4]="Fasle";
                        }
                        catch (Exception e){}*/
                       // arr[4]=String.valueOf(temp.getStatus());
                        //arr[4]= String.valueOf(Utilities.getListOfDailyRoutineAlarms().get(i).getAlarmStatus());
                        cw.writeNext(arr);
                     }
                    Log.d("Download_Data","DONE" );
                    cw.flush();
                    cw.close();
                } catch (IOException e) { e.printStackTrace(); }
            }
        });
        Toast.makeText(getApplicationContext(),"Files downloaded",Toast.LENGTH_LONG).show();

    }

    public void updateMedToFireBase(ArrayList<Medication> meds)
    {
        if (internetOnFlag)
        {
            Log.d("firebase", "Internet present");
            for (final Medication m : meds)
            {
                DocumentReference documentReference = db.collection("alarms/" + app.getAppUser(null).getId() + "/medication").document(m.getName());
                documentReference.set(m).addOnFailureListener(new OnFailureListener()
                {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("firebase","failure in adding "+m.getName());
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("firebase","sucessss in adding main activity"+m.getName());
                    }
                });

            }

        }


    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

}


