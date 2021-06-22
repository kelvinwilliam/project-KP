package com.kelvin.dompet;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.kelvin.dompet.databinding.ProfileBinding;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Profile extends AppCompatActivity {

    ProfileBinding binding;
    String id;
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = new String[1];
                field[0] = "userId";

                //Creating array for data
                String[] data = new String[1];
                data[0] = id;

                PutData putData = new PutData("http://cashflow.it.maranatha.edu/services/service_fetch_profile.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        JSONArray mJsonArray = null;
                        try {
                            mJsonArray = new JSONArray(result);
                            JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                            String uId = mJsonObject.getString("uId");
                            String name = mJsonObject.getString("uName");
                            String email = mJsonObject.getString("uEmail");
                            binding.pName.setText(name);
                            binding.pEmail.setText(email);
                            binding.pJoinSince.setText(uId.substring(0,4) + " - " + monthformat(uId.substring(4,6)) + " - " + uId.substring(6,8));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.i("PutData", result);
                    }
                }
                //End Write and Read data with URL
            }
        });

        binding.cvedit.setOnClickListener(v -> {
            String nameedit = binding.pName.getText().toString();
            String emailedit = binding.pEmail.getText().toString();

            Handler handler2 = new Handler(Looper.getMainLooper());
            handler2.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[3];
                    field[0] = "userId";
                    field[1] = "uName";
                    field[2] = "uEmail";
                    //Creating array for data
                    String[] data = new String[3];
                    data[0] = id;
                    data[1] = nameedit;
                    data[2] = emailedit;

                    PutData putData = new PutData("http://cashflow.it.maranatha.edu/services/service_update_profile.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult();
                            JSONArray mJsonArray = null;
                            try {
                                mJsonArray = new JSONArray(result);
                                JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.i("PutData", result);
                        }
                    }
                    //End Write and Read data with URL
                }
            });
        });

        binding.cvlogout.setOnClickListener(v -> {
            logout();
        });

        binding.pPassword.setOnClickListener(v -> {
            createNewDialog();
        });
    }

    private String monthformat(String substring) {
        String month = null;
        switch (substring){
            case "01":
                month = "Jan";
                break;
            case "02":
                month = "Feb";
                break;
            case "03":
                month = "Mar";
                break;
            case "04":
                month = "Apr";
                break;
            case "05":
                month = "Mei";
                break;
            case "06":
                month = "Jun";
                break;
            case "07":
                month = "Jul";
                break;
            case "08":
                month = "Agu";
                break;
            case "09":
                month = "Sep";
                break;
            case "10":
                month = "Okt";
                break;
            case "11":
                month = "Nov";
                break;
            case "12":
                month = "Des";
                break;
        }
        return month;
    };

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null && !bundle.isEmpty()) {
            id = bundle.getString("id");
        }
    }

    public void createNewDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View popup_password = getLayoutInflater().inflate(R.layout.popup_password,null);

        EditText pOld = (EditText) popup_password.findViewById(R.id.pOld);
        EditText pNew = (EditText) popup_password.findViewById(R.id.pNew);
        EditText pConfirm = (EditText) popup_password.findViewById(R.id.pConfirm);

        dialogBuilder.setView(popup_password);
        dialog = dialogBuilder.create();
        dialog.show();

        CardView cvChange = popup_password.findViewById(R.id.cvChange);


        cvChange.setOnClickListener(v -> {
            String oldpass = pOld.getText().toString();
            String newpass = pNew.getText().toString();
            String confirmpass = pConfirm.getText().toString();

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[4];
                    field[0] = "userId";
                    field[1] = "oldPassword";
                    field[2] = "newPassword";
                    field[3] = "confirmPassword";
                    //Creating array for data
                    String[] data = new String[4];
                    data[0] = id;
                    data[1] = oldpass;
                    data[2] = newpass;
                    data[3] = confirmpass;

                    PutData putData = new PutData("http://cashflow.it.maranatha.edu/services/service_update_password.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult();
                            JSONArray mJsonArray = null;
                            String ErrMsg = "Error Connect to Internet";
                            try {
                                mJsonArray = new JSONArray(result);
                                JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                                ErrMsg = mJsonObject.getString("ErrMsg");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), ErrMsg, Toast.LENGTH_SHORT).show();

                            Log.i("PutData", result);
                        }
                    }
                    //End Write and Read data with URL
                }
            });
            dialog.dismiss();
        });
    }

    public void logout(){
        SessionManagment sessionManagment;
        sessionManagment = new SessionManagment(getApplicationContext());
        sessionManagment.logoutUser();
    }
}
