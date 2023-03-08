package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gokulsundar4545.connectwithpeople.databinding.ActivityPayMentBinding;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;


public class PayMentActivity extends AppCompatActivity {

    private ActivityPayMentBinding binding;
    public static final String GPAY_PACKAGE_NAME="com.google.android.apps.nbu.paisa.user";
    int GOOGLE_PAY_REQUEST_CODE=103;
    String amount;
    String name="Gokul Sundarraj";
    String upId="9344350383@paytm";
    String transcation="Transcation for Hindustan IT";
    String status;
    Uri uri;


    private static boolean isAppInstalled(Context context, String packagename) {
        try {
            context.getPackageManager().getApplicationInfo(packagename,0);
            return true;
        }catch (PackageManager.NameNotFoundException e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();


            return false;
        }
    }



    private static Uri getUpIPaymentUri(String name,String upId,String transcation,String amount) {
        return new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa",upId)
                .appendQueryParameter("pn",name)
                .appendQueryParameter("tn",transcation)
                .appendQueryParameter("am",amount)
                .appendQueryParameter("cu","INR")
                .build();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPayMentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.googlepaybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount=binding.amountEd1.getText().toString().trim();

                if (!amount.isEmpty()){
                    uri=getUpIPaymentUri(name,upId,transcation,amount);
                    payWithPay();
                }else {
                    binding.amountEd1.setError("Ammount is Required");
                    binding.amountEd1.requestFocus();
                }


            }
        });


        binding.phonepay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount=binding.amountEd1.getText().toString().trim();

                if (!amount.isEmpty()){
                    uri=getUpIPaymentUri(name,upId,transcation,amount);
                    payWithPay();
                }else {
                    binding.amountEd1.setError("Ammount is Required");
                    binding.amountEd1.requestFocus();
                }


            }
        });

        binding.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount=binding.amountEd1.getText().toString().trim();

                if (!amount.isEmpty()){
                    uri=getUpIPaymentUri(name,upId,transcation,amount);
                    payWithPay();
                }else {
                    binding.amountEd1.setError("Ammount is Required");
                    binding.amountEd1.requestFocus();
                }


            }
        });


        binding.google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount=binding.amountEd1.getText().toString().trim();

                if (!amount.isEmpty()){
                    uri=getUpIPaymentUri(name,upId,transcation,amount);
                    payWithPay();
                }else {
                    binding.amountEd1.setError("Ammount is Required");
                    binding.amountEd1.requestFocus();
                }


            }
        });
    }

    private void payWithPay() {

        if (isAppInstalled(this,GPAY_PACKAGE_NAME)){
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
//            intent.setPackage(GPAY_PACKAGE_NAME);
            startActivityForResult(intent,GOOGLE_PAY_REQUEST_CODE);

        }else {
            Toast.makeText(this, "Please Install GPay", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null){
            status=data.getStringExtra("Status").toLowerCase();

        }

        if ((RESULT_OK==resultCode) && status.equals("success")){
            Toast.makeText(this, "Transcation Successful", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Transcation Fails", Toast.LENGTH_SHORT).show();

        }
    }






}