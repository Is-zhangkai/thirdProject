package com.example.thirdproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.thirdproject.tool.Data;
import com.example.thirdproject.tool.ShoppingCarAdapter;
import com.example.thirdproject.tool.StoreAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShoppingCarActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private ShoppingCarAdapter adapter;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_shopping_car);
        Button button_back=findViewById(R.id.car_back);
        recyclerView=findViewById(R.id.car_recycler);
        SmartRefreshLayout refreshLayout=findViewById(R.id.car_smart);

        recyclerView.setLayoutManager(new LinearLayoutManager(ShoppingCarActivity.this));
        sharedPreferences=getSharedPreferences("token",MODE_PRIVATE);
        token=sharedPreferences.getString("token","null");
        List<Data> list=new ArrayList<>();
        GET(list);

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                List<Data> list=new ArrayList<>();
                GET(list);
                refreshLayout.finishRefresh();
            }
        });
    }

    public void GET(final List<Data> list){
        try {
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    Request request = new Request.Builder()
                            .url("http://49.232.214.94/api/order")
                            .method("GET", null)
                            .addHeader("Accept", "application/json")
                            .addHeader("Authorization", token)
                            .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(ShoppingCarActivity.this,"连接网络失败，请检查网络！",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            try {
                                JSONObject jsonObject=new JSONObject(response.body().string());
                                final String msg=jsonObject.getString("msg");
                                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                                final JSONArray jsonArray=jsonObject1.getJSONArray("orders");
                                for (int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject2=jsonArray.getJSONObject(i);
                                    final Data data = new Data();

                                    data.setGoods_count(jsonObject2.getInt("goods_count"));
                                    data.setGood_id(jsonObject2.getInt("good_id"));

                                    OkHttpClient client = new OkHttpClient().newBuilder()
                                            .build();
                                    Request request = new Request.Builder()
                                            .url("http://49.232.214.94/api/goods/"+jsonObject2.getInt("good_id"))
                                            .method("GET", null)
                                            .addHeader("Accept", "application/json")
                                            .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                                            .build();
                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                        }

                                        @Override
                                        public void onResponse(@NotNull Call call, @NotNull Response response1) throws IOException {

                                            try {
                                                JSONObject jsonObject3=new JSONObject(response1.body().string());
                                                JSONObject jsonObject4=jsonObject3.getJSONObject("data");
                                                JSONObject jsonObject5=jsonObject4.getJSONObject("good");
                                                data.setName(jsonObject5.getString("name"));
                                                data.setPrice(jsonObject5.getDouble("price"));
                                                data.setImg(jsonObject5.getString("img"));
                                                list.add(data);
                                                if (list.size()==jsonArray.length()){
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(ShoppingCarActivity.this,msg,Toast.LENGTH_SHORT).show();

                                                            adapter=new ShoppingCarAdapter(ShoppingCarActivity.this, list);
                                                            recyclerView.setAdapter(adapter);
                                                        }
                                                    });}
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}