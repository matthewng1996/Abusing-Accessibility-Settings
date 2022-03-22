package com.article.funwithaccessibility;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;
    private boolean startedAccessSettings = false;
    Button permissionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String packageName2 = getPackageName();
        String GetAppNameFromPackage = Utils.GetAppNameFromPackage(this, packageName2);
        MyAccessibilityService.SetSmsAutoAccept(true, GetAppNameFromPackage);
        if (!isAccessibilityServiceEnabled(this, MyAccessibilityService.class)) {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        }
        permissionBtn = findViewById(R.id.permsBtn);

        permissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestSmsPermission();
            }
        });
    }

    public void StartAccessSettings() {
        this.startedAccessSettings = true;
        startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
    }

    public void InstructionsDialog() {
        String format = String.format("TEST 0", getString(R.string.app_name), getString(R.string.app_name), getString(R.string.app_name), getString(R.string.app_name));
        final long currentTimeMillis = System.currentTimeMillis();
        new AlertDialog.Builder(this).setTitle("TEST 1").setMessage(format).setPositiveButton("HELLO 1", new DialogInterface.OnClickListener() { // from class: com.example.myapplicationtest.MainActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                long currentTimeMillis2 = System.currentTimeMillis();
                if (MainActivity.this.startedAccessSettings || currentTimeMillis2 - currentTimeMillis >= 2500) {
                    MainActivity.this.StartAccessSettings();
                    return;
                }
                Toast.makeText(MainActivity.this.getApplicationContext(), String.format("TEST 2", MainActivity.this.getString(MY_PERMISSIONS_REQUEST_READ_SMS)), Toast.LENGTH_SHORT).show();
                MainActivity.this.InstructionsDialog();
            }
        }).setIcon(R.drawable.ic_launcher_background);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyAccessibilityService.GetStarted()) {
            Toast.makeText(getApplicationContext(), "TEST 3", Toast.LENGTH_SHORT).show();
            finishAndRemoveTask();
            return;
        }
        InstructionsDialog();
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, MY_PERMISSIONS_REQUEST_READ_SMS);
        } else {
            //Do smth
            Toast.makeText(this, "Permissions are already allowed", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Toast.makeText(this, "Permissions are already allowed", Toast.LENGTH_SHORT);
                } else {
                    requestSmsPermission();
                }
                return;
            }
        }
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(service.getName()))
                return true;
        }
        return false;
    }
}