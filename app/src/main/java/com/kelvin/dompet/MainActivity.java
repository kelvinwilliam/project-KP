package com.kelvin.dompet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kelvin.dompet.databinding.MainBinding;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public String id;
    private MainBinding binding;
    SessionManagment sessionManagment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainBinding.inflate(getLayoutInflater());

        sessionManagment = new SessionManagment(getApplicationContext());
        sessionManagment.checkLogin();

        if(sessionManagment.isLoggedIn()) {

            setContentView(binding.getRoot());

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

            bottomNavigationView.setSelectedItemId(R.id.main);

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.history:
                            Bundle bundle = new Bundle();
                            bundle.putString("id", id);
                            Intent intent = new Intent(getApplicationContext(), History.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.main:
                            return true;
                        case R.id.transaction:
                            Bundle bundle2 = new Bundle();
                            bundle2.putString("id", id);
                            Intent intent2 = new Intent(getApplicationContext(), Transaction.class);
                            intent2.putExtras(bundle2);
                            startActivity(intent2);
                            overridePendingTransition(0, 0);
                            return true;
                    }
                    return false;
                }
            });

            binding.btImage.setOnClickListener(v -> {
                Bundle bundle3 = new Bundle();
                bundle3.putString("id", id);
                Intent intent3 = new Intent(getApplicationContext(), Profile.class);
                intent3.putExtras(bundle3);
                startActivity(intent3);
                overridePendingTransition(0, 0);
            });

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[2];
                    field[0] = "userId";
                    field[1] = "jenis";

                    //Creating array for data
                    String[] data = new String[2];
                    data[0] = id;
                    data[1] = "dashboard";

                    PutData putData = new PutData("http://cashflow.it.maranatha.edu/services/service_fetch_dashboard.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult();
                            String income = "";
                            String expense = "";
                            JSONArray mJsonArray = null;
                            try {
                                mJsonArray = new JSONArray(result);
                                JSONObject mJsonObject = mJsonArray.getJSONObject(0);

                                income = mJsonObject.getString("income");
                                if (income.equals("null")) {
                                    income = "0";
                                }

                                expense = mJsonObject.getString("expense");
                                if (expense.equals("null")) {
                                    expense = "0";
                                }
                                BigInteger bigIncome = new BigInteger(income);
                                BigInteger bigExpense = new BigInteger(expense);
                                binding.btincome.setText(formater(bigIncome));
                                binding.btexpense.setText(formater(bigExpense.multiply(new BigInteger("-1"))));
                                binding.amount.setText(formater(bigIncome.add(bigExpense)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.i("PutData", result);
                        }
                    }
                    //End Write and Read data with URL
                }
            });

            ArrayList<BarEntry> bars = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            Handler handler2 = new Handler(Looper.getMainLooper());
            handler2.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[2];
                    field[0] = "userId";
                    field[1] = "jenis";

                    String[] data = new String[2];
                    data[0] = id;
                    data[1] = "mostCategory";

                    PutData putData = new PutData("http://cashflow.it.maranatha.edu/services/service_fetch_dashboard.php", "POST", field, data);
                    bars.clear();
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult();
                            JSONArray mJsonArray = null;
                            try {
                                mJsonArray = new JSONArray(result);
                                System.out.println(mJsonArray.length());
                                for (int i = 0; i < mJsonArray.length(); i++) {
                                    JSONObject mJsonObject = mJsonArray.getJSONObject(i);
                                    bars.add(new BarEntry(i, Integer.parseInt(mJsonObject.getString("mostCat"))));
                                    labels.add(mJsonObject.getString("cName"));
                                }
                                System.out.println(labels);
                                BarDataSet barDataSet = new BarDataSet(bars, "");
                                barDataSet.setColor(Color.rgb(255, 123, 126));
                                barDataSet.setValueTextColor(Color.BLACK);
                                barDataSet.setValueTextSize(16f);

                                BarData barData = new BarData(barDataSet);

                                binding.barChart.setFitBars(true);
                                binding.barChart.setData(barData);
                                binding.barChart.getDescription().setText("");
                                binding.barChart.getXAxis().setGranularity(1f);
                                binding.barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                                binding.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

                                YAxis rightYAxis = binding.barChart.getAxisRight();
                                rightYAxis.setGranularity(1f);
                                rightYAxis.setAxisMinimum(0);

                                YAxis leftYAxis = binding.barChart.getAxisLeft();
                                rightYAxis.setGranularity(1f);
                                leftYAxis.setAxisMinimum(0);

                                binding.barChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
                                    @Override
                                    public String getFormattedValue(float value) {
                                        return String.valueOf((int) Math.floor(value));
                                    }
                                });

                                binding.barChart.getAxisRight().setValueFormatter(new ValueFormatter() {
                                    @Override
                                    public String getFormattedValue(float value) {
                                        return String.valueOf((int) Math.floor(value));
                                    }
                                });

                                binding.barChart.animateY(1000);


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

    }

    @Override
    protected void onStart() {
        super.onStart();
        HashMap<String, String> user = sessionManagment.getUserDetails();
        id = user.get(sessionManagment.key_id);
    }

    public static String formater(BigInteger amount) {
        String money;

        DecimalFormat idn = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols Rp = new DecimalFormatSymbols();
        Rp.setCurrencySymbol("Rp. ");
        Rp.setMonetaryDecimalSeparator(',');
        Rp.setGroupingSeparator('.');
        idn.setDecimalFormatSymbols(Rp);
        money = idn.format(amount);
        return money;
    }

}