package com.example.thirdproject.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.thirdproject.R;
import com.example.thirdproject.ShoppingCarActivity;
import com.example.thirdproject.SignInActivity;
import com.example.thirdproject.UserDataActivity;
import com.example.thirdproject.WelcomeActivity;
import com.example.thirdproject.tool.BottomPopupOption;
import com.example.thirdproject.tool.CameraActivity;


import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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

public class UserCenterFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private TextView tv_name,tv_info;
    private ImageView imageView;
    private String name,account,info,head,token;
    private int user_id;
    private Boolean sex;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_usercenter,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView=view.findViewById(R.id.user_photo);
        final LinearLayout user=view.findViewById(R.id.btn_user);
        LinearLayout car=view.findViewById(R.id.btn_layout_car);
        LinearLayout signOut=view.findViewById(R.id.btn_leave);
        tv_name=view.findViewById(R.id.user_name);
        tv_info=view.findViewById(R.id.user_info);
        sharedPreferences= Objects.requireNonNull(getActivity()).getSharedPreferences("token",getActivity().MODE_PRIVATE);
        Glide.with(Objects.requireNonNull(getContext())).load(R.drawable.user)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(imageView);

        GET();


        token=sharedPreferences.getString("token","null");

        user.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), UserDataActivity.class);
                startActivity(intent);
            }
        });

        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ShoppingCarActivity.class);
                startActivity(intent);
            }
        });

//退出登录
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().clear().apply();
                Intent intent=new Intent(getContext(), SignInActivity.class);
                startActivity(intent);
                Objects.requireNonNull(getActivity()).finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomPopupOption bottomPopupOption = new BottomPopupOption(getContext());
                bottomPopupOption.setItemText("拍照", "选择相册");
                bottomPopupOption.showPopupWindow();
                bottomPopupOption.setItemClickListener(new BottomPopupOption.onPopupWindowItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                        if (position==0){
                            startActivity(new Intent(getContext(), CameraActivity.class).putExtra(CameraActivity.ExtraType, CameraActivity.CAMERA));
                            bottomPopupOption.dismiss();
                        }else {
                            startActivity(new Intent(getContext(), CameraActivity.class).putExtra(CameraActivity.ExtraType, CameraActivity.PHOTO));
                            bottomPopupOption.dismiss();
                        }
                    }
                });
            }
        });

    }




    public void onResume() {
        super.onResume();

        //获得相册、相机返回的结果，并显示
        if (CameraActivity.LISTENING) {
            Log.i("TAG", "返回的Uri结果：" + CameraActivity.IMG_URI);
            Log.i("TAG", "返回的File结果：" + CameraActivity.IMG_File.getPath());
            CameraActivity.LISTENING = false;   //关闭获取结果
            //Glide.with(getContext()).load(CameraActivity.IMG_URI) .apply(RequestOptions.bitmapTransform(new CircleCrop())).into(imageView);

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
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(),"头像上传失败，请检查网络，重新上传！",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

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
                                        }
                                        @Override
                                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                            try {
                                                final JSONObject jsonObject1=new JSONObject(response.body().string());
                                                final String msg=jsonObject1.getString("msg");
                                                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                                       GET();
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
                    }
                });
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ////////////////////////////////////////
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(),"连接网络失败，请检查网络！",Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            try {
                                JSONObject jsonObject=new JSONObject(Objects.requireNonNull(response.body()).string());
                                if (!jsonObject.getString("msg").equals("获取成功")){
                                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(),"验证过期，请重新登录！",Toast.LENGTH_SHORT).show();
                                            Intent intent=new Intent(getActivity(), SignInActivity.class);
                                            getActivity().startActivity(intent);
                                            getActivity().finish();    }
                                    });
                                }else {
                                    JSONObject jsonObject1=jsonObject.getJSONObject("data");
                                    JSONObject jsonObject2=jsonObject1.getJSONObject("user");


                                    user_id=jsonObject2.getInt("user_id");
                                    account=jsonObject2.getString("account");
                                    name=jsonObject2.getString("name");
                                    info=jsonObject2.getString("info");
                                    head=jsonObject2.getString("head");
                                    sex=jsonObject2.getBoolean("sex");
                                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_name.setText(name);
                                            tv_info.setText(info);
                                            if (!head.equals("")){  Glide.with(Objects.requireNonNull(getContext())).load("http://49.232.214.94/api/img/"+head)
                                                    .apply(RequestOptions.bitmapTransform(new CircleCrop())).into(imageView);}
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
