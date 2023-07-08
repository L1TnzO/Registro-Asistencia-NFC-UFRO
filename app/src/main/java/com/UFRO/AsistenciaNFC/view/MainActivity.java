package com.UFRO.AsistenciaNFC.view;

import com.example.nfc_test.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AbstractView {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonSubjectList = findViewById(R.id.button_subject_list);
        buttonSubjectList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SubjectListActivity.class);
                startActivity(intent);
            }
        });
    }
}