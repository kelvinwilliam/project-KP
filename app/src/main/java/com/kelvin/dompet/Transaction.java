package com.kelvin.dompet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kelvin.dompet.Entity.Category;
import com.kelvin.dompet.databinding.TransactionBinding;
import com.squareup.picasso.Picasso;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Transaction extends AppCompatActivity {
    private static final int PERMISSION_CODE = 101;
    private static final int IMAGE_CAPTURE_CODE = 102;
    String transAmount,transDate,transDesc,transPhoto,transCId;
    private static final String url = "http://cashflow.it.maranatha.edu/services/FileUpload.php";
    String typeCat = null;
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    ImageButton datepicker;
    TextView showDate;
    SimpleDateFormat dateFormatter;
    String id;
    private TransactionBinding binding;
    ArrayList<Category> arrayList = new ArrayList<Category>();
    String encodeImage;
    private Category category;
    Uri image_uri;
    String catName = "";
    Category category2;
    String tId = "kosong";
    String tAmount,tDate,tDescription,tPhoto,tbCategory_id;
    ArrayAdapter<Category> categoryArrayAdapter;
    Bitmap bitmap;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        datepicker = findViewById(R.id.datepicker);
        showDate = findViewById(R.id.showdate);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setSelectedItemId(R.id.transaction);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.history:
                        Bundle bundle = new Bundle();
                        bundle.putString("id",id);
                        Intent intent = new Intent(getApplicationContext(),History.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.main:
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("id",id);
                        Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);
                        intent2.putExtras(bundle2);
                        startActivity(intent2);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.transaction:
                        return true;
                }
                return false;
            }
        });

        onStart();
        editTransaction(tId);

    }

    private void showDateDialog(){

        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */

                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */
                showDate.setText( dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("tId")!=null && !bundle.isEmpty()) {
            id = bundle.getString("id");
            tId = bundle.getString("tId");
            tAmount = bundle.getString("tAmount");
            tDate = bundle.getString("tDate");
            tDescription = bundle.getString("tDescription");
            tPhoto = bundle.getString("tPhoto");
            tbCategory_id = bundle.getString("tbCategory_id");
        } else {
            tId = "kosong";
        }
        if(bundle!=null && !bundle.isEmpty()) {
            id = bundle.getString("id");
        }
        categoryArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
    }

    public void createNewDialog(){

        dialogBuilder = new AlertDialog.Builder(this);
        final View popup_category = getLayoutInflater().inflate(R.layout.popup_category,null);

        EditText pcName = (EditText) popup_category.findViewById(R.id.pcName);

        dialogBuilder.setView(popup_category);
        dialog = dialogBuilder.create();
        dialog.show();

        CardView cvSend = popup_category.findViewById(R.id.cvaddcat);

        CardView pcvincome = popup_category.findViewById(R.id.pcvincome);
        pcvincome.setOnClickListener(v -> {
            typeCat = "Income";
        });

        CardView pcvexpense = popup_category.findViewById(R.id.pcvexpense);
        pcvexpense.setOnClickListener(v -> {
            typeCat = "Expense";
        });


        cvSend.setOnClickListener(v -> {
            String catName = pcName.getText().toString();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[4];
                    field[0] = "userId";
                    field[1] = "catName";
                    field[2] = "catType";
                    field[3] = "jenis";
                    //Creating array for data
                    String[] data = new String[4];
                    data[0] = id;
                    data[1] = catName;
                    data[2] = typeCat;
                    data[3] = "user";

                    PutData putData = new PutData("http://cashflow.it.maranatha.edu/services/service_add_category.php", "POST", field, data);
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

    private void askCameraPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ){
                String[] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission,PERMISSION_CODE);
            } else {
                openCamera();
            }
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"New Picture");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(image_uri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                binding.imageView.setImageBitmap(bitmap);
                encodeBitmapImage(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void encodeBitmapImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        byte[] bytes=byteArrayOutputStream.toByteArray();
        encodeImage = android.util.Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public void addTransaction(){
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        binding.cvincome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCate("Income");
            }

        });

        binding.cvexpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCate("Expense");
            }

        });

        binding.imgbutton.setOnClickListener(v -> {
            askCameraPermissions();
        });

        binding.addCat.setOnClickListener(v -> {
            createNewDialog();
        });

        binding.cvadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String transDate,transDesc,transPhoto = "",transAmount,transCId;
                uploadImagetoServer("trans");
            }
        });
    }

    private void handletrans(){
        transAmount = binding.ammount.getText().toString();
        transDate = binding.showdate.getText().toString();
        transCId = category.getId() ;
        transDesc = binding.dsc.getText().toString();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = new String[6];
                field[0] = "userId";
                field[1] = "transAmount";
                field[2] = "transDate";
                field[3] = "transDesc";
                field[4] = "transPhoto";
                field[5] = "transCId";

                //Creating array for data
                String[] data = new String[6];
                data[0] = id;
                data[1] = transAmount;
                data[2] = transDate;
                data[3] = transDesc;
                data[4] = transPhoto;
                data[5] = transCId;

                PutData putData = new PutData("http://cashflow.it.maranatha.edu/services/service_add_transaction.php", "POST", field, data);
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
    }

    private void handleedit(String transID){
        transAmount = binding.ammount.getText().toString();
        transDate = binding.showdate.getText().toString();
        transCId = category.getId() ;
        transDesc = binding.dsc.getText().toString();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = new String[7];
                field[0] = "userId";
                field[1] = "transEditId";
                field[2] = "transEditAmount";
                field[3] = "transEditDate";
                field[4] = "transEditDesc";
                field[5] = "transEditPhoto";
                field[6] = "transEditCId";

                //Creating array for data
                String[] data = new String[7];
                data[0] = id;
                data[1] = transID;
                data[2] = transAmount;
                data[3] = transDate;
                data[4] = transDesc;
                data[5] = transPhoto;
                data[6] = transCId;

                PutData putData = new PutData("http://cashflow.it.maranatha.edu/services/service_update_transaction.php", "POST", field, data);
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
    }

    private String uploadImagetoServer(String jenis) {
        final String[] fileName = {""};
        if (encodeImage!=null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(Transaction.this, response.toString(), Toast.LENGTH_SHORT).show();
                    JSONArray mJsonArray = null;
                    try {
                        JSONObject mJsonObject = new JSONObject(response);
                        fileName[0] = mJsonObject.getString("filename");
                        transPhoto = fileName[0];
                        if (jenis.equalsIgnoreCase("trans")) {
                            handletrans();
                        } else {
                            handleedit(jenis);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("TAG", "onResponse: done");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Transaction.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("userId", id);
                    map.put("image", encodeImage);
                    return map;
                }
            };

            stringRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        } else {
            transPhoto="";
            handletrans();
        }

        return fileName[0];
    }

    public void editTransaction(String transID){

        if (transID.equalsIgnoreCase("kosong")){
            addTransaction();
        }
        else {
            binding.ammount.setText(tAmount);
            binding.showdate.setText(tDate);
            binding.dsc.setText(tDescription);
            catName = tbCategory_id;
            if(Integer.parseInt(tAmount)>0){
                getCate("Income");
            } else {
                getCate("Expense");
            }

            dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            datepicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDateDialog();
                }
            });

            binding.cvincome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCate("Income");
                }

            });

            binding.cvexpense.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCate("Expense");
                }

            });

            binding.imgbutton.setOnClickListener(v -> {
                askCameraPermissions();
            });

            binding.addCat.setOnClickListener(v -> {
                createNewDialog();
            });

            Picasso.with(this).load("http://cashflow.it.maranatha.edu/uploads/"+ id + "/" + tPhoto).into(binding.imageView);

            binding.cvadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String transDate,transDesc,transPhoto = "",transAmount,transCId;
                    uploadImagetoServer(transID);
                }
            });
        }
    }

    public void getCate(String jenis2){

                Handler handler = new Handler(Looper.getMainLooper());
                handler.removeCallbacksAndMessages(null);
                handler.post(new Runnable() {
                    @Override
                    public void run () {
                        String[] field = new String[2];
                        field[0] = "userId";
                        field[1] = "jenis";
                        //Creating array for data
                        String[] data = new String[2];
                        data[0] = id;
                        data[1] = jenis2;
                        String url = "http://cashflow.it.maranatha.edu/services/service_fetch_categories.php";
                        PutData putData = new PutData(url, "POST", field, data);
                        if (putData.startPut()) {
                            if (putData.onComplete()) {
                                String result = putData.getResult();
                                String ErrCode = "1";
                                String id = "";
                                JSONArray mJsonArray = null;
                                arrayList.clear();
                                try {
                                    mJsonArray = new JSONArray(result);
                                    for (int i = 0; i < mJsonArray.length(); i++) {
                                        JSONObject mJsonObject = mJsonArray.getJSONObject(i);
                                        category2 = new Category();
                                        category2.setId(mJsonObject.getString("cId"));
                                        category2.setTipe(mJsonObject.getString("cType"));
                                        category2.setName(mJsonObject.getString("cName"));
                                        arrayList.add(category2);
                                    }
                                    binding.category.setAdapter(categoryArrayAdapter);
                                    binding.category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            category = (Category) parent.getItemAtPosition(getIndex(arrayList, catName));
                                        }
                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.i("PutData", result);
                            }
                        }
                        //End Write and Read data with URL
                    }
                });


    }

    public int getIndex(ArrayList<Category> spinner,String s){
        for(int i=0; i<spinner.size(); i++){
            if(spinner.get(i).getId().equalsIgnoreCase(s)){
                return i;
            }
        }
        return 0;
    }
}