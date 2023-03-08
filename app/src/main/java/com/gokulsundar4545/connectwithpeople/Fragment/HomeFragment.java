package com.gokulsundar4545.connectwithpeople.Fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerAdapter;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.gokulsundar4545.connectwithpeople.Adapter.PostAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.StoryAdapter;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.Story;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.Model.UserStories;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;


public class HomeFragment extends Fragment {


    ShimmerRecyclerView dashboardRv,StroyRv;
    ArrayList<Story> Storylist;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    ArrayList<Post> dashboardlist;

    RoundedImageView addStoryImage;
    ProgressDialog progressDialog;

    de.hdodenhof.circleimageview.CircleImageView profile;



    ActivityResultLauncher<String> galleryLauncher;
    public HomeFragment() {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog=new ProgressDialog(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View view =inflater.inflate(R.layout.fragment_home, container, false);

         profile=view.findViewById(R.id.profile_image);



        dashboardRv=view.findViewById(R.id.dashboardRv);
        dashboardRv.showShimmerAdapter();

         auth=FirebaseAuth.getInstance();
         database=FirebaseDatabase.getInstance();
         storage=FirebaseStorage.getInstance();

         StroyRv=view.findViewById(R.id.storyRV);
         StroyRv.showShimmerAdapter();



         Storylist=new ArrayList<>();

         progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
         progressDialog.setTitle("Story Uploading");
         progressDialog.setMessage("Please wait while Story Uploading!........");
         progressDialog.setCancelable(false);



        StoryAdapter adapter=new StoryAdapter(Storylist,getContext());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        StroyRv.setLayoutManager(linearLayoutManager);
        StroyRv.setNestedScrollingEnabled(false);


        database.getReference()
                .child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {

                Storylist.clear();
                if (snapshot.exists()){
                    for (DataSnapshot storySnapshot:snapshot.getChildren()){
                        Story story=new Story();
                        story.setStoryBy(storySnapshot.getKey());
                        story.setStoryAt(storySnapshot.child("postedBy").getValue(Long.class));

                        ArrayList<UserStories> Stories=new ArrayList<>();

                        for (DataSnapshot Snapshot1:storySnapshot.child("userStories").getChildren()){

                            UserStories userStories=Snapshot1.getValue(UserStories.class);
                            Stories.add(userStories);

                        }
                        story.setStories(Stories);
                        Storylist.add(story);
                    }
                    StroyRv.setAdapter(adapter);
                    StroyRv.hideShimmerAdapter();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });


        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if (snapshot.exists()){

                    User user=snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getProfile_photo())
                            .into(profile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        dashboardlist=new ArrayList<>();



        PostAdapter dashboardAdapter=new PostAdapter(dashboardlist,getContext());
        LinearLayoutManager layoutManager11=new LinearLayoutManager(getContext());
        layoutManager11.setReverseLayout(true);
        layoutManager11.setStackFromEnd(true);
        dashboardRv.setLayoutManager(layoutManager11);
        dashboardRv.setNestedScrollingEnabled(false);


        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                dashboardlist.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Post post=dataSnapshot.getValue(Post.class);
                    post.setPostId(dataSnapshot.getKey());
                    dashboardlist.add(post);
                }
                dashboardRv.setAdapter(dashboardAdapter);
                dashboardRv.hideShimmerAdapter();
                dashboardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

        addStoryImage=view.findViewById(R.id.postimage);
        addStoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                galleryLauncher.launch("image/*");
            }
        });

        galleryLauncher=registerForActivityResult(new ActivityResultContracts.GetContent()
                , new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {

                        addStoryImage.setImageURI(result);

                        progressDialog.show();
                        final StorageReference reference=storage.getReference()
                                .child("stories")
                                .child(FirebaseAuth.getInstance().getUid())
                                .child(new Date().getTime() + "");

                        reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Story story=new Story();
                                        story.setStoryAt(new Date().getTime());

                                        database.getReference()
                                                .child("stories")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .child("postedBy")
                                                .setValue(story.getStoryAt()).addOnSuccessListener(new OnSuccessListener<Void>() {

                                            @Override
                                            public void onSuccess(Void unused) {

                                                UserStories stories=new UserStories(uri.toString(),story.getStoryAt());

                                                database.getReference()
                                                        .child("stories")
                                                        .child(FirebaseAuth.getInstance().getUid())
                                                        .child("userStories")
                                                        .push()
                                                        .setValue(stories).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getContext(), "Status updated Successfully", Toast.LENGTH_SHORT).show();

                                                        progressDialog.dismiss();
                                                    }
                                                });


                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                });
        return view;
    }
}