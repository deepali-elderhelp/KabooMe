package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.images.BlurTransformation;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.views.widgets.MessagesListStyle;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;


public class TextSentMessageHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMSentMessageHolder";

    TextView messageText, timeText, continueText, yourAliasText, yourRoleText, newMessageHeader;

    ImageView urgentImage;
    CircleImageView profileImage;
    RequestManager requestManager;
    Context context;


    TextSentMessageHolder(View itemView, Context context, RequestManager requestManager, MessagesListStyle style) {
        super(itemView);

        this.requestManager = requestManager;
        this.context = context;
        messageText =  itemView.findViewById(R.id.text_message_sent);
        timeText =  itemView.findViewById(R.id.text_time_sent);
        continueText = itemView.findViewById(R.id.continue_sent);
        yourAliasText = itemView.findViewById(R.id.text_user_alias_sent);
        yourRoleText = itemView.findViewById(R.id.text_user_role_sent);

        urgentImage = itemView.findViewById(R.id.urgent_image_sent);
        newMessageHeader = itemView.findViewById(R.id.newMessagesLabel);
        profileImage = (CircleImageView) itemView.findViewById(R.id.image_user_profile);
//        applyStyle(style);
    }

    public void onBind(final Message message, Handler handler, View.OnClickListener messageClickListener, View.OnLongClickListener messageLongClickListener, final UserImageClickListener userImageClickListener, boolean showNewMessageHeader) {


        itemView.setOnClickListener(messageClickListener);
        itemView.setOnLongClickListener(messageLongClickListener);

        if(showNewMessageHeader){
            newMessageHeader.setVisibility(View.VISIBLE);
        }
        else{
            newMessageHeader.setVisibility(View.GONE);
        }


        if(message.getDeleted()){
            messageText.setText("Message Deleted");
        }
        else{
//            messageText.setText(message.getMessageText());
            messageText.setText(getMessageTextTrimmed(message.getMessageText()));
//            messageText.setMovementMethod(LinkMovementMethod.getInstance());
//            messageText.setLongClickable(true);
            Linkify.addLinks(messageText, Linkify.WEB_URLS);
        }

        yourAliasText.setText(message.getAlias());
        if(message.getRole() != null && !(message.getRole().trim().isEmpty())){
            yourRoleText.setText(" - "+message.getRole());
        }


        // Format the stored timestamp into a readable String using method.
        if(message.getSentAt() != null){
//            timeText.setText(DateFormatter.format(new Date(message.getSentAt()), DateFormatter.Template.TIME));
            timeText.setText(DateHelper.getPrettyTime(message.getSentAt()));
        }

        if(message.getMessageText() != null && message.getMessageText().length() >= 750){
            continueText.setVisibility(View.VISIBLE);
            continueText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    messageText.setText(message.getMessageText());
                    continueText.setVisibility(View.GONE);
                }
            });
        }
        else{
            continueText.setVisibility(View.GONE);
        }


        if(message.getNotify() == 1){
            urgentImage.setVisibility(View.VISIBLE);
        }
        else{
            urgentImage.setVisibility(View.INVISIBLE);
        }

        Drawable imageErrorAndPlaceholder = itemView.getContext().getResources().getDrawable(R.drawable.account_gray_192);

        ImageHelper.getInstance().loadGroupUserImage(message.getGroupId(), ImageTypeConstants.THUMBNAIL, message.getSentBy(), message.getSentByImageTS(),
                ImageHelper.getInstance().getRequestManager(itemView.getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, profileImage, null);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userImageClickListener.onUserImageClicked(message);
            }
        });

    }

    private String getMessageTextTrimmed(String messageText){
        if(messageText.length() >= 750){
            return messageText.substring(0, 750)+"...";
        }
        return messageText;

    }

    /**
     * Initially thought of making a spannable with clickable span for the continuing
     * but it messes up the long press - that movement method is messed up
     * There are some solutions on internet but I think this continue link on the bottom is
     * also okay
     */

//    private SpannableStringBuilder getMessageTextTrimmed(String messageText){
//        SpannableStringBuilder ss = new SpannableStringBuilder();
//        if(messageText.length() >= 750){
//            ss.append(messageText.substring(0, 750));
//            ss.append("...");
//            ClickableSpan clickableSpan = new ClickableSpan() {
//                @Override
//                public void onClick(View widget) {
//                    Log.d(TAG, "get the rest of the message");
//                    Toast.makeText(context, "Get the rest of the message", Toast.LENGTH_SHORT).show();
//                }
//                @Override
//                public void updateDrawState(TextPaint ds) {
//                    super.updateDrawState(ds);
//                    ds.setUnderlineText(false);
//                }
//            };
//            ss.setSpan(clickableSpan, 751, 753, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//        else{
//            ss.append(messageText);
//        }
//        return ss;
//
//    }




//    private final void applyStyle(MessagesListStyle style) {
//        if (timeText != null) {
//            timeText.setTextColor(style.getOutcomingTimeTextColor());
//            timeText.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getOutcomingTimeTextSize());
//            timeText.setTypeface(timeText.getTypeface(), style.getOutcomingTimeTextStyle());
//        }
//        if (bubble != null) {
//            bubble.setPadding(style.getOutcomingDefaultBubblePaddingLeft(),
//                    style.getOutcomingDefaultBubblePaddingTop(),
//                    style.getOutcomingDefaultBubblePaddingRight(),
//                    style.getOutcomingDefaultBubblePaddingBottom());
//            ViewCompat.setBackground(bubble, style.getOutcomingBubbleDrawable());
//        }
//
//        if (messageText != null) {
//            messageText.setTextColor(style.getOutcomingTextColor());
//            messageText.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getOutcomingTextSize());
//            messageText.setTypeface(messageText.getTypeface(), style.getOutcomingTextStyle());
//            messageText.setAutoLinkMask(style.getTextAutoLinkMask());
//            messageText.setLinkTextColor(style.getOutcomingTextLinkColor());
////            configureLinksBehavior(messageText);
//        }
//    }


//    protected void configureLinksBehavior(final TextView text) {
//        text.setLinksClickable(false);
//        text.setMovementMethod(new LinkMovementMethod() {
//            @Override
//            public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
//                boolean result = false;
//                if (!MessagesListAdapter.isSelectionModeEnabled) {
//                    result = super.onTouchEvent(widget, buffer, event);
//                }
//                itemView.onTouchEvent(event);
//                return result;
//            }
//        });
//    }
}
