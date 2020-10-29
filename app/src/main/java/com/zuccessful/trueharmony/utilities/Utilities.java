package com.zuccessful.trueharmony.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.zuccessful.trueharmony.DatabaseRoom;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.RoomEntity.DailyProgressEntity;
import com.zuccessful.trueharmony.RoomEntity.LogEntity;
import com.zuccessful.trueharmony.RoomEntity.MedicineProgressEntity;
import com.zuccessful.trueharmony.activities.AddDailyRoutActivity;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.pojo.WeeklyTask;
import com.zuccessful.trueharmony.receivers.AlarmReceiver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;

public class Utilities {


    private static final String MY_PREFS_NAME = "Saksham_Pref";
//    public static final String KEY_HEIGHT = "height";
//    public static final String KEY_WEIGHT = "weight";
//    public static final String KEY_NAME = "name";
//    public static final String KEY_ALARM_PREF = "alarmPref";
//    public static final String KEY_LANGUAGE_PREF = "langPref";
//    public static final String KEY_PHY_ACT_LIST = "physicalActList";
//    public static final String KEY_LEISURE_ACT_LIST = "leisureActList";
//    public static final String KEY_MIDICINES_LIST = "medicinesList";
//    public static final String KEY_BREAKFAST = "breakfast";
//    public static final String KEY_LUNCH = "lunch";
//    public static final String KEY_DINNER = "dinner";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    public static void changeLanguage(Context context){
        String langPrefType = Utilities.getDataFromSharedpref(context,Constants.KEY_LANGUAGE_PREF);
        String languageToLoad;
//        Log.v("Lang",Integer.parseInt((langPrefType))+"");
        if(langPrefType!=null) {
            int  lang = Integer.parseInt(langPrefType);
            if(lang==1) {
                languageToLoad = "hi"; // your language
            }else{
                languageToLoad = "en";
            }

        }else{
            languageToLoad = "en"; // default language
        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        updateResources(context,languageToLoad);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());

    }



    public static void saveListToSharedPref(ArrayList<String> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public static ArrayList<String> getListFromSharedPref(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        if(gson.fromJson(json, type)!=null) {
            return gson.fromJson(json, type);
        }else{
            return new ArrayList<>();
        }
    }

    public static void saveMedicineToList(Medication medication){
        ArrayList<Medication> medicationArrayList;
        medicationArrayList = getListOfMedication();
        if(medicationArrayList == null)
        { medicationArrayList = new ArrayList<>();}
        medicationArrayList.add(medication);
        saveListOfMedicine(medicationArrayList);
    }

    public static void saveListOfMedicine(ArrayList<Medication> medicationArrayList){
        SharedPreferences shref;
        SharedPreferences.Editor editor;
        shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());

        Gson gson = new Gson();
        String json = gson.toJson(medicationArrayList);

        editor = shref.edit();
        editor.remove(Constants.KEY_MIDICINES_LIST).commit();
        editor.putString(Constants.KEY_MIDICINES_LIST, json);
        editor.commit();
    }

    public static ArrayList<Medication> getListOfMedication(){
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_MIDICINES_LIST , "");
        ArrayList<Medication> medicationArrayList = gson.fromJson(response, new TypeToken<List<Medication>>(){}.getType());
        return medicationArrayList;
    }

    public static void saveDailyRoutineAlarms(DailyRoutine dailyRoutine)
    {
        ArrayList<DailyRoutine> alarmArrayList;
        alarmArrayList = getListOfDailyRoutineAlarms();

        if(alarmArrayList == null)
        {
            alarmArrayList = new ArrayList<>();
            Log.d("saumya","NULL LIST ");
            Log.d("saumya","size before adding NULL CASE"+alarmArrayList.size());
        }
        Log.d("saumya","size before adding "+alarmArrayList.size());

        String name= dailyRoutine.getName();
        for(int i=0;i<alarmArrayList.size();i++)
        {
            DailyRoutine temp= alarmArrayList.get(i);
            if(temp.getName()==name) {
                alarmArrayList.remove(temp);
                break;
            }
        }
        alarmArrayList.add(dailyRoutine);
        Log.d("saumya","size after adding "+alarmArrayList.size());

        SharedPreferences shref;
        SharedPreferences.Editor editor;
        shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());

        Gson gson = new Gson();
        String json = gson.toJson(alarmArrayList);

        editor = shref.edit();
        editor.remove(Constants.KEY_ALARM_LIST).commit();
        editor.putString(Constants.KEY_ALARM_LIST, json);
        editor.commit();
    }


    public static ArrayList<DailyRoutine> getListOfDailyRoutineAlarms(){
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_ALARM_LIST , "");
        ArrayList<DailyRoutine> alarmArrayList = gson.fromJson(response, new TypeToken<List<DailyRoutine>>(){}.getType());
        return alarmArrayList;
    }
    public static void removeFromListOfDailyRoutineAlarms(DailyRoutine dailyRoutine){
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_ALARM_LIST , "");
        ArrayList<DailyRoutine> alarmArrayList = gson.fromJson(response, new TypeToken<List<DailyRoutine>>(){}.getType());
        Log.d("saumya","size before removing  "+alarmArrayList.size());
        for(DailyRoutine d:alarmArrayList)
        {
            Log.d("saumya------",d.getName()+" "+dailyRoutine.getName());
            if(d.getName().equals(dailyRoutine.getName()))
            {
                Log.d("saumya","Match found");
                alarmArrayList.remove(d);
                break;
            }
            else
            {
                Log.d("saumya","No Match found");
            }
        }
        String json = gson.toJson(alarmArrayList);
        Log.d("saumya","size after removing  "+alarmArrayList.size());
        SharedPreferences.Editor editor;
        shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        editor = shref.edit();
        editor.remove(Constants.KEY_ALARM_LIST).commit();
        editor.putString(Constants.KEY_ALARM_LIST, json);
        editor.commit();
    }

    public static void removeMed(Medication med){
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_MIDICINES_LIST , "");
        ArrayList<Medication> medArrayList = gson.fromJson(response, new TypeToken<List<Medication>>(){}.getType());
        Log.d("saumya","size before removing  "+medArrayList.size());
        for(Medication d:medArrayList)
        {
            Log.d("saumya------",d.getName()+" "+med.getName());
            if(d.getName().equals(med.getName()))
            {
                Log.d("saumya","Match found");
                medArrayList.remove(d);
                break;
            }
            else
            {
                Log.d("saumya","No Match found");
            }
        }
        String json = gson.toJson(medArrayList);
        Log.d("saumya","size after removing  "+medArrayList.size());
        SharedPreferences.Editor editor;
        shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        editor = shref.edit();
        editor.remove(Constants.KEY_MIDICINES_LIST).commit();
        editor.putString(Constants.KEY_MIDICINES_LIST, json);
        editor.commit();
        // Remove the deleted medicine from taken list
        //   SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();


        //String date = sdf2.format(new Date());

        //  Utilities.saveMedProgress(med.getName(),date, 0, "remTime", 0,ctxt );
    }

    public static void removemedprogressLog(final String medName,final String date,final Context c)
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = shref.edit();
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_MED_PROGRESS , null);
        editor.remove(Constants.KEY_MED_PROGRESS).commit();
        HashMap<String,HashMap<String,ArrayList<Integer>>> measurementMap = gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,ArrayList<Integer>>>>(){}.getType());
        HashMap<String ,ArrayList<Integer>> temp=measurementMap.get(date);
        Log.d("saumya","PREVIOUS LENGTH OF MAP for current date "+temp.size());
        temp.remove(medName);
        measurementMap.put(date,temp);
        Log.d("saumya","AFTER LENGTH OF MAP "+temp.size());
        String jsonList = gson.toJson(measurementMap);
        editor.putString(Constants.KEY_MED_PROGRESS,jsonList);
        editor.commit();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseRoom database = DatabaseRoom.getInstance(c);
                List<MedicineProgressEntity> ret = database.medicineProgressRecords().getMedicineProgressRecords();
//                Log.d("Logg", " before size of retrieved arraylist : " + ret.size());
                for(int i=0;i<ret.size();i++)
                {
                    MedicineProgressEntity temp= ret.get(i);
                    if(temp.getDate().equals(date) && temp.getMedicineName().equals(medName))
                    {
//                        Log.d("logg", " already an entry for "+temp.getActivityName()+ "present "+ temp.getStatus());
                        database.medicineProgressRecords().delete(temp);
                    }
                }
                ret = database.medicineProgressRecords().getMedicineProgressRecords();
                Log.d("Logg", " after size of retrieved arraylist : " + ret.size());
            }
        });
    }
    public static void removeListFromSharedPref(String key,String value){
        ArrayList<String> actList = getListFromSharedPref(key);
        if(actList.contains(value)){
            actList.remove(value);
            saveListToSharedPref(actList,key);
        }
    }
    //Saves all height,bp,sugar,cholestrol info with time
    public static void saveMeasurements(String typeOfMeasurement,String d,ArrayList<Double> v)
    {
        Log.d("saumya"," IN SAVE MEASUREMENTS");
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = shref.edit();
        Gson gson = new Gson();
        String response=shref.getString(typeOfMeasurement , null);

        if(response==null)
        {
            HashMap<String,ArrayList<Double>> measurementMap = new HashMap<>();
            measurementMap.put(d,v);
            String jsonList = gson.toJson(measurementMap);
            editor.putString(typeOfMeasurement,jsonList);
            editor.apply();
            Log.d("saumya","saved the new list to shared preferences");
        }
        else
        {
            editor.remove(typeOfMeasurement).commit();
            HashMap<String,ArrayList<Double>> measurementMap = gson.fromJson(response, new TypeToken<HashMap<String,ArrayList<Double>>>(){}.getType());
            Log.d("saumya","PREVIOUS LENGTH OF MAP "+measurementMap.size());
            Log.d("saumya",d+" "+v);
            measurementMap.put(d,v);
            Log.d("saumya","AFTER LENGTH OF MAP "+measurementMap.size());
            String jsonList = gson.toJson(measurementMap);
            editor.putString(typeOfMeasurement,jsonList);
            editor.commit();
        }
    }

    public static HashMap<String,ArrayList<Double>> getMeasurements(String typeOfMeasurement)
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String response=shref.getString(typeOfMeasurement , null);
        HashMap<String,ArrayList<Double>> measurementMap = gson.fromJson(response, new TypeToken<HashMap<String,ArrayList<Double>>>(){}.getType());
        return measurementMap;
    }
    public static void saveMedProgress(final String name, final String date, Integer slot, final String time, final int status, final Context c)
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = shref.edit();
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_MED_PROGRESS, null);
        try{
            Log.v("Response",response);}
        catch (Exception e){e.printStackTrace();}
        if(response==null)
        {

            HashMap<String,HashMap<String,ArrayList<Integer>>> map = new HashMap<>();
            HashMap<String,ArrayList<Integer>> temp=new HashMap<>();
            ArrayList<Integer> list=new ArrayList<>();
            list.add(status);
            Log.v("swati list:",list+"");

            temp.put(name,list);
            Log.v("swati temp:",temp+"");

            map.put(date,temp);
            Log.v("swati map:",temp+"");

            String jsonList = gson.toJson(map);
            editor.putString(Constants.KEY_MED_PROGRESS,jsonList);
            Log.v(" swati last",jsonList);

            editor.apply();
        }
        else
        {
            HashMap<String,HashMap<String,ArrayList<Integer>>> map= gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,ArrayList<Integer>>>>(){}.getType());
            if(map.containsKey(date))  //entry for current date already there
            {
                HashMap<String,ArrayList<Integer>> temp= map.get(date); //get current date hashmap

                //check if there is an entry for given medicine
                if(temp.containsKey(name))
                {
                    ArrayList<Integer> list= temp.get(name);
                    list.add(status);
                    temp.put(name,list);
                    Log.d("saumya"," Medicine "+name+" entry found now adding status "+list.size());
                }
                else
                {
                    ArrayList<Integer> list=new ArrayList<>();
                    list.add(status);
                    temp.put(name,list);
                    Log.d("saumya"," Medicine "+name+" entry made first time now adding status "+list.size());
                }
                map.put(date,temp);

            }
            else // no entry for current date means no entry for the med on the current day
            {
                HashMap<String,ArrayList<Integer>> temp=new HashMap<>();
                ArrayList<Integer> list=new ArrayList<>();
                list.add(status);
                temp.put(name,list);
                map.put(date,temp);
            }
            Log.d("saumya ",map+"");
            String jsonList = gson.toJson(map);
            editor.remove(Constants.KEY_MED_PROGRESS).commit();
            editor.putString(Constants.KEY_MED_PROGRESS,jsonList);
            editor.commit();
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                boolean taken=false;
                if(status==1)
                {
                    taken=true;
                }
                DatabaseRoom database = DatabaseRoom.getInstance(c);
                MedicineProgressEntity me= new MedicineProgressEntity(date,name,time,taken);
                Log.d("Logg", " " + date + " " + name + " " + time+" "+taken);
                database.medicineProgressRecords().addMedicineProgressRecord(me);
                List<MedicineProgressEntity> ret = database.medicineProgressRecords().getMedicineProgressRecords();
                Log.d("Logg", " size of retrieved arraylist : " + ret.size());
            }
        });
    }

    public static void saveDailyProgress(final String name, final String date, Integer slot, final String time, final int status, final Context c)
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = shref.edit();
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_DAILY_LOG_LIST, null);
        // String response=shref.getString(Constants.KEY_DAILY_LOG, null);

        try {
            Log.d("response:", response);
        }catch (Exception e){
            e.printStackTrace();}
        if(response==null)
        {
            HashMap<String,HashMap<String,ArrayList<Integer>>> map = new HashMap<>();
            HashMap<String,ArrayList<Integer>> temp=new HashMap<>();
            ArrayList<Integer> list=new ArrayList<>();
            list.add(status);
            Log.v("swati list:",list+"");

            temp.put(name,list);
            Log.v("swati temp:",temp+"");

            map.put(date,temp);
            Log.v("swati map:",temp+"");

            String jsonList = gson.toJson(map);
            editor.putString(Constants.KEY_DAILY_LOG_LIST,jsonList);
            Log.v(" swati last",jsonList);

            editor.apply();

        }
        else
        {
            HashMap<String,HashMap<String,ArrayList<Integer>>> map= gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,ArrayList<Integer>>>>(){}.getType());
            if(map.containsKey(date))  //entry for current date already there
            {
                HashMap<String,ArrayList<Integer>> temp= map.get(date); //get current date hashmap

                //check if there is an entry for given medicine
                if(temp.containsKey(name))
                {
                    ArrayList<Integer> list= temp.get(name);
                    list.add(status);
                    temp.put(name,list);
                    Log.d("saumya"," Medicine "+name+" entry found now adding status "+list.size());
                }
                else
                {
                    ArrayList<Integer> list=new ArrayList<>();
                    list.add(status);
                    temp.put(name,list);
                    Log.d("saumya"," Medicine "+name+" entry made first time now adding status "+list.size());
                }
                map.put(date,temp);

            }
            else // no entry for current date means no entry for the med on the current day
            {
                HashMap<String,ArrayList<Integer>> temp=new HashMap<>();
                ArrayList<Integer> list=new ArrayList<>();
                list.add(status);
                temp.put(name,list);
                map.put(date,temp);
            }
            Log.d("saumya ",map+"");
            String jsonList = gson.toJson(map);
            editor.remove(Constants.KEY_DAILY_LOG_LIST).commit();
            editor.putString(Constants.KEY_DAILY_LOG_LIST,jsonList);
            editor.commit();
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                boolean taken=false;
                if(status==1)
                {
                    taken=true;
                }
                DatabaseRoom database = DatabaseRoom.getInstance(c);
                DailyProgressEntity me= new DailyProgressEntity(date,name,time,taken);
                Log.d("Logg", " " + date + " " + name + " " + time+" "+taken);
                database.dailyProgressRecords().addDailyProgressRecord(me);
                List<DailyProgressEntity> ret = database.dailyProgressRecords().getDailyProgressRecords();
                Log.d("Logg", " size of retrieved arraylist : " + ret.size());
            }
        });
    }

    public static ArrayList<String> getListofSubtasks(WeeklyTask med) {
        ArrayList<String> array_items=new ArrayList<String>();
        String weekday_name = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());
        Log.v("weekday",weekday_name);
        if(array_items.size()<=7 && med.getSubtasks().size()>0)
        {   Log.v("sssssssss",med.getSubtasks()+"");

            if(weekday_name.equalsIgnoreCase("Monday"))
                if (!med.getSubtasks().get("Monday").isEmpty() && med.getSubtasks().get("Monday").split("\\$")[0].length()>0)
                    array_items.add("Monday:" + "   " + med.getSubtasks().get("Monday").split("\\$")[0]);

            if(weekday_name.equalsIgnoreCase("Tuesday")&&med.getSubtasks().get("Tuesday").split("\\$")[0].length()>0)
                if (!med.getSubtasks().get("Tuesday").isEmpty())
                    array_items.add("Tuesday:" + "   " + med.getSubtasks().get("Tuesday").split("\\$")[0]);

            if(weekday_name.equalsIgnoreCase("Wednesday")&& med.getSubtasks().get("Wednesday").split("\\$")[0].length()>0)
                if (!med.getSubtasks().get("Wednesday").isEmpty())

                    array_items.add("Wednesday:" + "   " + med.getSubtasks().get("Wednesday").split("\\$")[0]);

            if(weekday_name.equalsIgnoreCase("Thursday")&& med.getSubtasks().get("Thursday").split("\\$")[0].length()>0)
                if (!med.getSubtasks().get("Thursday").isEmpty())

                { array_items.add("Thursday:" + "   " + med.getSubtasks().get("Thursday").split("\\$")[0]);


                }

            if(weekday_name.equalsIgnoreCase("Friday")&& med.getSubtasks().get("Friday").split("\\$")[0].length()>0)
                if (!med.getSubtasks().get("Friday").isEmpty())
                    array_items.add("Friday:" + "   " + med.getSubtasks().get("Friday").split("\\$")[0]);

            if(weekday_name.equalsIgnoreCase("Saturday")&& med.getSubtasks().get("Saturday").split("\\$")[0].length()>0)
                if (!med.getSubtasks().get("Saturday").isEmpty())

                    array_items.add("Saturday:" + "   " + med.getSubtasks().get("Saturday").split("\\$")[0]);

            if(weekday_name.equalsIgnoreCase("Sunday")&& med.getSubtasks().get("Sunday").split("\\$")[0].length()>0)
                if (!med.getSubtasks().get("Sunday").isEmpty())

                    array_items.add("Sunday:" + "   " + med.getSubtasks().get("Sunday").split("\\$")[0]);
        }
        return array_items ;
    }
    public static void removeTasks(WeeklyTask med){
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_WEEKLY_TASKS_LIST , "");
        ArrayList<WeeklyTask> medArrayList = gson.fromJson(response, new TypeToken<List<WeeklyTask>>(){}.getType());
        Log.d("saumya","size before removing  "+medArrayList.size());
        for(WeeklyTask d:medArrayList)
        {
            Log.d("saumya------",d.getName()+" "+med.getName());
            if(d.getName().equals(med.getName()))
            {
                Log.d("saumya","Match found");
                medArrayList.remove(d);
                break;
            }
            else
            {
                Log.d("saumya","No Match found");
            }
        }
        String json = gson.toJson(medArrayList);
        Log.d("saumya","size after removing  "+medArrayList.size());
        SharedPreferences.Editor editor;
        shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        editor = shref.edit();
        editor.remove(Constants.KEY_WEEKLY_TASKS_LIST).commit();
        editor.putString(Constants.KEY_WEEKLY_TASKS_LIST, json);
        editor.commit();
    }

    /* public static void saveDailyProgress(final String name, final String date, Integer slot, final String time, final int status, final Context c)
    {
        Log.v("Save","in save");
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = shref.edit();
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_DAILY_LOG_LIST, null);

        if(response==null)
        {        Log.v("Save","in save response null");

            HashMap<String,HashMap<String,ArrayList<Integer>>> map = new HashMap<>();
            HashMap<String,ArrayList<Integer>> temp=new HashMap<>();
            ArrayList<Integer> list=new ArrayList<>();
            list.add(status);
            temp.put(name,list);
            map.put(date,temp);
            String jsonList = gson.toJson(map);
            editor.putString(Constants.KEY_DAILY_LOG_LIST,jsonList);
            editor.apply();
        }
        else
        {
                     Log.v("Save","in save response not null"+response);

                HashMap<String,HashMap<String,ArrayList<Integer>>> map= gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,ArrayList<Integer>>>>(){}.getType());
            if(map.containsKey(date))  //entry for current date already there
            {
                HashMap<String,ArrayList<Integer>> temp= map.get(date); //get current date hashmap

                //check if there is an entry for given medicine
                if(temp.containsKey(name))
                {
                    ArrayList<Integer> list= temp.get(name);
                    list.add(status);
                    temp.put(name,list);
                }
                else
                {
                    ArrayList<Integer> list=new ArrayList<>();
                    list.add(status);
                    temp.put(name,list);
                }
                map.put(date,temp);

            }
            else // no entry for current date means no entry for the med on the current day
            {
                HashMap<String,ArrayList<Integer>> temp=new HashMap<>();
                ArrayList<Integer> list=new ArrayList<>();
                list.add(status);
                temp.put(name,list);
                map.put(date,temp);
            }
            Log.d("saumya ",map+"");
            String jsonList = gson.toJson(map);
            editor.remove(Constants.KEY_DAILY_LOG_LIST).commit();
            editor.putString(Constants.KEY_DAILY_LOG_LIST,jsonList);
            editor.commit();
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                boolean taken=false;
                if(status==1)
                {
                    taken=true;
                }
                DatabaseRoom database = DatabaseRoom.getInstance(c);
                DailyProgressEntity me= new DailyProgressEntity(date,name,time,taken);
                Log.d("Logg", " " + date + " " + name + " " + time+" "+taken);
                database.dailyProgressRecords().addDailyProgressRecord(me);
                List<DailyProgressEntity> ret = database.dailyProgressRecords().getDailyProgressRecords();
                Log.d("Logg", " size of retrieved arraylist : " + ret.size());
            }
        });
    } */
    public static HashMap<String,HashMap<String,ArrayList<Integer>>> getMedProgress()
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = shref.edit();
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_MED_PROGRESS, null);
        HashMap<String,HashMap<String,ArrayList<Integer>>> map= gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,ArrayList<Integer>>>>(){}.getType());
        return  map;
    }
    public static HashMap<String,HashMap<String,ArrayList<Integer>>> getDailyRoutineProgress()
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = shref.edit();
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_DAILY_LOG_LIST, null);
        HashMap<String,HashMap<String,ArrayList<Integer>>> map= gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,ArrayList<Integer>>>>(){}.getType());
        return  map;
    }
    /*public static HashMap<String,HashMap<String, ArrayList<Integer>>> getDailyRoutineProgress()
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = shref.edit();
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_DAILY_LOG_LIST, null);
        HashMap<String,HashMap<String,ArrayList<Integer>>> map= gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,Integer>>>(){}.getType());
        Log.v("saurabh",map+""+map.size());
        return  map;
    }*/


    public static void removeEntryDailyLog(final String activityName,final String date,final Context c)
    {

        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = shref.edit();
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_DAILY_LOG_LIST , null);
        Log.d("swati"," for current date "+response);

        editor.remove(Constants.KEY_DAILY_LOG_LIST).commit();
        HashMap<String,HashMap<String,ArrayList<Integer>>> measurementMap = gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,ArrayList<Integer>>>>(){}.getType());
        HashMap<String ,ArrayList<Integer>> temp=new HashMap<>();
        if(measurementMap!=null)
        { temp=measurementMap.get(date);
            Log.d("saumya","PREVIOUS LENGTH OF MAP for current date "+temp.size());
            temp.remove(activityName);}

        else{
            measurementMap=new HashMap<>();}
        measurementMap.put(date,temp);
        Log.d("saumya","AFTER LENGTH OF MAP "+temp.size());
        String jsonList = gson.toJson(measurementMap);
        editor.putString(Constants.KEY_DAILY_LOG_LIST,jsonList);
        editor.commit();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseRoom database = DatabaseRoom.getInstance(c);
                List<DailyProgressEntity> ret = database.dailyProgressRecords().getDailyProgressRecords();
//                Log.d("Logg", " before size of retrieved arraylist : " + ret.size());
                for(int i=0;i<ret.size();i++)
                {
                    DailyProgressEntity temp= ret.get(i);
                    if(temp.getDate().equals(date) && temp.getActivityName().equals(activityName))
                    {
//                        Log.d("logg", " already an entry for "+temp.getActivityName()+ "present "+ temp.getStatus());
                        database.dailyProgressRecords().delete(temp);
                    }
                }
                ret = database.dailyProgressRecords().getDailyProgressRecords();
                Log.d("Logg", " after size of retrieved arraylist : " + ret.size());
            }
        });
    }
/*    public static void saveDailyLog(final String activityName, final String date, final Integer v, final Context c  )
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = shref.edit();
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_DAILY_LOG_LIST , null);

        if(response==null)
        {
            HashMap<String,HashMap<String,Integer>> measurementMap = new HashMap<>();
            HashMap<String ,Integer> temp=new HashMap<String, Integer>();
            temp.put(activityName,v);
            measurementMap.put(date,temp);
            String jsonList = gson.toJson(measurementMap);
            editor.putString(Constants.KEY_DAILY_LOG_LIST,jsonList);
            editor.apply();
            Log.d("saumya","saved the new daily routine map to shared preferences");
        }
        else
        {
            editor.remove(Constants.KEY_DAILY_LOG_LIST).commit();
            HashMap<String,HashMap<String,Integer>> measurementMap = gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,Integer>>>(){}.getType());
            HashMap<String, Integer> temp;
            if(measurementMap.containsKey(date)==true)
            {
                temp =  measurementMap.get(date);
            }
            else {
                temp = new HashMap<>();
            }
            temp.put(activityName,v);
            measurementMap.put(date,temp);
            String jsonList = gson.toJson(measurementMap);
            editor.putString(Constants.KEY_DAILY_LOG_LIST,jsonList);
            editor.commit();
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseRoom database = DatabaseRoom.getInstance(c);
                boolean f=false;
                if(v==1)
                {
                    f=true;
                }
                List<DailyProgressEntity> ret = database.dailyProgressRecords().getDailyProgressRecords();
//                Log.d("Logg", " before size of retrieved arraylist : " + ret.size());
                for(int i=0;i<ret.size();i++)
                {
                    DailyProgressEntity temp= ret.get(i);
                    if(temp.getDate().equals(date) && temp.getActivityName().equals(activityName))
                    {
//                        Log.d("logg", " already an entry for "+temp.getActivityName()+ "present "+ temp.getStatus());
                        database.dailyProgressRecords().delete(temp);
                    }
                }
                DailyProgressEntity ce = new DailyProgressEntity(date,activityName,f);
//                Log.d("Logg", " " + date + " " + activityName + " " + f);
                database.dailyProgressRecords().addDailyProgressRecord(ce);
                ret = database.dailyProgressRecords().getDailyProgressRecords();
                Log.d("Logg", " after size of retrieved arraylist : " + ret.size());
            }
        });
    }*/


    public static void saveDailyLog(final String activityName, final String date,final String time,final Integer v, final Context c  )
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = shref.edit();
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_DAILY_LOG_LIST , null);

        if(response==null)
        {
            HashMap<String,HashMap<String,Integer>> measurementMap = new HashMap<>();
            HashMap<String ,Integer> temp=new HashMap<String, Integer>();
            temp.put(activityName,v);
            measurementMap.put(date,temp);
            String jsonList = gson.toJson(measurementMap);
            editor.putString(Constants.KEY_DAILY_LOG_LIST,jsonList);
            editor.apply();
            Log.d("saumya","saved the new daily routine map to shared preferences");
        }
        else
        {
            editor.remove(Constants.KEY_DAILY_LOG_LIST).commit();
            HashMap<String,HashMap<String,Integer>> measurementMap = gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,Integer>>>(){}.getType());
            HashMap<String, Integer> temp;
            if(measurementMap.containsKey(date)==true)
            {
                temp =  measurementMap.get(date);
            }
            else {
                temp = new HashMap<>();
            }
            temp.put(activityName,v);
            measurementMap.put(date,temp);
            String jsonList = gson.toJson(measurementMap);
            editor.putString(Constants.KEY_DAILY_LOG_LIST,jsonList);
            editor.commit();
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseRoom database = DatabaseRoom.getInstance(c);
                boolean f=false;
                if(v==1)
                {
                    f=true;
                }
                List<DailyProgressEntity> ret = database.dailyProgressRecords().getDailyProgressRecords();
//                Log.d("Logg", " before size of retrieved arraylist : " + ret.size());
                for(int i=0;i<ret.size();i++)
                {
                    DailyProgressEntity temp= ret.get(i);
                    if(temp.getDate().equals(date) && temp.getActivityName().equals(activityName))
                    {
//                        Log.d("logg", " already an entry for "+temp.getActivityName()+ "present "+ temp.getStatus());
                        database.dailyProgressRecords().delete(temp);
                    }
                }


/*                DailyProgressEntity me= new DailyProgressEntity(date,name,time,taken);
                Log.d("Logg", " " + date + " " + name + " " + time+" "+taken);
                database.dailyProgressRecords().addDailyProgressRecord(me);
                List<DailyProgressEntity> ret = database.dailyProgressRecords().getDailyProgressRecords();
                Log.d("Logg", " size of retrieved arraylist : " + ret.size());*/


                DailyProgressEntity ce = new DailyProgressEntity(date,activityName,time,f);

                Log.d("Adding!!!!!", " " + date + " " + activityName + " " + f+ "!!!!!!!!!!!!!!"+time);
                database.dailyProgressRecords().addDailyProgressRecord(ce);
                ret = database.dailyProgressRecords().getDailyProgressRecords();
                Log.d("Logg", " after size of retrieved arraylist : " + ret.size());
            }
        });
    }


    public static HashMap<String,HashMap<String,ArrayList<Integer>>> getDailyLog()
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_DAILY_LOG_LIST , null);
        HashMap<String,HashMap<String,ArrayList<Integer>>> measurementMap = gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,ArrayList<Integer>>>>(){}.getType());
        return measurementMap;
    }
    public static HashMap<String,HashMap<String,Boolean>> getDailyLog_bool()
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_DAILY_LOG_LIST , null);
        HashMap<String,HashMap<String,Boolean>> measurementMap = gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,Boolean>>>(){}.getType());
        return measurementMap;
    }

    public static ArrayList<WeeklyTask> getListOfWeeklytasks(){
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_WEEKLY_TASKS_LIST , "");
        Log.v("Response:",response);
        ArrayList<WeeklyTask> medicationArrayList = gson.fromJson(response, new TypeToken<List<WeeklyTask>>(){}.getType());
        return medicationArrayList;
    }
    public static void saveWeeklyTasksToList(WeeklyTask tasks){
        ArrayList<WeeklyTask> taskArrayList;
        ArrayList<WeeklyTask> newtaskArrayList;

        newtaskArrayList=new ArrayList<>();
        taskArrayList = getListOfWeeklytasks();
        if(taskArrayList == null)
        { taskArrayList = new ArrayList<>();}
      //  taskArrayList.clear();
        for(int i=0;i<taskArrayList.size();i++)
        {if(taskArrayList.get(i).getName().equalsIgnoreCase(tasks.getName()))
        newtaskArrayList.add(tasks);
        else
            newtaskArrayList.add(taskArrayList.get(i));
        }
        saveListOfWeeklyTasks(newtaskArrayList);
    }

    public static void saveListOfWeeklyTasks(ArrayList<WeeklyTask> taskArrayList){
        SharedPreferences shref;
        SharedPreferences.Editor editor;
        shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());

        Gson gson = new Gson();
        String json = gson.toJson(taskArrayList);

        editor = shref.edit();
        editor.remove(Constants.KEY_WEEKLY_TASKS_LIST).commit();
        editor.putString(Constants.KEY_WEEKLY_TASKS_LIST, json);
        editor.commit();
    }
    public static void saveDataInSharedpref(Context context,String key,String value){
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getDataFromSharedpref(Context context,String key){
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    public static void saveBooleanDataInSharedpref(Context context, String key, boolean value){
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBooleanDataFromSharedpref(Context context, String key){
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(key, true);
    }

    public static void saveArrayListTimers(ArrayList<String> list){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("timerlist", json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static ArrayList<String> getArrayListTimers(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String json = prefs.getString("timerlist", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void addToTimersList(String notID){
        ArrayList<String> timerList = getArrayListTimers();
        if(timerList==null) timerList = new ArrayList<>();
        timerList.add(notID);
        saveArrayListTimers(timerList);
    }

    public static void cancelAlarms(Context context, ArrayList<Integer> alarmIDs){

    }
    public static void removeTimerFromIDList(String id){
        ArrayList<String> timerList = getArrayListTimers();
        if (timerList == null) return;
        timerList.remove(id);
        saveArrayListTimers(timerList);
    }

    public static void removeFromTimersList(int notID){
        ArrayList<String> timerList = getArrayListTimers();
        if(timerList==null) return;
        timerList.remove(String.valueOf(notID));
        saveArrayListTimers(timerList);
    }

    public static void incrementTimerCount(Context context){
        SharedPreferences preferences = context.getSharedPreferences(
                "com.zuccessful.trueharmony.ALARM_PREFERENCES", MODE_PRIVATE);
        int count = preferences.getInt("timerCounter", 0);
        preferences.edit().putInt("timerCounter", count+1).commit();
    }

    public static void resetTimerCount(Context context){
        SharedPreferences preferences = context.getSharedPreferences(
                "com.zuccessful.trueharmony.ALARM_PREFERENCES", MODE_PRIVATE);
        preferences.edit().putInt("timerCounter", 0).commit();
    }

    public static int getTimerCount(Context context){
        SharedPreferences preferences = context.getSharedPreferences(
                "com.zuccessful.trueharmony.ALARM_PREFERENCES", MODE_PRIVATE);
        return preferences.getInt("timerCounter", 0);
    }

    public static int getNextAlarmId(Context context) {

        // TODO: fix if the app is reinstalled, sync with servers max value
        SharedPreferences preferences = context.getSharedPreferences("com.zuccessful.trueharmony.ALARM_PREFERENCES", MODE_PRIVATE);
        int id = preferences.getInt("new_alarm_index", 0);
        setNextAlarmId(context, id + 1);
        return id;
    }

    private static void setNextAlarmId(Context context, int i) {
        SharedPreferences preferences = context.getSharedPreferences("com.zuccessful.trueharmony.ALARM_PREFERENCES", MODE_PRIVATE);
        preferences.edit().putInt("new_alarm_index", i).apply();
    }

    public static Intent setExtraForIntent(Intent intent, String key, Object object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            byte[] data = bos.toByteArray();
            intent.putExtra(key, data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return intent;
    }

    public static Object getExtraFromIntent(Intent intent, String key) {
        Object object = null;
        byte[] rawObj = intent.getByteArrayExtra(key);
        if (rawObj != null && rawObj.length > 0) {
            ByteArrayInputStream bis = new ByteArrayInputStream(rawObj);
            ObjectInput in = null;
            try {
                in = new ObjectInputStream(bis);
                object = in.readObject();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return object;
    }

    public static int getPixelValue(Context context, int dimenId) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dimenId,
                resources.getDisplayMetrics()
        );
    }

    public static SimpleDateFormat getSimpleDateFormat(){
        return new SimpleDateFormat("MM-dd-yy", Locale.US);
    }

    public static void clearPatientId(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREF_PID, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public static Context onAttach(Context newBase) {
        String langPrefType = Utilities.getDataFromSharedpref(newBase,Constants.KEY_LANGUAGE_PREF);
        String languageToLoad;
        if(langPrefType!=null) {
            int lang = Integer.parseInt(langPrefType);
            if(lang==1) {
                languageToLoad = "hi"; // your language
            }else{
                languageToLoad = "en";
            }

        }else{
            languageToLoad = "en"; // default language
        }

        return updateResource(newBase, languageToLoad);
    }
    public static Context updateResource(Context context, String language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }
        return updateResourcesLegacy(context, language);
    }

    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }

    public static final boolean isInternetOn(Context context) {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            // if connected with internet
//            Toast.makeText(context, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            Toast.makeText(context, context.getResources().getString(R.string.internet_connectivity), Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }



    /*
        ## Script to lookup alarms in the device
        echo "Please set a search filter"
        read search

        adb shell dumpsys alarm | grep $search | (while read i; do echo $i; _DT=$(echo $i | grep -Eo 'when\s+([0-9]{10})' | tr -d '[[:alpha:][:space:]]'); if [ $_DT ]; then echo -e "\e[31m$(date -d @$_DT)\e[0m"; fi; done;)

     */
}
