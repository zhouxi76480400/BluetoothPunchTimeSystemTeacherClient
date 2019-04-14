package org.group.bluetoothpunchtimesystemteacherclient.activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import org.group.bluetoothpunchtimesystemteacherclient.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private LinearLayout ll_show_all_session, ll_start_new_session, ll_cv_set_students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ll_show_all_session = findViewById(R.id.ll_show_all_session);
        ll_show_all_session.setOnClickListener(this);
        ll_start_new_session = findViewById(R.id.ll_start_new_session);
        ll_start_new_session.setOnClickListener(this);
        ll_cv_set_students = findViewById(R.id.ll_cv_set_students);
        ll_cv_set_students.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_cv_set_students:
                gotoSetStudentsActivity();
                break;
        }
    }

    private void gotoSetStudentsActivity() {
        Intent intent = new Intent(this, SetStudentsActivity.class);
        startActivity(intent);
    }
}
