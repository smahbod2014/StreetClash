package com.cse190sc.streetclash;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button m_ToastButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_ToastButton = (Button) findViewById(R.id.button_toast);
    }

    public void toastClicked(View v)
    {
        Toast.makeText(this, "Toast", Toast.LENGTH_SHORT).show();
        m_ToastButton.setText("Done");
        m_ToastButton.setEnabled(false);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.relLayout);
        layout.setBackgroundColor(Color.parseColor("#ff00aa"));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_ToastButton.setEnabled(true);
    }
}
