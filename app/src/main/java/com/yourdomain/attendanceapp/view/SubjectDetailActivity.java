package com.yourdomain.attendanceapp.view;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sistemaasistencianfcuniversidaddelafrontera.R;
import com.yourdomain.attendanceapp.data.AttendanceManager;
import com.yourdomain.attendanceapp.data.Subject;

import java.io.IOException;

public class SubjectDetailActivity extends AppCompatActivity {
    public TextView textViewSubjectName;
    public TextView textViewAttendancePercentage;
    public AttendanceManager attendanceManager;
    public final String csvFilePath = "/storage/emulated/0/NFCDataBase/database.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);

        try {
            attendanceManager = new AttendanceManager(csvFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not load CSV file", Toast.LENGTH_SHORT).show();
            return;
        }


        String subjectCode = getIntent().getStringExtra("subjectCode");
        Subject subject = attendanceManager.getSubject(subjectCode);

        textViewSubjectName = findViewById(R.id.textView_subjectName);
        textViewAttendancePercentage = findViewById(R.id.textView_attendancePercentage);

        textViewSubjectName.setText(subject.getSubjectName());
        textViewAttendancePercentage.setText(String.valueOf(subject.calculateAttendance()));

        // TODO: Create and fill the attendance matrix based on the subject's attendanceData
    }
}

