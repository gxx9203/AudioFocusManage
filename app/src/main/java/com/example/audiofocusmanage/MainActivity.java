package com.example.audiofocusmanage;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Focus";
    // media player instance to playback
    // the media file from the raw folder
    MediaPlayer mediaPlayer;

    // Audio manager instance to manage or
    // handle the audio interruptions
    AudioManager audioManager;

    // Audio attributes instance to set the playback
    // attributes for the media player instance
    // these attributes specify what type of media is
    // to be played and used to callback the audioFocusChangeListener
    AudioAttributes playbackAttributes;

    // media player is handled according to the
    // change in the focus which Android system grants for
    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.d(TAG, "focusChange " + focusChange);
            if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Log.d(TAG,"onAudioFocusChange  get  AUDIOFOCUS_GAIN");
                mediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                Log.d(TAG,"onAudioFocusChange  get  AUDIOFOCUS_LOSS_TRANSIENT");
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
               // audioManager.abandonAudioFocusRequest(focusRequest);
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Log.d(TAG,"onAudioFocusChange  get  AUDIOFOCUS_LOSS");
                mediaPlayer.release();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                Log.d(TAG,"onAudioFocusChange  AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK \n");
                mediaPlayer.pause();
               // mediaPlayer.seekTo(0);
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // get the audio system service for
        // the audioManger instance
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // initiate the audio playback attributes
        playbackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        // set the playback attributes for the focus requester  AUDIOFOCUS_GAIN
        final AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(playbackAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .setWillPauseWhenDucked(true)
                .build();

        // request the audio focus and
        // store it in the int variable


        // register all three buttons
        Button bPlay = findViewById(R.id.playButton);
        Button bPause = findViewById(R.id.pasueButton);
        Button bStop = findViewById(R.id.stopButton);

        // initiate the media player instance with
        // the media file from the raw folder
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.dukou);

        // handle the PLAY button to play the audio
        bPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request the audio focus by the Android system
                // if the system grants the permission
                // then start playing the audio file
                Log.d(TAG,"bPlay  click" );
                final int audioFocusRequest = audioManager.requestAudioFocus(focusRequest);
                if (audioFocusRequest == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    mediaPlayer.start();
                }
            }
        });

        // handle the PAUSE button to pause the media player
        bPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"pause  click" );
                mediaPlayer.pause();
            }
        });

        // handle the STOP button to stop the media player
        bStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"btop  click" );
                mediaPlayer.stop();
                audioManager.abandonAudioFocusRequest(focusRequest);
                try {
                    // if the mediaplayer is stopped then
                    // it should be again prepared for
                    // next instance of play
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}