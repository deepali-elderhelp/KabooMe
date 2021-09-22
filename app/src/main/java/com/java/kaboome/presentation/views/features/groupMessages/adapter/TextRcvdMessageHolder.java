package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.views.widgets.MessagesListStyle;

import de.hdodenhof.circleimageview.CircleImageView;


public class TextRcvdMessageHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMSentMessageHolder";

    TextView messageText, continueText, timeText, aliasText, roleText, newMessageHeader;

    ImageView urgentImage;
    CircleImageView profileImage;
    RequestManager requestManager;
    Context context;
    Handler handler; //Glide needs it


    TextRcvdMessageHolder(View itemView, Context context, RequestManager requestManager, MessagesListStyle style) {
        super(itemView);

        this.requestManager = requestManager;
        this.context = context;

        messageText = (TextView) itemView.findViewById(R.id.text_message);
        timeText = (TextView) itemView.findViewById(R.id.text_time);
        aliasText = (TextView) itemView.findViewById(R.id.text_user_alias);
        roleText = (TextView) itemView.findViewById(R.id.text_user_role);
        continueText = itemView.findViewById(R.id.continue_rcvd);
        profileImage = (CircleImageView) itemView.findViewById(R.id.image_user_profile);
        newMessageHeader = itemView.findViewById(R.id.newMessagesLabel);

        urgentImage = itemView.findViewById(R.id.urgent_image_recvd);

//        applyStyle(style);
    }

    public void onBind(final Message message, Handler handler, View.OnClickListener messageClickListener, View.OnLongClickListener messageLongClickListener, final UserImageClickListener userImageClickListener, boolean showNewMessageHeader) {


        itemView.setOnClickListener(messageClickListener);
        itemView.setOnLongClickListener(messageLongClickListener);
        this.handler = handler;

        if(showNewMessageHeader){
            newMessageHeader.setVisibility(View.VISIBLE);
        }
        else{
            newMessageHeader.setVisibility(View.GONE);
        }

        if(message.getNotify() == 1){
            urgentImage.setVisibility(View.VISIBLE);
        }
        else{
            urgentImage.setVisibility(View.INVISIBLE);
        }


        if(message.getDeleted() != null && message.getDeleted()){
            messageText.setText(message.getMessageText()); //the message text must be updated to - deleted by so and so
            urgentImage.setVisibility(View.INVISIBLE);
        }
        else{
//            messageText.setText(message.getMessageText());
            messageText.setText(getMessageTextTrimmed(message.getMessageText()));

//            messageText.setMovementMethod(LinkMovementMethod.getInstance());
//            messageText.setLongClickable(true);
            Linkify.addLinks(messageText, Linkify.WEB_URLS);
        }

        aliasText.setText(message.getAlias());
        if(message.getRole() != null && !(message.getRole().trim().isEmpty())){
            roleText.setText(" - "+message.getRole());
        }


        // Format the stored timestamp into a readable String using method.
        if(message.getSentAt() != null){
//            timeText.setText(DateFormatter.format(new Date(message.getSentAt()), DateFormatter.Template.TIME));
            timeText.setText(DateHelper.getPrettyTime(message.getSentAt()));
        }


//        Drawable imageErrorAndPlaceholder = context.getResources().getDrawable(R.drawable.bs_profile);
//        ImageHelper.loadUserImage(message.getSentBy(), null, requestManager,handler, profileImage, null);
//        BitmapDrawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(itemView.getContext(),R.dimen.message_recvd_sender_image_width, message.getAlias());
        Drawable imageErrorAndPlaceholder = itemView.getContext().getResources().getDrawable(R.drawable.bs_profile);
//        ImageHelper.loadUserImage(message.getSentBy(), message.getSentByImageTS(),
//                ImageHelper.getRequestManager(itemView.getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
//                handler, profileImage, null);
        ImageHelper.getInstance().loadGroupUserImage(message.getGroupId(), ImageTypeConstants.THUMBNAIL, message.getSentBy(), message.getSentByImageTS(),
                ImageHelper.getInstance().getRequestManager(itemView.getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, profileImage, null);


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
