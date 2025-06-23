package com.example.quanlybanan.activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlybanan.adapter.ManagerAdapter;
import com.example.quanlybanan.R;
import com.example.quanlybanan.model.User;
import com.example.quanlybanan.database.DBHelper;

import java.util.ArrayList;

public class AccountManagerActivity extends AppCompatActivity {
    DBHelper db;
    ArrayList<User> managers;
    ManagerAdapter managerAdapter;
    RecyclerView recyclerManagers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);

        db = new DBHelper(this);

        recyclerManagers = findViewById(R.id.recyclerManagers);
        recyclerManagers.setLayoutManager(new LinearLayoutManager(this));
        managers = new ArrayList<>();
        managerAdapter = new ManagerAdapter(managers, new ManagerAdapter.OnManagerListener() {
            @Override
            public void onEdit(int pos) { showEditDialog(pos); }
            @Override
            public void onDelete(int pos) { deleteManager(pos); }
        });
        recyclerManagers.setAdapter(managerAdapter);
        loadManagers();

        findViewById(R.id.btnAddManager).setOnClickListener(v -> showAddDialog());
    }

    private void loadManagers() {
        managers.clear();
        Cursor c = db.getAllManagers();
        if (c == null) return;
        int idxId = c.getColumnIndex("id");
        int idxUser = c.getColumnIndex("username");
        if (idxId == -1 || idxUser == -1) {
            Toast.makeText(this, "Lỗi tên cột DB!", Toast.LENGTH_LONG).show();
            c.close();
            return;
        }
        while (c.moveToNext()) {
            int id = c.getInt(idxId);
            String username = c.getString(idxUser);
            managers.add(new User(id, username));
        }
        c.close();
        managerAdapter.notifyDataSetChanged();
    }

    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_manager, null);
        EditText edtUser = view.findViewById(R.id.edtManagerUser);
        EditText edtPass = view.findViewById(R.id.edtManagerPass);

        new AlertDialog.Builder(this)
                .setTitle("Thêm tài khoản quản lý")
                .setView(view)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String u = edtUser.getText().toString().trim();
                    String p = edtPass.getText().toString().trim();
                    if (!u.isEmpty() && !p.isEmpty()) {
                        long kq = db.insertUser(u, p, 0);
                        if (kq == -1) {
                            Toast.makeText(this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
                        }
                        loadManagers();
                    } else {
                        Toast.makeText(this, "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void showEditDialog(int pos) {
        User m = managers.get(pos);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);
        EditText edtNew = view.findViewById(R.id.edtNewPass);
        new AlertDialog.Builder(this)
                .setTitle("Đổi mật khẩu quản lý: " + m.getUsername())
                .setView(view)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String newPass = edtNew.getText().toString().trim();
                    if (!newPass.isEmpty()) {
                        db.changePassword(m.getId(), newPass);
                        Toast.makeText(this, "Đã đổi mật khẩu!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void deleteManager(int pos) {
        User m = managers.get(pos);
        new AlertDialog.Builder(this)
                .setTitle("Xoá tài khoản quản lý?")
                .setMessage("Bạn chắc chắn xoá tài khoản " + m.getUsername() + " ?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    db.deleteUser(m.getId());
                    loadManagers();
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    //
}