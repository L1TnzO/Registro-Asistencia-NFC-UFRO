package com.UFRO.AsistenciaNFC.view;

import com.UFRO.AsistenciaNFC.data.AttendanceManager;
import com.example.nfc_test.R;
import com.UFRO.AsistenciaNFC.util.NFCReader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private NFCReader nfcReader;

    private AttendanceManager attendanceManager;

    private static final int REQUEST_MANAGE_STORAGE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verify storage permissions
        verifyStoragePermissions();


        // Initialise NFCReader
        nfcReader = new NFCReader(this);
        if (!nfcReader.isNFCEnabled()) {
            Toast.makeText(this, "El dispositivo no cuenta con NFC", Toast.LENGTH_SHORT).show();
            finish();
        }
        //Shows UI

        Button buttonSubjectList = findViewById(R.id.button_subject_list);
        buttonSubjectList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SubjectListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcReader.enableForegroundDispatch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcReader.disableForegroundDispatch();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        nfcReader.resolveIntent(intent);

        try {
            String nfcCode = nfcReader.getSavedResult();
            attendanceManager.updateAttendance(nfcCode);
            attendanceManager.saveSubjects();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "No se pudo actualizar la asistencia", Toast.LENGTH_SHORT).show();
        }
    }

    // Storage
    private void verifyStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                initializeAttendanceManager();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_MANAGE_STORAGE);
            }
        }
    }

    private void initializeAttendanceManager() {
        try {
            String csvFilePath = "/storage/emulated/0/NFCDataBase/database.csv";
            attendanceManager = new AttendanceManager(csvFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "No se pudo cargar la base de datos", Toast.LENGTH_SHORT).show();
        }
    }
}




