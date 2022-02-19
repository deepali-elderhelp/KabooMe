package com.java.kaboome.presentation.views.features.groupMessages;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.bumptech.glide.RequestManager;
import com.java.kaboome.R;
import com.java.kaboome.constants.MessageGroupsConstants;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroupConversation;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;
import com.java.kaboome.presentation.entities.ContactModel;
import com.java.kaboome.presentation.entities.IoTMessage;
import com.java.kaboome.presentation.entities.UserGroupConversationModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.helpers.GeneralHelper;
import com.java.kaboome.presentation.helpers.ImagesUtilHelper;
import com.java.kaboome.presentation.helpers.IoTHelper;
import com.java.kaboome.presentation.helpers.MediaHelper;
import com.java.kaboome.presentation.helpers.MessageDeleteCheckHelper;
import com.java.kaboome.presentation.helpers.MessageGroupsHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.mappers.UserGroupConversationModelMapper;
import com.java.kaboome.presentation.viewModelProvider.CustomViewModelProvider;
import com.java.kaboome.presentation.views.features.BaseFragment;
import com.java.kaboome.presentation.views.features.groupMessages.adapter.DownloadClickListener;
import com.java.kaboome.presentation.views.features.groupMessages.adapter.MediaPlayClickListener;
import com.java.kaboome.presentation.views.features.groupMessages.adapter.MessageListViewAdapter;
import com.java.kaboome.presentation.views.features.groupMessages.adapter.PublishMessageCallback;
import com.java.kaboome.presentation.views.features.groupMessages.adapter.UploadClickListener;
import com.java.kaboome.presentation.views.features.groupMessages.adapter.UserImageClickListener;
import com.java.kaboome.presentation.views.features.groupMessages.adapter.WelcomeMessageClickListener;
import com.java.kaboome.presentation.views.features.groupMessages.viewmodel.AdminUserMessagesViewModel;
import com.java.kaboome.presentation.views.widgets.MessageInput;
import com.java.kaboome.presentation.views.widgets.MessagesList;
import com.paginate.Paginate;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * This class is for the admins to see specific messages sent by a particular user to them
 * All admins will see the messages
 * This User is an Admin
 */
public class GroupAdminUserMessagesFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks, MessageInput.InputListener, MessageInput.TypingListener, MessageInput.AttachmentsListener,
    MessageListViewAdapter.OnMessageLongClickListener<Message>, MessageListViewAdapter.OnMessageClickListener,
    MediaPlayClickListener, WelcomeMessageClickListener, UserImageClickListener,
    DownloadClickListener, UploadClickListener {

    //checking for code changes

        private static final String TAG = "KMGroupAdminMessFrag";
        private AdminUserMessagesViewModel messagesViewModel;
        private View rootView;
        private Toolbar mainToolbar;
        private ImageView networkOffImageView;
        private MessageListViewAdapter adapter;
        private MessagesList recyclerView;
        private MessageInput messageInput;
        private AppCompatButton urgentButton;
        private AppCompatButton normalButton;
        private boolean urgentChecked = false;
        private NavController navController;
        private boolean firstLoad = true;
        private boolean connectionEstablished = false;
        private FrameLayout newMessagesFlashIcon;
        private boolean alreadyThere = false;
        private static final int REQUEST_READ_EXTERNAL_STORAGE = 101;
        private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 102;
        private ImageView sendButton;
        private ImageView attachmentButton;
        private MenuItem clearChat;
        private MenuItem clearMedia;
        private MenuItem groupChat;
        private int newMessagesCount = 0;
        private LinearLayout messagePrivacyLL;
        private TextView userLeftMessage;
        private TextView newMessagesCountTextView;
        private ImageView scrollDownArrowImage;
        private boolean userScrolled = false;
        private String thumbnailString = "";
        private TextView margueeTextView;
        private UserGroupConversationModel userGroupConversationModel;
        private UserGroupModel userGroupModel;
        private MenuItem groupChatLit;
        private TextView numberOfNewMessages;


    public GroupAdminUserMessagesFragment() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            Log.d(TAG, "onCreate: ");
            super.onCreate(savedInstanceState);

            this.userGroupConversationModel = (UserGroupConversationModel)getArguments().getSerializable("conversation");
            this.userGroupModel = (UserGroupModel)getArguments().getSerializable("group");


            messagesViewModel = ViewModelProviders.of(this, new CustomViewModelProvider(userGroupConversationModel, userGroupModel)).get(AdminUserMessagesViewModel.class);
            alreadyThere = false;

            String topicName = MessageGroupsHelper.getTopicName(userGroupConversationModel.getGroupId(), userGroupConversationModel.getOtherUserId(), MessageGroupsConstants.USER_ADMIN_MESSAGES);
            messagesViewModel.setTopicName(topicName);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            Log.d(TAG, "onCreateView: ");
            //toolbar needs to be set again since it was reset by other viewer fragments
            final AppCompatActivity act = (AppCompatActivity) getActivity();
            mainToolbar = act.findViewById(R.id.mainToolbar);

            mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavigationUI.navigateUp(navController, (DrawerLayout) null);
                }
            });
            mainToolbar.getMenu().clear(); //clearing old menu if any
            networkOffImageView = act.findViewById(R.id.mainToolbarNetworkOff);

            mainToolbar.inflateMenu(R.menu.messages_menu);

            mainToolbar.setTitle(userGroupModel.getGroupName());
            clearChat = mainToolbar.getMenu().findItem(R.id.group_message_clear);
            clearChat.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(NetworkHelper.isOnline()){
                        messagesViewModel.clearMessages();
                    }
                    else{
                        Toast.makeText(getContext(), "Sorry, this action needs Network Connection", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
            });

//            clearMedia = mainToolbar.getMenu().findItem(R.id.group_media_clear);
//            clearMedia.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    messagesViewModel.clearMedia();
//                    return true;
//                }
//            });


            groupChat = mainToolbar.getMenu().findItem(R.id.group_chat);
            groupChat.setVisible(true);
            groupChat.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    onGroupChatClicked();
                    return true;
                }
            });

            groupChatLit = mainToolbar.getMenu().findItem(R.id.group_chat_lit);
            groupChatLit.setVisible(false);
            groupChatLit.setActionView(R.layout.new_message_count);
            View menu_messages_action_bar = groupChatLit.getActionView();
            numberOfNewMessages = (TextView) menu_messages_action_bar.findViewById(R.id.new_messages);
            menu_messages_action_bar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onGroupChatClicked();
                }
            });
//            groupChatLit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    onGroupChatClicked();
//                    return true;
//                }
//            });

            MenuItem adminChat = mainToolbar.getMenu().findItem(R.id.group_admin_chat);
            adminChat.setVisible(false);
            MenuItem inviteMembers = mainToolbar.getMenu().findItem(R.id.group_message_invite);
            inviteMembers.setVisible(false);
            MenuItem adminChatLit = mainToolbar.getMenu().findItem(R.id.group_admin_chat_lit);
            adminChatLit.setVisible(false);


            if(alreadyThere)
                return rootView;

            // Inflate the layout for this fragment
            rootView = inflater.inflate(R.layout.fragment_group_admin_user_messages, container, false);
//        nestedScrollView = rootView.findViewById(R.id.frag_messages_scroll_top);

//        MessageInput input = (MessageInput) rootView.findViewById(R.id.fr_gr_me_layout_chatbox);
//        input.setInputListener(this);
//        input.setTypingListener(this);
//        input.setAttachmentsListener(this);

//        TextView input = rootView.findViewById(R.id.fr_gr_me_layout_chatbox);

            recyclerView = rootView.findViewById(R.id.fr_gr_me_reyclerview_message_list);
            newMessagesFlashIcon = rootView.findViewById(R.id.fr_gr_me_new_messages_icon);
            newMessagesFlashIcon.setOnClickListener(scrollDownClicked);

            newMessagesCountTextView = rootView.findViewById(R.id.fr_gr_me_new_messages_count);

            scrollDownArrowImage = rootView.findViewById(R.id.fr_gr_me_scroll_down_arrow);

            urgentButton = rootView.findViewById(R.id.urgent_button);
            urgentButton.setOnClickListener(urgentButtonClicked);
            normalButton = rootView.findViewById(R.id.normal_button);
            normalButton.setOnClickListener(normalButtonClicked);
            messageInput = rootView.findViewById(R.id.fr_gr_me_layout_chatbox);
            messagePrivacyLL = rootView.findViewById(R.id.messages_priority_ll);
            userLeftMessage = rootView.findViewById(R.id.userLeftMessage);
            sendButton = messageInput.getButton();
            attachmentButton = messageInput.getAttachmentButton();

            messageInput.setInputListener(this);
            messageInput.setTypingListener(this);
            messageInput.setAttachmentsListener(this);

            margueeTextView = rootView.findViewById(R.id.marquee_text_1);
            margueeTextView.setSelected(true);

            if(userGroupConversationModel.getDeleted() != null && userGroupConversationModel.getDeleted()){
                messagePrivacyLL.setVisibility(View.GONE);
                messageInput.setVisibility(View.GONE);
                userLeftMessage.setVisibility(View.VISIBLE);
            }
            else{
                messagePrivacyLL.setVisibility(View.VISIBLE);
                messageInput.setVisibility(View.VISIBLE);
                userLeftMessage.setVisibility(View.GONE);
            }

            navController = NavHostFragment.findNavController(GroupAdminUserMessagesFragment.this);

            //for system back button
//            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
//                @Override
//                public void handleOnBackPressed() {
//                    //I am handling this because I want it to go back to the group messages, not the conversation mode
//                    navController.popBackStack(R.id.groupMessagesFragment, false);
//                }
//            });
//
//            mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //I am handling this because I want it to go back to the group messages, not the conversation mode
//                    navController.popBackStack(R.id.groupMessagesFragment, false);
//                }
//            });



            return rootView;
        }

    private void onGroupChatClicked() {
//        navController.popBackStack(R.id.groupMessagesFragment, false);

        //if the group messages are in the backstack, pop back up
        //but if not - this will be the case when you come to this fragment
        //from the GroupInfo->Group User click-> private message him

        try {
            navController.getBackStackEntry(R.id.groupMessagesFragment);
            navController.popBackStack(R.id.groupMessagesFragment, false);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Bundle bundle = new Bundle();
            bundle.putSerializable("group", userGroupModel);

            navController.navigate(R.id.action_groupAdminUserMessagesFragment_to_groupMessagesFragment, bundle);

        }
    }

    @SuppressLint("FragmentLiveDataObserve")
        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        
            if(alreadyThere)
                return;

        Log.d(TAG, "onViewCreated: ");
            MutableLiveData attachmentPicked = navController.getCurrentBackStackEntry()
                    .getSavedStateHandle()
                    .getLiveData("attachments");
            attachmentPicked.observe(this, new Observer() {
                @Override
                public void onChanged(Object o) {
                    Log.d(TAG, "Received attachments - " + o);
                    if (o != null) { //it could be null if coming from Viewers

                        Attachment[] attachments = (Attachment[]) o;
                        if (NetworkHelper.isOnline() && connectionEstablished) {
                            //TODO: right now only one attachment, handle more than one
                            final Attachment attachment = attachments[0];
                            Log.d(TAG, "Attachment details - " + attachment.getAttachmentURI());
                            final String caption = attachment.getAttachmentCaption();
                            final String attachmentURI = attachment.getAttachmentURI();
                            final String attachmentPath = attachment.getAttachmentPath();

                            //start observing imageupload first, before publishing the message
//                    messagesViewModel.getUploadMessageAttachment().observe(getViewLifecycleOwner(), new Observer<DomainUpdateResource>() {
//                        @Override
//                        public void onChanged(DomainUpdateResource domainUpdateResource) {
//                            /**
//                             * two things need to happen here -
//                             * If status is success, that means that the image is uploaded, now -
//                             * 1. publish the message again - messageId is same, so new record should not be created
//                             */
//                            if(DomainUpdateResource.Status.SUCCESS == domainUpdateResource.status){
////                            Log.d(TAG, "type - : "+ getContext().getContentResolver().getType(Uri.fromFile(new File(attachment.getAttachmentURI()))));
//                                //viewmodel call to update the cache use case
//                                MessageTempDataHolder messageTempDataHolder  = (MessageTempDataHolder) domainUpdateResource.data;
//
//                                //publish again - but this time send the old messageId, sentAt and file extension, so that the message is updated not new created
//                                messagesViewModel.publishIoTMessage(messageTempDataHolder.getMessageId(), String.valueOf(caption), messageTempDataHolder.getSentAt(), attachment.isAttachmentPriority()? 1 : 2, true, true, false, messageTempDataHolder.getFileExtension(), messageTempDataHolder.getFileMime());
////                            messagesViewModel.updateMessageForAttachment(messageTempDataHolder.getMessageId(), true, true, true, attachment.getAttachmentURI());
//
//                            }
//
//                        }
//                    });
                            //user has selected the attachment, now create a message for it and send it
//                        final String fileExtension = FileUtils.getExtension(Uri.parse(attachmentURI), getContext());
//                        final String fileMime = FileUtils.getMimeType(getContext(), Uri.parse(attachmentURI));
//                        final String messageId = UUID.randomUUID().toString();
//                        final Long sentAt = (new Date()).getTime();

                            final String fileExtension = FileUtils.getExtension(attachmentPath);
                            final String fileMime = FileUtils.getMimeType(fileExtension);
                            final String messageId = UUID.randomUUID().toString();
                            final Long sentAt = (new Date()).getTime();

                            //if the message is type image or video, get a thumbnail of the image
                            //add that to the message and publish
//                        if(fileMime.contains("image") || fileMime.contains("video")){
//                            Bitmap thumbnail = FileUtils.getThumbnail(getContext(), Uri.parse(attachmentURI), fileMime);
//                            Bitmap evenSmallerBitmap = Bitmap.createScaledBitmap(thumbnail, 20, 20, true);
//                            thumbnailString = GeneralHelper.bitmapToBase64(evenSmallerBitmap);
//                        }
                            if (fileMime.contains("image") || fileMime.contains("video")) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    try {
                                        Bitmap thumbnail = getActivity().getContentResolver().loadThumbnail(Uri.parse(attachmentURI), new Size(20, 20), null);
                                        thumbnailString = GeneralHelper.bitmapToBase64(thumbnail);
                                    } catch (IOException e) {
                                        Log.d(TAG, "exception on thumbnail; generation - " + e.getMessage());
                                        e.printStackTrace();
                                    }

                                } else {
                                    Bitmap thumbnail = FileUtils.getThumbnail(getContext(), Uri.parse(attachmentURI), fileMime);
                                    Bitmap exifedBitmap = thumbnail;
                                    if (thumbnail == null) {
                                        //somehow bitmap is null, put default
                                        Drawable d = getResources().getDrawable(R.drawable.attachment_default);
                                        thumbnail = ImagesUtilHelper.drawableToBitmap(d);
                                    }
                                    try {
                                        //sometimes for the older versions, the exif is coming off by 90 degrees
                                        //hence rotating it
                                        int angle = 0;
                                        ExifInterface oldExif = new ExifInterface(attachmentPath);
                                        int orientation = oldExif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                                ExifInterface.ORIENTATION_UNDEFINED);
                                        switch (orientation) {
                                            case ExifInterface.ORIENTATION_ROTATE_90:
                                                angle = 90;
                                                break;

                                            case ExifInterface.ORIENTATION_ROTATE_180:
                                                angle = 180;
                                                break;

                                            case ExifInterface.ORIENTATION_ROTATE_270:
                                                angle = 270;
                                                break;

                                            case ExifInterface.ORIENTATION_NORMAL:
                                            default:
                                                angle = 0;
                                        }

                                        Matrix matrix = new Matrix();
                                        matrix.postRotate(angle);
                                        exifedBitmap = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(),
                                                matrix, true);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Bitmap evenSmallerBitmap = Bitmap.createScaledBitmap(exifedBitmap, 20, 20, true);
                                    thumbnailString = GeneralHelper.bitmapToBase64(evenSmallerBitmap);
                                }
                            }

                            messagesViewModel.addToMessageAttachmentMap(messageId, new String[]{attachmentPath, attachmentURI});

                            messagesViewModel.publishIoTMessage(messageId, String.valueOf(caption), AppConfigHelper.getUserId(), sentAt, attachment.isAttachmentPriority() ? 1 : 2, true, false, true,
                                    fileExtension, fileMime, false, thumbnailString, MessageGroupsConstants.USER_ADMIN_MESSAGES, attachmentURI,
                                    userGroupConversationModel.getOtherUserId(),
                                    userGroupConversationModel.getOtherUserName(), userGroupConversationModel.getOtherUserRole(), userGroupConversationModel.getImageUpdateTimestamp(),
                                    new PublishMessageCallback() {

                                        @Override
                                        public void publishSuccessful() {

//                                Message message = new Message();
//                                message.setMessageId(messageId);
//                                message.setGroupId(userGroupConversationModel.getGroupId());
//                                message.setSentBy(AppConfigHelper.getUserId());
//                                message.setSentByImageTS(userGroupModel.getUserImageUpdateTimestamp());
//                                message.setAlias(userGroupModel.getAlias());
//                                message.setRole(userGroupModel.getRole());
//                                message.setIsAdmin(userGroupModel.getIsAdmin());
//                                message.setHasAttachment(true);
//                                message.setAttachmentUploaded(false);
//                                message.setAttachmentMime(fileMime);
//                                message.setAttachmentExtension(fileExtension);
//                                message.setSentAt(sentAt);
//                                message.setSentTo(userGroupConversationModel.getOtherUserId());
//                                message.setNotify(attachment.isAttachmentPriority() ? 1 : 2);
//                                message.setMessageText(String.valueOf(caption));
//                                message.setTnBlob(thumbnailString);
//                                message.setAttachmentUri(attachmentURI);
//                                message.setAttachmentLoadingGoingOn(true);
//                                message.setDeleted(false);
//                                message.setSentToUserName(userGroupConversationModel.getOtherUserName());
//                                message.setSentToUserRole(userGroupConversationModel.getOtherUserRole());
//                                message.setSentToImageTS(userGroupConversationModel.getImageUpdateTimestamp());
////                                handleUploadAttachment(message, FileUtils.getFile(getContext(), Uri.parse(attachmentURI)));
////                                messagesViewModel.startUploadingAttachment(message, new File(attachmentPath));
//                                messagesViewModel.startUploadingAttachment(message, attachmentPath);

                                        }

                                        @Override
                                        public void publishFailed() {
                                            //show alert that the message sent failed
//                            IoTHelper.getInstance().setLastMessage(null);
                                            Toast.makeText(getContext(), "There seems to be some network problem...please try sending again in some time", Toast.LENGTH_SHORT).show();
                                        }


                                    });

                        } else {
                            Toast.makeText(getContext(), "There seems to be some network problem...please try sending again in some time", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }



        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

//        if(alreadyThere)
//            return;
//
//        initRecyclerView();
//        subscribeObservers();
//
//        alreadyThere = true;

//        firstLoad = false;
        }

        @Override
        public void onResume() {

            Log.d(TAG, "onResume: ");
            mainToolbar.setTitle(userGroupConversationModel.getOtherUserName()+"-"+userGroupConversationModel.getOtherUserRole());
            mainToolbar.setSubtitle(userGroupModel.getGroupName());
            //the following is needed because when waking up after screen change on something, the old value comes up
            //which is true for enabled - then it tries to subscribe to a topic which fails since really there is no
            //connection.
            //so here I set the value to false, to subscribe topic does not happen
            //onChange is called but it does nothing when the value is false
            IoTHelper.getInstance().resetConnectionEstablished();
            Log.d(TAG, "onResume: connection reset");

            super.onResume();
            //check if the mqtt connection is still there
            //if it has been dropped, you need to reconnect and resubscribe
            //there is no way to get the status of the connection
            //so you just call connect (it will return the same connection of existing
            //or it will return a new connection
            //now you don't know whether the old subscription is there or it has been dropped
            //so, just unsubscribe it (it will not throw any error if the subscription does not exist)
            //and then subscribe it
            //there is no suback for subscription - so the only way to know if the subscription failed
            //is that on sending message, there is no ack or the message is not received

//        IoTHelper.getInstance().connectToIoT(); //this will trigger onChanged() for connectionEstablished
            //the above method has been moved to onLoginSuccess() because the valid token are there
            String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
            if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
                if (!EasyPermissions.hasPermissions(getContext(), perms)) {
                    EasyPermissions.requestPermissions(this, "This permission is needed for showing image messages", REQUEST_READ_EXTERNAL_STORAGE, perms);
                }
            }

            MutableLiveData contactPicked = navController.getCurrentBackStackEntry()
                    .getSavedStateHandle()
                    .getLiveData("contact");
            contactPicked.observe(getViewLifecycleOwner(), new Observer() {
                @Override
                public void onChanged(Object o) {
                    if(o != null){
                        ContactModel contactModel = (ContactModel) o;
                        messageInput.getInputEditText().setText(contactModel.getName()+" \n "+contactModel.getPhone());
                    }
                }
            });

//            messagesViewModel.updateLastAccess();

        }

        @Override
        public void onPause() {
            super.onPause();
            Log.d(TAG, "onPause: ");
            //this is to take care that the GroupLastSeenTS gets updated on pause
            messagesViewModel.onBackPressed();


        }

        @SuppressLint("FragmentLiveDataObserve")
        private void subscribeObservers() {

            messagesViewModel.getUserGroupConversationFromCache().observe(this, new Observer<DomainUserGroupConversation>() {
                @Override
                public void onChanged(DomainUserGroupConversation domainUserGroupConversation) {
                    if (domainUserGroupConversation != null) {
                        userGroupConversationModel = UserGroupConversationModelMapper.getUserModelFromDomain(domainUserGroupConversation);
                        //if this group has been deleted, and you are in messages
                        //set a toast that this group has been deleted
                        if (userGroupConversationModel.getDeleted() != null && userGroupConversationModel.getDeleted()) {
                            Toast.makeText(getContext(), "This user is now DELETED, no more messages can be sent to him", Toast.LENGTH_LONG).show();
                            messagePrivacyLL.setVisibility(View.GONE);
                            messageInput.setVisibility(View.GONE);
                            userLeftMessage.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

            messagesViewModel.getMessagesList().observe(this, new Observer<PagedList<Message>>() {
                @Override
                public void onChanged(@Nullable PagedList<Message> messages) {
//                int scrollToIndex = messages.size() - 1; //by default to the bottom
                    int indexToInsertNewMessageHeader = -1;
//                if(!alreadyThere){
//                    if(firstLoad){
////                        Long lastMessageTSForGroup = group.getLastAccessed();
//                        Long lastMessageTSForGroup = userGroupConversationModel.getLastAccessed();
//                        for(int i=(messages.size()-1);i>= 0;i--){
//                            Message message = messages.get(i);
//                            if((message.getSentAt() != null) && (message.getSentAt() > lastMessageTSForGroup)){
//                                indexToInsertNewMessageHeader = i; //first message which is new
//                                break;
//                            }
//                        }
//                    }

                    for(int i=(messages.size()-1);i>= 0;i--){
                        Message message = messages.get(i);
                        if((message.getUnread() == 0 && message.getSentAt() > userGroupModel.getLastAccessed())){
                            indexToInsertNewMessageHeader = i; //first message which is new
                            break;
                        }
                    }

                    adapter.submitList(messages, indexToInsertNewMessageHeader);
                    if(indexToInsertNewMessageHeader != -1) {
                        if(!userScrolled) {
                            if(indexToInsertNewMessageHeader > 0){
                                recyclerView.scrollToPosition(indexToInsertNewMessageHeader - 1);
                            }
                            else {
                                recyclerView.scrollToPosition(indexToInsertNewMessageHeader);
                            }
                        }
//                        recyclerView.scrollToPosition(indexToInsertNewMessageHeader);
                    }

                    Log.d(TAG, "onSubmitList called for adapter and size is "+messages.size());
                }
            });

            messagesViewModel.getServerMessages().observe(this, new Observer<DomainResource<List<DomainMessage>>>() {
                @Override
                public void onChanged(@Nullable DomainResource<List<DomainMessage>> listDomainResource) {
                    Log.d(TAG, "onChanged: Data came back, but not needed really");
                }
            });


            Paginate.with(recyclerView, messageListPaginateCallback)
                    .setLoadingTriggerThreshold(2)
                    .addLoadingListItem(false)
                    .build();

            messagesViewModel.getConnectionEstablished().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean aBoolean) {
                    if(aBoolean){
                        Log.d(TAG, "connection established, enable the send button");

                        try {
//                            IoTHelper.getInstance().subscribeToGroup(group.getGroupId());
                            IoTHelper.getInstance().subscribeToGroup(messagesViewModel.getTopicName());
                            messageInput.getButton().setEnabled(true);
                            connectionEstablished = true;
                        } catch (Exception e) {
                            if(messagesViewModel.connectionErrorToBeDisplayed(e)){
                                Toast.makeText(getContext(), "Could not connect to network, please check", Toast.LENGTH_LONG).show();
                            }
                            Log.d(TAG, "Exception - "+e.getMessage()+" e "+e);
                        }
                    }
                    else{
                        messageInput.getButton().setEnabled(false);
                        connectionEstablished = false;
                    }
                }
            });

            messagesViewModel.getIotMessageReceived().observe(this, new Observer<IoTMessage>() {
                @Override
                public void onChanged(@Nullable IoTMessage message) {
                    if(message != null){
                        Log.d(TAG, "New message received - "+message);
                        if(!message.getDeleted()) {
                            firstLoad = false;
                            newMessagesCount++;
                            scrollDownArrowImage.setVisibility(View.GONE);
                            newMessagesCountTextView.setVisibility(View.VISIBLE);
                            newMessagesCountTextView.setText(String.valueOf(newMessagesCount));
                        }
                        //also add to the internal cache
                        messagesViewModel.handleMessageArrival(message);

                    }

                }
            });

            messagesViewModel.getGroupUnreadMessages().observe(this, new Observer<List<DomainMessage>>() {
                @Override
                public void onChanged(List<DomainMessage> domainMessages) {
                    if(domainMessages != null && domainMessages.size() > 0) {
                        //TODO: also check if the messages are not for this conversation only
                        //new group message has arrived, lit the menu icon
                        groupChat.setVisible(false);
                        groupChatLit.setVisible(true);
                        numberOfNewMessages.setVisibility(View.VISIBLE);
                        numberOfNewMessages.setText(String.valueOf(domainMessages.size()));
                    }
                    else{
                        //no new group message has arrived, lit the menu icon
                        groupChat.setVisible(true);
                        groupChatLit.setVisible(false);
                        numberOfNewMessages.setVisibility(View.GONE);
                    }
                }
            });

        }

        private void initRecyclerView() {


//            adapter = new MessageListViewAdapter(getContext(), exoPlayer, dataSourceFactory);
            adapter = new MessageListViewAdapter(getContext());
            adapter.setPrivateGroup(this.userGroupModel.getPrivate());
            adapter.setOnMessageLongClickListener(this);
            adapter.setOnMessageClickListener(this);
            adapter.setDownloadClickListener(this);
            adapter.setUploadClickListener(this);
            adapter.setMediaPlayClickListener(this);
            adapter.setWelcomeMessageClickListener(this);
            adapter.setUserImageClickListener(this);

            recyclerView.setAdapter(adapter, true, (RequestManager) initGlide(), newMessagesFlashIcon, new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
//                    super.onItemRangeInserted(positionStart, itemCount);
                    if(!userScrolled){
                        Log.d(TAG, "Going to move recycler view to position 0");
                        recyclerView.smoothScrollToPosition(0);
                    }
                }
            });
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    userScrolled = true;
                    if(newMessagesFlashIcon != null){
                        if(!recyclerView.canScrollVertically(1)){
                            newMessagesFlashIcon.setVisibility(View.GONE);
                            newMessagesCount = 0;
                            userScrolled = false;
                        }
                        else{
                            newMessagesFlashIcon.setVisibility(View.VISIBLE);
                            if(newMessagesCount == 0){
                                newMessagesCountTextView.setVisibility(View.GONE);
                                scrollDownArrowImage.setVisibility(View.VISIBLE);

                            }

                        }
                    }

                }

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    firstLoad = false;
                }
            });
            recyclerView.setItemAnimator(new DefaultItemAnimator());

        }


        private Object initGlide() {
            return ImageHelper.getInstance().getRequestManager(getContext(), R.drawable.bs_profile, R.drawable.bs_profile);
        }

        @Override
        public void onMessageLongClick(Message message) {
            getMessageCopyDeleteDialog(message);
        }

        @Override
        public boolean onSubmit(CharSequence input) {
            firstLoad = false;
            //check if the connection is there
            if(NetworkHelper.isOnline() && connectionEstablished){
//                messagesViewModel.publishIoTMessage(String.valueOf(input), urgentChecked ? 1 : 2, MessageGroupsConstants.USER_ADMIN_MESSAGES, AppConfigHelper.getUserId(), new PublishMessageCallback() {

                messagesViewModel.publishIoTMessage(String.valueOf(input), AppConfigHelper.getUserId(), urgentChecked ? 1 : 2, MessageGroupsConstants.USER_ADMIN_MESSAGES,
                        userGroupConversationModel.getOtherUserId(), userGroupConversationModel.getOtherUserName(), userGroupConversationModel.getOtherUserRole(),
                        userGroupConversationModel.getImageUpdateTimestamp(), new PublishMessageCallback() {
                    @Override
                    public void publishSuccessful() {
                        //do nothing
                        Log.d(TAG, "publishSuccessful: ");

                    }

                    @Override
                    public void publishFailed() {
                        Log.d(TAG, "publishFailed: ");
                        //show message that it failed, connection issue could be there, try again
                        Toast.makeText(getContext(), "There seems to be some network problem...still trying", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                Log.d(TAG, "onSubmit: no network connection");
                Toast.makeText(getContext(), "Sorry, seems like there is no network connection", Toast.LENGTH_SHORT).show();
            }

//        hideKeyboardFrom(getContext(), this.rootView);
            return true; //so that the text is cleared
        }

//    public static void hideKeyboardFrom(Context context, View view) {
//        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }

        private View.OnClickListener urgentButtonClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urgentButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_button_red_gradient_background));
                messageInput.setUrgentMessageColors();
//            messageInput.getButton().setImageDrawable(style.getInputButtonIcon());
//            ViewCompat.setBackground(messageInput.getButton(), getResources().getDrawable(R.drawable.send_urgent_background));
                urgentChecked =  true;
                urgentButton.setEnabled(false);
                normalButton.setEnabled(true);
            }
        };

        private View.OnClickListener normalButtonClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urgentButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_button_grey_gradient_background));
                messageInput.setNormalMessageColors();
//            ViewCompat.setBackground(messageInput.getButton(), getResources().getDrawable(R.drawable.send_background));
                urgentChecked =  false;
                urgentButton.setEnabled(true);
                normalButton.setEnabled(false);
            }
        };

//        private View.OnClickListener scrollDownClicked = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                newMessagesCount = 0;
//                recyclerView.smoothScrollToPosition(0);
//            }
//        };
        private View.OnClickListener scrollDownClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        //            newMessagesCount = 0;
                if(newMessagesCount > 1)
                    recyclerView.smoothScrollToPosition(0+newMessagesCount-1);
                else
                    recyclerView.smoothScrollToPosition(0);
                newMessagesCount = 0;
            }
        };

        @Override
        public void onAddAttachments() {

//        Intent openIntent = new Intent(Intent.ACTION_PICK);
////        openIntent.setType(ContactsContract.Contacts.CONTENT_VCARD_TYPE);
//        openIntent.setType("text/plain");
//        startActivityForResult(openIntent, OPEN_REQUEST_CODE);


            Bundle args = new Bundle();
            args.putString("groupId", userGroupModel.getGroupId());
            args.putString("groupName", userGroupModel.getGroupName());
            args.putString("goingBackTo", "AdminUserMessages");
            navController.navigate(R.id.action_groupAdminUserMessagesFragment_to_messageAttachmentsDialog, args);

        }

        @Override
        public void onStartTyping() {
            recyclerView.getLayoutManager().scrollToPosition(0);
        }

        @Override
        public void onStopTyping() {

        }



        private Paginate.Callbacks messageListPaginateCallback = new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                //call the use case to load the messages
                Log.d(TAG, "onLoadMore: ");
                if(messagesViewModel != null)
                    messagesViewModel.loadServerMessages();
            }

            @Override
            public boolean isLoading() {
//            Log.d(TAG, "isLoading: ");
                if(messagesViewModel != null)
                    return messagesViewModel.isLoading();
                else
                    return false;
            }

            @Override
            public boolean hasLoadedAllItems() {
//            Log.d(TAG, "hasLoadedAllItems: ");
                if(messagesViewModel != null){
                    if(messagesViewModel.isHasLoadedAll()){
                        Log.d(TAG, "hasLoadedAllItems: ");
                    }
                    return messagesViewModel.isHasLoadedAll();
                }

                else
                    return false;
            }
        };

    private void getMessageCopyDeleteDialog(final Message message){
        if(!message.getDeleted()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setMessage("What do you want to do?");
            alertDialogBuilder.setNeutralButton("Delete Only for Me", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    messagesViewModel.deleteLocalMessage(message);
                }
            });
            alertDialogBuilder.setPositiveButton("Copy text",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
//                        Toast.makeText(MessagesActivity.this,"Yes, copy text",Toast.LENGTH_LONG).show();
                            GroupAdminUserMessagesFragment.this.adapter.copyToClipboard(getContext(), message.getMessageText());
                        }
                    });

            alertDialogBuilder.setNegativeButton("Delete the Message for all", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!NetworkHelper.isOnline()) {
                        Toast.makeText(getContext(), "No Network: Delete message needs network connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (MessageDeleteCheckHelper.canDeleteMessage(message, userGroupModel.getIsAdmin())) {
                        messagesViewModel.deleteMessage(message, new PublishMessageCallback() {
                            @Override
                            public void publishSuccessful() {
                                Toast.makeText(getContext(), "Message is marked deleted", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void publishFailed() {
                                //show alert - could not do it - could be server connection issue
                                Toast.makeText(getContext(), "There seems to be some network problem...please try sending again in some time", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Sorry, not authorized to delete the message", Toast.LENGTH_LONG).show();
                    }
                }
            });


            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

//        private void getMessageCopyDeleteDialog(final Message message) {
//            if (!message.getDeleted()) {
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
//                alertDialogBuilder.setMessage("What do you want to do?");
//                alertDialogBuilder.setPositiveButton("Copy text",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface arg0, int arg1) {
////                        Toast.makeText(MessagesActivity.this,"Yes, copy text",Toast.LENGTH_LONG).show();
//                                GroupAdminUserMessagesFragment.this.adapter.copyToClipboard(getContext(), message.getMessageText());
//                            }
//                        });
//
//                alertDialogBuilder.setNegativeButton("Delete the Message For All", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (!NetworkHelper.isOnline()) {
//                            Toast.makeText(getContext(), "No Network: Delete message needs network connection", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        if (MessageDeleteCheckHelper.canDeleteMessage(message, userGroupModel.getIsAdmin())) {
//                            messagesViewModel.deleteMessage(message, new PublishMessageCallback() {
//                                @Override
//                                public void publishSuccessful() {
//                                    Toast.makeText(getContext(), "Message is marked deleted", Toast.LENGTH_SHORT).show();
//                                }
//
//                                @Override
//                                public void publishFailed() {
//                                    //show alert - could not do it - could be server connection issue
//                                    Toast.makeText(getContext(), "There seems to be some network problem...please try sending again in some time", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        } else {
//                            Toast.makeText(getContext(), "Sorry, not authorized to delete the message", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//
//
//                AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.show();
//            }
//        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();

//        mainToolbar.getMenu().clear();

        }



        @Override
        public void onMessageClick(Message message) {

            //if message is deleted, do nothing
            if(message.getDeleted()){
                return;
            }

            //if message has attachment, open the viewer to handle
            //otherwise, do nothing
            if(message.getHasAttachment() != null && message.getHasAttachment()
                    && message.getAttachmentUploaded() != null && message.getAttachmentUploaded()){
                //check if the attachment is downloaded
//            try {
//                File attachment = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getAttachmentExtension(), message.getAttachmentMime());
//                if(attachment != null && attachment.exists()){
                //open web viewer
                if(message.getAttachmentMime().contains("image")){
                    openImage(message);
                }
//                    if(message.getAttachmentMime().contains("audio")){
//                        openAudio(message, null);
//                    }
//                    if(message.getAttachmentMime().contains("video")){
//                        openVideo(message);
//                    }
//
            }

        }

        private void openAudio(Message message, Uri uri) {
            //if the message is not uploaded yet, there is no use of trying to download or play it
            if(message.getAttachmentUploaded()) {
                Bundle args = new Bundle();
                args.putSerializable("message", message);
                args.putString("audioUri", String.valueOf(uri));
                navController.navigate(R.id.action_groupAdminUserMessagesFragment_to_audioViewerFragment, args);
            }
            else{
                Toast.makeText(getContext(), "Audio not on found on server. If the message is too new, please wait for the audio to be uploaded completely to the server", Toast.LENGTH_LONG).show();
            }


        }

        private void openImage(Message message) {
            //for image we need to check if it is downloaded
            //if not, start downloading it
            if(message.getAttachmentUri() != null && MediaHelper.doesUriFileExists(getContext().getContentResolver(), Uri.parse(message.getAttachmentUri()))){
                Bundle args = new Bundle();
                args.putSerializable("message", message);
                navController.navigate(R.id.action_groupAdminUserMessagesFragment_to_imageViewerFragment, args);
            }
            else{
                //if the message is not uploaded yet, there is no use of trying it to download
                if(message.getAttachmentUploaded()){
                    handleDownloadAttachment(message);
                }
                else{
                    Toast.makeText(getContext(), "Image not on found on server. If the message is too new, please wait for the image to be uploaded completely to the server", Toast.LENGTH_LONG).show();
                }

            }

        }

        private void openVideo(Message message, Uri uri) {
            //if the message is not uploaded yet, there is no use of trying to download or play it
            if(message.getAttachmentUploaded()){
                Bundle args = new Bundle();
                args.putSerializable("message", message);
                args.putString("videoUri", String.valueOf(uri));
                navController.navigate(R.id.action_groupAdminUserMessagesFragment_to_videoViewerFragment, args);
            }
            else{
                Toast.makeText(getContext(), "Video not on found on server. If the message is too new, please wait for the video to be uploaded completely to the server", Toast.LENGTH_LONG).show();
            }

        }



        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }

        @Override
        public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

            if(requestCode == REQUEST_WRITE_EXTERNAL_STORAGE){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext())
                        .setTitle("Permissions granted")
                        .setMessage("Thank you for the permission. Please click on download again to start the download.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
            else if(requestCode == REQUEST_READ_EXTERNAL_STORAGE){
                Log.d(TAG, "onPermissionsGranted: read external storage permission granted");
                messagesViewModel.loadServerMessages();
//                messagesViewModel.startNetGroupUnreadMessages();
            }

        }

        @Override
        public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                new AppSettingsDialog.Builder(this).build().show();
            }
        }

        private void handleDownloadAttachment(Message message){

            File attachment;

//            messagesViewModel.getDownloadedMessageAttachment().observe(this, new Observer<DomainUpdateResource>() {
//                @Override
//                public void onChanged(DomainUpdateResource domainUpdateResource) {
//                    if(DomainUpdateResource.Status.SUCCESS == domainUpdateResource.status){
//                        messagesViewModel.getDownloadedMessageAttachment().removeObservers(GroupAdminUserMessagesFragment.this);
//                        //viewmodel call to update the cache use case
//                        MessageTempDataHolder messageTempDataHolder  = (MessageTempDataHolder) domainUpdateResource.data;
//                        //just a dummy update call, does nothing really, but updates the cache, so a refresh is forced
//                        messagesViewModel.updateMessageForAttachment(messageTempDataHolder.getMessageId(), true, true, false, messageTempDataHolder.getFileMime(), null);
//
//                    }
//                    if(DomainUpdateResource.Status.ERROR == domainUpdateResource.status){
//                        messagesViewModel.getDownloadedMessageAttachment().removeObservers(getViewLifecycleOwner());
//                        //there was an error downloading the file from server
//                        Toast.makeText(getContext(), "Error downloading from server", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });

            //check permission first
//                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//            if(EasyPermissions.hasPermissions(getContext(), perms)){
//                try {
//                    attachment = FileUtils.createAttachmentFileForMessage(message.getMessageId(), message.getGroupId(), userGroupConversationModel.getOtherUserId(), message.getAttachmentExtension(), message.getAttachmentMime());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return;
//                }
//                messagesViewModel.startDownloadingAttachment(message.getMessageId(), message.getGroupId(), message.getSentAt(), message.getAttachmentExtension(), attachment.getAbsolutePath(), message.getAttachmentMime() );
//            }
//            else{
//                EasyPermissions.requestPermissions(this, "This permission is needed for downloading image messages", REQUEST_WRITE_EXTERNAL_STORAGE, perms);
//            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                //first set downloading to true
                messagesViewModel.updateMessageForAttachment(message.getMessageId(), true, true, true, message.getAttachmentMime(), null);

                try {
                    attachment = FileUtils.createAttachmentFileForMessageAttachment(getContext(), message.getGroupId(), userGroupConversationModel.getOtherUserId(), message.getMessageId(),message.getAttachmentExtension(), message.getAttachmentMime());
//                messagesViewModel.startDownloadingAttachment(message.getMessageId(), message.getGroupId(), message.getSentAt(), message.getAttachmentExtension(), attachment.getAbsolutePath(), message.getAttachmentMime() );
                    messagesViewModel.startDownloadingAttachment(message, attachment.getAbsolutePath() );

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Exception - "+e.getMessage());
                    return;
                }


            }
            else{
                String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if(EasyPermissions.hasPermissions(getContext(), perms)){
                    //first set downloading to true
                    messagesViewModel.updateMessageForAttachment(message.getMessageId(), true, true, true, message.getAttachmentMime(), null);
                    try {
                        attachment = FileUtils.createAttachmentFileForMessage(message.getMessageId(), userGroupModel.getGroupName(),userGroupConversationModel.getOtherUserId(), message.getAttachmentExtension(), message.getAttachmentMime());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Exception - "+e.getMessage());
                        return;
                    }
//                messagesViewModel.startDownloadingAttachment(message.getMessageId(), message.getGroupId(), message.getSentAt(), message.getAttachmentExtension(), attachment.getAbsolutePath(), message.getAttachmentMime() );
                    messagesViewModel.startDownloadingAttachment(message, attachment.getAbsolutePath() );
                }
                else{
                    EasyPermissions.requestPermissions(this, "This permission is needed for downloading image messages", REQUEST_WRITE_EXTERNAL_STORAGE, perms);
                }
            }
        }


        private void handleUploadAttachment(final Message message, File attachment){
            //start observing imageupload first, before publishing the message
//            messagesViewModel.getUploadMessageAttachment().observe(this, new Observer<DomainUpdateResource>() {
//                @Override
//                public void onChanged(DomainUpdateResource domainUpdateResource) {
//                    /**
//                     * two things need to happen here -
//                     * If status is success, that means that the image is uploaded, now -
//                     * 1. publish the message again - messageId is same, so new record should not be created
//                     */
//                    if(DomainUpdateResource.Status.SUCCESS == domainUpdateResource.status){
//                        messagesViewModel.getUploadMessageAttachment().removeObservers(GroupAdminUserMessagesFragment.this);
////                            Log.d(TAG, "type - : "+ getContext().getContentResolver().getType(Uri.fromFile(new File(attachment.getAttachmentURI()))));
//                        //viewmodel call to update the cache use case
//                        MessageTempDataHolder messageTempDataHolder  = (MessageTempDataHolder) domainUpdateResource.data;
//
//                        //publish again - but this time send the old messageId, sentAt and file extension, so that the message is updated not new created
//                        messagesViewModel.publishIoTMessage(messageTempDataHolder.getMessageId(), String.valueOf(message.getMessageText()), messageTempDataHolder.getSentAt(), message.getNotify(), true, true, false, messageTempDataHolder.getFileExtension(), messageTempDataHolder.getFileMime(), false, message.getTnBlob(),
//                                MessageGroupsConstants.USER_ADMIN_MESSAGES, message.getSentTo(), message.getSentToUserName(), message.getSentToUserRole(), message.getSentToImageTS(), new PublishMessageCallback() {
//                            @Override
//                            public void publishSuccessful() {
//                                //do nothing, all good
//                            }
//
//                            @Override
//                            public void publishFailed() {
//                                Toast.makeText(getContext(), "There seems to be some problem uploading, please try again after sometime", Toast.LENGTH_SHORT).show();
//                            }
//                        });
////                            messagesViewModel.updateMessageForAttachment(messageTempDataHolder.getMessageId(), true, true, true, attachment.getAttachmentURI());
//
//                    }
//
//                }
//            });

            //first copy to app folder
//            File attachmentFile = FileUtils.copyAttachmentToApp(FileUtils.getUri(attachment), message.getSentTo(), message.getMessageId(), message.getGroupId(), getContext());
//            messagesViewModel.startUploadingAttachment(message.getMessageId(), message.getGroupId(), message.getSentAt(), message.getAttachmentExtension(), message.getAttachmentMime(), attachmentFile);
            //first copy to app folder
            if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
//            File attachmentFile = FileUtils.copyAttachmentToApp(FileUtils.getUri(attachment), "Group", message.getMessageId(), message.getGroupId(), getContext());
//            String galleryImageUri = MediaHelper.saveMediaToGallery(getContext(), getActivity().getContentResolver(), attachmentFile.getAbsolutePath(), message.getMessageId()+"-"+message.getGroupId(), "image/*", group.getGroupName());
//            message.setAttachmentUri(galleryImageUri);
//            messagesViewModel.startUploadingAttachment(message.getMessageId(), message.getGroupId(), message.getSentAt(), message.getAttachmentExtension(), message.getAttachmentMime(), attachmentFile);
//            messagesViewModel.startUploadingAttachment(message.getMessageId(), message.getGroupId(), message.getSentAt(), message.getAttachmentExtension(), message.getAttachmentMime(), attachment);
                messagesViewModel.startUploadingAttachment(message.getMessageId(), new String[]{attachment.getPath()});
            }
            else{
//            messagesViewModel.startUploadingAttachment(message.getMessageId(), message.getGroupId(), message.getSentAt(), message.getAttachmentExtension(), message.getAttachmentMime(), attachment);
                messagesViewModel.startUploadingAttachment(message.getMessageId(), new String[]{attachment.getPath()});
            }
        }

        @Override
        public void onDownloadClicked(Message message) {
            if(!NetworkHelper.isOnline()){
                Toast.makeText(getContext(), "No Network: Download needs network connection", Toast.LENGTH_SHORT).show();
                return;
            }
            handleDownloadAttachment(message);
        }

        @Override
        public void onUploadClicked(Message message, File fileToUpload) {
            if(!NetworkHelper.isOnline()){
                Toast.makeText(getContext(), "No Network: Upload needs network connection", Toast.LENGTH_SHORT).show();
                return;
            }
//            handleUploadAttachment(message, fileToUpload);
            messagesViewModel.startUploadingAttachment(message.getMessageId(), new String[]{fileToUpload.getPath()});

        }

        @Override
        public void onMediaPlayClicked(Message message, Uri uri) {
            //if message is deleted, do nothing
            if(message.getDeleted()){
                return;
            }

            //if message has attachment, open the viewer to handle
            //otherwise, do nothing
            if(message.getHasAttachment() != null && message.getHasAttachment()){

                if(message.getAttachmentMime().contains("image")){
                    openImage(message);
                }
                if(message.getAttachmentMime().contains("audio")){
                    openAudio(message, uri);
                }
                if(message.getAttachmentMime().contains("video")){
                    openVideo(message, uri);
                }
            }
        }

        @Override
        public void onLoginSuccess() {

            Log.d(TAG, "onLoginSuccess: ");

            /**
             * sometimes when the user is on messages for a long time, the cognito session is expired
             * when user comes back, onResume() happens which starts the process of getting credentials
             * but the connectToIoT is called before waiting for the new token. So, basically, connectToIoT is called
             * before the session is established and hence credentials needed for IoT fail and the connection is not formed
             * so, we need to make the IoT connection only after login is successful
             */
//            IoTHelper.getInstance().connectToIoT(group.getGroupId(), false); //this will trigger onChanged() for connectionEstablished
            IoTHelper.getInstance().connectToIoT(messagesViewModel.getTopicName(), false); //this will trigger onChanged() for connectionEstablished


            Log.d(TAG, "onLoginSuccess: connection done");
            if(alreadyThere)
                return;

//            initRecyclerView();
//            subscribeObservers();

            messagesViewModel.loadServerMessages();
            messagesViewModel.startNetGroupUnreadMessages();


            alreadyThere = true;
        }


    @Override
    public void whileLoginInProgress() {
        Log.d(TAG, "whileLoginInProgress: ");

        if(alreadyThere)
            return;

        initRecyclerView();
        subscribeObservers();

        messagesViewModel.loadServerMessages();
        messagesViewModel.startNetGroupUnreadMessages();

    }

        @Override
        public void onNetworkOff() {
            networkOffImageView.setVisibility(View.VISIBLE);
            //can't do this here, there could be a case that this method gets called
            //when the onCreateView() has not finished. So, all the widgets are null right now.
            //this ends up throwing NPE
//        sendButton.setEnabled(false);
//        attachmentButton.setEnabled(false);
//        normalButton.setEnabled(false);
//        urgentButton.setEnabled(false);
//        clearChat.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Toast.makeText(getContext(), "Sorry, network connection needed for this action to work", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });
        }

        @Override
        public void onNetworkOn() {
            networkOffImageView.setVisibility(View.GONE);
            //how about check IoT connection here and try again when connected?
            if(IoTHelper.getInstance().getCurrentStatus() != null && IoTHelper.getInstance().getCurrentStatus() == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost){
                Log.d(TAG, "onNetworkOn: going to recreate on thread - "+Thread.currentThread().getName());
                //so connection was lost, try connecting again
//                IoTHelper.getInstance().connectToIoT(group.getGroupId(), true);
                IoTHelper.getInstance().connectToIoT(messagesViewModel.getTopicName(), true);
            }
            //can't do this here, there could be a case that this method gets called
            //when the onCreateView() has not finished. So, all the widgets are null right now.
            //this ends up throwing NPE
//        sendButton.setEnabled(true);
//        attachmentButton.setEnabled(true);
//        normalButton.setEnabled(true);
//        urgentButton.setEnabled(true);
//        clearChat.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                messagesViewModel.clearMessages();
//                return true;
//            }
//        });
        }

        @Override
        public void onInviteMembersClicked() {
            //if message is a welcome message and user has clicked the invite members button
            //this is a welcome message
            Bundle args = new Bundle();
            args.putSerializable("group", userGroupModel);
            navController.navigate(R.id.action_groupAdminUserMessagesFragment_to_inviteContactsDialog, args);
            return;
        }

        @Override
        public void onCloseWelcomeMessageClicked(Message message) {
            //remove the message from the cache
            messagesViewModel.deleteLocalMessage(message);
        }

        @Override
        public void onUserImageClicked(Message message) {
//        Toast.makeText(getContext(), "Show user Image bigger or something", Toast.LENGTH_SHORT).show();
            Bundle args = new Bundle();
            args.putSerializable("message", message);
            navController.navigate(R.id.action_groupAdminUserMessagesFragment_to_userImageDisplayFragment, args);
        }
    }

