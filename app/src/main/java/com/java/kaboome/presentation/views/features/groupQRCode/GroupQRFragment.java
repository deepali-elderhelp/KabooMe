package com.java.kaboome.presentation.views.features.groupQRCode;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.helpers.PrintHelper;
import com.java.kaboome.presentation.helpers.QRCodeHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.viewModelProvider.CustomViewModelProvider;
import com.java.kaboome.presentation.views.features.BaseFragment;
import com.java.kaboome.presentation.views.features.groupQRCode.viewmodel.GroupQRViewModel;

import java.io.ByteArrayOutputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupQRFragment extends BaseFragment implements  EasyPermissions.PermissionCallbacks{

    private static final String TAG = "KMGroupQRFragment";

    private View rootView;
    private GroupQRViewModel groupQRViewModel;
    private UserGroupModel userGroupModel;
    private GroupModel groupModel;
    private CircleImageView groupImage;
    private ProgressBar groupImageProgressBar;
    private ImageView qrCodeImage;
    private ImageView appQrImage;
    private Handler handler = new Handler(); //needed for Glide
    private Toolbar mainToolbar;
    private MenuItem shareGroup;
    private WebView mWebView;
    private MenuItem printGroup;

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 102;



    public GroupQRFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userGroupModel = (UserGroupModel) getArguments().getSerializable("group");
        groupQRViewModel = ViewModelProviders.of(this, new CustomViewModelProvider(userGroupModel.getGroupId())).get(GroupQRViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_group_qr, container, false);

        groupImage = rootView.findViewById(R.id.group_qr_code_image);
        groupImageProgressBar = rootView.findViewById(R.id.group_qr_code_image_progress);
        qrCodeImage = rootView.findViewById(R.id.group_qr_code_qr_image);
        appQrImage = rootView.findViewById(R.id.qr_code_for_app);

//        setHasOptionsMenu(true);
        final AppCompatActivity act = (AppCompatActivity) getActivity();
        mainToolbar = act.findViewById(R.id.mainToolbar);
        mainToolbar.getMenu().clear(); //clearing old menu if any
        mainToolbar.inflateMenu(R.menu.qr_print_menu);

        printGroup = mainToolbar.getMenu().findItem(R.id.print_qr_code);
        printGroup.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                doWebViewPrint();
                return true;
            }
        });

        shareGroup = mainToolbar.getMenu().findItem(R.id.share_qr_code);
        shareGroup.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                shareQRCode();
                return true;
            }
        });


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        subscribeObservers();
//        initiateLoading();
//
////        ImageHelper.loadGroupImage(userGroupModel.getGroupId(), userGroupModel.getImageUpdateTimestamp(), ImageHelper.getRequestManager(getActivity(), R.drawable.account_group_grey, R.drawable.account_group_grey), handler, groupImage, null);
//        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(),R.dimen.group_actions_dialog_image_width, userGroupModel.getGroupName());
//        ImageHelper.loadGroupImage(userGroupModel.getGroupId(), userGroupModel.getImageUpdateTimestamp(),
//                ImageHelper.getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
//                handler, groupImage, null);
//
//
////        Bitmap qrCodeImageBitmap = getQRImage(userGroupModel.getGroupId(),2, 4.8, 2.6);
//        Bitmap qrCodeImageBitmap = QRCodeHelper.getQRImage(getContext(),userGroupModel.getGroupId(),2, 4.8, 2.6);
//        qrCodeImage.setImageBitmap(qrCodeImageBitmap);
//
////        Bitmap qrCodeAppBitmap = getQRImage("http://www.kaboome.com", 2, 6, 4);
//        Bitmap qrCodeAppBitmap = QRCodeHelper.getQRImage(getContext(),"http://www.kaboome.com", 2, 6, 4);
//        appQrImage.setImageBitmap(qrCodeAppBitmap);


    }

    private void initiateLoading() {
        groupQRViewModel.loadGroup();
    }

    private void subscribeObservers() {

        groupQRViewModel.getGroup().removeObservers(getViewLifecycleOwner()); //if any old one hanging there
        groupQRViewModel.getGroup().observe(getViewLifecycleOwner(), new Observer<GroupModel>() {
            @Override
            public void onChanged(GroupModel currentGroupModel) {
                Log.d(TAG, "onChanged: ");
                groupModel = currentGroupModel;
                renderData(currentGroupModel);
            }
        });
    }

    //It seems crazy, but the group when comes back with status loading, the data is not null
    //the groupId is set, but all other fields are null
    //once the data comes back after loading from DB, it is fine
    //hence taking care of the null values by either setting them to empty text
    //or loading the data from UserGroupModel if available
    private void renderData(GroupModel groupModel) {
        TextView groupName = rootView.findViewById(R.id.group_qr_code_name);
        if(groupModel.getGroupName() != null)
            groupName.setText(groupModel.getGroupName());
        else
            groupName.setText(userGroupModel.getGroupName());

        TextView publicOrPrivateGroup = rootView.findViewById(R.id.group_qr_code_public_private);
        if(groupModel.getGroupPrivate() == null){
            if(userGroupModel.getPrivate()){
                publicOrPrivateGroup.setText("Private Group");
            }
            else{
                publicOrPrivateGroup.setText("Public Group");
            }
        }
        else{
            if(groupModel.getGroupPrivate()){
                publicOrPrivateGroup.setText("Private Group");
            }
            else{
                publicOrPrivateGroup.setText("Public Group");
            }
        }


        TextView groupDescription = rootView.findViewById(R.id.group_qr_code_description);
        if(groupModel.getGroupDescription() != null)
            groupDescription.setText(groupModel.getGroupDescription());
        else
            groupDescription.setText("");

        //render image again if changed
        if(groupModel.getImageUpdateTimestamp() != null){
//            ImageHelper.loadGroupImage(groupModel.getGroupId(), groupModel.getImageUpdateTimestamp(), ImageHelper.getRequestManager(getContext(), R.drawable.account_group_grey, R.drawable.account_group_grey), handler, groupImage, null);
            Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(),R.dimen.group_actions_dialog_image_width, userGroupModel.getGroupName());
            ImageHelper.getInstance().loadGroupImage(groupModel.getGroupId(), ImageTypeConstants.MAIN, groupModel.getImageUpdateTimestamp(),
                    ImageHelper.getInstance().getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                    handler, groupImage, null);
        }


    }

//    private Bitmap getQRImage(String textToEncodeInQR, int margin, double heightDivider, double widthDivider) {
//        return QRCodeHelper.generate(getContext(), textToEncodeInQR, ErrorCorrectionLevel.H, margin, heightDivider, widthDivider);
//
//    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.qr_print_menu, menu);
//    }


    @AfterPermissionGranted(REQUEST_WRITE_EXTERNAL_STORAGE)
    private void shareQRCode(){
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            //        String htmlDocument = getHtmlOfLayout();

//        Bitmap groupQRCodeBitmap = getQRImage(userGroupModel.getGroupId(),2, 8.2, 6.2);
            Bitmap groupQRCodeBitmap = QRCodeHelper.getQRImage(getContext(),userGroupModel.getGroupId(),2, 8.2, 6.2 );

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            groupQRCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            Bitmap decodedByte = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Uri imageToShare = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), decodedByte, "Share image", null));
//        String textToShare = getTextForSharing();
            String textToShare = PrintHelper.getTextForSharing(getContext(),userGroupModel.getPrivate(), userGroupModel.getGroupName(), groupModel.getGroupDescription() );

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(textToShare));
            share.putExtra(Intent.EXTRA_STREAM, imageToShare);
            startActivity(Intent.createChooser(share, "Share with"));


//        String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
//        String image = "data:image/png;base64," + imgageBase64;


//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "KabooMe Group Invitation");
//        sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(htmlDocument));
////        sendIntent.putExtra(Intent.EXTRA_HTML_TEXT, htmlDocument);
//        sendIntent.setType("text/html");
//
//        Intent shareIntent = Intent.createChooser(sendIntent, "Share using");
//        startActivity(shareIntent);
        }
        else{
            EasyPermissions.requestPermissions(this, "This permission is needed to access your gallery", REQUEST_WRITE_EXTERNAL_STORAGE, perms);
        }

    }


    private void doWebViewPrint() {
        // Create a WebView object specifically for printing
        WebView webView = new WebView(getActivity());
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "page finished loading " + url);
//                createWebPrintJob(view);
                String jobName = getString(R.string.app_name) + " Document";
                PrintHelper.createWebPrintJob(view, getActivity(), jobName);
                mWebView = null;
            }
        });

        // Generate an HTML document on the fly:
//        String htmlDocument = getHtmlOfLayout();
        String htmlDocument = PrintHelper.getHtmlOfLayout(getContext(), userGroupModel.getGroupId(), userGroupModel.getPrivate(),
                userGroupModel.getGroupName(), groupModel.getGroupDescription()
        );


//        String htmlDocument = "<html><body><h1>"+userGroupModel.getGroupName()+"</h1><b>"+groupPrivacyString+"</b><p>"+groupModel.getGroupDescription()+"</p><p><img src='"+image+"' /></p></body></html>";
//        htmlDocument.replace("{IMAGE_PLACEHOLDER}", image);
        Log.d(TAG, "html "+htmlDocument);
        webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);

        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager
        mWebView = webView;
    }

//    private void createWebPrintJob(WebView webView) {
//
//        // Get a PrintManager instance
//        PrintManager printManager = (PrintManager) getActivity()
//                .getSystemService(Context.PRINT_SERVICE);
//
//        String jobName = getString(R.string.app_name) + " Document";
//
//        PrintDocumentAdapter printAdapter;
//        // Get a print adapter instance
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            printAdapter = webView.createPrintDocumentAdapter(jobName);
//        }
//        else{
//            printAdapter = webView.createPrintDocumentAdapter();
//        }
//
//
//        // Create a print job with name and adapter instance
//        PrintJob printJob = printManager.print(jobName, printAdapter,
//                new PrintAttributes.Builder().build());
//
//
////        // Save the job object for later status checking
////        printJobs.add(printJob);
//    }


//    private String getTextForSharing(){
//        StringBuilder stringBuilder = new StringBuilder().append("<html><body>");
//        String printingMain = getString(R.string.group_qr_label_0);
//        String instructionsHeader = getString(R.string.group_qr_label_2);
//        String instruction1 = getString(R.string.group_qr_label_3);
//        String instruction2 = getString(R.string.group_qr_label_4);
//        String instruction3 = getString(R.string.group_qr_label_5);
//        String instruction4 = getString(R.string.group_qr_label_6);
//        String instructionEnd = getString(R.string.group_qr_label_7);
//        String groupPrivacyString = userGroupModel.getPrivate() ? "Private Group":"Public Group";
//
//        stringBuilder.append("<h1 style='text-align: center'>"+userGroupModel.getGroupName()+"</h1>");
//        stringBuilder.append("<br>"+groupPrivacyString);
//        stringBuilder.append("<p>"+groupModel.getGroupDescription()+"</p>");
//        stringBuilder.append("<p><b>"+printingMain+"</b></p>");
//        stringBuilder.append("<p><b>"+instructionsHeader+"</b>");
//        stringBuilder.append("<br>"+instruction1);
//        stringBuilder.append("<br>"+instruction2);
//        stringBuilder.append("<br>"+instruction3);
//        stringBuilder.append("<br>"+instruction4);
//        stringBuilder.append("<br>"+instructionEnd+"</p>");
//
//        return  stringBuilder.toString();
//    }

//    private String getHtmlOfLayout(){
//
//        //first get smaller qr code images
////        Bitmap groupQRCodeBitmap = getQRImage(userGroupModel.getGroupId(),2, 8.2, 6.2);
//        Bitmap groupQRCodeBitmap = QRCodeHelper.getQRImage(getContext(),userGroupModel.getGroupId(),2, 8.2, 6.2);
//
//
////        Bitmap appQRCodeBitmap = getQRImage("http://www.kaboome.com", 2, 20, 16);
//        Bitmap appQRCodeBitmap = QRCodeHelper.getQRImage(getContext(),"http://www.kaboome.com", 2, 20, 16);
//
//        // Convert bitmap to Base64 encoded image for web
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        groupQRCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//        byte[] byteArray = byteArrayOutputStream.toByteArray();
//        String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
//        String image = "data:image/png;base64," + imgageBase64;
//
//        byteArrayOutputStream = new ByteArrayOutputStream();
//        appQRCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//        byteArray = byteArrayOutputStream.toByteArray();
//        String appImgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
//        String appImage = "data:image/png;base64," + appImgageBase64;
//
//
//        String groupPrivacyString = userGroupModel.getPrivate() ? "Private Group":"Public Group";
//        String printingMain = getString(R.string.group_qr_label_1);
//        String instructionsHeader = getString(R.string.group_qr_label_2);
//        String instruction1 = getString(R.string.group_qr_label_3);
//        String instruction2 = getString(R.string.group_qr_label_4);
//        String instruction3 = getString(R.string.group_qr_label_5);
//        String instruction4 = getString(R.string.group_qr_label_6);
//        String instructionEnd = getString(R.string.group_qr_label_7);
//        String helperAppInstall = getString(R.string.group_qr_label_8);
//        String appLink = getString(R.string.group_qr_app_link);
//
//        StringBuilder stringBuilder = new StringBuilder().append("<html><body>");
//        stringBuilder.append("<h1 style='text-align: center'>"+userGroupModel.getGroupName()+"</h1>");
//        stringBuilder.append("<br>"+groupPrivacyString);
//        stringBuilder.append("<p>"+groupModel.getGroupDescription()+"</p>");
//        stringBuilder.append("<p><b>"+printingMain+"</b></p>");
//        stringBuilder.append("<p style='text-align: center'>"+"<img src='"+image+"' /></p>");
//        stringBuilder.append("<p><b>"+instructionsHeader+"</b>");
//        stringBuilder.append("<br>"+instruction1);
//        stringBuilder.append("<br>"+instruction2);
//        stringBuilder.append("<br>"+instruction3);
//        stringBuilder.append("<br>"+instruction4);
//        stringBuilder.append("<br>"+instructionEnd+"</p>");
//        stringBuilder.append("<p><b>"+helperAppInstall+"</b>");
//        stringBuilder.append("<br><h4 style='color:DodgerBlue;'>"+appLink+"</h4></p>");
//        stringBuilder.append("<p style='text-align: right'>"+"<img src='"+appImage+"' /></p>");
//        stringBuilder.append("</body></html>");
//
//       return stringBuilder.toString();
//
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

//        mainToolbar.getMenu().clear();

        shareGroup.setOnMenuItemClickListener(null);
        printGroup.setOnMenuItemClickListener(null);

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if(requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            shareQRCode();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onLoginSuccess() {
//        subscribeObservers();
//        initiateLoading();

//        ImageHelper.loadGroupImage(userGroupModel.getGroupId(), userGroupModel.getImageUpdateTimestamp(), ImageHelper.getRequestManager(getActivity(), R.drawable.account_group_grey, R.drawable.account_group_grey), handler, groupImage, null);
        if(getContext() != null) {
            Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(), R.dimen.group_actions_dialog_image_width, userGroupModel.getGroupName());
            ImageHelper.getInstance().loadGroupImage(userGroupModel.getGroupId(), ImageTypeConstants.MAIN, userGroupModel.getImageUpdateTimestamp(),
                    ImageHelper.getInstance().getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                    handler, groupImage, null);
        }


//        Bitmap qrCodeImageBitmap = getQRImage(userGroupModel.getGroupId(),2, 4.8, 2.6);
//        Bitmap qrCodeImageBitmap = QRCodeHelper.getQRImage(getContext(),userGroupModel.getGroupId(),2, 4.8, 2.6);
//        qrCodeImage.setImageBitmap(qrCodeImageBitmap);
//
////        Bitmap qrCodeAppBitmap = getQRImage("http://www.kaboome.com", 2, 6, 4);
//        Bitmap qrCodeAppBitmap = QRCodeHelper.getQRImage(getContext(),"http://www.kaboome.com", 2, 6, 4);
//        appQrImage.setImageBitmap(qrCodeAppBitmap);

    }

    @Override
    public void whileLoginInProgress() {
        subscribeObservers();
        initiateLoading();

        if(getContext() != null) {
//        ImageHelper.loadGroupImage(userGroupModel.getGroupId(), userGroupModel.getImageUpdateTimestamp(), ImageHelper.getRequestManager(getActivity(), R.drawable.account_group_grey, R.drawable.account_group_grey), handler, groupImage, null);
            Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(), R.dimen.group_actions_dialog_image_width, userGroupModel.getGroupName());
            ImageHelper.getInstance().loadGroupImage(userGroupModel.getGroupId(), ImageTypeConstants.MAIN, userGroupModel.getImageUpdateTimestamp(),
                    ImageHelper.getInstance().getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                    handler, groupImage, null);


//        Bitmap qrCodeImageBitmap = getQRImage(userGroupModel.getGroupId(),2, 4.8, 2.6);
            Bitmap qrCodeImageBitmap = QRCodeHelper.getQRImage(getContext(), userGroupModel.getGroupId(), 2, 4.8, 2.6);
            qrCodeImage.setImageBitmap(qrCodeImageBitmap);

//        Bitmap qrCodeAppBitmap = getQRImage("http://www.kaboome.com", 2, 6, 4);
            Bitmap qrCodeAppBitmap = QRCodeHelper.getQRImage(getContext(), "http://www.kaboome.com", 2, 6, 4);
            appQrImage.setImageBitmap(qrCodeAppBitmap);
        }
    }

    @Override
    public void onNetworkOff() { }

    @Override
    public void onNetworkOn() { }
}
