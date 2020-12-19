package com.example.thirdproject.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thirdproject.AddActivity;
import com.example.thirdproject.R;
import com.example.thirdproject.SignInActivity;
import com.example.thirdproject.tool.Data;
import com.example.thirdproject.tool.ErrorAdapter;
import com.example.thirdproject.tool.StoreAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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

public class StoreFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private StoreAdapter adapter;
    private ErrorAdapter errorAdapter;
    private int page=0;
    private  int pageSize=6;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_store,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button button=view.findViewById(R.id.store_add);
        SmartRefreshLayout smartRefreshLayout = view.findViewById(R.id.store_smart);
        recyclerView = view.findViewById(R.id.store_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        List<Data> list=new ArrayList<>();
        GetData(list);

        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                List<Data> list=new ArrayList<>();
                GetData(list);
                refreshLayout.finishLoadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page=0;
                List<Data> list=new ArrayList<>();
                GetData(list);
                refreshLayout.finishRefresh();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), AddActivity.class);
                startActivity(intent);
            }
        });
    }



    /////////////////////////////////////////////////////
    public void GetData(final List<Data> list){
        sharedPreferences= Objects.requireNonNull(getActivity()).getSharedPreferences("token",getActivity().MODE_PRIVATE);
        final String token=sharedPreferences.getString("token","null");
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    Request request = new Request.Builder()
                            .url("http://49.232.214.94/api/goods")
                            .method("GET", null)
                            .addHeader("Accept", "application/json")
                            .addHeader("Authorization", token)
                            .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                            public void run() {


                                                errorAdapter=new ErrorAdapter(getContext());
                                                recyclerView.setAdapter(adapter);


                                    Toast.makeText(getActivity(),"连接网络失败，请检查网络！",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {

                                    try {
                                        JSONObject jsonObject=new JSONObject(response.body().string());
                                        if (!jsonObject.getString("msg").equals("查询成功")){
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                            Toast.makeText(getActivity(),"验证过期，请重新登录！",Toast.LENGTH_SHORT).show();
                                            Intent intent=new Intent(getActivity(), SignInActivity.class);
                                            getActivity().startActivity(intent);
                                            getActivity().finish();    }
                                            });
                                        }else {
                                            page++;
                                            JSONObject jsonObject1=jsonObject.getJSONObject("data");
                                            JSONArray jsonArray = jsonObject1.getJSONArray("goods");
                                            if (page*pageSize<jsonArray.length()){
                                                for (int i = (page-1)*pageSize; i < page*pageSize; i++) {
                                                JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                                                Data data = new Data();
                                                data.setGood_id(jsonObject3.getInt("good_id"));
                                                data.setImg(jsonObject3.getString("img"));
                                                data.setPrice(jsonObject3.getDouble("price"));
                                                data.setName(jsonObject3.getString("name"));
                                                data.setQuantity(jsonObject3.getInt("quantity"));
                                                data.setInfo(jsonObject3.getString("info"));
                                                list.add(data);

                                                }}else {
                                                for (int i =(page-1)* pageSize; i < jsonArray.length(); i++){
                                                    JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                                                    Data data = new Data();
                                                    data.setGood_id(jsonObject3.getInt("good_id"));
                                                    data.setImg(jsonObject3.getString("img"));
                                                    data.setPrice(jsonObject3.getDouble("price"));
                                                    data.setName(jsonObject3.getString("name"));
                                                    data.setQuantity(jsonObject3.getInt("quantity"));
                                                    data.setInfo(jsonObject3.getString("info"));
                                                    list.add(data); }
                                            }
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (page==1){
                                                    adapter=new StoreAdapter(getContext(), list);
                                                    recyclerView.setAdapter(adapter);}else {
                                                        adapter.addData(list);
                                                    }
                                                }
                                            });
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }     }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
