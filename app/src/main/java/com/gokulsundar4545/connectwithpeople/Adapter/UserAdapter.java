package com.gokulsundar4545.connectwithpeople.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.ChartActivity;
import com.gokulsundar4545.connectwithpeople.Model.Follow;
import com.gokulsundar4545.connectwithpeople.Model.Notification;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.databinding.UserSampleBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewHolder> {

    Context context;
    ArrayList<User> list1;
    FirebaseAuth firebaseAuth;

    public UserAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list1 = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_sample,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  UserAdapter.viewHolder holder, int position) {


        final String hisUid=list1.get(position).getUid();
        firebaseAuth=FirebaseAuth.getInstance();



        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        String myuid=user1.getUid();



        holder.binding.ChatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ChartActivity.class);
                intent.putExtra("hisUId",hisUid);
                intent.putExtra("myUId",myuid);
                context.startActivity(intent);

            }
        });

        User user=list1.get(position);

        Picasso.get()
                .load(user.getProfile_photo())
                .into(holder.binding.profileImage);

        holder.binding.name.setText(user.getName());
        holder.binding.profession.setText(user.getProfission());


        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(user.getUserID())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {

                if (snapshot.exists()){
                    holder.binding.followbtn.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.follow_bg));
                    holder.binding.followbtn.setText("Following");
                    holder.binding.followbtn.setTextColor(context.getResources().getColor(R.color.black));
                    holder.binding.followbtn.setEnabled(false);
                }else {
                    holder.binding.followbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Follow follow=new Follow();
                            follow.setFollowedBy(FirebaseAuth.getInstance().getUid());
                            follow.setFollowedAt(new Date().getTime());


                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users")
                                    .child(user.getUserID())
                                    .child("followers")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .setValue(follow).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(user.getUserID())
                                            .child("followerCount")
                                            .setValue(user.getFollowerCount()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "you Followed"+user.getName(), Toast.LENGTH_SHORT).show();

                                            Notification notification=new Notification();
                                            notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                            notification.setNotificationAt(new Date().getTime());
                                            notification.setType("follows");

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("notification")
                                                    .child(user.getUserID())
                                                    .push()
                                                    .setValue(notification);
                                        }
                                    });
                                }
                            });

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });






    }

    @Override
    public int getItemCount() {
        return list1.size();

    }

    public class viewHolder extends RecyclerView.ViewHolder{

        UserSampleBinding binding;
        public viewHolder(@NonNull  View itemView) {
            super(itemView);

            binding=UserSampleBinding.bind(itemView);
        }
    }
}
