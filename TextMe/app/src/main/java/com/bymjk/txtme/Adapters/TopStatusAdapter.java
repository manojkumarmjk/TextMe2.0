package com.bymjk.txtme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bymjk.txtme.Activities.MainActivity;
import com.bymjk.txtme.Activities.StatusActivity;
import com.bymjk.txtme.Components.HelperFunctions;
import com.bymjk.txtme.Models.Status;
import com.bymjk.txtme.Models.UserStatus;
import com.bymjk.txtme.R;
import com.bymjk.txtme.databinding.ItemStatusBinding;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class TopStatusAdapter extends RecyclerView.Adapter<TopStatusAdapter.TopStatusViewHolder> {

    Context context;
    ArrayList<UserStatus> userStatuses;

    String lastUpdatedTime = "";

    public TopStatusAdapter(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @Override
    public TopStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =  LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new TopStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopStatusViewHolder holder, int position) {

        UserStatus userStatus = userStatuses.get(position);

        Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size() - 1);


        long currentTimeMillis = System.currentTimeMillis();
        // create a Calendar object and set it to the current time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);

        // get the day of the year for the current time
        int currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        // set the calendar object to the time you want to check
        calendar.setTimeInMillis(userStatus.getLastUpdated());

        // get the day of the year for the time you want to check
        int yourDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        // compare the day of the year for the current time and the time you want to check
        if (yourDayOfYear == currentDayOfYear) {
            // the time is today
            lastUpdatedTime = "Today, "+ HelperFunctions.getDateTime(userStatus.getLastUpdated(),1);
        } else if (yourDayOfYear == currentDayOfYear - 1) {
            // the time is yesterday
            lastUpdatedTime = "Yesterday, "+ HelperFunctions.getDateTime(userStatus.getLastUpdated(),1);
        } else {
            lastUpdatedTime = HelperFunctions.getDateTime(userStatus.getLastUpdated(),0);
        }


        Glide.with(context).load(lastStatus.getImageUrl())
                .into(holder.binding.image);
        holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());
        holder.binding.usrName.setText(userStatus.getName());
        holder.binding.statusTime.setText(lastUpdatedTime);


        holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for (Status status: userStatus.getStatuses()){
                    myStories.add(new MyStory(status.getImageUrl()));
                }
                new StoryView.Builder(((StatusActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(6000)// Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStatus.getName()) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(userStatus.getProfileImage()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for (Status status: userStatus.getStatuses()){
                    myStories.add(new MyStory(status.getImageUrl()));
                }
                new StoryView.Builder(((StatusActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(6000)// Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStatus.getName()) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(userStatus.getProfileImage()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class TopStatusViewHolder extends RecyclerView.ViewHolder{

        ItemStatusBinding binding;

        public TopStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStatusBinding.bind(itemView);
        }
    }

}
