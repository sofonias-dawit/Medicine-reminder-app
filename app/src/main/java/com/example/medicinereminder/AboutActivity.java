package com.example.medicinereminder;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tvAppNameVersion = findViewById(R.id.tvAppNameVersion);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            tvAppNameVersion.setText(getString(R.string.app_name) + " v" + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            tvAppNameVersion.setText(getString(R.string.app_name));
        }
    }
}