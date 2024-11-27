package com.example.and103_lab5;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.and103_lab5.adapter.DistributorAdapter;
import com.example.and103_lab5.adapter.FruitAdapter;
import com.example.and103_lab5.model.Fruit;
import com.example.and103_lab5.model.Page;
import com.example.and103_lab5.model.Response;
import com.example.and103_lab5.services.HttpRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;

public class HomeActivity extends AppCompatActivity implements FruitAdapter.FruitClick {

    HttpRequest httpRequest;
    RecyclerView rycFruit;
    FruitAdapter adapter;
    SharedPreferences sharedPreferences;
    String token;
    FloatingActionButton btnThem;
    ArrayList<File> listImage = new ArrayList<>();
    ArrayList<Uri> imageUris = new ArrayList<>();
    private ProgressBar loadmore;
    ArrayList<Fruit> ds = new ArrayList<>();
    private int page = 1;
    private int totalPage = 10;
    private NestedScrollView nestedScrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        httpRequest = new HttpRequest();
        rycFruit = findViewById(R.id.rycFruit);
        loadmore = findViewById(R.id.loadmore);
        nestedScrollView = findViewById(R.id.nestScrollView);
        btnThem = findViewById(R.id.btnAdd);
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        token = sharedPreferences.getString("token","");
        httpRequest.callAPI().getList("Bearer" + token).enqueue(getList);
        httpRequest.callAPI().getPageFruit("Bearer " + token,page).enqueue(getListFruitReponse);


        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,AddFruit.class));
            }
        });

        nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (nestedScrollView.getChildAt(0).getMeasuredHeight() - nestedScrollView.getMeasuredHeight())) {
                    if(totalPage == page) return;
                    if(loadmore.getVisibility() == View.GONE){
                        loadmore.setVisibility(View.VISIBLE);
                        page++;
                        httpRequest.callAPI().getPageFruit("Bearer " + token,page).enqueue(getListFruitReponse);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        httpRequest.callAPI().getPageFruit("Bearer " + token,page).enqueue(getListFruitReponse);
    }

    Callback<Response<ArrayList<Fruit>>> getList = new Callback<Response<ArrayList<Fruit>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Fruit>>> call, retrofit2.Response<Response<ArrayList<Fruit>>> response) {
            if(response.isSuccessful()){
                if(response.body().getStatus() == 200){

                    ArrayList<Fruit> ds = response.body().getData();

                    // Đặt dữ liệu lên recycle
                    getData(ds);
                    // Toast ra thông tin từ Messenger
                    Toast.makeText(HomeActivity.this, "abc " + response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Fruit>>> call, Throwable t) {

        }
    };

    Callback<Response<Page<ArrayList<Fruit>>>> getListFruitReponse = new Callback<Response<Page<ArrayList<Fruit>>>>() {
        @Override
        public void onResponse(Call<Response<Page<ArrayList<Fruit>>>> call, retrofit2.Response<Response<Page<ArrayList<Fruit>>>> response) {
            if(response.isSuccessful()){
                if (response.body().getStatus() == 200){
                    totalPage = response.body().getData().getTotalPage();
                    ArrayList<Fruit> ds = response.body().getData().getData();
                    getData(ds);
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Page<ArrayList<Fruit>>>> call, Throwable t) {
            Log.d("zzz", "onFailure: " + t.getMessage());
        }
    };

    @Override
    public void delete(Fruit fruit) {

    }

    @Override
    public void edit(Fruit fruit) {
        Intent intent =new Intent(HomeActivity.this, UpdateActity.class);
        intent.putExtra("fruit", fruit);
        startActivity(intent);
    }

    @Override
    public void showDetail(Fruit fruit) {
        Intent intent =new Intent(HomeActivity.this, DetailProduct.class);
        intent.putExtra("fruit", fruit);
        startActivity(intent);

    }


    private void getData(ArrayList<Fruit> _ds) {
        // Kiểm tra nếu process load more chạy thì chỉ cần add thêm fruits vào list
        if (loadmore.getVisibility() == View.VISIBLE) {
            // Do chạy ở local nên tốc độ mạng tốt
            // Nên sẽ thêm 1 đoạn code delay
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Thông báo adapter dữ liệu thay đổi
                    adapter.notifyItemInserted(ds.size() - 1);
                    loadmore.setVisibility(View.GONE);
                    ds.addAll(_ds);
                    adapter.notifyDataSetChanged();
                }
            }, 1000);
            return;
        }

        ds.addAll(_ds);
        adapter = new FruitAdapter(HomeActivity.this,ds, this);
        rycFruit.setLayoutManager(new GridLayoutManager(this, 2));
        rycFruit.setAdapter(adapter);
    }


    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == Activity.RESULT_OK) {
                        // Clear danh sách cũ
                        listImage.clear();
                        imageUris.clear();

                        Intent data = o.getData();
                        if (data != null) {
                            // Nếu chọn nhiều ảnh
                            if (data.getClipData() != null) {
                                int count = data.getClipData().getItemCount();
                                for (int i = 0; i < count; i++) {
                                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                    imageUris.add(imageUri);

                                    // Thêm File vào listImage
                                    File file = createFileFormUri(imageUri, "image" + i);
                                    listImage.add(file);
                                }
                            } else if (data.getData() != null) { // Nếu chỉ chọn 1 ảnh
                                Uri imageUri = data.getData();
                                imageUris.add(imageUri);

                                // Thêm File vào listImage
                                File file = createFileFormUri(imageUri, "image");
                                listImage.add(file);
                            }

                            // Hiển thị tất cả ảnh đã chọn
                            displaySelectedImages();
                        }
                    }
                }
            });

    private void displaySelectedImages() {
        // Lấy container LinearLayout

        LinearLayout linearLayout = findViewById(R.id.imageContainer);
        if (linearLayout != null) {
            linearLayout.removeAllViews();
        } else {
            Log.e("Error", "LinearLayout is null!");
        }
        // Xóa tất cả các ảnh cũ

        // Dùng Glide để load danh sách ảnh mới
        for (Uri uri : imageUris) {
            ImageView imageView = new ImageView(this);
            Log.d("image", "displaySelectedImages: "+ uri);
            Glide.with(this)
                    .load(uri)
                    .override(200, 350) // Kích thước ảnh nhỏ hơn
                    .centerCrop()
                    .thumbnail(Glide.with(this).load(R.drawable.baseline_broken_image_24))
                    .into(imageView);

            // Thêm ImageView mới vào container
            linearLayout.addView(imageView);
        }
    }



    private File createFileFormUri (Uri path, String name) {
        File _file = new File(HomeActivity.this.getCacheDir(), name + ".png");
        try {
            InputStream in = HomeActivity.this.getContentResolver().openInputStream(path);
            OutputStream out = new FileOutputStream(_file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) >0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            Log.d("123123", "createFileFormUri: " +_file);
            return _file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }



}