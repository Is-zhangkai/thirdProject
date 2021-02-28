package com.example.thirdproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GoodsActivity extends AppCompatActivity {
    private ImageView imageView;
    private SharedPreferences sharedPreferences;
    private TextView name,price,info,quantity;
    private Pattern httpPattern;
    private int number=1,goods_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_goods);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        imageView=findViewById(R.id.goods_img);
        Button button_back=findViewById(R.id.goods_btn_back);
        Button btn_plus=findViewById(R.id.good_btn_add);
        Button btn_reduce=findViewById(R.id.good_btn);
        Button btn_buy=findViewById(R.id.btn_buy);
        final EditText et_number=findViewById(R.id.good_et);
        name=findViewById(R.id.goods_name);
        price=findViewById(R.id.goods_price);
        info=findViewById(R.id.goods_info);
        quantity=findViewById(R.id.goods_quantity);


        final int id=getIntent().getIntExtra("id",0);
        GetData(id);

        sharedPreferences= Objects.requireNonNull(getSharedPreferences("token",MODE_PRIVATE));
        final String token=sharedPreferences.getString("token","null");

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number=Integer.parseInt(et_number.getText().toString())+1;
                et_number.setText(number+"");
            }
        });

        btn_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (( number=Integer.parseInt(et_number.getText().toString())-1)>0){
                    et_number.setText(number+"");
                }else {
                    Toast.makeText(GoodsActivity.this,"不能再少了！",Toast.LENGTH_SHORT).show();

                }

            }
        });


        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number=Integer.parseInt(et_number.getText().toString());
                if (number>=goods_number) {
                    Log.i("asd", number + "gs");
                    try {
                        final JSONObject jsonObject = new JSONObject();
                        jsonObject.put("good_id", id);
                        jsonObject.put("quantity", number);

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                OkHttpClient client = new OkHttpClient().newBuilder()
                                        .build();
                                MediaType mediaType = MediaType.parse("application/json");
                                RequestBody body = RequestBody.create(mediaType, String.valueOf(jsonObject));
                                Request request = new Request.Builder()
                                        .url("http://49.232.214.94/api/order")
                                        .method("POST", body)
                                        .addHeader("Accept", "application/json")
                                        .addHeader("Authorization", token)
                                        .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                                        .addHeader("Content-Type", "application/json")
                                        .build();
                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(GoodsActivity.this, "连接网络失败，请检查网络！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        try {

                                            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                                            final String msg = jsonObject.getString("msg");


                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(GoodsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                }
                                            });
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

                } else{
                    Toast.makeText(GoodsActivity.this,"库存不足！",Toast.LENGTH_SHORT).show();
                }
            }
        });


        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    public void GetData(final int id){

        try {

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url("http://49.232.214.94/api/goods/"+id)
                        .method("GET", null)
                        .addHeader("Accept", "application/json")
                        .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GoodsActivity.this,"连接网络失败，请检查网络！",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                        try {
                        JSONObject jsonObject1=new JSONObject(Objects.requireNonNull(response.body()).string());
                        JSONObject jsonObject2=jsonObject1.getJSONObject("data");
                        final JSONObject jsonObject3=jsonObject2.getJSONObject("good");
                        final String img=jsonObject3.getString("img");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        goods_number=jsonObject3.getInt("quantity");
                                        name.setText(jsonObject3.getString("name"));
                                        price.setText("￥"+jsonObject3.getDouble("price"));
                                        quantity.setText("剩余库存:"+goods_number+"件");
                                        info.setText(jsonObject3.getString("info"));
                                        httpPattern = Pattern
                                                .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~/])+$");

                                        if (httpPattern.matcher(img).matches()) {
                                            Glide.with(GoodsActivity.this).load(img).into(imageView);
                                        }else{
                                            Glide.with(GoodsActivity.this).load("http://49.232.214.94/api/img/"+img).into(imageView);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }



                                }
                            });
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