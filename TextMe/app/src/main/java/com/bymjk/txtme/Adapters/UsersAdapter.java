package com.bymjk.txtme.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bymjk.txtme.Activities.ChatsActivity;
import com.bymjk.txtme.Activities.MainActivity;
import com.bymjk.txtme.Activities.UserProfile_Activity;
import com.bymjk.txtme.Components.GenericCallback;
import com.bymjk.txtme.Components.TimeUtils;
import com.bymjk.txtme.DB.UserRepository;
import com.bymjk.txtme.Models.ChatRoom;
import com.bymjk.txtme.Models.User;
import com.bymjk.txtme.R;
import com.bymjk.txtme.TextMeApplication;
import com.bymjk.txtme.databinding.RowConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    Context context;
    ArrayList<User> users;

    String lastMsg = "";

    public UsersAdapter(Context context,ArrayList<User> users){
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);

        String senderId = FirebaseAuth.getInstance().getUid();

        String senderRoom = senderId + user.getUid();

        UserRepository userRepository = new UserRepository(context);

        userRepository.getChatRoomByRoomID(senderRoom, new GenericCallback<ChatRoom>() {
            @Override
            public void onSuccess(ChatRoom result) {
                TextMeApplication.getInstance().getMainActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            lastMsg = result.getLastMsg();
                            long timeStamp = result.getLastMsgTime();
                            String senderUid = result.getSenderId();

                            String lastMsgTimeStr = "";

                            lastMsgTimeStr = TimeUtils.getDateTimeAndDayInStr(timeStamp);

                            holder.binding.lastMsgTime.setText(lastMsgTimeStr);

                            if (senderUid != null && senderUid.equals(FirebaseAuth.getInstance().getUid())){
                                lastMsg = "You: "+lastMsg;
                                holder.binding.lastMsg.setText(lastMsg);
                            } else if (senderUid != null){
                                UserRepository userRepository = new UserRepository(context);
                                userRepository.getUserFromUID(senderId, new GenericCallback<User>() {
                                    @Override
                                    public void onSuccess(User result) {
                                        String userName = result.getName();
                                        lastMsg = getFirstName(userName).isEmpty() ? lastMsg : getFirstName(userName) +": "+lastMsg;
                                        holder.binding.lastMsg.setText(lastMsg);
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                            }else {
                                holder.binding.lastMsg.setText(lastMsg);
                            }
                        }
                        else {
                            holder.binding.lastMsg.setText("Tap to chat");
                            holder.binding.lastMsgTime.setText(" ");
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                TextMeApplication.getInstance().getMainActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.binding.lastMsg.setText("Tap to chat");
                        holder.binding.lastMsgTime.setText(" ");
                    }
                });
            }
        });

//        FirebaseDatabase.getInstance().getReference()
//                .child("chats")
//                .child(senderRoom)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.exists()) {
//                            lastMsg = snapshot.child("lastMsg").getValue(String.class);
//                            long timeStamp = snapshot.child("lastMsgTime").getValue(Long.class);
//                            String senderUid = snapshot.child("senderId") != null ? snapshot.child("senderId").getValue(String.class) != null ? snapshot.child("senderId").getValue(String.class) : "" : "";
//
//                            String lastMsgTimeStr = "";
//
//                            lastMsgTimeStr = TimeUtils.getDateTimeAndDayInStr(timeStamp);
//
//                            holder.binding.lastMsgTime.setText(lastMsgTimeStr);
//
//                            if (senderUid != null && senderUid.equals(FirebaseAuth.getInstance().getUid())){
//                                lastMsg = "You: "+lastMsg;
//                                holder.binding.lastMsg.setText(lastMsg);
//                            } else if (senderUid != null){
//                                UserRepository userRepository = new UserRepository(context);
//                                userRepository.getUserFromUID(senderId, new GenericCallback<User>() {
//                                    @Override
//                                    public void onSuccess(User result) {
//                                        String userName = result.getName();
//                                        lastMsg = getFirstName(userName).isEmpty() ? lastMsg : getFirstName(userName) +": "+lastMsg;
//                                        holder.binding.lastMsg.setText(lastMsg);
//                                    }
//
//                                    @Override
//                                    public void onError(Exception e) {
//
//                                    }
//                                });
//                            }else {
//                                holder.binding.lastMsg.setText(lastMsg);
//                            }
//                        }
//                        else {
//                            holder.binding.lastMsg.setText("Tap to chat");
//                            holder.binding.lastMsgTime.setText(" ");
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

        holder.binding.usrName.setText(user.getName());

        holder.binding.phoneNumber.setText(user.getPhoneNumber());

        if (user.getAbout() == null) {
            user.setAbout("Hey There ! I am using TextMe");
        }

        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.profile);

//       String Uri = uri.toString();

        holder.binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserProfile_Activity.class);
                intent.putExtra("name",user.getName());
                intent.putExtra("image",user.getProfileImage());
                intent.putExtra("phonenumber",user.getPhoneNumber());
                intent.putExtra("uid" ,user.getUid());
                intent.putExtra("token",user.getToken());
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatsActivity.class);
                intent.putExtra("name",user.getName());
                intent.putExtra("image",user.getProfileImage());
                intent.putExtra("phonenumber",user.getPhoneNumber());
                intent.putExtra("uid" ,user.getUid());
                intent.putExtra("token",user.getToken());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static String getFirstName(String fullName) {
        // Split the full name by space
        String[] parts = fullName.split(" ");
        // Return the first part as the first name
        return parts.length > 0 ? parts[0] : "";
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        RowConversationBinding binding;

        public UserViewHolder(@NonNull View itemView){
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
