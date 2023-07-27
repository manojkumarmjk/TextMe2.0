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
import com.bymjk.txtme.Components.HelperFunctions;
import com.bymjk.txtme.Models.User;
import com.bymjk.txtme.R;
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

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            long timeStamp = snapshot.child("lastMsgTime").getValue(Long.class);

                            String lastMsgTimeStr = "";

                            long currentTimeMillis = System.currentTimeMillis();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(currentTimeMillis);
                            int currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
                            calendar.setTimeInMillis(timeStamp);
                            int yourDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
                            if (yourDayOfYear == currentDayOfYear) {
                                // the time is today
                                lastMsgTimeStr = HelperFunctions.getDateTime(timeStamp,1);
                            } else if (yourDayOfYear == currentDayOfYear - 1) {
                                // the time is yesterday
                                lastMsgTimeStr = "Yesterday";
                            } else {
                                lastMsgTimeStr = HelperFunctions.getDateTime(timeStamp,2);
                            }
                            holder.binding.lastMsgTime.setText(lastMsgTimeStr);

                            holder.binding.lastMsg.setText(lastMsg);
                          //  holder.binding.lastMsgTime.setText(lasttime);
                        }
                        else {
                            holder.binding.lastMsg.setText("Tap to chat");
                            holder.binding.lastMsgTime.setText(" ");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.usrName.setText(user.getName());

        holder.binding.phoneNumber.setText(user.getPhoneNumber());

        if (user.getAbout() == null) {
            user.setAbout("Hey There ! I am using TextMe");
        }

        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.profile);

      /*  stringUri = uri.toString();

        holder.binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserProfile_Activity.class);
                intent.putExtra("name",user.getName());
                intent.putExtra("phoneno",user.getPhoneNumber());
                intent.putExtra("profileImage",user.getProfileImage());
                context.startActivity(intent);
            }
        });*/

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

    public class UserViewHolder extends RecyclerView.ViewHolder{
        RowConversationBinding binding;

        public UserViewHolder(@NonNull View itemView){
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
