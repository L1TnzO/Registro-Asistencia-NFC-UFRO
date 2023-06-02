package com.UFRO.AsistenciaNFC.view;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.UFRO.AsistenciaNFC.data.AttendanceManager;
import com.UFRO.AsistenciaNFC.data.Subject;
import com.example.nfc_test.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SubjectListActivity extends AbstractView {
    public ListView listViewSubjects;


    // Create a HashMap to keep track of subjects by their names
    private HashMap<String, String> subjectNameToCodeMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        listViewSubjects = findViewById(R.id.listView_subjects);

        subjectNameToCodeMap = new HashMap<>();
        for (Subject subject : subjectManager.getSubjects().values()) {
            subjectNameToCodeMap.put(subject.getSubjectName(), subject.getSubjectCode());
        }

        // Display subject names instead of codes
        ArrayList<String> subjectNames = new ArrayList<>(subjectNameToCodeMap.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subjectNames);
        listViewSubjects.setAdapter(adapter);


        listViewSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String subjectName = (String) parent.getItemAtPosition(position);
                onSubjectClick(subjectName);
            }
        });
    }

    private void onSubjectClick(String subjectName) {
        String subjectCode = subjectNameToCodeMap.get(subjectName);
        if (subjectCode != null) {
            Intent intent = new Intent(this, SubjectDetailActivity.class);
            intent.putExtra("subjectCode", subjectCode);
            startActivity(intent);
        } else {
            // Handle error
            Toast.makeText(this, "Subject code not found for " + subjectName, Toast.LENGTH_SHORT).show();
        }
    }
}


