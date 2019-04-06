package com.piyush.newu.kiitcab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main3Activity extends AppCompatActivity {

    public void studentsignup(View v){

        if(v.getId()==R.id.student)
            startActivity(new Intent(getApplicationContext(),SignUpActivity.class));

        else if(v.getId()==R.id.teacher_button)
            startActivity(new Intent(getApplicationContext(),Main2Activity.class));

        else
            startActivity(new Intent(getApplicationContext(),nonteachingsignup.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
    }
}
