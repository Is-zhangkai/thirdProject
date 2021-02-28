package com.example.thirdproject.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thirdproject.tool.Data;
import com.example.thirdproject.R;
import com.example.thirdproject.tool.ShoppingAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
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

public class ShoppingFragment extends Fragment {

    private RecyclerView recyclerView;
    private ShoppingAdapter adapter;
    private int page=0;
    private  int pageSize=6;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        SmartRefreshLayout smartRefreshLayout = view.findViewById(R.id.shopping_srl);
        recyclerView = view.findViewById(R.id.shopping_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final List<Data> list=new ArrayList<>();
        GetData(list);



        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                final List<Data> list=new ArrayList<>();
                GetData(list);
                refreshLayout.finishLoadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page=0;
                final List<Data> list=new ArrayList<>();
                GetData(list);
                refreshLayout.finishRefresh();
            }
        });



    }

/////////////////////////////////////////////////////////////////////



public void GetData(final List<Data> list){
        try {

            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    Request request = new Request.Builder()
                            .url("http://49.232.214.94/api/goods")
                            .method("GET", null)
                            .addHeader("Accept", "application/json")
                            .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "连接网络失败，请检查网络！", Toast.LENGTH_SHORT).show();
                                    Log.i("asd","khfdkhgsfjfkhkasj");
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            try {page++;
                                JSONObject jsonObject1 = new JSONObject(Objects.requireNonNull(response.body()).string());
                                    JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                                    JSONArray jsonArray = jsonObject2.getJSONArray("goods");
                                    if (page*pageSize<jsonArray.length()){
                                    for (int i = (page-1)*pageSize; i < page*pageSize; i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        Data data = new Data();
                                        data.setGood_id(jsonObject.getInt("good_id"));
                                        data.setImg(jsonObject.getString("img"));
                                        data.setPrice(jsonObject.getDouble("price"));
                                        data.setName(jsonObject.getString("name"));
                                        data.setQuantity(jsonObject.getInt("quantity"));
                                        list.add(data);
                                    }
                                    }else {
                                        for (int i =(page-1)* pageSize; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            Data data = new Data();
                                            data.setGood_id(jsonObject.getInt("good_id"));
                                            data.setImg(jsonObject.getString("img"));
                                            data.setPrice(jsonObject.getDouble("price"));
                                            data.setName(jsonObject.getString("name"));
                                            data.setQuantity(jsonObject.getInt("quantity"));
                                            list.add(data);
                                        }
                                    }
                                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (page==1) {
                                                    adapter=new ShoppingAdapter(getContext(), list);
                                                    recyclerView.setAdapter(adapter);
                                                }else {
                                                    adapter.addData(list);
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
        }catch (Exception e) {
            e.printStackTrace();
        }

}

}
