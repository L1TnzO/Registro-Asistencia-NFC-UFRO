package com.yourdomain.attendanceapp.view;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sistemaasistencianfcuniversidaddelafrontera.R;
import com.yourdomain.attendanceapp.data.AttendanceManager;

import java.io.IOException;
import java.util.ArrayList;

public class SubjectListActivity extends AppCompatActivity {
    public ListView listViewSubjects;
    public AttendanceManager attendanceManager;
    public final String csvFilePath = "/storage/emulated/0/NFCDataBase/database.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        listViewSubjects = findViewById(R.id.listView_subjects);

        try {
            attendanceManager = new AttendanceManager(csvFilePath);
            ArrayList<String> subjects = new ArrayList<>(attendanceManager.getSubjects().keySet());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subjects);
            listViewSubjects.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not load CSV file", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<>(attendanceManager.getSubjects().keySet()));
        listViewSubjects.setAdapter(adapter);

        listViewSubjects = findViewById(R.id.listView_subjects);

        // TODO: Fetch subjects from CSV and bind data to listViewSubjects

        listViewSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String subjectCode = (String) parent.getItemAtPosition(position);
                onSubjectClick(subjectCode);
            }
        });
    }

    private void onSubjectClick(String subjectCode) {
        Intent intent = new Intent(this, SubjectDetailActivity.class);
        intent.putExtra("subjectCode", subjectCode);
        startActivity(intent);
    }
}

