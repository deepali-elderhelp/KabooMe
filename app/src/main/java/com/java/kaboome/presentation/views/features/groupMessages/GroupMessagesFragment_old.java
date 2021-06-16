//package com.java.kaboome.presentation.views.features.groupMessages;
//
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.FrameLayout;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AppCompatButton;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.view.ViewCompat;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.Observer;
//import androidx.lifecycle.ViewModelProviders;
//import androidx.navigation.NavController;
//import androidx.navigation.fragment.NavHostFragment;
//import androidx.paging.PagedList;
//import androidx.recyclerview.widget.DefaultItemAnimator;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.RequestManager;
//import com.java.kaboome.R;
//import com.java.kaboome.data.entities.Message;
//import com.java.kaboome.domain.entities.DomainMessage;
//import com.java.kaboome.domain.entities.DomainResource;
//import com.java.kaboome.domain.entities.DomainUpdateResource;
//import com.java.kaboome.presentation.entities.ContactModel;
//import com.java.kaboome.presentation.entities.IoTMessage;
//import com.java.kaboome.presentation.entities.UserGroupModel;
//import com.java.kaboome.presentation.helpers.FileUtils;
//import com.java.kaboome.presentation.helpers.IoTHelper;
//import com.java.kaboome.presentation.helpers.MessageDeleteCheckHelper;
//import com.java.kaboome.presentation.images.ImageHelper;
//import com.java.kaboome.presentation.viewModelProvider.CustomViewModelProvider;
//import com.java.kaboome.presentation.views.features.groupMessages.adapter.DownloadClickListener;
//import com.java.kaboome.presentation.views.features.groupMessages.adapter.MediaPlayClickListener;
//import com.java.kaboome.presentation.views.features.groupMessages.adapter.MessageListViewAdapter;
//import com.java.kaboome.presentation.views.features.groupMessages.adapter.UploadClickListener;
//import com.java.kaboome.presentation.views.features.groupMessages.viewmodel.MessageTempDataHolder;
//import com.java.kaboome.presentation.views.features.groupMessages.viewmodel.MessagesViewModel;
//import com.java.kaboome.presentation.views.widgets.MessageInput;
//import com.java.kaboome.presentation.views.widgets.MessagesList;
//import com.paginate.Paginate;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Date;
//import java.util.List;
//import java.util.UUID;
//
//import pub.devrel.easypermissions.AppSettingsDialog;
//import pub.devrel.easypermissions.EasyPermissions;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class GroupMessagesFragment_old extends Fragment implements EasyPermissions.PermissionCallbacks, MessageInput.InputListener, MessageInput.TypingListener, MessageInput.AttachmentsListener,
//        MessageListViewAdapter.OnMessageLongClickListener<Message>, MessageListViewAdapter.OnMessageClickListener,
//        MediaPlayClickListener,
//        DownloadClickListener, UploadClickListener {
//
//    private static final String TAG = "KMIoTGroupMessagesFrag";
//    private MessagesViewModel messagesViewModel;
//    private View rootView;
//    private Toolbar mainToolbar;
//    private UserGroupModel group;
//    private MessageListViewAdapter adapter;
//    private MessagesList recyclerView;
//    private MessageInput messageInput;
//    private AppCompatButton urgentButton;
//    private AppCompatButton normalButton;
//    private boolean urgentChecked = false;
//    private NavController navController;
//    private boolean firstLoad = true;
//    private FrameLayout newMessagesFlashIcon;
////    private SimpleExoPlayer exoPlayer; //Media Player for all the audio files
////    private DefaultDataSourceFactory dataSourceFactory;
//    private static final int REQUEST_DOWNLOAD_CODE=1001;
//    private static final int REQUEST_READ_EXTERNAL_STORAGE = 101;
//    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 102;
//
//
//    public GroupMessagesFragment_old() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        Log.d(TAG, "onCreate: ");
//        super.onCreate(savedInstanceState);
//
//        this.group = (UserGroupModel)getArguments().getSerializable("group");
//        messagesViewModel = ViewModelProviders.of(this, new CustomViewModelProvider(group)).get(MessagesViewModel.class);
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        rootView = inflater.inflate(R.layout.fragment_group_messages, container, false);
////        nestedScrollView = rootView.findViewById(R.id.frag_messages_scroll_top);
//        final AppCompatActivity act = (AppCompatActivity) getActivity();
//        mainToolbar = act.findViewById(R.id.mainToolbar);
//        mainToolbar.getMenu().clear(); //clearing old menu if any
//        mainToolbar.inflateMenu(R.menu.messages_menu);
//
//        mainToolbar.setTitle(group.getGroupName());
//        MenuItem clearChat = mainToolbar.getMenu().findItem(R.id.group_message_clear);
//        clearChat.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                messagesViewModel.clearMessages();
//                return true;
//            }
//        });
//
////        MessageInput input = (MessageInput) rootView.findViewById(R.id.fr_gr_me_layout_chatbox);
////        input.setInputListener(this);
////        input.setTypingListener(this);
////        input.setAttachmentsListener(this);
//
////        TextView input = rootView.findViewById(R.id.fr_gr_me_layout_chatbox);
//
//        recyclerView = rootView.findViewById(R.id.fr_gr_me_reyclerview_message_list);
//        newMessagesFlashIcon = rootView.findViewById(R.id.fr_gr_me_new_messages_icon);
//        urgentButton = rootView.findViewById(R.id.urgent_button);
//        urgentButton.setOnClickListener(urgentButtonClicked);
//        normalButton = rootView.findViewById(R.id.normal_button);
//        normalButton.setOnClickListener(normalButtonClicked);
//        messageInput = rootView.findViewById(R.id.fr_gr_me_layout_chatbox);
//
//        messageInput.setInputListener(this);
//        messageInput.setTypingListener(this);
//        messageInput.setAttachmentsListener(this);
//
//        navController = NavHostFragment.findNavController(GroupMessagesFragment_old.this);
//
//        return rootView;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//
//
//
//        MutableLiveData attachmentPicked = navController.getCurrentBackStackEntry()
//                .getSavedStateHandle()
//                .getLiveData("attachments");
//        attachmentPicked.observe(getViewLifecycleOwner(), new Observer() {
//            @Override
//            public void onChanged(Object o) {
//                Log.d(TAG, "Received attachments - "+o);
//                if( o != null){ //it could be null if coming from Viewers
//
//                    Attachment[] attachments = (Attachment[]) o;
//
//                    //TODO: right now only one attachment, handle more than one
//                    final Attachment attachment = attachments[0];
//                    Log.d(TAG, "Attachment details - "+attachment.getAttachmentURI());
//                    final String caption = attachment.getAttachmentCaption();
//                    final String attachmentURI = attachment.getAttachmentURI();
//                    //start observing imageupload first, before publishing the message
////                    messagesViewModel.getUploadMessageAttachment().observe(getViewLifecycleOwner(), new Observer<DomainUpdateResource>() {
////                        @Override
////                        public void onChanged(DomainUpdateResource domainUpdateResource) {
////                            /**
////                             * two things need to happen here -
////                             * If status is success, that means that the image is uploaded, now -
////                             * 1. publish the message again - messageId is same, so new record should not be created
////                             */
////                            if(DomainUpdateResource.Status.SUCCESS == domainUpdateResource.status){
//////                            Log.d(TAG, "type - : "+ getContext().getContentResolver().getType(Uri.fromFile(new File(attachment.getAttachmentURI()))));
////                                //viewmodel call to update the cache use case
////                                MessageTempDataHolder messageTempDataHolder  = (MessageTempDataHolder) domainUpdateResource.data;
////
////                                //publish again - but this time send the old messageId, sentAt and file extension, so that the message is updated not new created
////                                messagesViewModel.publishIoTMessage(messageTempDataHolder.getMessageId(), String.valueOf(caption), messageTempDataHolder.getSentAt(), attachment.isAttachmentPriority()? 1 : 2, true, true, false, messageTempDataHolder.getFileExtension(), messageTempDataHolder.getFileMime());
//////                            messagesViewModel.updateMessageForAttachment(messageTempDataHolder.getMessageId(), true, true, true, attachment.getAttachmentURI());
////
////                            }
////
////                        }
////                    });
//                    //user has selected the attachment, now create a message for it and send it
//                    String fileExtension = FileUtils.getExtension(Uri.parse(attachmentURI), getContext());
//                    String fileMime = FileUtils.getMimeType(getContext(), Uri.parse(attachmentURI));
//                    String messageId = UUID.randomUUID().toString();
//                    Long sentAt = (new Date()).getTime();
//                    messagesViewModel.publishIoTMessage(messageId, String.valueOf(caption), sentAt, attachment.isAttachmentPriority()? 1 : 2, true, false, true, fileExtension, fileMime, false);
//
//                    Message message = new Message();
//                    message.setMessageId(messageId);
//                    message.setGroupId(group.getGroupId());
//                    message.setAttachmentMime(fileMime);
//                    message.setAttachmentExtension(fileExtension);
//                    message.setSentAt(sentAt);
//                    message.setNotify(attachment.isAttachmentPriority()? 1 : 2);
//                    message.setMessageText(String.valueOf(caption));
//                    handleUploadAttachment(message, FileUtils.getFile(getContext(), Uri.parse(attachmentURI)));
//                    //first copy to app folder
////                    File attachmentFile = FileUtils.copyAttachmentToApp(Uri.parse(attachmentURI), messageId, getContext());
////                    messagesViewModel.startUploadingAttachment(messageId, sentAt, fileExtension, fileMime, attachmentFile);
//                }
//
//
//            }
//        });
//
//
//    }
//
//
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        initRecyclerView();
//        subscribeObservers();
//
////        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
////        if (!EasyPermissions.hasPermissions(getContext(), perms)) {
////            EasyPermissions.requestPermissions(this, "This permission is needed for displaying image messages", REQUEST_EXTERNAL_STORAGE, perms);
////        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        //the following is needed because when waking up after screen change on something, the old value comes up
//        //which is true for enabled - then it tries to subscribe to a topic which fails since really there is no
//        //connection.
//        //so here I set the value to false, to subscribe topic does not happen
//        //onChange is called but it does nothing when the value is false
//        IoTHelper.getInstance().setConnectionEstablished(false);
//        Log.d(TAG, "onResume: ");
//        //check if the mqtt connection is still there
//        //if it has been dropped, you need to reconnect and resubscribe
//        //there is no way to get the status of the connection
//        //so you just call connect (it will return the same connection of existing
//        //or it will return a new connection
//        //now you don't know whether the old subscription is there or it has been dropped
//        //so, just unsubscribe it (it will not throw any error if the subscription does not exist)
//        //and then subscribe it
//        //there is no suback for subscription - so the only way to know if the subscription failed
//        //is that on sending message, there is no ack or the message is not received
//
//        IoTHelper.getInstance().connectToIoT(); //this will trigger onChanged() for connectionEstablished
//        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
//        if(!EasyPermissions.hasPermissions(getContext(), perms)){
//            EasyPermissions.requestPermissions(this, "This permission is needed for showing image messages", REQUEST_READ_EXTERNAL_STORAGE, perms);
//        }
//
//        MutableLiveData contactPicked = navController.getCurrentBackStackEntry()
//                .getSavedStateHandle()
//                .getLiveData("contact");
//        contactPicked.observe(getViewLifecycleOwner(), new Observer() {
//            @Override
//            public void onChanged(Object o) {
//                if(o != null){
//                    ContactModel contactModel = (ContactModel) o;
//                    messageInput.getInputEditText().setText(contactModel.getName()+" \n "+contactModel.getPhone());
//                }
//            }
//        });
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.d(TAG, "onPause: ");
//        //this is to take care that the GroupLastSeenTS gets updated on pause
//        messagesViewModel.onBackPressed();
//
//
//    }
//
//    private void subscribeObservers() {
//
//        messagesViewModel.getMessagesList().observe(getViewLifecycleOwner(), new Observer<PagedList<Message>>() {
//            @Override
//            public void onChanged(@Nullable PagedList<Message> messages) {
////                int scrollToIndex = messages.size() - 1; //by default to the bottom
//                int indexToInsertNewMessageHeader = -1;
//                if(firstLoad){
//                    Long lastMessageTSForGroup = group.getLastAccessed();
//                    for(int i=(messages.size()-1);i>= 0;i--){
//                        Message message = messages.get(i);
//                        if(message.getSentAt() > lastMessageTSForGroup){
//                            indexToInsertNewMessageHeader = i; //first message which is new
//                            break;
//                        }
//                    }
//                }
//                adapter.submitList(messages, indexToInsertNewMessageHeader);
//
//
//                Log.d(TAG, "onSubmitList called for adapter and size is "+messages.size());
//            }
//        });
//
//        messagesViewModel.getServerMessages().observe(getViewLifecycleOwner(), new Observer<DomainResource<List<DomainMessage>>>() {
//            @Override
//            public void onChanged(@Nullable DomainResource<List<DomainMessage>> listDomainResource) {
//                Log.d(TAG, "onChanged: Data came back, but not needed really");
//            }
//        });
//
//
//        Paginate.with(recyclerView, messageListPaginateCallback)
//                .setLoadingTriggerThreshold(2)
//                .addLoadingListItem(false)
//                .build();
//
//        messagesViewModel.getConnectionEstablished().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(@Nullable Boolean aBoolean) {
//                if(aBoolean){
//                    Log.d(TAG, "connection established, enable the send button");
//
//                    try {
//                        IoTHelper.getInstance().subscribeToGroup(group.getGroupId());
//                        messageInput.getButton().setEnabled(true);
//                    } catch (Exception e) {
//                        if(messagesViewModel.connectionErrorToBeDisplayed(e)){
//                            Toast.makeText(getContext(), "Could not connect to network, please check", Toast.LENGTH_LONG).show();
//                        }
//                        Log.d(TAG, "Exception - "+e.getMessage());
//                    }
//                }
//                else{
//                    messageInput.getButton().setEnabled(false);
//                }
//            }
//        });
//
//        messagesViewModel.getIotMessageReceived().observe(getViewLifecycleOwner(), new Observer<IoTMessage>() {
//            @Override
//            public void onChanged(@Nullable IoTMessage message) {
//                if(message != null){
//                    Log.d(TAG, "New message received - "+message);
//                    //also add to the internal cache
//                    messagesViewModel.handleMessageArrival(message);
//
//                }
//
//            }
//        });
//
//    }
//
//    private void initRecyclerView() {
//
//
////            adapter = new MessageListViewAdapter(getContext(), exoPlayer, dataSourceFactory);
//            adapter = new MessageListViewAdapter(getContext());
//            adapter.setOnMessageLongClickListener(this);
//            adapter.setOnMessageClickListener(this);
//            adapter.setDownloadClickListener(this);
//            adapter.setUploadClickListener(this);
//            adapter.setMediaPlayClickListener(this);
//
//            recyclerView.setAdapter(adapter, true, (RequestManager) initGlide(), newMessagesFlashIcon);
//            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//                    if(!recyclerView.canScrollVertically(1) && newMessagesFlashIcon != null){
//                        newMessagesFlashIcon.setVisibility(View.INVISIBLE);
//                    }
//                }
//
//                @Override
//                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                    super.onScrollStateChanged(recyclerView, newState);
//                    firstLoad = false;
//                }
//            });
//            recyclerView.setItemAnimator(new DefaultItemAnimator());
//
//    }
//
//
//    private Object initGlide() {
//        return ImageHelper.getRequestManager(getContext(), -1, R.drawable.account_group_grey);
//    }
//
//    @Override
//    public void onMessageLongClick(Message message) {
//        getMessageCopyDeleteDialog(message);
//    }
//
//    @Override
//    public boolean onSubmit(CharSequence input) {
//        messagesViewModel.publishIoTMessage(String.valueOf(input), urgentChecked? 1 : 2);
////        hideKeyboardFrom(getContext(), this.rootView);
//        return true; //so that the text is cleared
//    }
//
//    public static void hideKeyboardFrom(Context context, View view) {
//        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }
//
//    private View.OnClickListener urgentButtonClicked = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            urgentButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_button_red_gradient_background));
//            ViewCompat.setBackground(messageInput.getButton(), getResources().getDrawable(R.drawable.send_urgent_background));
//            urgentChecked =  true;
//            urgentButton.setEnabled(false);
//            normalButton.setEnabled(true);
//        }
//    };
//
//    private View.OnClickListener normalButtonClicked = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            urgentButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_button_grey_gradient_background));
//            ViewCompat.setBackground(messageInput.getButton(), getResources().getDrawable(R.drawable.send_background));
//            urgentChecked =  false;
//            urgentButton.setEnabled(true);
//            normalButton.setEnabled(false);
//        }
//    };
//
//    @Override
//    public void onAddAttachments() {
//
////        Intent openIntent = new Intent(Intent.ACTION_PICK);
//////        openIntent.setType(ContactsContract.Contacts.CONTENT_VCARD_TYPE);
////        openIntent.setType("text/plain");
////        startActivityForResult(openIntent, OPEN_REQUEST_CODE);
//
//
//        Bundle args = new Bundle();
//        args.putString("groupId", group.getGroupId());
//        navController.navigate(R.id.action_groupMessagesFragment_to_messageAttachmentsDialog, args);
//
//    }
//
//    @Override
//    public void onStartTyping() {
//        recyclerView.getLayoutManager().scrollToPosition(0);
//    }
//
//    @Override
//    public void onStopTyping() {
//
//    }
//
//
//
//    private Paginate.Callbacks messageListPaginateCallback = new Paginate.Callbacks() {
//        @Override
//        public void onLoadMore() {
//            //call the use case to load the messages
//            Log.d(TAG, "onLoadMore: ");
//            if(messagesViewModel != null)
//                messagesViewModel.loadServerMessages();
//        }
//
//        @Override
//        public boolean isLoading() {
////            Log.d(TAG, "isLoading: ");
//            if(messagesViewModel != null)
//                return messagesViewModel.isLoading();
//            else
//                return false;
//        }
//
//        @Override
//        public boolean hasLoadedAllItems() {
////            Log.d(TAG, "hasLoadedAllItems: ");
//            if(messagesViewModel != null){
//                if(messagesViewModel.isHasLoadedAll()){
//                    Log.d(TAG, "hasLoadedAllItems: ");
//                }
//                return messagesViewModel.isHasLoadedAll();
//            }
//
//            else
//                return false;
//        }
//    };
//
//    private void getMessageCopyDeleteDialog(final Message message){
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
//        alertDialogBuilder.setMessage("What do you want to do?");
//        alertDialogBuilder.setPositiveButton("Copy text",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
////                        Toast.makeText(MessagesActivity.this,"Yes, copy text",Toast.LENGTH_LONG).show();
//                        GroupMessagesFragment_old.this.adapter.copyToClipboard(getContext(), message.getMessageText());
//                    }
//                });
//
//        alertDialogBuilder.setNegativeButton("Delete the Message",new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if(MessageDeleteCheckHelper.canDeleteMessage(message)){
//                    messagesViewModel.deleteMessage(message);
//                }
//                else{
//                    Toast.makeText(getContext(),"Sorry, not authorized to delete the message",Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//
////        mainToolbar.getMenu().clear();
//
//    }
//
//
//
//    @Override
//    public void onMessageClick(Message message) {
//
//        //if message is deleted, do nothing
//        if(message.getDeleted()){
//            return;
//        }
//
//        //if message has attachment, open the viewer to handle
//        //otherwise, do nothing
//        if(message.getHasAttachment() != null && message.getHasAttachment()
//            && message.getAttachmentUploaded() != null && message.getAttachmentUploaded()){
//            //check if the attachment is downloaded
////            try {
////                File attachment = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getAttachmentExtension(), message.getAttachmentMime());
////                if(attachment != null && attachment.exists()){
//                    //open web viewer
//                    if(message.getAttachmentMime().contains("image")){
//                        openImage(message);
//                    }
////                    if(message.getAttachmentMime().contains("audio")){
////                        openAudio(message, null);
////                    }
////                    if(message.getAttachmentMime().contains("video")){
////                        openVideo(message);
////                    }
////
//        }
//
//    }
//
//    private void openAudio(Message message, Uri uri) {
//        Bundle args = new Bundle();
//        args.putSerializable("message", message);
//        args.putString("audioUri", String.valueOf(uri));
//        navController.navigate(R.id.action_groupMessagesFragment_to_audioViewerFragment, args);
//    }
//
//    private void openImage(Message message) {
//        Bundle args = new Bundle();
//        args.putSerializable("message", message);
////        args.putString("caption", caption);
//        navController.navigate(R.id.action_groupMessagesFragment_to_imageViewerFragment, args);
//    }
//
//    private void openVideo(Message message, Uri uri) {
//        Bundle args = new Bundle();
//        args.putSerializable("message", message);
//        args.putString("videoUri", String.valueOf(uri));
////        args.putString("imagePath", imagePath);
////        args.putString("caption", caption);
//        navController.navigate(R.id.action_groupMessagesFragment_to_videoViewerFragment, args);
//    }
//
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//    }
//
//    @Override
//    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
//
//        if(requestCode == REQUEST_WRITE_EXTERNAL_STORAGE){
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext())
//                    .setTitle("Permissions granted")
//                    .setMessage("Thank you for the permission. Please click on download again to start the download.")
//                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.show();
//        }
//        else if(requestCode == REQUEST_READ_EXTERNAL_STORAGE){
//            Log.d(TAG, "onPermissionsGranted: read external storage permission granted");
//            messagesViewModel.loadServerMessages();
//        }
//
//    }
//
//    @Override
//    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            new AppSettingsDialog.Builder(this).build().show();
//        }
//    }
//
//    private void handleDownloadAttachment(Message message){
//        File attachment;
//        //first set downloading to true
//        messagesViewModel.updateMessageForAttachment(message.getMessageId(), true, true, true, message.getAttachmentMime());
//
//        messagesViewModel.getDownloadedMessageAttachment().observe(this, new Observer<DomainUpdateResource>() {
//            @Override
//            public void onChanged(DomainUpdateResource domainUpdateResource) {
//                if(DomainUpdateResource.Status.SUCCESS == domainUpdateResource.status){
//                    messagesViewModel.getDownloadedMessageAttachment().removeObservers(getViewLifecycleOwner());
//                    //viewmodel call to update the cache use case
//                    MessageTempDataHolder messageTempDataHolder  = (MessageTempDataHolder) domainUpdateResource.data;
//                    //just a dummy update call, does nothing really, but updates the cache, so a refresh is forced
//                    messagesViewModel.updateMessageForAttachment(messageTempDataHolder.getMessageId(), true, true, false, messageTempDataHolder.getFileMime());
//
//                }
//                if(DomainUpdateResource.Status.ERROR == domainUpdateResource.status){
//                    messagesViewModel.getDownloadedMessageAttachment().removeObservers(getViewLifecycleOwner());
//                    //there was an error downloading the file from server
//                    Toast.makeText(getContext(), "Error downloading from server", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        //check permission first
////                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        if(EasyPermissions.hasPermissions(getContext(), perms)){
//            try {
//                attachment = FileUtils.createAttachmentFileForMessage(message.getMessageId(), message.getGroupId(), message.getAttachmentExtension(), message.getAttachmentMime());
//            } catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }
//            messagesViewModel.startDownloadingAttachment(message.getMessageId(), message.getGroupId(), message.getSentAt(), message.getAttachmentExtension(), attachment.getAbsolutePath(), message.getAttachmentMime() );
//        }
//        else{
//            EasyPermissions.requestPermissions(this, "This permission is needed for downloading image messages", REQUEST_WRITE_EXTERNAL_STORAGE, perms);
//        }
//    }
//
//
//    private void handleUploadAttachment(final Message message, File attachment){
//        //start observing imageupload first, before publishing the message
//        messagesViewModel.getUploadMessageAttachment().observe(getViewLifecycleOwner(), new Observer<DomainUpdateResource>() {
//            @Override
//            public void onChanged(DomainUpdateResource domainUpdateResource) {
//                /**
//                 * two things need to happen here -
//                 * If status is success, that means that the image is uploaded, now -
//                 * 1. publish the message again - messageId is same, so new record should not be created
//                 */
//                if(DomainUpdateResource.Status.SUCCESS == domainUpdateResource.status){
//                    messagesViewModel.getUploadMessageAttachment().removeObservers(getViewLifecycleOwner());
////                            Log.d(TAG, "type - : "+ getContext().getContentResolver().getType(Uri.fromFile(new File(attachment.getAttachmentURI()))));
//                    //viewmodel call to update the cache use case
//                    MessageTempDataHolder messageTempDataHolder  = (MessageTempDataHolder) domainUpdateResource.data;
//
//                    //publish again - but this time send the old messageId, sentAt and file extension, so that the message is updated not new created
//                    messagesViewModel.publishIoTMessage(messageTempDataHolder.getMessageId(), String.valueOf(message.getMessageText()), messageTempDataHolder.getSentAt(), message.getNotify(), true, true, false, messageTempDataHolder.getFileExtension(), messageTempDataHolder.getFileMime(), false);
////                            messagesViewModel.updateMessageForAttachment(messageTempDataHolder.getMessageId(), true, true, true, attachment.getAttachmentURI());
//
//                }
//
//            }
//        });
//
//        //first copy to app folder
//        File attachmentFile = FileUtils.copyAttachmentToApp(FileUtils.getUri(attachment), message.getMessageId(), message.getGroupId(), getContext());
//        messagesViewModel.startUploadingAttachment(message.getMessageId(), message.getGroupId(), message.getSentAt(), message.getAttachmentExtension(), message.getAttachmentMime(), attachmentFile);
//    }
//
//    @Override
//    public void onDownloadClicked(Message message) {
//        handleDownloadAttachment(message);
//    }
//
//    @Override
//    public void onUploadClicked(Message message, File fileToUpload) {
//        handleUploadAttachment(message, fileToUpload);
//    }
//
//    @Override
//    public void onMediaPlayClicked(Message message, Uri uri) {
//        //if message is deleted, do nothing
//        if(message.getDeleted()){
//            return;
//        }
//
//        //if message has attachment, open the viewer to handle
//        //otherwise, do nothing
//        if(message.getHasAttachment() != null && message.getHasAttachment()){
//
//            if(message.getAttachmentMime().contains("image")){
//                openImage(message);
//            }
//            if(message.getAttachmentMime().contains("audio")){
//                openAudio(message, uri);
//            }
//            if(message.getAttachmentMime().contains("video")){
//                openVideo(message, uri);
//            }
//        }
//    }
//}
