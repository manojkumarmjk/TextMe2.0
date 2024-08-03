package com.bymjk.txtme.Adapters;

import static com.bymjk.txtme.Components.HelperFunctions.isSameDay;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.text.method.LinkMovementMethod;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bymjk.txtme.Components.TextFormatter;
import com.bymjk.txtme.Models.Message;
import com.bymjk.txtme.Models.OTPAuth;
import com.bymjk.txtme.R;
import com.bymjk.txtme.databinding.ItemChatMediafileReceiveBinding;
import com.bymjk.txtme.databinding.ItemChatMediafileSentBinding;
import com.bymjk.txtme.databinding.ItemDateSeparatorBinding;
import com.bymjk.txtme.databinding.ItemOtpMessageBinding;
import com.bymjk.txtme.databinding.ItemRecieveBinding;
import com.bymjk.txtme.databinding.ItemRecieveConsecutivelyBinding;
import com.bymjk.txtme.databinding.ItemSentBinding;
import com.bymjk.txtme.databinding.ItemSentConsecutivelyBinding;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MassagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messages;
    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;
    final int ITEM_DATE_SEPARATOR = 3;
    final int ITEM_SENT_CONSECUTIVE = 4;
    final int ITEM_RECEIVE_CONSECUTIVE = 5;
    final int ITEM_OTP_AUTH = 6;
    final int ITEM_MEDIA_SENT = 7;
    final int ITEM_MEDIA_RECEIVE = 8;
    final int VIEW_TYPE_MEDIA = 9;




    // Item Sending status
    final int STATUS_SENDING_UNDER_PROCESS = 1;
    final int STATUS_SENT = 2;
    final int STATUS_DELIVERED = 3;
    final int STATUS_READED = 4;
    String senderRoom ,receiverRoom;

    public MassagesAdapter(Context context, ArrayList<Message> messages, String senderRoom, String receiverRoom) {
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent, parent, false);
            return new SentViewHolder(view);
        } else if (viewType == ITEM_SENT_CONSECUTIVE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent_consecutively, parent, false);
            return new SentConsecutiveViewHolder(view);
        } else if (viewType == ITEM_RECEIVE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recieve, parent, false);
            return new ReceiverViewHolder(view);
        } else if (viewType == ITEM_RECEIVE_CONSECUTIVE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recieve_consecutively, parent, false);
            return new ReceiverConsecutiveViewHolder(view);
        } else if (viewType == ITEM_OTP_AUTH) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_otp_message, parent, false);
            return new OTPMessageViewHolder(view);
        } else if (viewType == ITEM_MEDIA_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_mediafile_sent, parent, false);
            return new MediaMessageSenderViewHolder(view);
        } else if (viewType == ITEM_MEDIA_RECEIVE){
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_mediafile_receive, parent, false);
            return new MediaMessageRecieverViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_date_separator, parent, false);
            return new DateSeparatorViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        switch (Message.MessageType.fromString(message.getMessageType())) {
            case DATE_SEPARATOR:
                return ITEM_DATE_SEPARATOR;
            case OTP_AUTH:
                return ITEM_OTP_AUTH;
            case IMAGE:
            case VIDEO:
            case AUDIO:
            case DOCS:
                Message currentMediaMessage = messages.get(position);

                if (FirebaseAuth.getInstance().getUid() != null) {
                    if (FirebaseAuth.getInstance().getUid().equals(currentMediaMessage.getSenderId())) {
                        return ITEM_MEDIA_SENT;
                    } else {
                        return ITEM_MEDIA_RECEIVE;
                    }
                }
            case GENERAL_ANNOUNCEMENT:
            case MARKETING_MESSAGE:
            case SUPPORT_MESSAGE:
            case LOCATION:
            case WEATHER:
            case CONTACT_DETAIL:
            case POLL:
                // Handle other types as necessary
                return VIEW_TYPE_MEDIA;
            case TEXT:
                Message currentMessage = messages.get(position);
                Message previousMessage = position > 0 ? messages.get(position - 1) : null;

                if (FirebaseAuth.getInstance().getUid() != null) {
                    if (FirebaseAuth.getInstance().getUid().equals(currentMessage.getSenderId())) {
                        if (isConsecutive(position)) {
                            return ITEM_SENT_CONSECUTIVE;
                        } else {
                            return ITEM_SENT;
                        }
                    } else {
                        if (isConsecutive(position)) {
                            return ITEM_RECEIVE_CONSECUTIVE;
                        } else {
                            return ITEM_RECEIVE;
                        }
                    }
                }
            default:
                Message currentMessage1 = messages.get(position);
                Message previousMessage1 = position > 0 ? messages.get(position - 1) : null;

                if (FirebaseAuth.getInstance().getUid() != null) {
                    if (FirebaseAuth.getInstance().getUid().equals(currentMessage1.getSenderId())) {
                        if (isConsecutive(position)) {
                            return ITEM_SENT_CONSECUTIVE;
                        } else {
                            return ITEM_SENT;
                        }
                    } else {
                        if (isConsecutive(position)) {
                            return ITEM_RECEIVE_CONSECUTIVE;
                        } else {
                            return ITEM_RECEIVE;
                        }
                    }
                }
        }
        return super.getItemViewType(position);
    }

    private boolean isDateSeparator(int position) {
        if (position == 0) return true;
        long currentDate = messages.get(position).getTimestamp();
        long previousDate = messages.get(position - 1).getTimestamp();
        return !isSameDay(currentDate, previousDate);
    }

    private int getAdjustedPosition(int position) {
        int count = 0;
        for (int i = 0; i <= position; i++) {
            if (isDateSeparator(i)) {
                count++;
            }
        }
        return position - count;
    }

    private boolean isConsecutive(int position) {
        if (position == 0) return false;
        Message currentMessage = messages.get(position);
        Message previousMessage = messages.get(position - 1);
        return currentMessage.getSenderId().equals(previousMessage.getSenderId()); /* &&
                (currentMessage.getTime() - previousMessage.getTime() < 5 * 60 * 1000); // 5 minutes threshold */
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        int adjustedPosition = getAdjustedPosition(position);
        Message message = messages.get(position);

        if (holder instanceof SentViewHolder) {
            SentViewHolder viewHolder = (SentViewHolder) holder;

            if (Message.MessageType.fromString(message.getMessageType()) == Message.MessageType.IMAGE) {
                viewHolder.binding.SenderImageCardView.setVisibility(View.VISIBLE);
                viewHolder.binding.senderText.setVisibility(View.GONE);
                viewHolder.binding.SenderImage.setOnClickListener(v -> openImageViewerDialog(message.getImageUrl()));
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.placeholderimg2)
                        .into(viewHolder.binding.SenderImage);
            } else {
                viewHolder.binding.senderText.setText(TextFormatter.formatText(context,message.getMassage()));
                viewHolder.binding.senderText.setMovementMethod(LinkMovementMethod.getInstance());
            }

            viewHolder.binding.senderText.setOnLongClickListener(v -> {
                String textToCopy = viewHolder.binding.senderText.getText().toString();
                copyToClipboard(textToCopy);
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            });

            viewHolder.binding.senderTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(message.getTimestamp())));
            viewHolder.binding.messageStatus.setImageResource(getStatusIcon(message.getMessageStatus()));

        } else if (holder instanceof SentConsecutiveViewHolder) {
            SentConsecutiveViewHolder viewHolder = (SentConsecutiveViewHolder) holder;

            if (Message.MessageType.fromString(message.getMessageType()) == Message.MessageType.IMAGE) {
                viewHolder.binding.SenderImageCardView.setVisibility(View.VISIBLE);
                viewHolder.binding.senderText.setVisibility(View.GONE);
                viewHolder.binding.SenderImage.setOnClickListener(v -> openImageViewerDialog(message.getImageUrl()));
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.placeholderimg2)
                        .into(viewHolder.binding.SenderImage);
            } else {
                viewHolder.binding.senderText.setText(TextFormatter.formatText(context,message.getMassage()));
                viewHolder.binding.senderText.setMovementMethod(LinkMovementMethod.getInstance());
            }

            viewHolder.binding.senderText.setOnLongClickListener(v -> {
                String textToCopy = viewHolder.binding.senderText.getText().toString();
                copyToClipboard(textToCopy);
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            });

            viewHolder.binding.senderTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(message.getTimestamp())));
            viewHolder.binding.messageStatus.setImageResource(getStatusIcon(message.getMessageStatus()));

        } else if (holder instanceof ReceiverViewHolder) {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;

            if (Message.MessageType.fromString(message.getMessageType()) == Message.MessageType.IMAGE) {
                viewHolder.binding.RecieverImageCardView.setVisibility(View.VISIBLE);
                viewHolder.binding.recieverText.setVisibility(View.GONE);
                viewHolder.binding.RecieverImage.setOnClickListener(v -> openImageViewerDialog(message.getImageUrl()));
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.placeholderimg2)
                        .into(viewHolder.binding.RecieverImage);
            } else {
                viewHolder.binding.recieverText.setText(TextFormatter.formatText(context,message.getMassage()));
                viewHolder.binding.recieverText.setMovementMethod(LinkMovementMethod.getInstance());
            }

            viewHolder.binding.recieverText.setOnLongClickListener(v -> {
                String textToCopy = viewHolder.binding.recieverText.getText().toString();
                copyToClipboard(textToCopy);
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            });

            viewHolder.binding.recieverTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(message.getTimestamp())));

        } else if (holder instanceof ReceiverConsecutiveViewHolder) {
            ReceiverConsecutiveViewHolder viewHolder = (ReceiverConsecutiveViewHolder) holder;

            if (Message.MessageType.fromString(message.getMessageType()) == Message.MessageType.IMAGE) {
                viewHolder.binding.RecieverImageCardView.setVisibility(View.VISIBLE);
                viewHolder.binding.recieverText.setVisibility(View.GONE);
                viewHolder.binding.RecieverImage.setOnClickListener(v -> openImageViewerDialog(message.getImageUrl()));
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.placeholderimg2)
                        .into(viewHolder.binding.RecieverImage);
            } else {
                viewHolder.binding.recieverText.setText(TextFormatter.formatText(context,message.getMassage()));
                viewHolder.binding.recieverText.setMovementMethod(LinkMovementMethod.getInstance());
            }

            viewHolder.binding.recieverText.setOnLongClickListener(v -> {
                String textToCopy = viewHolder.binding.recieverText.getText().toString();
                copyToClipboard(textToCopy);
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            });

            viewHolder.binding.recieverTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(message.getTimestamp())));

        }
        else if (holder instanceof MediaMessageSenderViewHolder) {
            MediaMessageSenderViewHolder viewHolder = (MediaMessageSenderViewHolder) holder;

//            if (Message.MessageType.fromString(message.getMessageType()) == Message.MessageType.IMAGE) {
                viewHolder.binding.SenderImageCardView.setVisibility(View.VISIBLE);
                viewHolder.binding.senderText.setVisibility(View.GONE);
                viewHolder.binding.SenderImage.setOnClickListener(v -> openImageViewerDialog(message.getImageUrl()));
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.placeholderimg2)
                        .into(viewHolder.binding.SenderImage);

            viewHolder.binding.senderText.setOnLongClickListener(v -> {
                String textToCopy = viewHolder.binding.senderText.getText().toString();
                copyToClipboard(textToCopy);
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            });

            viewHolder.binding.senderTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(message.getTimestamp())));
            viewHolder.binding.messageStatus.setImageResource(getStatusIcon(message.getMessageStatus()));

        } else if (holder instanceof MediaMessageRecieverViewHolder) {
            MediaMessageRecieverViewHolder viewHolder = (MediaMessageRecieverViewHolder) holder;

//            if (Message.MessageType.fromString(message.getMessageType()) == Message.MessageType.IMAGE) {
                viewHolder.binding.RecieverImageCardView.setVisibility(View.VISIBLE);
                viewHolder.binding.recieverText.setVisibility(View.GONE);
                viewHolder.binding.RecieverImage.setOnClickListener(v -> openImageViewerDialog(message.getImageUrl()));
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.placeholderimg2)
                        .into(viewHolder.binding.RecieverImage);
//            } else {
//                viewHolder.binding.recieverText.setText(TextFormatter.formatText(context,message.getMassage()));
//                viewHolder.binding.recieverText.setMovementMethod(LinkMovementMethod.getInstance());
//            }

            viewHolder.binding.recieverText.setOnLongClickListener(v -> {
                String textToCopy = viewHolder.binding.recieverText.getText().toString();
                copyToClipboard(textToCopy);
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            });

            viewHolder.binding.recieverTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(message.getTimestamp())));

        } else if (holder instanceof OTPMessageViewHolder) {

            OTPMessageViewHolder viewHolder = (OTPMessageViewHolder) holder;

            viewHolder.binding.messageTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(message.getTimestamp())));

            /*
                    {
                      "companyName": "ABC pvt. Ltd.",
                      "companyIcon": "https://example.com/img.png",
                      "otp_size": 6,
                      "otp": "123456"
                    }
             */

            try {
                // Convert the string to a JSONObject
                String jsonString = message.getMassage();

                // Unescape the JSON string
                String unescapedJsonString = unescapeJsonString(jsonString);

                Gson gson = new Gson();

                // Convert JSON string to Java object
                OTPAuth otpAuth = gson.fromJson(unescapedJsonString, OTPAuth.class);

                String companyName = otpAuth.getCompanyName();
                String companyIcon = otpAuth.getCompanyIcon();
                int otpSize = otpAuth.getOtp_size();
                String otp = otpAuth.getOtp();

                if (otpSize == 4){
                    viewHolder.binding.otpText5.setVisibility(View.GONE);
                    viewHolder.binding.otpText6.setVisibility(View.GONE);

                    viewHolder.binding.otpText1.setText(String.valueOf(otp.charAt(0)));
                    viewHolder.binding.otpText2.setText(String.valueOf(otp.charAt(1)));
                    viewHolder.binding.otpText3.setText(String.valueOf(otp.charAt(2)));
                    viewHolder.binding.otpText4.setText(String.valueOf(otp.charAt(3)));
                }else if (otpSize == 6){
                    viewHolder.binding.otpText5.setVisibility(View.VISIBLE);
                    viewHolder.binding.otpText6.setVisibility(View.VISIBLE);

                    viewHolder.binding.otpText1.setText(String.valueOf(otp.charAt(0)));
                    viewHolder.binding.otpText2.setText(String.valueOf(otp.charAt(1)));
                    viewHolder.binding.otpText3.setText(String.valueOf(otp.charAt(2)));
                    viewHolder.binding.otpText4.setText(String.valueOf(otp.charAt(3)));
                    viewHolder.binding.otpText5.setText(String.valueOf(otp.charAt(4)));
                    viewHolder.binding.otpText6.setText(String.valueOf(otp.charAt(5)));
                }

                char firstLetter = companyName.charAt(0);

                viewHolder.binding.companyFirstLetter.setText(String.valueOf(firstLetter));

                viewHolder.binding.companyName.setText(companyName);

                viewHolder.binding.copyBtn.setOnClickListener(v -> {
                    copyToClipboard(otp);
                    Toast.makeText(context, "OTP { "+otp+" } copied to your clipboard", Toast.LENGTH_SHORT).show();
                });


            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (holder instanceof DateSeparatorViewHolder) {
            DateSeparatorViewHolder viewHolder = (DateSeparatorViewHolder) holder;

            viewHolder.binding.dateTextView.setText(message.getDay());

        } else if (holder instanceof DateSeparatorViewHolder) {
            DateSeparatorViewHolder viewHolder = (DateSeparatorViewHolder) holder;

            viewHolder.binding.dateTextView.setText(message.getDay());

        }
    }

    private void openImageViewerDialog(String imageUrl) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_full_image);
        SubsamplingScaleImageView fullImageView = dialog.findViewById(R.id.fullImageView);
        ImageButton closeButton = dialog.findViewById(R.id.closeButton);

        // Load the image into the SubsamplingScaleImageView using Glide
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .placeholder(R.drawable.placeholderimg2)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        fullImageView.setImage(ImageSource.bitmap(resource));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle placeholder
                    }
                });

        // Set the click listener for the close button
        closeButton.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    private static String unescapeJsonString(String jsonString) {
        return jsonString.replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
    }

    private int getStatusIcon(int status) {
        switch (status) {
            case STATUS_SENDING_UNDER_PROCESS: // sending in process
                return R.drawable.clock;
            case STATUS_SENT: // sent
                return R.drawable.single_tick;
            case STATUS_DELIVERED: // delivered
                return R.drawable.double_tick;
            case STATUS_READED: // Read
                return R.drawable.double_tick_read;
            default:
                return android.R.color.transparent;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder {
        ItemSentBinding binding;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentBinding.bind(itemView);
        }
    }

    public class SentConsecutiveViewHolder extends RecyclerView.ViewHolder {
        ItemSentConsecutivelyBinding binding;

        public SentConsecutiveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentConsecutivelyBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        ItemRecieveBinding binding;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRecieveBinding.bind(itemView);
        }
    }

    public class ReceiverConsecutiveViewHolder extends RecyclerView.ViewHolder {
        ItemRecieveConsecutivelyBinding binding;

        public ReceiverConsecutiveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRecieveConsecutivelyBinding.bind(itemView);
        }
    }

    public class DateSeparatorViewHolder extends RecyclerView.ViewHolder {
        ItemDateSeparatorBinding binding;

        public DateSeparatorViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemDateSeparatorBinding.bind(itemView);
        }
    }

    public class OTPMessageViewHolder extends RecyclerView.ViewHolder {
        ItemOtpMessageBinding binding;

        public OTPMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemOtpMessageBinding.bind(itemView);
        }
    }

    public class MediaMessageSenderViewHolder extends RecyclerView.ViewHolder {
        ItemChatMediafileSentBinding binding;

        public MediaMessageSenderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding =  ItemChatMediafileSentBinding.bind(itemView);
        }
    }

    public class MediaMessageRecieverViewHolder extends RecyclerView.ViewHolder {
        ItemChatMediafileReceiveBinding binding;

        public MediaMessageRecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding =  ItemChatMediafileReceiveBinding.bind(itemView);
        }
    }
}
