package com.example.googlelogindemo;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;

public class Registration extends AppCompatActivity {

    EditText EdtName,EdtAddress,EdtPincode,EdtEmail,EdtPassword,EdtOldPassword,Edtconfirmpassword, Edtmobilenumber;
    TextInputLayout txtoldpassword;
    String Name="",Address="",Email="",Password="",Confirmpassword="",MobileNumber="";
    String pincode;
    Button BtnRegister;
    TextView txtselimg;
    ImageView ImgView;
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String encodedimage = "";
    Blob profpic;
    int calls;
    Long userid;
    User user1 = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BtnRegister=findViewById(R.id.btnregister);
        EdtName=findViewById(R.id.edname);
        EdtAddress=findViewById(R.id.edaddress);
        EdtPincode=findViewById(R.id.edpincode);
        Edtmobilenumber=findViewById(R.id.edmobileno);
        EdtEmail=findViewById(R.id.edemail);
        EdtPassword=findViewById(R.id.edpassword);
        EdtOldPassword=findViewById(R.id.edoldpassword);
        Edtconfirmpassword=findViewById(R.id.edconfirmpassword);
        ImgView = findViewById(R.id.imgview);
        txtoldpassword = findViewById(R.id.txtoldpassword);





        ImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    Name=EdtName.getText().toString().trim();
                    Address=EdtAddress.getText().toString().trim();
                    Email=EdtEmail.getText().toString().trim();
                    MobileNumber=Edtmobilenumber.getText().toString().trim();
                    pincode=EdtPincode.getText().toString().trim();
                    Password=EdtPassword.getText().toString().trim();
                    Confirmpassword=Edtconfirmpassword.getText().toString().trim();

                    if(Password.equals(Confirmpassword)){


                        User user = new User();
                        user.setName(Name);
                        user.setAddress(Address);
                        user.setEmail(Email);
                        user.setMobileNo(MobileNumber);
                        user.setPinCode(pincode);
                        user.setPassword(Password);
                        user.setProfPic(encodedimage);
                        user.setUserName(Email);

                        UserRepository userRepository = new UserRepository(getApplicationContext());

                        if(calls==1){
                            userRepository.updateUser(user);

                        }else {
                            userRepository.register(user);
                        }

                         finish();



                    }else{
                        new AlertDialog.Builder(Registration.this)
                                .setMessage("Password and Confirm-password Not Match")
                                .setCancelable(true)
                                .setPositiveButton("OK",new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                } ).show();
                    }

                }

            }
        });



        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){

            calls = bundle.getInt("call");
            userid = bundle.getLong("userid");



        }

        if(calls==1){

            txtoldpassword.setVisibility(View.VISIBLE);

            UserRepository userRepository = new UserRepository(this);
            userRepository.userdetails(userid).observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    user1 = user;
                    setData(user);
                }
            });


        }


    }


    public void setData(User user){


        EdtName.setText(user.getName());
        EdtAddress.setText(user.getAddress());
        EdtPincode.setText(user.getPinCode());
        EdtEmail.setText(user.getEmail());
        Edtmobilenumber.setText(user.getMobileNo());

        //decode base64 string to image
        byte[] imageBytes = Base64.decode(user.getProfPic(), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        ImgView.setImageBitmap(decodedImage);



    }

    private  boolean validate() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (EdtName.getText().toString().isEmpty()) {
            EdtName.setError("Please Enter Name");
            return false;

        } else if (EdtAddress.getText().toString().isEmpty()) {

            EdtAddress.setError("Please Enter Address");
            return false;
        } else if (EdtPincode.getText().toString().isEmpty()) {

            EdtPincode.setError("Please Enter Pincode");
            return false;
        } else if (EdtEmail.getText().toString().isEmpty() ) {//EdtEmail.getText().toString().isEmpty() && EdtEmail.getText().toString().trim().matches(emailPattern)

            EdtEmail.setError("Please Enter Correct Email");
            return false;
        }
        else if (Edtmobilenumber.getText().toString().isEmpty()) {

            Edtmobilenumber.setError("Please Enter Mobile Number");
            return false;
        }
        else if (Edtmobilenumber.getText().toString().length()<10) {

            Edtmobilenumber.setError("Please Enter Valid 10-digit Number");
            return false;
        }
      else if (calls==1 && EdtOldPassword.getText().toString().isEmpty()) {

            EdtOldPassword.setError("Please Enter Current Password");
            return false;
        } else if (calls==1 && !EdtOldPassword.getText().toString().equals(user1.getPassword())) {

            EdtOldPassword.setError("Please Enter Correct Current Password");
            return false;
        }  else if (EdtPassword.getText().toString().isEmpty()) {

            EdtPassword.setError("Please Enter Password");
            return false;
        }
        else if (Edtconfirmpassword.getText().toString().isEmpty()) {
            Edtconfirmpassword.setError("Please Enter Confirm-password");
            return false;
        }else{
            return true;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(Registration.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {


        if (android.support.v4.content.ContextCompat.checkSelfPermission(Registration.this, android.Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);

            }
        }else {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImgView.setImageBitmap(thumbnail);
        encodeImage(thumbnail);
    }

    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ImgView.setImageBitmap(bm);
        encodeImage(bm);
    }


    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] b = baos.toByteArray();
        encodedimage = Base64.encodeToString(b, 0);
        Log.i("Image", encodedimage);
        return encodedimage;

    }



}
