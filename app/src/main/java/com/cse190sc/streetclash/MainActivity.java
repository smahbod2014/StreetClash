package com.cse190sc.streetclash;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button m_PostButton;
    private static final String TAG = "MainActivity";
    String URL = "http://52.88.99.161:3000/users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_PostButton = (Button) findViewById(R.id.btn_post);
    }

    public void postClicked(View v)
    {
        /*JsonArrayRequest request = new JsonArrayRequest(
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

        Volley.newRequestQueue(this).add(request);*/

        /**/
        JSONObject jo = null;
        try {
            jo = new JSONObject("{\"first\":\"Donald\", \"last\":\"Trump\"}");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                URL,
                jo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "Got response: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "Error: " + error.getMessage());
                    }
                });
        Volley.newRequestQueue(this).add(req);
        Toast.makeText(MainActivity.this, "Sent post...", Toast.LENGTH_SHORT).show();
    }

    public void getClicked(View v) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i(TAG, "Response: " + response.getString("first"));
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
                        Log.i(TAG, "Error: " + error.getMessage());
                    }
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}
