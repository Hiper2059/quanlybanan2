package com.example.quanlybanan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlybanan.R;
import com.example.quanlybanan.database.DBHelper;

public class LoginActivity extends AppCompatActivity {
    DBHelper db;
    EditText edtUser, edtPass;
    Button btnLogin;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DBHelper(this);
        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);
        prefs = getSharedPreferences("user_session", MODE_PRIVATE);

        // Tạo tài khoản chủ mặc định nếu chưa có
        Cursor curOwner = db.getReadableDatabase().rawQuery("SELECT * FROM users WHERE is_owner=1", null);
        if (curOwner.getCount() == 0) {
            db.insertUser("admin", "admin", 1); // username, password, is_owner=1
        }
        curOwner.close();

        btnLogin.setOnClickListener(v -> {
            String u = edtUser.getText().toString().trim();
            String p = edtPass.getText().toString().trim();
            Cursor c = db.login(u, p);
            if (c.moveToFirst()) {
                // --- Kiểm tra tên các cột có thực sự tồn tại không ---
                int idxId = c.getColumnIndex("id");
                int idxOwner = c.getColumnIndex("is_owner");

                if (idxId == -1 || idxOwner == -1) {
                    // In ra các cột thực tế cho debug
                    StringBuilder colList = new StringBuilder();
                    for (int i = 0; i < c.getColumnCount(); i++) {
                        colList.append(c.getColumnName(i)).append(", ");
                    }
                    Toast.makeText(this, "Lỗi cột DB! Các cột có: " + colList, Toast.LENGTH_LONG).show();
                    c.close();
                    return;
                }

                int userId = c.getInt(idxId);
                int isOwner = c.getInt(idxOwner);

                prefs.edit()
                        .putInt("userId", userId)
                        .putInt("isOwner", isOwner)
                        .putString("username", u)
                        .apply();

                c.close();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                c.close();
            }
        });
    }
}
