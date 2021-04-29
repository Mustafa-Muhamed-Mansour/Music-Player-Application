package com.Music.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReadSongActivity extends AppCompatActivity
{
    private ListView listView;
    private String[] itemsAllSong;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_song);


        initView();
        externalStoragePermission();

    }


    private void initView()
    {
        listView = findViewById(R.id.list_view);
    }

    private void externalStoragePermission()
    {
        Dexter
                .withContext(getApplicationContext())
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener()
                {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport)
                    {
                        displaySongName();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken)
                    {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private ArrayList<File> readSong(File file)
    {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] allFiles = file.listFiles();

        for (File individualFile : allFiles)
        {
            if (individualFile.isDirectory() && !individualFile.isHidden())
            {
                arrayList.addAll(readSong(individualFile));
            }
            else
            {
                if (individualFile.getName().endsWith(".mp3") || individualFile.getName().endsWith(".aac") || individualFile.getName().endsWith(".wav") || individualFile.getName().endsWith(".wma"))
                {
                    arrayList.add(individualFile);
                }
            }
        }
        return arrayList;
    }

    private void displaySongName()
    {
        final ArrayList<File> audioSongs = readSong(Environment.getExternalStorageDirectory());
        itemsAllSong = new String[audioSongs.size()];

        for (int songCounter = 0; songCounter < audioSongs.size(); songCounter ++)
        {
            itemsAllSong[songCounter] = audioSongs.get(songCounter).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String >(getApplicationContext(), android.R.layout.simple_list_item_1, itemsAllSong);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String songName = listView.getItemAtPosition(position).toString();
                Intent intent = new Intent(view.getContext(), SmartPlayerActivity.class);
                intent.putExtra("song", audioSongs);
                intent.putExtra("name", songName);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }
}