package com.gokulsundar4545.connectwithpeople.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gokulsundar4545.connectwithpeople.Adapter.AdapterChatlist;
import com.gokulsundar4545.connectwithpeople.Model.ModelChat;
import com.gokulsundar4545.connectwithpeople.Model.ModelChatlist;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {


    FirebaseAuth auth;
    RecyclerView recyclerView;
    List<ModelChatlist> chatlistList;
    List<User> userList;
    DatabaseReference reference;
    FirebaseUser currentuser;

    AdapterChatlist adapterChatlist;
    public ChatListFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_chat, container, false);

        auth=FirebaseAuth.getInstance();
        currentuser=FirebaseAuth.getInstance().getCurrentUser();
        recyclerView=view.findViewById(R.id.RecyclerView);
        chatlistList=new ArrayList<>();

        reference= FirebaseDatabase.getInstance().getReference("Chatlist").child(currentuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                chatlistList.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    ModelChatlist chatlist = ds.getValue(ModelChatlist.class);
                    chatlistList.add(chatlist);
                }
                loadChat();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
        return view;
    }

    private void loadChat() {

        userList=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    User user=ds.getValue(User.class);
                    for (ModelChatlist chatlist:chatlistList){
                        if (user.getUid() !=null && user.getUid().equals(chatlist.getId())){
                            userList.add(user);
                            break;
                        }
                    }

                    adapterChatlist=new AdapterChatlist(getContext(),userList);
                    recyclerView.setAdapter(adapterChatlist);
                    for (int i=0;i<userList.size();i++){
                        lastMessage(userList.get(i).getUid());

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

    }

    private void lastMessage(String userId) {
        DatabaseReference reference1= FirebaseDatabase.getInstance().getReference("Chat");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                String lastMessage="default";
                for (DataSnapshot ds:snapshot.getChildren()){
                    ModelChat chat=ds.getValue(ModelChat.class);
                    if (chat==null){
                        continue;
                    }
                    String sender=chat.getSender();
                    String reciver=chat.getReceiver();
                    if (sender==null || reciver==null){
                        continue;
                    }
                    if (chat.getReceiver().equals(currentuser.getUid()) &&
                            chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) &&
                    chat.getSender().equals(currentuser.getUid())){

                        lastMessage=chat.getMessage();


                    }
                }

                adapterChatlist.setLastMessageMap(userId,lastMessage);
                adapterChatlist.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }
}