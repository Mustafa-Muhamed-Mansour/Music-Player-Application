package com.Music.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Music.musicplayer.R;
import com.airbnb.lottie.LottieAnimationView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import spencerstudios.com.fab_toast.FabToast;

public class SmartPlayerActivity extends AppCompatActivity
{
    private LinearLayout parentLinearLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private LottieAnimationView lottieAnimationView;
    private TextView textViewNameSong;
    private ImageView imageViewBack;
    private MediaPlayer mediaPlayer;
    private ArrayList<File> mySong;
    private String keeper = "", mode = "ON";
    private int position;
    private String mySongName;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_player);


        initViews();
        initSpeech();
        checkVoicePermission();
        relativeData();
        clickSpeechRecognizer();
        clickView();


    }

    private void initViews()
    {
        mySong = new ArrayList<>();
        lottieAnimationView = findViewById(R.id.animation_view);
        textViewNameSong = findViewById(R.id.txt_name_song);
        imageViewBack = findViewById(R.id.img_back);
        parentLinearLayout = findViewById(R.id.parent_linear_layout);
    }

    private void initSpeech()
    {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());
    }

    private void relativeData()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mySong = (ArrayList) bundle.getParcelableArrayList("song");
        mySongName = mySong.get(position).getName();
        String songName = intent.getStringExtra("name");
        textViewNameSong.setText(songName);
        textViewNameSong.setSelected(true);
        position = bundle.getInt("position", 0);
        Uri uri = Uri.parse(mySong.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

    }

    private void clickSpeechRecognizer()
    {
        parentLinearLayout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        break;
                }
                return true;
            }
        });

        speechRecognizer.setRecognitionListener(new RecognitionListener()
        {
            @Override
            public void onReadyForSpeech(Bundle params)
            {
            }

            @Override
            public void onBeginningOfSpeech()
            {
            }

            @Override
            public void onRmsChanged(float rmsdB)
            {
            }

            @Override
            public void onBufferReceived(byte[] buffer)
            {
            }

            @Override
            public void onEndOfSpeech()
            {
            }

            @Override
            public void onError(int error)
            {
            }

            @Override
            public void onResults(Bundle results)
            {
                ArrayList<String> matchesFound = results.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);

                if (matchesFound != null)
                {
                    if (mode.equals("ON"))
                    {
                        keeper = matchesFound.get(0);
                        if (keeper.equals("وقف") || keeper.equals("stop"))
                        {
                            songPlayPause();
                            lottieAnimationView.pauseAnimation();
                            FabToast.makeText(getApplicationContext(), "أنا وقفت أهو D:", FabToast.LENGTH_LONG, FabToast.SUCCESS,  FabToast.POSITION_DEFAULT).show();
                        }
                        else if (keeper.equals("شغل") || keeper.equals("play"))
                        {
                            songPlayPause();
                            lottieAnimationView.playAnimation();
                            FabToast.makeText(getApplicationContext(), "اشتغلت بس لجلك أنت يا جميل", FabToast.LENGTH_LONG, FabToast.SUCCESS,  FabToast.POSITION_DEFAULT).show();
                        }
                        else if (keeper.equals("اللي بعدها") || keeper.equals("play the next song"))
                        {
                            lottieAnimationView.pauseAnimation();
                            songNext();
                            lottieAnimationView.playAnimation();
                            FabToast.makeText(getApplicationContext(), "أي خدمة يا زميلي", FabToast.LENGTH_LONG, FabToast.SUCCESS,  FabToast.POSITION_DEFAULT).show();
                        }
                        else if (keeper.equals("اللي قبلها") || keeper.equals("play the previous song"))
                        {
                            lottieAnimationView.pauseAnimation();
                            songPrevious();
                            lottieAnimationView.playAnimation();
                            FabToast.makeText(getApplicationContext(), "وفرت عليك كتير ها D:", FabToast.LENGTH_LONG, FabToast.SUCCESS,  FabToast.POSITION_DEFAULT).show();
                        }
                    }
                }

            }

            @Override
            public void onPartialResults(Bundle partialResults)
            {
            }

            @Override
            public void onEvent(int eventType, Bundle params)
            {
            }
        });
    }

    private void clickView()
    {
        imageViewBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                speechRecognizer.stopListening();
                startActivity(new Intent(getApplicationContext(), ReadSongActivity.class));
            }
        });
    }

    private void checkVoicePermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (!(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == (PackageManager.PERMISSION_GRANTED)))
            {
                FabToast.makeText(getApplicationContext(), "معلش افتح السماحية للميكروفون (الPermssion يعني)", FabToast.LENGTH_LONG, FabToast.WARNING,  FabToast.POSITION_DEFAULT).show();
            }
        }
    }

    private void songPlayPause()
    {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
        }
        else
        {
            mediaPlayer.start();
        }
    }

    private void songNext()
    {
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        position = ((position+1)%mySong.size());
        Uri uri = Uri.parse(mySong.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mySongName = mySong.get(position).toString();
        textViewNameSong.setText(mySongName);
        mediaPlayer.start();
    }

    private void songPrevious()
    {
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        position = ((position-1)<0 ? (mySong.size() - 1) : (position - 1));
        Uri uri = Uri.parse(mySong.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mySongName = mySong.get(position).toString();
        textViewNameSong.setText(mySongName);
        mediaPlayer.start();
    }
}