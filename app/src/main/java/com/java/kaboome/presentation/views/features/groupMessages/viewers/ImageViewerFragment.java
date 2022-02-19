package com.java.kaboome.presentation.views.features.groupMessages.viewers;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.kaboome.R;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.presentation.images.ImageHelper;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageViewerFragment extends Fragment {

    private View rootView;
    private FrameLayout frameLayout;
    private ImageView imageView;
    private TextView textView;
//    private String pathToPicture;
    private String caption;
    private Message message;
    private Uri uriImage;

    private static final String TAG = "KMImageViewerFragment";

    public ImageViewerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        Bundle messageInfo = getArguments();
        message = (Message) messageInfo.get("message");

        String displayName =  message.getGroupId()+"_Group_"+message.getMessageId()+message.getAttachmentExtension();

//        uriImage = MediaHelper.getMediaImageUri(getActivity().getContentResolver(), displayName);
        uriImage = Uri.parse(message.getAttachmentUri());


//        File attachment = FileUtils.getAttachmentFileForMessage(message.getMessageId(), message.getSentTo(), message.getGroupId(), message.getAttachmentExtension(), message.getAttachmentMime());
//        if(attachment != null && attachment.exists()){
//            pathToPicture = attachment.getPath();
//        }
        caption = message.getMessageText();

        NavController navController = NavHostFragment.findNavController(this);
        navController.getBackStackEntry(R.id.groupMessagesFragment).getSavedStateHandle().set("attachments", null);
        navController.getBackStackEntry(R.id.groupMessagesFragment).getSavedStateHandle().set("contact", null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_image_viewer, container, false);
        frameLayout = rootView.findViewById(R.id.image_viewer_frame);
        textView = rootView.findViewById(R.id.image_viewer_caption);
        imageView = rootView.findViewById(R.id.image_viewer_image);

//        if(pathToPicture != null){
//            Bitmap imageBitmap = BitmapFactory.decodeFile(pathToPicture);
//            int orientation = getImageOrientation((pathToPicture));
//            if(orientation != 0){
//                imageBitmap = checkRotationFromCamera(imageBitmap, orientation);
//            }
//            imageView.setImageBitmap(imageBitmap);
//        }

        if(uriImage != null){
            ImageHelper.getInstance().getRequestManager(getContext())
                    .asBitmap()
                    .load(uriImage)
                    .into(imageView);
        }

        textView.setText(caption);
        return rootView;
    }



    private int getImageOrientation(String imagePath) {
        int rotate = 0;
        if(imagePath == null || imagePath.isEmpty()){
            return rotate;
        }
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    private Bitmap checkRotationFromCamera(Bitmap bitmap, int rotate) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: ");
        super.onDestroyView();

    }
}
