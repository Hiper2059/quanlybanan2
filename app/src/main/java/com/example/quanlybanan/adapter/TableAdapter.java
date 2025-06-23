package com.example.quanlybanan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlybanan.R;
import com.example.quanlybanan.model.Table;

import java.util.ArrayList;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {
    private ArrayList<Table> tables;
    private OnTableListener listener;

    public interface OnTableListener {
        void onEdit(int position);
        void onDelete(int position);
        void onSelectMenu(int position); // Thêm phương thức để chọn món
    }

    public TableAdapter(ArrayList<Table> tables, OnTableListener listener) {
        this.tables = tables;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_table_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Table table = tables.get(position);
        holder.txtId.setText("ID: " + table.getId());
        holder.txtSeats.setText("Seats: " + table.getSeats());
        holder.txtStatus.setText("Status: " + table.getStatus());

        holder.itemView.setOnLongClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(holder.itemView.getContext());
            builder.setTitle("Chọn thao tác")
                    .setItems(new CharSequence[]{"Sửa", "Xoá", "Chọn món"}, (dialog, which) -> {
                        if (which == 0) listener.onEdit(position);
                        else if (which == 1) listener.onDelete(position);
                        else listener.onSelectMenu(position); // Khi chọn món
                    })
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtId, txtSeats, txtStatus;
        public ViewHolder(View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id  .txtId);
            txtSeats = itemView.findViewById(R.id.txtSeats);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }
}
