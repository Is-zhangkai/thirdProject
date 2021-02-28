package com.example.thirdproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserDataActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private String name,account,info,head,token;
    private int user_id;
    private Boolean sex;
    private ImageView imageView;
    private TextView tv_account,tv_name,tv_sex,tv_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_data);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        imageView=findViewById(R.id.change_head);
         tv_account=findViewById(R.id.change_account);
         tv_name=findViewById(R.id.change_name);
         tv_sex=findViewById(R.id.change_sex);
         tv_info=findViewById(R.id.change_info);
        LinearLayout layout_account=findViewById(R.id.layout_account);
        LinearLayout layout_name=findViewById(R.id.layout_name);
        LinearLayout layout_sex=findViewById(R.id.layout_sex);
        LinearLayout layout_info=findViewById(R.id.layout_info);

        Button button_back=findViewById(R.id.change_back);
        Button button_save=findViewById(R.id.change_save);

        sharedPreferences= Objects.requireNonNull(getSharedPreferences("token",MODE_PRIVATE));
        token=sharedPreferences.getString("token","null");

        //获取信息
        GET();


        Glide.with(UserDataActivity.this).load(R.drawable.user)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(imageView);



//返回按钮
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//保存按钮
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Thread thread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final JSONObject jsonObject=new JSONObject();
                                jsonObject.put("name",name);
                                jsonObject.put("sex",sex);
                                jsonObject.put("info",info);
                                OkHttpClient client = new OkHttpClient().newBuilder()
                                        .build();
                                MediaType mediaType = MediaType.parse("application/json");
                                RequestBody body = RequestBody.create(mediaType, String.valueOf(jsonObject));
                                Request request = new Request.Builder()
                                        .url("http://49.232.214.94/api/user")
                                        .method("PUT", body)
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
                                              Toast.makeText(UserDataActivity.this, "未连接网络，请检查后重试！", Toast.LENGTH_SHORT).show();
                                          }
                                      });
                                  }
                                  @Override
                                  public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                      try {
                                          final JSONObject jsonObject1=new JSONObject(response.body().string());
                                          final String msg=jsonObject1.getString("msg");
                                          runOnUiThread(new Runnable() {
                                              @Override
                                              public void run() {
                                                  Toast.makeText(UserDataActivity.this, msg, Toast.LENGTH_SHORT).show();
                                              }
                                          });
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
                    thread.start();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
//换头像
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomPopupOption bottomPopupOption = new BottomPopupOption(UserDataActivity.this);
                bottomPopupOption.setItemText("拍照", "选择相册");
                bottomPopupOption.showPopupWindow();
                bottomPopupOption.setItemClickListener(new BottomPopupOption.onPopupWindowItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                        if (position==0){
                            startActivity(new Intent(UserDataActivity.this, CameraActivity.class).putExtra(CameraActivity.ExtraType, CameraActivity.CAMERA));
                            bottomPopupOption.dismiss();
                        }else {
                            startActivity(new Intent(UserDataActivity.this, CameraActivity.class).putExtra(CameraActivity.ExtraType, CameraActivity.PHOTO));
                            bottomPopupOption.dismiss();
                        }
                    }
                });
            }
        });
//更改信息

        layout_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserDataActivity.this, "账号不可更改！", Toast.LENGTH_SHORT).show();

            }
        });

        layout_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(UserDataActivity.this);
                final AlertDialog.Builder builder = new AlertDialog.Builder(UserDataActivity.this);
                builder.setTitle("输入新昵称").setIcon(R.drawable.write).setView(input)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (TextUtils.isEmpty(input.getText())) {
                            Toast.makeText(UserDataActivity.this, "未输入内容！", Toast.LENGTH_SHORT).show();
                        }else {name = input.getText().toString();
                            tv_name.setText(name);}
                    }
                    });
                builder.show();
                }
            });


        layout_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(UserDataActivity.this);
                final AlertDialog.Builder builder = new AlertDialog.Builder(UserDataActivity.this);
                builder.setTitle("输入个性签名").setIcon(R.drawable.write).setView(input)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (TextUtils.isEmpty(input.getText())) {
                            Toast.makeText(UserDataActivity.this, "未输入内容！", Toast.LENGTH_SHORT).show();
                        }else {info = input.getText().toString();
                            tv_info.setText(info);}
                    }
                });
                builder.show();
            }
        });

        layout_sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {"男", "女"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(UserDataActivity.this);
                builder.setTitle("性别");
                builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i==0){sex=true;tv_sex.setText("男");}else {sex=false;tv_sex.setText("女");} dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
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
            Log.i("TAG", "返回的Uri结果：" + CameraActivity.IMG_URI);
            Log.i("TAG", "返回的File结果：" + CameraActivity.IMG_File.getPath());
            CameraActivity.LISTENING = false;   //关闭获取结果
            Glide.with(UserDataActivity.this).load(CameraActivity.IMG_URI) .apply(RequestOptions.bitmapTransform(new CircleCrop())).into(imageView);

            try {
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient().newBuilder()
                                .build();
                        MediaType mediaType = MediaType.parse("text/plain");
                        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("img",CameraActivity.IMG_File.getName(),
                                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                                new File(CameraActivity.IMG_File.getPath())))
                                .build();
                        Request request = new Request.Builder()
                                .url("http://49.232.214.94/api/upload/head")
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
                                        Toast.makeText(UserDataActivity.this,"头像上传失败，请检查网络，重新上传！",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                                try {
                                    final JSONObject jsonObject=new JSONObject(response.body().string());
                                    final String s=jsonObject.getString("msg");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(UserDataActivity.this,s,Toast.LENGTH_SHORT).show();
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

    public void GET(){

        try {
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    Request request = new Request.Builder()
                            .url("http://49.232.214.94/api/user")
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
                                    Toast.makeText(UserDataActivity.this,"连接网络失败，请检查网络！",Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            try {
                                JSONObject jsonObject=new JSONObject(Objects.requireNonNull(response.body()).string());

                                    JSONObject jsonObject1=jsonObject.getJSONObject("data");
                                    JSONObject jsonObject2=jsonObject1.getJSONObject("user");

                                    user_id=jsonObject2.getInt("user_id");
                                    account=jsonObject2.getString("account");
                                    name=jsonObject2.getString("name");
                                    info=jsonObject2.getString("info");
                                    head=jsonObject2.getString("head");
                                    sex=jsonObject2.getBoolean("sex");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {



                                            if (sex){tv_sex.setText("男");}else {tv_sex.setText("女");}
                                            tv_account.setText(account);
                                            tv_name.setText(name);
                                            tv_info.setText(info);
                                            if (!head.equals("")){  Glide.with(UserDataActivity.this).load("http://49.232.214.94/api/img/"+head)
                                                    .apply(RequestOptions.bitmapTransform(new CircleCrop())).into(imageView);}
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