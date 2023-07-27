package com.bymjk.txtme.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bymjk.txtme.Activities.ChatsActivity;
import com.bymjk.txtme.Models.User;
import com.bymjk.txtme.R;
import com.bymjk.txtme.databinding.CallingUserItemItemBinding;
import com.bymjk.txtme.databinding.RowConversationBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class CallingViewAdapter extends RecyclerView.Adapter<CallingViewAdapter.CallingViewHolder> {

    Context context;
    ArrayList<User> users;

    public CallingViewAdapter(Context context,ArrayList<User> users){
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public CallingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.calling_user_item_item,parent,false);
        return new CallingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallingViewHolder holder, int position) {
        User user = users.get(position);

        String senderId = FirebaseAuth.getInstance().getUid();

        String senderRoom = senderId + user.getUid();

        holder.binding.usrName.setText(user.getName());

        holder.binding.phoneNumber.setText(user.getPhoneNumber());

        holder.binding.callingTime.setText("Tap to call");

        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.profile);

        FirebaseDatabase.getInstance().getReference()
                .child("calls")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String time = snapshot.child("time").getValue(String.class);
                            holder.binding.callingTime.setText(time);
                        }
                        else {
                            holder.binding.callingTime.setText("Tap to call");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a");
                sdfTime.setTimeZone(TimeZone.getDefault());
                String currentTime = sdfTime.format(new Date());
                SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM");
                sdfDate.setTimeZone(TimeZone.getDefault());
                currentTime = "at "+currentTime + " on " + sdfDate.format(new Date());

                HashMap<String, Object> map = new HashMap<>();
                map.put("time",currentTime);

                FirebaseDatabase.getInstance().getReference()
                        .child("calls")
                        .child(senderRoom)
                        .updateChildren(map);

                Intent phone_intent = new Intent(Intent.ACTION_CALL);
                phone_intent.setData(Uri.parse("tel:" + user.getPhoneNumber()));
                context.startActivity(phone_intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class CallingViewHolder extends RecyclerView.ViewHolder{
        CallingUserItemItemBinding binding;

        public CallingViewHolder(@NonNull View itemView){
            super(itemView);
            binding = CallingUserItemItemBinding.bind(itemView);
        }
    }

//    void getformetTime(){
//        String strDateTime = d.getmLastLocation().date + " "+ d.getmLastLocation().time;
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLL yyyy hh:mm:ss a", Locale.ENGLISH);
//        ZonedDateTime zdt = LocalDateTime.parse(strDateTime, formatter).atZone(ZoneId.systemDefault());
//    }


}
