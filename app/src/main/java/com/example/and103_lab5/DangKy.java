package com.example.and103_lab5;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DangKy extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextInputEditText txtHoTen,txtPass;
    Button btnDangKy,btnTroVe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ky);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtHoTen = findViewById(R.id.txtHoTen);
        txtPass = findViewById(R.id.txtPass);
        btnDangKy = findViewById(R.id.btnDangNhap);
        btnTroVe = findViewById(R.id.btnTroVe);


        mAuth = FirebaseAuth.getInstance();

        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtHoTen.getText().toString();
                String password = txtPass.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(DangKy.this, "Vui lòng nhập đầy đủ thông tin !!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(DangKy.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(DangKy.this, "Đăng ký thành công !! " +  email, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(DangKy.this, DangNhap.class));
                                } else {
                                    Log.w("zzzzz", "onComplete: ", task.getException());
                                    Toast.makeText(DangKy.this, "Đăng ký thất bại !!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        btnTroVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DangKy.this,DangNhap.class));
            }
        });
    }
}