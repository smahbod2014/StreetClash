package com.cse190sc.streetclash;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button m_ToastButton;
    TextView m_Display;
    NumberPicker m_NumberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_ToastButton = (Button) findViewById(R.id.button_toast);
        m_Display = (TextView) findViewById(R.id.tv_display);
        m_NumberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        m_NumberPicker.setMinValue(0);
        m_NumberPicker.setMaxValue(4);
    }

    public void toastClicked(View v)
    {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                "http://52.88.99.161:9000/data",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            StringBuilder sb = new StringBuilder();
                            JSONObject obj = response.getJSONObject(m_NumberPicker.getValue());
                            sb.append("Name: " + obj.getString("name") + "\n");
                            sb.append("Age: " + obj.getInt("age"));
                            m_Display.setText(sb.toString());
                        }
                        catch (JSONException e) {
                            m_Display.setText(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        m_Display.setText("Error: " + error.getMessage());
                    }
                }
        );

        Volley.newRequestQueue(this).add(request);

        /*JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "http://52.88.99.161:9000",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String body = "";
                            body += "Name: " + response.getString("name") + "\n";
                            JSONObject interests = response.getJSONObject("interests");
                            body += "Night interest: " + interests.getString("night");
                            m_Display.setText(body);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        m_Display.setText("Error: " + error.getMessage());
                    }
                }
        );

        Volley.newRequestQueue(this).add(request);*/
    }
}
