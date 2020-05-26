package com.example.bustracker_driver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity1 extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText inputEmail, inputPassword, inputNumberPlate;
    private boolean loggedIn = false;
    private ProgressBar progressBar;
    private Button btnLogin, btnReset;
    String email, password, NumberPlate;
    Spinner sp;
    String locat;
    ArrayAdapter<String> adp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        btnReset = findViewById(R.id.btn_reset_password);
        sp=findViewById(R.id.Route);
        sp.setOnItemSelectedListener(this);
        inputNumberPlate=findViewById(R.id.NumberPlate);

        List<String>list=new ArrayList<>();
        list.add(0,"<--Choose your route-->");
        list.add("Route A");
        list.add("Route B");
        list.add("Route C");
        list.add("Route D");
        list.add("Route E");
        list.add("Route F");

        adp = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, list);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adp);


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity1.this, ResetPasswordActivity1.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();
                NumberPlate = inputNumberPlate.getText().toString();

//              Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty() && !NumberPlate.isEmpty()) {
                    // login user
                    checkLogin();
                    updateDetail();

                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),"Please enter your details",
                            Toast.LENGTH_LONG).show();
                }

//                progressBar.setVisibility(View.VISIBLE);

            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        if (parent.getItemAtPosition(position).equals("<--Choose your route-->"))
        {

        }
        else {
            String locat = parent.getItemAtPosition(position).toString();

            // Showing selected spinner item
            Toast.makeText(parent.getContext(), "Selected: " + locat, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        Toast.makeText(arg0.getContext(), "Please Select Route!", Toast.LENGTH_LONG).show();

    }

    private void updateDetail(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://mobilehost2019.com/BusTracker/php/updateDetail.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                String route=sp.getSelectedItem().toString();
                String numberPlate= inputNumberPlate.getText().toString().trim();
                String email1 = inputEmail.getText().toString();
                Map<String, String> params = new HashMap<String, String>();
                params.put("Route",route);
                params.put("NumberPlate",numberPlate);
                params.put("email",email1);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void checkLogin(){
        //Getting values from edit texts
        final ProgressDialog loading = ProgressDialog.show(this,"Please Wait","Contacting Server",false,false);
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://mobilehost2019.com/BusTracker/php/login_user.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        //If we are getting success from server
                        if(response.equalsIgnoreCase("Success")){
                            //Creating a shared preference
                            SharedPreferences sharedPreferences = LoginActivity1.this.getSharedPreferences("Jane", Context.MODE_PRIVATE);

                            //Creating editor to store values to shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            //Adding values to editor
                            editor.putBoolean("loggedin", true);
                            editor.putString("user_email", email);
                            editor.commit();
                            Intent i = new Intent(LoginActivity1.this, FrontPage.class);
                            Toast.makeText(LoginActivity1.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(i);
                            finish();
//                            loadData(email);
                        }else{
                            //If the server response is not success
                            //Displaying an error message on toast
                            loading.dismiss();
                            Toast.makeText(LoginActivity1.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        loading.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(LoginActivity1.this,"No internet . Please check your connection",
                                    Toast.LENGTH_LONG).show();
                        }
                        else{

                            Toast.makeText(LoginActivity1.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }){


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                //Adding parameters to request
                params.put("email", email);
                params.put("password", password);

                //returning parameter
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

//    private void loadData(final String email){
//        //Creating a string request
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://mobilehost2019.com/BusTracker/php/getData.php?email="+email,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        Log.d("strrrrr", ">>" + response);
//
//                        try {
//                            JSONObject obj = new JSONObject(response);
//
//                            //Creating a shared preference
//                            SharedPreferences sharedPreferences = LoginActivity1.this.getSharedPreferences("Jane", Context.MODE_PRIVATE);
//
//                            //Creating editor to store values to shared preferences
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//
//                            //Adding values to editor
//                            editor.putString("bus_id", obj.getString("busid"));
//                            editor.putString("driver_name", obj.getString("drivername"));
//                            editor.putString("driver_phone", obj.getString("driverphone"));
//                            editor.commit();
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        //You can handle error here if you want
//                    }
//                }){
//        };
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                30000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        //Adding the string request to the queue
//        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity1.this);
//        requestQueue.add(stringRequest);
//    }


    protected void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences("Jane", Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean("loggedin", false);

        //If we will get true
        if(loggedIn) {
            //We will start the Profile Activity
            Intent intent = new Intent(LoginActivity1.this, DriverMapsActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
