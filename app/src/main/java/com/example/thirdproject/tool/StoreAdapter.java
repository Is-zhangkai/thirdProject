package com.example.thirdproject.tool;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thirdproject.ChangeGoodActivity;
import com.example.thirdproject.GoodsActivity;
import com.example.thirdproject.R;

import java.util.List;
import java.util.regex.Pattern;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {
    private Context context;
    private List<Data> list;
    private Pattern httpPattern;
    public StoreAdapter(Context context, List<Data> list){
        this.list=list;
        this.context=context;

    }
    public void addData(List<Data> addList){
        if (addList!=null){
            list.addAll(addList);
            notifyItemRangeChanged(list.size()-addList.size(),addList.size());
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_shopping, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {

        holder.name.setText(list.get(i).getName());
        holder.price.setText("￥ "+list.get(i).getPrice());
        httpPattern = Pattern
                .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~/])+$");

        if (httpPattern.matcher(list.get(i).getImg()).matches()) {
            Glide.with(context).load(list.get(i).getImg()).into(holder.imageView);
        }else{
            Glide.with(context).load("http://49.232.214.94/api/img/"+list.get(i).getImg()).into(holder.imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChangeGoodActivity.class);
                intent.putExtra("id",list.get(i).getGood_id());
                intent.putExtra("name",list.get(i).getName());
                intent.putExtra("price",list.get(i).getPrice());
                intent.putExtra("quantity",list.get(i).getQuantity());
                intent.putExtra("info",list.get(i).getInfo());
                intent.putExtra("img",list.get(i).getImg());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,price;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.item_name);
            price=itemView.findViewById(R.id.item_price);
            imageView=itemView.findViewById(R.id.item_img);

    }
}}
