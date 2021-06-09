package com.kelvin.dompet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class Login extends AppCompatActivity {

    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    String id;
    EditText txt_username,txt_password;
    CardView cardViewlogin;
    TextView tv_regis,tv_login;
    SessionManagment sessionManagment;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        sessionManagment = new SessionManagment(getApplicationContext());

        txt_username = findViewById(R.id.login_txt_username);
        txt_password = findViewById(R.id.login_txt_password);
        cardViewlogin = findViewById(R.id.cv_login);
        tv_login = findViewById(R.id.login_tv_login);
        tv_regis = findViewById(R.id.login_tv_regis);


        cardViewlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username,password;

                username = txt_username.getText().toString();
                password = txt_password.getText().toString();

                if(!username.equals("") && !password.equals("")) {
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
                            data[0] = "login";
                            data[1] = username;
                            data[2] = "";
                            data[3] = password;
                            data[4] = "";
                            PutData putData = new PutData("http://cashflow.it.maranatha.edu/services/service_authenticate.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    String ErrCode = "1";
                                    String ErrMsg = "Error Connect to Internet";
                                    String id = "";
                                    JSONArray mJsonArray = null;
                                    try {
                                        mJsonArray = new JSONArray(result);
                                        JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                                        ErrCode = mJsonObject.getString("ErrCode");
                                        id = mJsonObject.getString("uId");
                                        sessionManagment.createLoginSession(id);
                                        if(ErrCode.equals("0")){}
                                        else{ ErrMsg = mJsonObject.getString("ErrMsg");}

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if(ErrCode.equals("0")){
                                        Bundle bundle = new Bundle();
                                        bundle.putString("id",id);
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        intent.putExtras(bundle);
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
                    Toast.makeText(getApplicationContext(),"All Field Required",Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv_regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        TextView forgetpassword = findViewById(R.id.forgetpassword);
        forgetpassword.setOnClickListener(v -> {
            createNewDialog();
        });
    }

    public void createNewDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View popup_forget = getLayoutInflater().inflate(R.layout.popup_forget,null);

        EditText pfEmail = (EditText) popup_forget.findViewById(R.id.pfEmail);

        dialogBuilder.setView(popup_forget);
        dialog = dialogBuilder.create();
        dialog.show();

        CardView cvSend = popup_forget.findViewById(R.id.cvSend);


        cvSend.setOnClickListener(v -> {
//            String oldpass = pOld.getText().toString();
//            String newpass = pNew.getText().toString();
//            String confirmpass = pConfirm.getText().toString();

//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    String[] field = new String[4];
//                    field[0] = "userId";
//                    field[1] = "oldPassword";
//                    field[2] = "newPassword";
//                    field[3] = "confirmPassword";
//                    //Creating array for data
//                    String[] data = new String[4];
//                    data[0] = id;
//                    data[1] = oldpass;
//                    data[2] = newpass;
//                    data[3] = confirmpass;
//
//                    PutData putData = new PutData("http://cashflow.it.maranatha.edu/services/service_update_password.php", "POST", field, data);
//                    if (putData.startPut()) {
//                        if (putData.onComplete()) {
//                            String result = putData.getResult();
//                            JSONArray mJsonArray = null;
//                            String ErrMsg = "Error Connect to Internet";
//                            try {
//                                mJsonArray = new JSONArray(result);
//                                JSONObject mJsonObject = mJsonArray.getJSONObject(0);
//                                ErrMsg = mJsonObject.getString("ErrMsg");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            Toast.makeText(getApplicationContext(), ErrMsg, Toast.LENGTH_SHORT).show();
//
//                            Log.i("PutData", result);
//                        }
//                    }
//                    //End Write and Read data with URL
//                }
//            });
            dialog.dismiss();
        });
    }

}