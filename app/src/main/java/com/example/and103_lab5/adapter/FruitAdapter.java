package com.example.and103_lab5.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.and103_lab5.AddFruit;
import com.example.and103_lab5.R;
import com.example.and103_lab5.databinding.ItemDistributorBinding;
import com.example.and103_lab5.model.Distributor;
import com.example.and103_lab5.model.Fruit;
import com.example.and103_lab5.model.Response;
import com.example.and103_lab5.services.HttpRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.FruitHolder> {

    ActivityResultLauncher<Intent> getImage;
    Context context;
    ArrayList<Fruit> list;
    ArrayList<Distributor> listDis = new ArrayList<>();
    Spinner spinnerCompany;
    HttpRequest httpRequest;
    String id_Distributor;
    private ArrayList<File> ds_image = new ArrayList<>();
    ImageView ivChoseImage;
    ArrayList<Uri> imageUris = new ArrayList<>();
    LinearLayout imageContainer;
    private FruitClick fruitClick;

    public FruitAdapter(Context context,ArrayList<Fruit> list,FruitClick fruitClick) {
        this.context = context;
        this.list = list;
        this.fruitClick = fruitClick;
    };

    public interface FruitClick {
        void delete(Fruit fruit);
        void edit(Fruit fruit);

        void showDetail(Fruit fruit);
    }


//    public FruitAdapter(Context context, ArrayList<Fruit> list, ActivityResultLauncher<Intent> getImage) {
//        this.context = context;
//        this.list = list;
//        httpRequest = new HttpRequest();
//        this.getImage = getImage;
//    }

    @NonNull
    @Override
    public FruitHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_fruits, parent, false);
        return new FruitHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FruitHolder holder, int position) {

//        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
//        if (params instanceof ViewGroup.MarginLayoutParams) {
//            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) params;
//            layoutParams.setMargins(16, 16, 16, 16);
//            holder.itemView.setLayoutParams(layoutParams);
//        }
        httpRequest = new HttpRequest();
        Fruit fruit = list.get(position);
        holder.tvName.setText(fruit.getName());
        holder.tvDes.setText(fruit.getDescription());
        holder.tvPriceQuantity.setText("price :" +fruit.getPrice()+" - quantity:" + fruit.getQuantity());
        String url = fruit.getImage().get(0);
        String newUrl = url.replace("localhost", "192.168.0.100");
        Glide.with(context)
                .load(newUrl)
                .thumbnail(Glide.with(context).load(R.drawable.baseline_broken_image_24))
                .into(holder.img);
        Log.d("url", "onBindViewHolder: " + newUrl);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fruitClick.edit(fruit);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xóa người dùng")
                        .setMessage("Bạn có chắc chắn muốn xóa người dùng này không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            httpRequest.callAPI().deleteFruits(fruit.get_id()).enqueue(new Callback<Response<Fruit>>() {
                                @Override
                                public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
                                    if(response.isSuccessful()){
                                        if(response.body().getStatus() == 200){
                                            list.remove(fruit);
                                            notifyDataSetChanged();
                                            Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Response<Fruit>> call, Throwable t) {
                                    Log.d("zzzz", "onFailure: " + t.getMessage());
                                }
                            });
                        })
                        .setNegativeButton("Hủy", (dialog, which) -> {
                            dialog.dismiss();
                        });
                builder.create().show();
                return false;
            }
        });

        holder.btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fruitClick.showDetail(fruit);
            }
        });



//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("Xóa người dùng")
//                        .setMessage("Bạn có chắc chắn muốn xóa người dùng này không?")
//                        .setPositiveButton("Xóa", (dialog, which) -> {
//                            httpRequest.callAPI().deleteFruits(fruit.get_id()).enqueue(responseFruit);
//                            Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
//                        })
//                        .setNegativeButton("Hủy", (dialog, which) -> {
//                            dialog.dismiss();
//                        });
//                builder.create().show();
//                return false;
//            }
//        });
    }

    void DialogUpdate(Fruit fruit){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_update_fruits,null);
        builder.setView(v);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();

        //anh xa
        EditText etName = v.findViewById(R.id.etName);
        EditText etQuantity = v.findViewById(R.id.etQuantity);
        EditText etPrice = v.findViewById(R.id.etPrice);
        EditText etStatus = v.findViewById(R.id.etStatus);
        EditText etDescription = v.findViewById(R.id.etDescription);
        ivChoseImage = v.findViewById(R.id.ivChoseImage);
        spinnerCompany = v.findViewById(R.id.spinnerCompany);
        Button btnUpdate = v.findViewById(R.id.btnAdd);
        Button btnBack = v.findViewById(R.id.btnBack);
        imageContainer = v.findViewById(R.id.imageContainer);

        etName.setText(fruit.getName());
        etQuantity.setText(fruit.getQuantity());
        etPrice.setText(fruit.getPrice());
        etStatus.setText(fruit.getStatus());
        etDescription.setText(fruit.getDescription());
        Log.d("zzzz", "DialogUpdate: " + fruit.getImage());

        // Lấy danh sách URL của các hình ảnh
        ArrayList<String> imageUrls = fruit.getImage();  // Giả sử hình ảnh là một danh sách URL

        // Xóa các hình ảnh cũ trong imageContainer
        imageContainer.removeAllViews();

        // Lặp qua danh sách URL và hiển thị các ảnh
        for (String url : imageUrls) {
            String newUrl = url.replace("localhost", "192.168.0.100");

            // Tạo ImageView mới cho mỗi ảnh
            ImageView imageView = new ImageView(context);
            Glide.with(context)
                    .load(newUrl)
                    .override(200, 350)
                    .thumbnail(Glide.with(context).load(R.drawable.baseline_broken_image_24))
                    .into(imageView);

            // Thêm ImageView vào container
            imageContainer.addView(imageView);
        }

        httpRequest.callAPI().getListDistributor().enqueue(getDistributorAPI);
        spinnerCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                id_Distributor = listDis.get(position).getId();
                Log.d("123123", "onItemSelected: " + id_Distributor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerCompany.setSelection(0);

        ivChoseImage.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   chooseImage();
               }
           });



        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, RequestBody> mapRequestBody = new HashMap<>();
                String _name = etName.getText().toString().trim();
                String _quantity = etQuantity.getText().toString().trim();
                String _price = etPrice.getText().toString().trim();
                String _status = etStatus.getText().toString().trim();
                String _description = etDescription.getText().toString().trim();

                mapRequestBody.put("name", getRequestBody(_name));
                mapRequestBody.put("quantity", getRequestBody(_quantity));
                mapRequestBody.put("price", getRequestBody(_price));
                mapRequestBody.put("status", getRequestBody(_status));
                mapRequestBody.put("description", getRequestBody(_description));
                mapRequestBody.put("id_distributor", getRequestBody(id_Distributor));
                ArrayList<MultipartBody.Part> _ds_image = new ArrayList<>();
//
                // Kiểm tra xem người dùng đã chọn ảnh mới hay không
                if (ds_image.isEmpty()) {
//                    // Nếu không có ảnh mới, thêm các ảnh cũ vào danh sách
//                    for (String imagePath : fruit.getImage()) {
//                        File imageFile = new File(imagePath);
//                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
//                        MultipartBody.Part multipartBodyPart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
//                        _ds_image.add(multipartBodyPart);
//                    }
                    Log.e("aaaaaa", "onClick: Khoon co anh moi" );
                } else {
                    Log.e("aaaaaa", "onClick:  co anh moi" );

                    // Nếu có ảnh mới, thêm các ảnh mới vào danh sách
                    ds_image.forEach(file1 -> {
                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file1);
                        MultipartBody.Part multipartBodyPart = MultipartBody.Part.createFormData("image", file1.getName(), requestFile);
                        _ds_image.add(multipartBodyPart);
                    });
                }

                // Gửi yêu cầu cập nhật lên server
                httpRequest.callAPI().updateFruitWithFileImage(mapRequestBody,
                        fruit.get_id(), _ds_image).enqueue(responseFruit);
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });



    }

    private void chooseImage() {
//        if (ContextCompat.checkSelfPermission(RegisterActivity.this,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        Log.d("123123", "chooseAvatar: " +123123);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        getImage.launch(intent);
//        }else {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//
//        }
    }

//    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        // Clear danh sách cũ
//                        ds_image.clear();
//                        imageUris.clear();
//
//                        Intent data = result.getData();
//                        if (data != null) {
//                            // Nếu chọn nhiều ảnh
//                            if (data.getClipData() != null) {
//                                int count = data.getClipData().getItemCount();
//                                for (int i = 0; i < count; i++) {
//                                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
//                                    imageUris.add(imageUri);
//
//                                    // Thêm File vào listImage
//                                    File file = createFileFormUri(imageUri, "image" + i);
//                                    ds_image.add(file);
//                                }
//                            } else if (data.getData() != null) { // Nếu chỉ chọn 1 ảnh
//                                Uri imageUri = data.getData();
//                                imageUris.add(imageUri);
//
//                                // Thêm File vào listImage
//                                File file = createFileFormUri(imageUri, "image");
//                                ds_image.add(file);
//                            }
//
//                            // Hiển thị tất cả ảnh đã chọn
//                            displaySelectedImages();
//                        }
//                    }
//                }
//            });

//    private void displaySelectedImages() {
//        // Lấy container LinearLayout từ layout dialog
//        LinearLayout imageContainerr = imageContainer;
//
//        // Xóa tất cả các ảnh cũ
//        imageContainer.removeAllViews();
//
//        // Dùng Glide để load danh sách ảnh mới
//        for (Uri uri : imageUris) {
//            ImageView imageView = new ImageView(context);
//            Log.d("image", "displaySelectedImages: "+ uri);
//            Glide.with(context)
//                    .load(uri)
//                    .override(200, 350) // Kích thước ảnh nhỏ hơn
//                    .centerCrop()
//                    .thumbnail(Glide.with(context).load(R.drawable.baseline_broken_image_24))
//                    .into(imageView);
//
//            // Thêm ImageView mới vào container
//            imageContainer.addView(imageView);
//        }
//    }
//
//
//
//    private File createFileFormUri(Uri path, String name) {
//        File _file = new File(context.getCacheDir(), name + ".png");
//        try {
//            InputStream in = context.getContentResolver().openInputStream(path);
//            OutputStream out = new FileOutputStream(_file);
//            byte[] buf = new byte[1024];
//            int len;
//            while ((len = in.read(buf)) > 0) {
//                out.write(buf, 0, len);
//            }
//            out.close();
//            in.close();
//            Log.d("123123", "createFileFormUri: " + _file);
//            return _file;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    Callback<Response<Fruit>> responseFruit = new Callback<Response<Fruit>>() {
        @Override
        public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
            if (response.isSuccessful()) {
                Log.d("123123", "onResponse: " + response.body().getStatus());
                if (response.body().getStatus()==200) {
                    Toast.makeText(context, "Sửa thành công thành công", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Fruit>> call, Throwable t) {
            Toast.makeText(context, "Sửa sai thằng ngu ", Toast.LENGTH_SHORT).show();
            Log.e("zzzzzzzzzz", "onFailure: "+t.getMessage());
        }
    };

    private RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    Callback<Response<ArrayList<Distributor>>> getDistributorAPI = new Callback<Response<ArrayList<Distributor>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Distributor>>> call, retrofit2.Response<Response<ArrayList<Distributor>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    listDis = response.body().getData();
                    String[] items = new String[listDis.size()];

                    for (int i = 0; i< listDis.size(); i++) {
                        items[i] = listDis.get(i).getName();
                    }
                    ArrayAdapter<String> adapterSpin = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, items);
                    adapterSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCompany.setAdapter(adapterSpin);
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable t) {
            t.getMessage();
        }

    };

    @Override
    public int getItemCount() {
        return list.size();
    }

    class FruitHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvPriceQuantity, tvDes;
        Button btnCheck;

        public FruitHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPriceQuantity = itemView.findViewById(R.id.tv_priceQuantity);
            tvDes = itemView.findViewById(R.id.tv_des);
            btnCheck = itemView.findViewById(R.id.btn_check);
        }
    }
}
