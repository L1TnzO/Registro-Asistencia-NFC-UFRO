package com.UFRO.AsistenciaNFC.view;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.UFRO.AsistenciaNFC.data.Subject;
import com.example.nfc_test.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubjectDetailActivity extends AbstractView {
    public TextView textViewSubjectName;
    public TextView textViewAttendancePercentage;
    public ListView listViewAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);

        listViewAttendance = findViewById(R.id.listView_attendance);

        String subjectCode = getIntent().getStringExtra("subjectCode");
        final Subject subject = subjectManager.getSubject(subjectCode);

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, attendanceDates) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                if (attendanceValues.get(position)) {
                    view.setBackgroundColor(Color.GREEN);
                } else {
                    view.setBackgroundColor(Color.RED);
                }
                return view;
            }
        };

        listViewAttendance.setAdapter(adapter);
    }

}



