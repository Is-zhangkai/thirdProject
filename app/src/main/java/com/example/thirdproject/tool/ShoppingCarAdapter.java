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
import com.example.thirdproject.GoodsActivity;
import com.example.thirdproject.R;

import java.util.List;
import java.util.regex.Pattern;

public class ShoppingCarAdapter extends RecyclerView.Adapter<ShoppingCarAdapter.ViewHolder> {
    private Context context;
    private List<Data> list;
    private Pattern httpPattern;
    public ShoppingCarAdapter(Context context, List<Data> list) {
        this.list = list;
        this.context = context;
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_car, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {

        holder.name.setText(list.get(i).getName());
        holder.price.setText("￥"+list.get(i).getPrice());
        holder.count.setText("数量："+list.get(i).getGoods_count());
        httpPattern = Pattern
                .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~/])+$");

        if (httpPattern.matcher(list.get(i).getImg()).matches()) {
            Glide.with(context).load(list.get(i).getImg()).into(holder.imageView);
        }else{
            Glide.with(context).load("http://49.232.214.94/api/img/"+list.get(i).getImg()).into(holder.imageView);
        }

        final int id=list.get(i).getGood_id();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, GoodsActivity.class);
                intent.putExtra("id",id);
                v.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,price,count;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.item_car_name);
            price=itemView.findViewById(R.id.item_car_price);
            count=itemView.findViewById(R.id.item_car_quantity);
            imageView=itemView.findViewById(R.id.item_car_img);
        }

    }
}
