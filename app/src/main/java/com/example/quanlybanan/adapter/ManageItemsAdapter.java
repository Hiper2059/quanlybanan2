package com.example.quanlybanan.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quanlybanan.R;
import com.example.quanlybanan.model.MenuItemObject;

import java.util.ArrayList;

public class ManageItemsAdapter extends RecyclerView.Adapter<ManageItemsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<MenuItemObject> menuItems;
    private OnItemListener listener;

    public interface OnItemListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public ManageItemsAdapter(Context context, ArrayList<MenuItemObject> menuItems, OnItemListener listener) {
        this.context = context;
        this.menuItems = menuItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItemObject item = menuItems.get(position);
        holder.txtItemName.setText(item.getName());
        holder.txtItemPrice.setText(item.getPrice() + " đ");

        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            // SỬA DÒNG DƯỚI ĐÂY
            // Dòng cũ: .load(Uri.fromFile(new File(item.getImagePath())))
            // Dòng mới:
            Glide.with(context)
                    .load(Uri.parse(item.getImagePath())) // SỬA THÀNH Uri.parse()
                    .placeholder(R.drawable.ic_table)
                    .error(R.drawable.ic_table)
                    .into(holder.imgItem);
        } else {
            holder.imgItem.setImageResource(R.drawable.ic_table);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(holder.getAdapterPosition()));
        holder.btnDeleteItem.setOnClickListener(v -> listener.onDeleteClick(holder.getAdapterPosition()));
    }
    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem, btnDeleteItem;
        TextView txtItemName, txtItemPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgItem);
            btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);
            txtItemName = itemView.findViewById(R.id.txtItemName);
            txtItemPrice = itemView.findViewById(R.id.txtItemPrice);
        }
    }
}