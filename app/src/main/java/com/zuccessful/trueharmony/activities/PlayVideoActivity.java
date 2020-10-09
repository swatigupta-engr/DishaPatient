package com.zuccessful.trueharmony.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.zuccessful.trueharmony.R;

public class PlayVideoActivity extends AppCompatActivity{

    private VideoView videoView;
    private int position = 0;
    private MediaController mediaController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        String video_path = intent.getStringExtra("video_path");
//        Toast.makeText(PlayVideoActivity.this,"Tap Video for Media Controller " + video_path, Toast.LENGTH_LONG).show();
        videoView = (VideoView) findViewById(R.id.videoView);

        // Set the media controller buttons
        if (mediaController == null) {
            mediaController = new MediaController(PlayVideoActivity.this);

            // Set the videoView that acts as the anchor for the MediaController.
           // mediaController.setAnchorView(videoView);

            mediaController.requestFocus();


            // Set MediaController for VideoView
            videoView.setMediaController(mediaController);
        }


        try {
            // ID of video file.
            int id = this.getRawResIdByName("myvideo");
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/raw/"+video_path));

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();

videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //Toast.makeText(PlayVideoActivity.this,"Video Ended",Toast.LENGTH_LONG).show();
        String mess = getResources().getString(R.string.video_edit);

        new AlertDialog.Builder(PlayVideoActivity.this)
                .setTitle("")
                .setMessage(mess)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                     }
                })
/*
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)*/
                .show();
    }
});
        // When the video file ready for playback.
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaController.show();
                videoView.seekTo(position);
                if (position == 0) {
//                    videoView.start();
                }

                // When video Screen change size.
//                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
//                    @Override
//                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//
//                        // Re-Set the videoView that acts as the anchor for the MediaController
//                       // mediaController.setAnchorView(videoView);
//                    }
//                });
            }
        });

    }

    // Find ID corresponding to the name of the resource (in the directory raw).
    public int getRawResIdByName(String resName) {
        String pkgName = this.getPackageName();
        // Return 0 if not found.
        int resID = this.getResources().getIdentifier(resName, "raw", pkgName);
        Log.i("AndroidVideoView", "Res Name: " + resName + "==> Res ID = " + resID);
        return resID;
    }


    // When you change direction of phone, this method will be called.
    // It store the state of video (Current position)
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Store current position.
        savedInstanceState.putInt("CurrentPosition", videoView.getCurrentPosition());
        videoView.pause();
    }


    // After rotating the phone. This method is called.
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        position = savedInstanceState.getInt("CurrentPosition");
        videoView.seekTo(position);

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


