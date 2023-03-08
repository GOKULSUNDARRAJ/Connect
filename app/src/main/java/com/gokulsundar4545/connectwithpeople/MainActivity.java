package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gokulsundar4545.connectwithpeople.Fragment.AddPostFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.ChatListFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.HomeFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.Notification2Fragment;
import com.gokulsundar4545.connectwithpeople.Fragment.ProfileFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.SearchFragment;

import com.gokulsundar4545.connectwithpeople.Fragment.VedioViewFragment;

import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.iammert.library.readablebottombar.ReadableBottomBar;


public class MainActivity extends AppCompatActivity  {

    ActivityMainBinding binding;
    FirebaseAuth Auth=FirebaseAuth.getInstance();


    ConstraintLayout content;
    static final float END_SCALE = 0.7f;
    String mUID="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        content = findViewById(R.id.content);


        setSupportActionBar(binding.toolbar);
        MainActivity.this.setTitle("Alumni Users");

        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        binding.toolbar.setVisibility(View.GONE);
        transaction.replace(R.id.container,new HomeFragment());
        transaction.commit();





       String emial = (String)  MyPref.getFromPrefs(this,MyPref.EMAIL,"");
       String token = (String)  MyPref.getFromPrefs(this,MyPref.TOKEN,"");

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("Users");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    User post = ds.getValue(User.class);

                    if (post  != null && post.getEmail() != null && post.getEmail().equalsIgnoreCase(emial)) {
                        usersRef.child("token").setValue(token);
                    }



                    Log.d("TAG", "post: "  + post.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        usersRef.addListenerForSingleValueEvent(eventListener);









        binding.readablebottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {

                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();

                switch (i){
                    case 0:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container,new HomeFragment());
                        break;

                    case 1:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container,new VedioViewFragment());
                        break;

                    case 2:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container,new AddPostFragment());
                        break;

                    case 3:
                        binding.toolbar.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.container,new SearchFragment());
                        break;

                    case 4:
                        Intent intent=new Intent(MainActivity.this,NextActivity.class);
                        startActivity(intent);
                        break;
                }
                transaction.commit();
            }
        });






    }

    @Override
    protected void onResume() {

        super.onResume();
    }



    @Override
    public void onBackPressed() {

    }






    private  void checkUseStatus(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null){
            mUID=user.getUid();

            SharedPreferences sharedPreferences=getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("Current_USERID",mUID);
            editor.apply();
        }




    }









}