package com.zuccessful.trueharmony.services;

import android.app.IntentService;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class ExampleWakefulService extends IntentService {

    private static final String NAME = "com.zuccessful.trueharmony.services.ExampleWakefulService";

    public ExampleWakefulService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WakefulBroadcastReceiver
                .completeWakefulIntent(intent);
        // doing stuff
      Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        AlarmService.ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);

        Log.v("alarm data", AlarmService.isPlaying + "");

        if (!AlarmService.isPlaying) {
            AlarmService.ringtone.play();
            AlarmService.isPlaying = true;
        }

       // int alarm_id = intent.getIntExtra("alarm_id", 0);
       // ComponentName comp = new ComponentName(getApplicationContext().getPackageName(), AlarmService.class.getName());
        //AlarmService.enqueueWork(getApplicationContext(), alarm_id, intent.setComponent(comp));
       /* try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
      //  setResultCode(Activity.RESULT_OK);




    }
}