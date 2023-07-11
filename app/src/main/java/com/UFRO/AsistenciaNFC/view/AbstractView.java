package com.UFRO.AsistenciaNFC.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.UFRO.AsistenciaNFC.data.AttendanceManager;
import com.UFRO.AsistenciaNFC.data.CsvReader;
import com.UFRO.AsistenciaNFC.data.CsvWriter;
import com.UFRO.AsistenciaNFC.data.SubjectManager;
import com.UFRO.AsistenciaNFC.util.NFCReader;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public abstract class AbstractView extends AppCompatActivity {
    protected NFCReader nfcReader;
    protected SubjectManager subjectManager;
    protected static final int REQUEST_MANAGE_STORAGE = 2;
    protected AttendanceManager attendanceManager;
    protected final String rutaDirectorio = Environment.getExternalStorageDirectory().getAbsolutePath();
    protected final String csvFilePath = rutaDirectorio + "/NFCDataBase/database.csv";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        verifyStoragePermissions();

        nfcReader = new NFCReader(this);
        if (!nfcReader.isNFCEnabled()) {
            Toast.makeText(this, "El dispositivo no cuenta con NFC", Toast.LENGTH_SHORT).show();
        }
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
            subjectManager.getAttendanceManager().updateAttendance(nfcCode);
            subjectManager.saveSubjects();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "No se pudo actualizar la asistencia", Toast.LENGTH_SHORT).show();
        }
    }

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
            CsvReader csvReader = new CsvReader();
            CsvWriter csvWriter = new CsvWriter();

            subjectManager = new SubjectManager(csvFilePath, csvReader, csvWriter);

            // Create the current date and time
            LocalDate currentDate = LocalDate.now();
            LocalTime currentTime = LocalTime.now();

            // Pass them to the AttendanceManager constructor
            attendanceManager = new AttendanceManager(subjectManager, currentDate, currentTime);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "No se pudo cargar la base de datos", Toast.LENGTH_SHORT).show();
        }
    }

}

