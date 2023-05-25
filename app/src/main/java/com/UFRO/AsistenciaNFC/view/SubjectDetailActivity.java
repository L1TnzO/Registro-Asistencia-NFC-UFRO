package com.UFRO.AsistenciaNFC.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.UFRO.AsistenciaNFC.data.AttendanceManager;
import com.UFRO.AsistenciaNFC.data.Subject;
import com.example.nfc_test.R;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SubjectDetailActivity extends AppCompatActivity {
    public TextView textViewSubjectName;
    public TextView textViewAttendancePercentage;
    public AttendanceManager attendanceManager;
    public ListView listViewAttendance;
    public final String csvFilePath = "/storage/emulated/0/NFCDataBase/database.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);

        listViewAttendance = findViewById(R.id.listView_attendance);

        try {
            attendanceManager = new AttendanceManager(csvFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not load CSV file", Toast.LENGTH_SHORT).show();
            return;
        }

        String subjectCode = getIntent().getStringExtra("subjectCode");
        final Subject subject = attendanceManager.getSubject(subjectCode);

        textViewSubjectName = findViewById(R.id.textView_subjectName);
        textViewAttendancePercentage = findViewById(R.id.textView_attendancePercentage);

        textViewSubjectName.setText(subject.getSubjectName());
        textViewAttendancePercentage.setText(String.valueOf(subject.calculateAttendance()));

        Button buttonTheoreticalAttendance = findViewById(R.id.button_theoretical_attendance);
        Button buttonPracticalAttendance = findViewById(R.id.button_practical_attendance);

        buttonTheoreticalAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAttendanceData(subject.getTheoreticalAttendanceData());
            }
        });

        buttonPracticalAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAttendanceData(subject.getPracticalAttendanceData());
            }
        });
    }

    private void displayAttendanceData(HashMap<LocalDate, Boolean> attendanceData) {
        ArrayList<String> attendanceDates = new ArrayList<>();
        ArrayList<Boolean> attendanceValues = new ArrayList<>();

        for (Map.Entry<LocalDate, Boolean> entry : attendanceData.entrySet()) {
            attendanceDates.add(entry.getKey().toString());  // Add the date as a string
            attendanceValues.add(entry.getValue());  // Add the attendance value
        }

        AttendanceAdapter adapter = new AttendanceAdapter(this, attendanceDates, attendanceValues);
        listViewAttendance.setAdapter(adapter);
    }


    public class AttendanceAdapter extends ArrayAdapter<String> {
        private ArrayList<Boolean> attendanceValues;
        private Context context;

        public AttendanceAdapter(Context context, ArrayList<String> attendanceDates, ArrayList<Boolean> attendanceValues) {
            super(context, android.R.layout.simple_list_item_1, attendanceDates);
            this.context = context;
            this.attendanceValues = attendanceValues;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            if (attendanceValues.get(position)) {
                view.setBackgroundColor(Color.GREEN);
            } else {
                view.setBackgroundColor(Color.RED);
            }
            return view;
        }
    }
}



