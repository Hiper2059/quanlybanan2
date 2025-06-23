package com.example.quanlybanan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlybanan.R;
import com.example.quanlybanan.adapter.TableAdapter;
import com.example.quanlybanan.database.DBHelper;
import com.example.quanlybanan.model.Item;
import com.example.quanlybanan.model.Table;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TableAdapter adapter;
    ArrayList<Table> tableList;
    DBHelper db;
    FloatingActionButton btnAdd;
    SharedPreferences prefs;
    int userId, isOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        isOwner = prefs.getInt("isOwner", 0);

        if (userId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);
        db = new DBHelper(this);

        tableList = new ArrayList<>();
        adapter = new TableAdapter(tableList, new TableAdapter.OnTableListener() {
            @Override
            public void onEdit(int position) {
                showEditDialog(position);
            }
            @Override
            public void onDelete(int position) {
                deleteTable(position);
            }
            @Override
            public void onSelectMenu(int position) {
                handleTableSelection(position);
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> showAddDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTables();
    }

    private void loadTables() {
        tableList.clear();
        Cursor cursor = db.getAllTables();
        if (cursor != null) {
            int idCol = cursor.getColumnIndex("id");
            int seatsCol = cursor.getColumnIndex("seats");
            int statusCol = cursor.getColumnIndex("status");

            while (cursor.moveToNext()) {
                int id = cursor.getInt(idCol);
                int seats = cursor.getInt(seatsCol);
                String status = cursor.getString(statusCol);
                tableList.add(new Table(id, seats, status));
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void handleTableSelection(int position) {
        Table selectedTable = tableList.get(position);

        // SỬA DÒNG NÀY: Dùng equalsIgnoreCase để không phân biệt hoa/thường
        if ("Đang có khách".equalsIgnoreCase(selectedTable.getStatus())) {
            Toast.makeText(this, "Đang tải lại đơn hàng của bàn " + selectedTable.getId(), Toast.LENGTH_SHORT).show();

            ArrayList<Item> savedItems = db.getItemsForTable(selectedTable.getId());
            selectedTable.setItems(savedItems);
            Log.d("MainActivity", "Bàn " + selectedTable.getId() + " có " + savedItems.size() + " món đã lưu.");
        }

        Intent intent = new Intent(MainActivity.this, SelectMenuActivity.class);
        intent.putExtra("selectedTable", selectedTable);
        startActivity(intent);
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_table, null);
        EditText edtSeats = dialogView.findViewById(R.id.edtSeats);
        EditText edtStatus = dialogView.findViewById(R.id.edtStatus);

        // Giờ đây EditText sẽ trống và cho phép người dùng nhập tự do

        new AlertDialog.Builder(this)
                .setTitle("Thêm bàn mới")
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String seatsStr = edtSeats.getText().toString().trim();
                    // Lấy trạng thái từ người dùng nhập
                    String status = edtStatus.getText().toString().trim();

                    // Kiểm tra cả hai trường không được trống
                    if (!seatsStr.isEmpty() && !status.isEmpty()) {
                        int seats = Integer.parseInt(seatsStr);
                        // Sử dụng trạng thái người dùng nhập
                        db.insertTable(seats, status);
                        loadTables();
                    } else {
                        // Cập nhật lại thông báo cho rõ ràng
                        Toast.makeText(this, "Vui lòng điền đủ thông tin!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void showEditDialog(int position) {
        Table table = tableList.get(position);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_table, null);
        EditText edtSeats = dialogView.findViewById(R.id.edtSeats);
        EditText edtStatus = dialogView.findViewById(R.id.edtStatus);

        edtSeats.setText(String.valueOf(table.getSeats()));
        edtStatus.setText(table.getStatus());

        new AlertDialog.Builder(this)
                .setTitle("Sửa thông tin bàn")
                .setView(dialogView)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String seatsStr = edtSeats.getText().toString().trim();
                    String status = edtStatus.getText().toString().trim();
                    if (!seatsStr.isEmpty() && !status.isEmpty()) {
                        int seats = Integer.parseInt(seatsStr);
                        db.updateTable(table.getId(), seats, status);
                        loadTables();
                    } else {
                        Toast.makeText(this, "Điền đủ thông tin!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void deleteTable(int position) {
        Table table = tableList.get(position);
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xoá bàn?")
                .setMessage("Bạn chắc chắn muốn xoá bàn " + table.getId() + "?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    db.deleteTable(table.getId());
                    db.clearTableItems(table.getId());
                    loadTables();
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (isOwner == 0) {
            menu.findItem(R.id.menu_manage_items).setVisible(false);
            menu.findItem(R.id.menu_account).setVisible(false);
            menu.findItem(R.id.menu_revenue_report).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_logout) {
            prefs.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else if (itemId == R.id.menu_change_password) {
            showChangePasswordDialog();
            return true;
        } else if (itemId == R.id.menu_account && isOwner == 1) {
            startActivity(new Intent(this, AccountManagerActivity.class));
            return true;
        } else if (itemId == R.id.menu_revenue_report && isOwner == 1) {
            startActivity(new Intent(this, RevenueReportActivity.class));
            return true;
        } else if (itemId == R.id.menu_manage_items && isOwner == 1) {
            startActivity(new Intent(this, ManageItemsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showChangePasswordDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);
        EditText edtNew = view.findViewById(R.id.edtNewPass);

        new AlertDialog.Builder(this)
                .setTitle("Đổi mật khẩu")
                .setView(view)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String newPass = edtNew.getText().toString().trim();
                    if (!newPass.isEmpty()) {
                        db.changePassword(userId, newPass);
                        Toast.makeText(this, "Đã đổi mật khẩu!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Mật khẩu mới không được để trống!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }
}