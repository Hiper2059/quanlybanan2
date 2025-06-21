package com.example.quanlybanan.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quanlybanan.R;
import com.example.quanlybanan.model.Item;
import com.example.quanlybanan.model.Table;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    // << BƯỚC 1: ĐỊNH NGHĨA LISTENER INTERFACE >>
    // Bất kỳ class nào (như SelectMenuActivity) muốn lắng nghe sự kiện từ Adapter này
    // sẽ phải implement interface này.
    public interface OnOrderUpdateListener {
        void onOrderUpdated(); // Một phương thức đơn giản để thông báo "đơn hàng đã thay đổi"
    }

    private List<Item> menuItems; // Danh sách toàn bộ menu của quán
    private Table table; // Bàn ăn hiện tại, chứa các món đã chọn
    private OnOrderUpdateListener updateListener; // << BƯỚC 2: KHAI BÁO BIẾN LISTENER >>

    // << BƯỚC 3: SỬA CONSTRUCTOR ĐỂ NHẬN LISTENER >>
    public RecyclerViewAdapter(List<Item> items, Table table, OnOrderUpdateListener listener) {
        this.menuItems = items;
        this.table = table;
        this.updateListener = listener; // Lưu listener được truyền vào
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Lấy món ăn từ danh sách menu của quán
        Item menuItem = menuItems.get(position);

        holder.itemName.setText(menuItem.getName());
        holder.itemPrice.setText(menuItem.getPrice() + " đ");

        // Ghi chú: TextView itemQuantity ở đây có thể gây nhầm lẫn
        // vì nó hiển thị số lượng của món trong menu, không phải trong đơn hàng.
        // Tốt nhất là nên hiển thị số lượng ở danh sách "Món đã chọn".
        // Tạm thời, chúng ta sẽ làm cho nó hiển thị số lượng của món đó trong đơn hàng hiện tại.
        holder.itemQuantity.setText("Đã chọn: " + table.getQuantityOfItem(menuItem.getName()));

        // Dùng Glide để load ảnh
        if (menuItem.getImagePath() != null && !menuItem.getImagePath().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(menuItem.getImagePath()))
                    .placeholder(R.drawable.ic_table)
                    .error(R.drawable.ic_table)
                    .into(holder.itemImage);
        } else {
            holder.itemImage.setImageResource(R.drawable.ic_table);
        }

        // --- SỬA LOGIC CÁC NÚT BẤM ---
        holder.btnIncrease.setOnClickListener(v -> {
            // Dùng một hàm trong 'Table' để xử lý việc tăng số lượng
            table.increaseItemQuantity(menuItem);

            // << BƯỚC 4: GỌI LISTENER ĐỂ THÔNG BÁO CHO ACTIVITY >>
            if (updateListener != null) {
                updateListener.onOrderUpdated();
            }
            // Cập nhật lại số lượng hiển thị trên chính item này
            notifyItemChanged(position);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            // Dùng một hàm trong 'Table' để xử lý việc giảm số lượng
            table.decreaseItemQuantity(menuItem);

            // << BƯỚC 4: GỌI LISTENER ĐỂ THÔNG BÁO CHO ACTIVITY >>
            if (updateListener != null) {
                updateListener.onOrderUpdated();
            }
            // Cập nhật lại số lượng hiển thị trên chính item này
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice, itemQuantity;
        Button btnIncrease, btnDecrease;
        ImageView itemImage;
        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            itemImage = itemView.findViewById(R.id.itemImage);
        }
    }
}