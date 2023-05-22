package com.yourdomain.attendanceapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import com.example.sistemaasistencianfcuniversidaddelafrontera.R;
import com.yourdomain.attendanceapp.data.AttendanceManager;
import com.yourdomain.attendanceapp.util.NFCReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private NFCReader nfcReader;
    private Button buttonSubjects;
    private Button buttonOverallAttendance;
    private AttendanceManager attendanceManager;

    private final String csvFilePath = "/storage/emulated/0/NFCDataBase/database.csv";

    private final static Logger LOGGER = Logger.getLogger(MainActivity.class.getName());

    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean processingNfc = false;
    private boolean onNewIntentCalled = false;

    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techList;

    private static final int REQUEST_MANAGE_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LOGGER.info("onCreate called");
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefIntent.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        intentFiltersArray = new IntentFilter[] { ndefIntent };

        techList = new String[][] { { NfcA.class.getName(), Ndef.class.getName() } };


        // Initialize NFCReader
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            LOGGER.warning("Device doesn't support NFC.");
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            LOGGER.warning("NFC is disabled.");
        } else {
            LOGGER.info("NFC is enabled.");
        }

        try {
            attendanceManager = new AttendanceManager(csvFilePath);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not initialize AttendanceManager", e);
            return;
        }

        nfcReader = new NFCReader(nfcAdapter, attendanceManager);


        buttonSubjects = findViewById(R.id.button_subjects);
        buttonOverallAttendance = findViewById(R.id.button_overall_attendance);


        buttonSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SubjectListActivity.class);
                startActivity(intent);
            }
        });

        buttonOverallAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Calculate overall attendance
                float overallAttendance = attendanceManager.calculateOverallAttendance();
                // TODO: Display overall attendance to the user
            }
        });

        verifyStoragePermissions();
        LOGGER.info("onCreate completed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LOGGER.info("onResume called");
        if (nfcReader.nfcAdapter != null && nfcReader.nfcAdapter.isEnabled()) {
            nfcReader.nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techList);
        } else {
            LOGGER.warning("NFC is disabled.");
        }
        LOGGER.info("onResume completed");
    }


    @Override
    protected void onPause() {
        super.onPause();
        onNewIntentCalled = false;
        LOGGER.info("onPause called");
        if (nfcReader.nfcAdapter != null) {
            nfcReader.nfcAdapter.disableForegroundDispatch(this);
        } else {
            LOGGER.warning("nfcAdapter is null");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MANAGE_STORAGE) {
            if (Environment.isExternalStorageManager()) {
                initializeAttendanceManager();
            } else {
                Toast.makeText(this, "Cannot run application without required permission", Toast.LENGTH_LONG).show();
                finish(); // Close the application
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LOGGER.info("onNewIntent called");
        onNewIntentCalled = true;
        String action = intent.getAction();
        LOGGER.info("Intent action: " + action);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                NdefMessage[] messages = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    messages[i] = (NdefMessage) rawMsgs[i];
                }
                NdefRecord record = messages[0].getRecords()[0];
                byte[] payload = record.getPayload();
                String text = new String(payload);
                LOGGER.info("NFC Tag Read: " + text);
            }
        }

        processingNfc = true;
        processIntent(intent);

        LOGGER.info("Setting processingNfc to true");
        processingNfc = true;
    }


    private void processIntent(Intent intent) {
        LOGGER.info("processIntent called");
        String action = intent.getAction();
        LOGGER.info("Intent action: " + action);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {

            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage message = (NdefMessage) rawMessages[0];
                NdefRecord[] records = message.getRecords();
                for (NdefRecord record : records) {
                    if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                        byte[] payload = record.getPayload();
                        String payloadText = new String(payload, StandardCharsets.UTF_8);

                        // Extract the subject code from the payloadText
                        String subjectCode = extractSubjectCode(payloadText);

                        // Perform further processing with the subjectCode
                        // Call the NFCReader's onNfcTagScanned method passing the subjectCode
                        nfcReader.onNfcTagScanned(subjectCode);
                    }
                }
            } else {
                LOGGER.warning("Raw messages are null");
            }

            LOGGER.info("Resetting processingNfc flag");
            processingNfc = false;
            onNewIntentCalled = false;
        } else {
            LOGGER.warning("Intent action not recognized: " + action);
        }
        LOGGER.info("processIntent completed");
        processingNfc = false;
    }

    private String extractSubjectCode(String payloadText) {
        // Assuming the payloadText is in the format "enTest"
        // Extract the subject code from the payloadText
        // Modify this code to extract the subject code according to your payload format
        return payloadText.substring(2); // Return the subject code without the language identifier prefix
    }




    // Storage
    private void verifyStoragePermissions() {
        if (Environment.isExternalStorageManager()) {
            initializeAttendanceManager();
        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, REQUEST_MANAGE_STORAGE);
        }
    }

    private void initializeAttendanceManager() {
        try {
            attendanceManager = new AttendanceManager(csvFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not load CSV file", Toast.LENGTH_SHORT).show();
        }
    }

}



