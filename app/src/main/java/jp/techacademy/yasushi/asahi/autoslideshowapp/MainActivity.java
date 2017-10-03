package jp.techacademy.yasushi.asahi.autoslideshowapp;

import android.Manifest;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    Cursor cursor;
    Timer timer;
    Handler handler = new Handler();
    Button nextButton, previousButton, playBackButton;
    Boolean playingBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);

        previousButton = (Button) findViewById(R.id.previousButton);
        previousButton.setOnClickListener(this);

        playBackButton = (Button) findViewById(R.id.playBackButton);
        playBackButton.setOnClickListener(this);

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }


    private void getContentsInfo() {
        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            showImage();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nextButton) {
            if (!playingBack) {
                showNextImage();
            }
        } else if (v.getId() == R.id.previousButton) {
            if (!playingBack) {
                showPreviousImage();
            }
        } else if (v.getId() == R.id.playBackButton) {
            if (!playingBack) {
                startPlayBackImage();
            } else {
                stopPlayBackImage();
            }
        }
    }


    private void startPlayBackImage() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showNextImage();
                    }
                });
            }
        }, 100, 500);
        playBackButton.setText("停止");
        nextButton.setVisibility(View.GONE);
        previousButton.setVisibility(View.GONE);
        playingBack = true;
    }


    private void stopPlayBackImage() {
        timer.cancel();
        playBackButton.setText("再生");
        nextButton.setVisibility(View.VISIBLE);
        previousButton.setVisibility(View.VISIBLE);
        playingBack = false;
    }


    private void showNextImage() {
        if (cursor.moveToNext()) {
            showImage();
        } else {
            if (cursor.moveToFirst()) {
                showImage();
            }
        }
    }


    private void showPreviousImage() {
        if (cursor.moveToPrevious()) {
            showImage();
        } else {
            if (cursor.moveToLast()) {
                showImage();
            }
        }
    }

    private void showImage() {
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
        imageVIew.setImageURI(imageUri);
    }


}