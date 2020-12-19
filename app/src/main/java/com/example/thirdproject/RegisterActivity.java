package com.example.thirdproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private String account;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);

        Button button=findViewById(R.id.btn_register);
        Button button_back=findViewById(R.id.register_btn_back);
        final EditText et_account=findViewById(R.id.register_account);
        final EditText et_password=findViewById(R.id.register_password);

        sharedPreferences=getSharedPreferences("token",MODE_PRIVATE);

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account=et_account.getText().toString();

                try {


                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String json = "{'account':" + account + ",'password':" + et_password.getText().toString() + "}";
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                OkHttpClient client = new OkHttpClient();
                                MediaType mediaType = MediaType.parse("application/json");
                                RequestBody body = RequestBody.create(mediaType, String.valueOf(jsonObject));


                                Request request = new Request.Builder()

                                        .url("http://49.232.214.94/api/register")
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
                                                Toast.makeText(RegisterActivity.this, "连接网络失败，请检查网络！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                try {
                                                    JSONObject jsonObject1 = new JSONObject(Objects.requireNonNull(response.body()).string());
                                                    Toast.makeText(RegisterActivity.this, jsonObject1.getString("msg"), Toast.LENGTH_SHORT).show();

                                                    if (jsonObject1.getString("msg").equals("注册成功")) {
                                                        JSONObject jsonObject2=jsonObject1.getJSONObject("data");
                                                        sharedPreferences.edit().clear().apply();
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putString("token",jsonObject2.getString("token"));
                                                        editor.apply();
                                                        editor.commit();
                                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
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
                }catch (Exception e) {
                    e.printStackTrace();
                }





            }
        });



    }
}