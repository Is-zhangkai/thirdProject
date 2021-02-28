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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.thirdproject.tool.BottomPopupOption;
import com.example.thirdproject.tool.CameraActivity;
import com.jaygoo.selector.MultiSelectPopWindow;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private ImageView imageView;
    private String token;
    private String hash=null;
    private EditText et_name,et_price,et_quantity,et_info;
    private Button button_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_addgoods);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Button button_back=findViewById(R.id.add_btn_back);
        button_comment=findViewById(R.id.add_btn_comment);
        et_name=findViewById(R.id.add_et_name);
        et_price=findViewById(R.id.add_et_price);
        et_quantity=findViewById(R.id.add_et_quantity);
        et_info=findViewById(R.id.add_et_info);
        imageView=findViewById(R.id.add_img);

        sharedPreferences=getSharedPreferences("token",MODE_PRIVATE);
        token=sharedPreferences.getString("token","null");


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final BottomPopupOption bottomPopupOption = new BottomPopupOption(AddActivity.this);
                bottomPopupOption.setItemText("拍照", "选择相册");
                bottomPopupOption.showPopupWindow();
                bottomPopupOption.setItemClickListener(new BottomPopupOption.onPopupWindowItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                        if (position==0){
                            startActivity(new Intent(AddActivity.this, CameraActivity.class).putExtra(CameraActivity.ExtraType, CameraActivity.CAMERA));
                            bottomPopupOption.dismiss();
                        }else {
                            startActivity(new Intent(AddActivity.this, CameraActivity.class).putExtra(CameraActivity.ExtraType, CameraActivity.PHOTO));
                            bottomPopupOption.dismiss();
                        }
                    }
                });

            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        button_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hash!=null&&et_name.getText().toString()!=null&&et_info.getText().toString()!=null&&et_price.getText().toString()!=null&&et_quantity.getText().toString()!=null){
                    String name,info;
                    float price;
                    int quantity;
                    name=et_name.getText().toString();
                    price=Float.parseFloat(et_price.getText().toString());
                    quantity=Integer.parseInt(et_quantity.getText().toString());
                    info=et_info.getText().toString();
                    final JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("name",name);
                        jsonObject.put("price",price);
                        jsonObject.put("quantity",quantity);
                        jsonObject.put("info",info);
                        jsonObject.put("img","http://49.232.214.94/api/img/"+hash);

                        Thread thread=new Thread(new Runnable() {
                            @Override
                            public void run() {
                                OkHttpClient client = new OkHttpClient().newBuilder()
                                        .build();
                                MediaType mediaType = MediaType.parse("application/json");
                                RequestBody body = RequestBody.create(mediaType, String.valueOf(jsonObject));
                                Request request = new Request.Builder()
                                        .url("http://49.232.214.94/api/good")
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
                                                Toast.makeText(AddActivity.this,"连接网络失败，请检查网络！",Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    JSONObject jsonObject1=new JSONObject(response.body().string());
                                                    String s=jsonObject1.getString("msg");
                                                    Toast.makeText(AddActivity.this,s,Toast.LENGTH_SHORT).show();
                                                    if (s.equals("创建成功")){ finish();}

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        thread.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            else {
                    Toast.makeText(AddActivity.this,"信息不完整！",Toast.LENGTH_SHORT).show();

                }}
        });

    }
    ////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();
        //获得相册、相机返回的结果，并显示
        if (CameraActivity.LISTENING) {
            Log.i("TAG", "返回的Uri结果：" + CameraActivity.IMG_URI);
            Log.i("TAG", "返回的File结果：" + CameraActivity.IMG_File.getPath());
            CameraActivity.LISTENING = false;
            Glide.with(AddActivity.this).load(CameraActivity.IMG_URI).into(imageView);


        try {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {

    OkHttpClient client = new OkHttpClient().newBuilder()
            .build();
    MediaType mediaType = MediaType.parse("text/plain");
    RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("img", CameraActivity.IMG_File.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream"),
                            new File(CameraActivity.IMG_File.getPath())))
            .build();
    Request request = new Request.Builder()
            .url("http://49.232.214.94/api/upload/good")
            .method("POST", body)
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
                    Toast.makeText(AddActivity.this,"图片上传失败，请检查网络，重新上传！",Toast.LENGTH_SHORT).show();

                }
            });
        }
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            try {

                JSONObject jsonObject=new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                hash=jsonObject1.getString("hash");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });  }
        });
        thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
}


}
}