package com.zuccessful.trueharmony.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.zuccessful.trueharmony.services.AlarmService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("saumya-----------"," ALARM RECEIVER");

        //check the alarm notification preference for the user
       /* String alarmPref = Utilities.getDataFromSharedpref(context.getApplicationContext(), Constants.KEY_ALARM_PREF);


        if(alarmPref.equals("0")) {
            Intent startIntent = new Intent(context.getApplicationContext(), PlayRingtone.class);
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//            if (alarmUri == null) {
//                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            }
            startIntent.putExtra("ringtone-uri", alarmUri.toString());
//            PlayRingtone.enqueueWork(context,startIntent);
            context.getApplicationContext().startService(startIntent);
        }*/
//this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        AlarmService.ringtone = RingtoneManager.getRingtone(context.getApplicationContext(), alarmUri);

        Log.v("alarm data", AlarmService.isPlaying+"");

        if(!AlarmService.isPlaying)
        { AlarmService.ringtone.play();
        AlarmService.isPlaying=true;
        }


        int alarm_id = intent.getIntExtra("alarm_id", 0);
        ComponentName comp = new ComponentName(context.getPackageName(), AlarmService.class.getName());
        AlarmService.enqueueWork(context, alarm_id, intent.setComponent(comp));
       /* try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        setResultCode(Activity.RESULT_OK);
    }


}
