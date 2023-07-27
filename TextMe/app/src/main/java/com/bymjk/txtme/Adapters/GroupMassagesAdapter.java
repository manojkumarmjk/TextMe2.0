package com.bymjk.txtme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bymjk.txtme.Models.Massage;
import com.bymjk.txtme.Models.User;
import com.bymjk.txtme.R;
import com.bymjk.txtme.databinding.ItemRecieveBinding;
import com.bymjk.txtme.databinding.ItemRecieveGroupBinding;
import com.bymjk.txtme.databinding.ItemSentBinding;
import com.bymjk.txtme.databinding.ItemSentGroupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupMassagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Massage> massages;
    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;


    public GroupMassagesAdapter(Context context, ArrayList<Massage> massages) {
        this.context = context;
        this.massages = massages;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent_group,parent,false);
            return new SentViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recieve_group,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Massage massage = massages.get(position);
        if(FirebaseAuth.getInstance().getUid() != null) {
            if (FirebaseAuth.getInstance().getUid().equals(massage.getSenderId())) {
                return ITEM_SENT;
            }
            else if(!FirebaseAuth.getInstance().getUid().equals(massage.getSenderId())){
                return ITEM_RECEIVE;
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Massage massage = massages.get(position);

       /* int reactions[] = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {

            if(holder.getClass() == SentViewHolder.class){
                SentViewHolder viewHolder = (SentViewHolder)holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else{
                ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);

            }
            massage.setFeeling(pos);

            FirebaseDatabase.getInstance().getReference()
                    .child("public")
                    .child(massage.getMassageId()).setValue(massage);

            return true; // true is closing popup, false is requesting a new selection
        });*/

        if (holder.getClass() == SentViewHolder.class){
            SentViewHolder viewHolder = (SentViewHolder)holder;

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("users")
                    .child(massage.getSenderId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                User user = snapshot.getValue(User.class);
                                if (user != null) {
                                    ((SentViewHolder) holder).binding.myName.setText(user.getName());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            if (massage.getMassage().equals("photo")){
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.senderText.setVisibility(View.GONE);
                Glide.with(context)
                        .load(massage.getImageUrl())
                        .placeholder(R.drawable.placeholderimg2)
                        .into(viewHolder.binding.image);
            }

            viewHolder.binding.senderText.setText(massage.getMassage());

          /*  if(massage.getFeeling() >= 0){
//                massage.setFeeling(reactions[massage.getFeeling()]);
                viewHolder.binding.feeling.setImageResource(reactions[massage.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else{
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.senderText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });*/
        }
        else{
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;

            if (massage.getMassage().equals("photo")){
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.recieverText.setVisibility(View.GONE);
                Glide.with(context)
                        .load(massage.getImageUrl())
                        .placeholder(R.drawable.placeholderimg2)
                        .into(viewHolder.binding.image);
            }

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("users")
                    .child(massage.getSenderId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                User user = snapshot.getValue(User.class);
                                if (user != null) {
                                    ((ReceiverViewHolder) holder).binding.userName.setText(user.getName());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            viewHolder.binding.recieverText.setText(massage.getMassage());

         /*   if(massage.getFeeling() >= 0){
                viewHolder.binding.feeling.setImageResource(reactions[massage.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else{
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.recieverText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });*/
        }
    }

    @Override
    public int getItemCount() {
        return massages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder{

        ItemSentGroupBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentGroupBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{

        ItemRecieveGroupBinding binding;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRecieveGroupBinding.bind(itemView);
        }
    }

}
