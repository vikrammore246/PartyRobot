package com.example.vikram.partyrobot;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutUsActivity extends AppCompatActivity {

    private TextView about;
    int c = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        about = findViewById(R.id.txtAboutHeading);

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                c++;

                if (c==10){

                    c = 0;
                    ShowDeveloperInfo();

                }

            }
        });
    }

    private void ShowDeveloperInfo() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AboutUsActivity.this);
        View view = getLayoutInflater().inflate(R.layout.design_layout, null);

        mBuilder.setView(view);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }
}
