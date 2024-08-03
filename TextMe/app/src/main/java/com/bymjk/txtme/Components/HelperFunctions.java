package com.bymjk.txtme.Components;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.bymjk.txtme.Activities.SplashScreen;
import com.bymjk.txtme.R;
import com.bymjk.txtme.TextMeApplication;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HelperFunctions {
    FirebaseClientImplementation firebaseClientImplementation = new FirebaseClientImplementation();
    Dialog dialogDownloadInProgress;
    ProgressBar dialogDownloadProgressBar;
    TextView dialogDownloadProgressStatus;


    @SuppressLint("CheckResult")
    public void storeUserInLocalDatabase() {

        firebaseClientImplementation.getUserDetails(FirebaseAuth.getInstance().getUid());
        firebaseClientImplementation.userDetails.subscribe(user -> {

            TextMeApplication.getInstance().getMainActivity().getAppPreferences().setUser(user);

        });
    }


    public static String getDateTime(long timeStamp, int condition) {
        // Convert the timeStamp to a Date object
        Date dateObj = new Date(timeStamp);
        String currentTime = "";

        // Format the Date object into the desired string format
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a, dd/MM/yyyy");
        String dateTimeStr = dateFormat.format(dateObj);

        // Separate the time and date elements into separate variables
        String[] dateTimeArray = dateTimeStr.split(", ");
        String timeStr = dateTimeArray[0];
        String dateStr = dateTimeArray[1];

        // Format the time element in 12-hour format
        SimpleDateFormat timeFormat12 = new SimpleDateFormat("hh:mm a");
        timeFormat12.setDateFormatSymbols(new DateFormatSymbols(Locale.US) {
            @Override
            public String[] getAmPmStrings() {
                return new String[]{"AM", "PM"};
            }
        });
        String timeStr12 = timeFormat12.format(dateObj).toUpperCase();
        // Print the final formatted string
        currentTime = timeStr12 + " " + dateStr;

        if (condition == 1) {
            return timeStr12.toString();
        } else if (condition == 2) {
            return dateStr;
        } else {
            return currentTime;
        }

    }

    public static int getDaysDiff(long timestamp1, long timestamp2) {

        Date date1 = new Date(timestamp1);
        Date date2 = new Date(timestamp2);

        // Calculate the difference in milliseconds
        long diffInMs = Math.abs(date2.getTime() - date1.getTime());

        // Calculate the difference in days
        int diffInDays;
        diffInDays = (int) TimeUnit.DAYS.convert(diffInMs, TimeUnit.MILLISECONDS);

        return diffInDays;
    }

    private long downloadId;
    private String appNewVersion = "";
    private SplashScreen splashScreen;
    Runnable progressRunnable;
    final Handler handler = new Handler();
    final int delay = 1000;
    int counter = 0;


    public void downloadFile(String url, Context context, String newVersion, Activity activity) {
        appNewVersion = newVersion;
        this.splashScreen = (SplashScreen) activity;

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        // Create a new file in the Downloads directory with the desired filename
        File file = new File(downloadsDir, "TextMe " + newVersion + ".apk");

        splashScreen.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogDownloadInProgress = new Dialog(context);
                dialogDownloadInProgress.setContentView(R.layout.app_update_progress_bar_dialog);
                dialogDownloadInProgress.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialogDownloadProgressBar = dialogDownloadInProgress.findViewById(R.id.progressbarUpdateApp);
                dialogDownloadProgressStatus = dialogDownloadInProgress.findViewById(R.id.progressLevel);
                dialogDownloadInProgress.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogDownloadInProgress.setCancelable(false);
                dialogDownloadInProgress.show();

            }
        });

        //checking if android version is equal and greater than noughat

        //now if download complete file not visible now lets show it
        DownloadManager.Request request = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            request = new DownloadManager.Request(Uri.parse(url))
                    .setTitle("TextMe")
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationUri(Uri.fromFile(file))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true);
        } else {
            request = new DownloadManager.Request(Uri.parse(url))
                    .setTitle("TextMe")
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationUri(Uri.fromFile(file))
                    .setAllowedOverRoaming(true);
        }

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        downloadId = downloadManager.enqueue(request);


        progressRunnable = new Runnable() {
            @Override
            public void run() {
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cursor = downloadManager.query(query);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(columnIndex);

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        if(counter == 0) {
                            counter++;
                            splashScreen.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    handler.removeCallbacks(progressRunnable);
                                    dialogDownloadProgressBar.setProgress(100);
                                    String downloadLevel = "Download Completed";
                                    dialogDownloadProgressStatus.setText(downloadLevel);
                                    dialogDownloadProgressStatus.setTextColor(Color.parseColor("#14BD70"));

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialogDownloadProgressStatus.setText("App Installation is in process. Please wait!");
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                                        StrictMode.setVmPolicy(builder.build());
                                                    }
                                                    File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                                    File file = new File(downloadsDir, "TextMe " + appNewVersion + ".apk");
                                                    if (file.exists()) {
                                                        Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                                                        Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                                                        installIntent.setData(apkUri);
                                                        installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                        dialogDownloadInProgress.dismiss();
                                                        context.startActivity(installIntent);
                                                    }
                                                }
                                            }, 3500);
                                        }
                                    }, 3000);
                                }
                            });
                        }
                        // Download completed
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        splashScreen.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handler.removeCallbacks(progressRunnable);
                                dialogDownloadProgressBar.setProgress(5);
                                String downloadLevel = "Download failed";
                                dialogDownloadProgressStatus.setText(downloadLevel);
                                dialogDownloadProgressStatus.setTextColor(Color.parseColor("#FF0000"));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialogDownloadInProgress.dismiss();
                                    }
                                }, 3000);
                            }
                        });
                        // Download failed
                    } else if (status == DownloadManager.STATUS_PAUSED) {
                        // Download paused
                    } else if (status == DownloadManager.STATUS_RUNNING) {
                        // Download in progress
                        @SuppressLint("Range") int bytesDownloaded = ((Cursor) cursor).getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR) <= -1 ? 0 : cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        @SuppressLint("Range") int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES) <= -1 ? 0 : cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        int progress = (int) ((bytesDownloaded * 100L) / bytesTotal);
                        // Use 'progress' to update your UI
                        splashScreen.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialogDownloadProgressBar.setProgress(progress);
                                String downloadLevel = "Download : " + progress + "%";
                                dialogDownloadProgressStatus.setText(downloadLevel);
                            }
                        });
                    }
                }
                cursor.close();

                // Schedule the next progress update after the delay
                handler.postDelayed(this, delay);
            }
        };
        handler.post(progressRunnable);

    }


    public static String getFormattedDate(long timestamp) {
        // Current date
        Calendar currentCalendar = Calendar.getInstance();

        // Date from timestamp
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTimeInMillis(timestamp);

        // Date format for longer than a week
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Calculate the difference in days
        long diff = currentCalendar.getTimeInMillis() - targetCalendar.getTimeInMillis();
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffDays > 7) {
            // Longer than a week
            return dateFormat.format(new Date(timestamp));
        } else if (diffDays > 1) {
            // Greater than yesterday and less than or equal to a week
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return dayFormat.format(new Date(timestamp));
        } else if (diffDays == 1) {
            // Yesterday
            return "Yesterday";
        } else {
            // Today
            return "Today";
        }
    }

    public static boolean isSameDay(long date1, long date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(date1);
        cal2.setTimeInMillis(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

}
