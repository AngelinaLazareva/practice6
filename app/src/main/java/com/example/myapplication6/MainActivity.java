package com.example.myapplication6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final Context thisContext = this;

    Button main_activity_button;
    EditText main_activity_edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_activity_button = (Button) findViewById(R.id.activity_main_button);
        main_activity_edit_text = (EditText) findViewById(R.id.activity_main_edit_text);

        main_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String product_name = main_activity_edit_text.getText().toString();

                if (product_name.length() > 0 && !isNumeric(product_name)) {
                    String savedValue = main_activity_edit_text.getText().toString();
                    stopService(new Intent(thisContext, BannerService.class));
                    Intent serviceIntent = new Intent(thisContext, BannerService.class);
                    serviceIntent.putExtra("value", savedValue);
                    startService(serviceIntent);

                    main_activity_edit_text.setHintTextColor(Color.GREEN);
                    main_activity_edit_text.setEnabled(false);
                } else {
                    main_activity_edit_text.setText("");
                    main_activity_edit_text.setHint(getResources().getString(R.string.activity_main_edit_text_hint));
                    main_activity_edit_text.setHintTextColor(Color.RED);
                }
            }
        });

        if (!Settings.canDrawOverlays(this)) {
            requestPermissions();
        }

        while (!Settings.canDrawOverlays(this)) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Intent serviceIntent = new Intent(this, BannerService.class);
        serviceIntent.putExtra("value", "");
        startService(serviceIntent);
    }

    public void requestPermissions() {
        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(myIntent, 2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "No permission", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, BannerService.class));
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}