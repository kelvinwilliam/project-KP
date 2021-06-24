package com.kelvin.dompet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity {

    EditText txt_fullname,txt_username,txt_password,txt_password2,txt_email;
    CardView cardView;
    TextView tv_regis,tv_login;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        txt_fullname = findViewById(R.id.txt_fullname);
        txt_password = findViewById(R.id.txt_password);
        txt_password2 = findViewById(R.id.txt_password2);
        txt_email = findViewById(R.id.txt_email);
        cardView = findViewById(R.id.cv_regis);
        tv_login = findViewById(R.id.tv_regis);
        tv_regis = findViewById(R.id.tv_login);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fullname,password2,password,email;

                fullname = txt_fullname.getText().toString();
                password = txt_password2.getText().toString();
                password2 = txt_password.getText().toString();
                email = txt_email.getText().toString();

                if(!fullname.equals("") && !password2.equals("") && !password.equals("") && !email.equals("")) {
                    if(password.equals(password2)){
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Starting Write and Read data with URL
                                //Creating array for parameters
                                String[] field = new String[5];
                                field[0] = "jenis";
                                field[1] = "email";
                                field[2] = "namaUser";
                                field[3] = "password";
                                field[4] = "confirmPassword";
                                //Creating array for data
                                String[] data = new String[5];
                                data[0] = "register";
                                data[1] = email;
                                data[2] = fullname;
                                data[3] = password;
                                data[4] = password2;
                                PutData putData = new PutData("http://cashflow.it.maranatha.edu/services/service_authenticate.php", "POST", field, data);
                                if (putData.startPut()) {
                                    if (putData.onComplete()) {
                                        String result = putData.getResult();
                                        String ErrCode = "1";
                                        String ErrMsg = "Fail Connect to Web Service";
                                        JSONArray mJsonArray = null;
                                        try {
                                            mJsonArray = new JSONArray(result);
                                            JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                                            ErrCode = mJsonObject.getString("ErrCode");
                                            if(ErrCode.equals("0")){

                                            }
                                            else{ ErrMsg = mJsonObject.getString("ErrMsg");}

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        if(ErrCode.equals("0")){
                                            Toast.makeText(getApplicationContext(),"Account successfully registered",Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            Toast.makeText(getApplicationContext(), ErrMsg, Toast.LENGTH_SHORT).show();
                                        }
                                        Log.i("PutData", result);
                                    }
                                }
                                //End Write and Read data with URL
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(),"Those password didn't match",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"All Field Required",Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });

    }

}