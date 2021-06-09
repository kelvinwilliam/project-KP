package com.kelvin.dompet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kelvin.dompet.Adapter.detailAdapter;
import com.kelvin.dompet.Entity.detailEntity;
import com.kelvin.dompet.databinding.HistoryBinding;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class History extends AppCompatActivity {
    EditText fromDate,untilDate;
    DatePickerDialog picker;
    RecyclerView rv_detail;
    Button findbutton;
    String id;
    ArrayList<detailEntity> arrayList = new ArrayList<detailEntity>();
    private HistoryBinding binding;
    private detailAdapter detailadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String formattedDate = df.format(c);

        fromDate = findViewById(R.id.fromdate);
        untilDate = findViewById(R.id.fromdate2);
        rv_detail = findViewById(R.id.rv_detail);
        findbutton = findViewById(R.id.btfind);

        fromDate.setText(formattedDate);
        untilDate.setText(formattedDate);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setSelectedItemId(R.id.history);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.history:
                        return true;
                    case R.id.main:
                        Bundle bundle = new Bundle();
                        bundle.putString("id",id);
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.transaction:
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("id",id);
                        Intent intent2 = new Intent(getApplicationContext(),Transaction.class);
                        intent2.putExtras(bundle2);
                        startActivity(intent2);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                picker = new DatePickerDialog(History.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                fromDate.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        untilDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                picker = new DatePickerDialog(History.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                untilDate.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        findbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String From,Until;
                From = fromDate.getText().toString();
                Until = untilDate.getText().toString();

                if(!From.equals("") && !Until.equals("")) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[3];
                            field[0] = "userId";
                            field[1] = "start";
                            field[2] = "end";
                            //Creating array for data
                            String[] data = new String[3];
                            data[0] = id;
                            data[1] = From;
                            data[2] = Until;
                            PutData putData = new PutData("http://cashflow.it.maranatha.edu/services/service_fetch_transaction_range.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();

                                    try {
                                        arrayList.clear();
                                        JSONArray mJsonArray = new JSONArray(result);
                                        for(int i = 0; i<mJsonArray.length();i++){
                                                JSONObject mJsonObject = mJsonArray.getJSONObject(i);
                                                detailEntity detailEntity = new detailEntity();
                                                detailEntity.setAmmount(Integer.valueOf(mJsonObject.getString("tAmount")));
                                                detailEntity.setCategory(mJsonObject.getString("cName"));
                                                detailEntity.setPhoto(mJsonObject.getString("tPhoto"));
                                                detailEntity.setDescription(mJsonObject.getString("tDescription"));
                                                detailEntity.setDate(mJsonObject.getString("tDate"));
                                                arrayList.add(detailEntity);
                                        }
                                        if(!arrayList.isEmpty()) {
                                            detailadapter = new detailAdapter(arrayList);
                                            LinearLayoutManager manager = new LinearLayoutManager(History.this);
                                            DividerItemDecoration decoration = new DividerItemDecoration(findViewById(R.id.rv_detail).getContext(), manager.getOrientation());
                                            rv_detail.setLayoutManager(manager);
                                            rv_detail.setAdapter(detailadapter);
                                            rv_detail.addItemDecoration(decoration);

                                            detailadapter.setOnItemClickListener(new detailAdapter.DetailItemListener(){

                                                @Override
                                                public void detailDataClicked(detailEntity detail) {

                                                }

                                                @Override
                                                public void onEditClick(int position) {

                                                }
                                            });
                                        } else {
                                            Toast.makeText(History.this, "Tidak ada data", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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
    }
    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null && !bundle.isEmpty()) {
            id = bundle.getString("id");
        }


    }
}