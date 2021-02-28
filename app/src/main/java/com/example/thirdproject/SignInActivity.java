package com.example.thirdproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignInActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sign_in);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Button button=findViewById(R.id.btn_sign);
        final EditText et_account=findViewById(R.id.sign_account);
        final EditText et_password=findViewById(R.id.sign_password);
        TextView textView=findViewById(R.id.tv_sign);


        sharedPreferences=getSharedPreferences("token",MODE_PRIVATE);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignInActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String account = et_account.getText().toString();
                final String password = et_password.getText().toString();

                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {


                        try {

                            JSONObject jsonObject=new JSONObject();
                            jsonObject.put("account",account);
                            jsonObject.put("password",password);
                            Log.i("asd",String.valueOf(jsonObject));
                            OkHttpClient client = new OkHttpClient();
                            MediaType mediaType = MediaType.parse("application/json");
                            RequestBody body = RequestBody.create(mediaType, String.valueOf(jsonObject));
                            Log.i("saa",String.valueOf(jsonObject));
                            Request request = new Request.Builder()
                                    .url("http://49.232.214.94/api/login")
                                    .post(body)
                                    .addHeader("Accept", "application/json")
                                    .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                                    .addHeader("Content-Type", "application/json")
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SignInActivity.this,"连接网络失败，请检查网络！",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull final Response response) {


                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {

                                                JSONObject jsonObject1 = new JSONObject(Objects.requireNonNull(response.body()).string());

                                                Toast.makeText(SignInActivity.this,jsonObject1.getString("msg"),Toast.LENGTH_SHORT).show();
                                                if (jsonObject1.getString("msg").equals("登录成功")){
                                                    JSONObject jsonObject2=jsonObject1.getJSONObject("data");
                                                    sharedPreferences.edit().clear().apply();
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("token",jsonObject2.getString("token"));
                                                    editor.apply();
                                                    editor.commit();
Log.i("asd1",jsonObject2.getString("token"));
                                                    Intent intent=new Intent(SignInActivity.this,MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
    }

}