// --- SỬA FILE ManagerAdapter.java ---
package com.example.quanlybanan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlybanan.R;
import com.example.quanlybanan.model.User;

import java.util.ArrayList;

public class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.ViewHolder> {
    // SỬA Ở ĐÂY: từ ManagerActivity.User thành User
    ArrayList<User> users;
    OnManagerListener listener;

    public interface OnManagerListener {
        void onEdit(int pos);
        void onDelete(int pos);
    }

    // SỬA Ở ĐÂY: từ ManagerActivity.User thành User
    public ManagerAdapter(ArrayList<User> users, OnManagerListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int vType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {
        // SỬA Ở ĐÂY: Dùng getter để lấy username
        h.txtUser.setText(users.get(pos).getUsername());

        h.itemView.setOnLongClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(h.itemView.getContext());
            builder.setTitle("Chọn thao tác")
                    .setItems(new CharSequence[]{"Đổi mật khẩu", "Xoá"}, (dialog, which) -> {
                        if (which == 0) listener.onEdit(pos);
                        else listener.onDelete(pos);
                    })
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() { return users.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUser;
        public ViewHolder(View v) {
            super(v);
            txtUser = v.findViewById(R.id.txtManagerUser);
        }
    }
}