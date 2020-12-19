package com.example.thirdproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.thirdproject.tool.BottomPopupOption;
import com.example.thirdproject.tool.CameraActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

import kotlin.text.Regex;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangeGoodActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private String name, info, img, token;
    private TextView tv_name, tv_price, tv_quantity, tv_info;
    private ImageView imageView;
    private int good_id, quantity;
    private double price;
    private Pattern httpPattern;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_change_good);
        Button back = findViewById(R.id.change_good_back);
        Button save = findViewById(R.id.change_good_save);
        tv_name = findViewById(R.id.change_good_name);
        tv_price = findViewById(R.id.change_good_price);
        tv_quantity = findViewById(R.id.change_good_quantity);
        tv_info = findViewById(R.id.change_good_info);
        imageView = findViewById(R.id.change_good_img);
        LinearLayout layout_name = findViewById(R.id.layout_good_name);
        LinearLayout layout_price = findViewById(R.id.layout_good_price);
        LinearLayout layout_quantity = findViewById(R.id.layout_good_quantity);
        LinearLayout layout_info = findViewById(R.id.layout_good_info);

        sharedPreferences = Objects.requireNonNull(getSharedPreferences("token", MODE_PRIVATE));
        token = sharedPreferences.getString("token", "null");

        good_id = getIntent().getIntExtra("id", 0);
        name = getIntent().getStringExtra("name");
        price = getIntent().getDoubleExtra("price", 0);
        quantity = getIntent().getIntExtra("quantity", 0);
        info = getIntent().getStringExtra("info");
        img = getIntent().getStringExtra("img");

        tv_name.setText(name);
        tv_price.setText(price + "");
        tv_quantity.setText(quantity + "");
        tv_info.setText(info);
        httpPattern = Pattern
                .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~/])+$");

        if (httpPattern.matcher(img).matches()) {
            Glide.with(ChangeGoodActivity.this).load(img).into(imageView);
        }else{
            Glide.with(ChangeGoodActivity.this).load("http://49.232.214.94/api/img/"+img).into(imageView);
        }
//返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//保存
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final JSONObject jsonObject=new JSONObject();

                    jsonObject.put("name",name);
                    jsonObject.put("quantity",quantity);
                    jsonObject.put("price",price);
                    jsonObject.put("info",info);
                    jsonObject.put("good_id",good_id);
                    jsonObject.put("img",img);

                    Thread thread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient client = new OkHttpClient().newBuilder()
                                    .build();
                            MediaType mediaType = MediaType.parse("application/json");
                            RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                            Request request = new Request.Builder()
                                    .url("http://49.232.214.94/api/good")
                                    .method("PUT", body)
                                    .addHeader("Accept", "application/json")
                                    .addHeader("Authorization", token)
                                    .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                                    .addHeader("Content-Type", "application/json")
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    try {

                                        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                                        final String msg = jsonObject.getString("msg");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ChangeGoodActivity.this, msg, Toast.LENGTH_SHORT).show();
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
        });
//换图片
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomPopupOption bottomPopupOption = new BottomPopupOption(ChangeGoodActivity.this);
                bottomPopupOption.setItemText("拍照", "选择相册");
                bottomPopupOption.showPopupWindow();
                bottomPopupOption.setItemClickListener(new BottomPopupOption.onPopupWindowItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                        if (position == 0) {
                            startActivity(new Intent(ChangeGoodActivity.this, CameraActivity.class).putExtra(CameraActivity.ExtraType, CameraActivity.CAMERA));
                            bottomPopupOption.dismiss();
                        } else {
                            startActivity(new Intent(ChangeGoodActivity.this, CameraActivity.class).putExtra(CameraActivity.ExtraType, CameraActivity.PHOTO));
                            bottomPopupOption.dismiss();
                        }
                    }
                });
            }
        });
//改数据


        layout_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(ChangeGoodActivity.this);
                final AlertDialog.Builder builder = new AlertDialog.Builder(ChangeGoodActivity.this);
                builder.setTitle("输入名称").setIcon(R.drawable.write).setView(input)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (TextUtils.isEmpty(input.getText())) {
                            Toast.makeText(ChangeGoodActivity.this, "未输入内容！", Toast.LENGTH_SHORT).show();
                        }else {name = input.getText().toString();
                            tv_name.setText(name);}
                    }
                });
                builder.show();
            }
        });


        layout_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(ChangeGoodActivity.this);
                input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                final AlertDialog.Builder builder = new AlertDialog.Builder(ChangeGoodActivity.this);
                builder.setTitle("输入价格").setIcon(R.drawable.write).setView(input)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (TextUtils.isEmpty(input.getText())) {
                            Toast.makeText(ChangeGoodActivity.this, "未输入内容！", Toast.LENGTH_SHORT).show();
                        }else {
                            try {
                                price = Double.parseDouble(input.getText().toString());
                                tv_price.setText(price + "");
                            } catch (Exception e){
                                Toast.makeText(ChangeGoodActivity.this, "输入格式错误！", Toast.LENGTH_SHORT).show();
                            }

                           }
                    }
                });
                builder.show();
            }
        });

        layout_quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(ChangeGoodActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                final AlertDialog.Builder builder = new AlertDialog.Builder(ChangeGoodActivity.this);
                builder.setTitle("输入数量").setIcon(R.drawable.write).setView(input)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (TextUtils.isEmpty(input.getText())) {
                            Toast.makeText(ChangeGoodActivity.this, "未输入内容！", Toast.LENGTH_SHORT).show();
                        }else {quantity = Integer.parseInt(input.getText().toString());
                            tv_quantity.setText(quantity+"");}
                    }
                });
                builder.show();
            }
        });



        layout_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(ChangeGoodActivity.this);
                final AlertDialog.Builder builder = new AlertDialog.Builder(ChangeGoodActivity.this);
                builder.setTitle("输入简介").setIcon(R.drawable.write).setView(input)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (TextUtils.isEmpty(input.getText())) {
                            Toast.makeText(ChangeGoodActivity.this, "未输入内容！", Toast.LENGTH_SHORT).show();
                        }else {info = input.getText().toString();
                            tv_info.setText(info);}
                    }
                });
                builder.show();
            }
        });





    }


    @Override
    protected void onResume() {
        super.onResume();
        //获得相册、相机返回的结果，并显示
        if (CameraActivity.LISTENING) {
            Log.i("asd", "返回的name结果：" + CameraActivity.IMG_File.getName());
            Log.i("asd", "返回的File结果：" + CameraActivity.IMG_File.getPath());

            Glide.with(ChangeGoodActivity.this).load(CameraActivity.IMG_URI).into(imageView);

            try {
                Thread thread = new Thread(new Runnable() {
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
                                        Toast.makeText(ChangeGoodActivity.this, "图片上传失败，请检查网络，重新上传！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                try {
                                    JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                                    final String msg = jsonObject.getString("msg");
                                    final JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                    img = jsonObject1.getString("hash");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ChangeGoodActivity.this, msg, Toast.LENGTH_SHORT).show();
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
            }CameraActivity.LISTENING = false;
        }
    }
}
