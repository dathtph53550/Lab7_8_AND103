package com.example.and103_lab5;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.and103_lab5.model.Fruit;

public class DetailProduct extends AppCompatActivity {

    private int quantity = 1;
    ImageView btnBack;
    ImageView cakeImage;
    TextView cakeName;
    TextView description;
    TextView price;
    Button btnPlaceOrder;
    Button btnAddToCart;
    private Fruit fruit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //anh xa
        btnBack = findViewById(R.id.btnBack);

        cakeImage = findViewById(R.id.cakeImage);
        cakeName = findViewById(R.id.cakeName);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);


        Intent intent = getIntent();
        fruit = (Fruit) intent.getSerializableExtra("fruit");
        Log.d("aaaaaa", "getDataIntent: "+fruit.getImage().get(0));
        Log.d("aaaa", "onCreate: " + fruit.getName() + " -  " + fruit.getDescription() + fruit.getPrice());

        cakeName.setText(fruit.getName());
        description.setText(fruit.getDescription());
        price.setText ( "Price: "  + fruit.getPrice());
        Glide.with(this)
                .load(fruit.getImage().get(0)) // URL của ảnh
                .placeholder(R.drawable.baseline_broken_image_24) // Ảnh hiển thị khi đang tải
                .error(R.drawable.baseline_file_upload_off_24) // Ảnh hiển thị khi xảy ra lỗi
                .into(cakeImage);

        TextView quantityText = findViewById(R.id.quantity);
        Button btnPlus = findViewById(R.id.btnPlus);
        Button btnMinus = findViewById(R.id.btnMinus);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);

        btnPlus.setOnClickListener(v -> {
            quantity++;
            quantityText.setText(String.valueOf(quantity));
        });

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityText.setText(String.valueOf(quantity));
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("fruit", fruit);
                Intent intent = new Intent(DetailProduct.this, Location2.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}