package com.java.kaboome.presentation.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Base64;
import android.webkit.WebView;


import com.java.kaboome.R;

import java.io.ByteArrayOutputStream;

public class PrintHelper {

    public static void createWebPrintJob(WebView webView, Activity activity, String jobName) {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) activity
                .getSystemService(Context.PRINT_SERVICE);

//

        PrintDocumentAdapter printAdapter;
        // Get a print adapter instance
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            printAdapter = webView.createPrintDocumentAdapter(jobName);
        }
        else{
            printAdapter = webView.createPrintDocumentAdapter();
        }


        // Create a print job with name and adapter instance
        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());


//        // Save the job object for later status checking
//        printJobs.add(printJob);
    }


    public static String getTextForSharing(Context context, boolean groupPrivacy, String groupName, String groupDescription){
        StringBuilder stringBuilder = new StringBuilder().append("<html><body>");
        String printingMain = context.getString(R.string.group_qr_label_0);
        String instructionsHeader = context.getString(R.string.group_qr_label_2);
        String instruction1 = context.getString(R.string.group_qr_label_3);
        String instruction2 = context.getString(R.string.group_qr_label_4);
        String instruction3 = context.getString(R.string.group_qr_label_5);
        String instruction4 = context.getString(R.string.group_qr_label_6);
        String instructionEnd = context.getString(R.string.group_qr_label_7);
        String groupPrivacyString = groupPrivacy ? "Private Group":"Public Group";

        stringBuilder.append("<h1 style='text-align: center'>"+groupName+"</h1>");
        stringBuilder.append("<br>"+groupPrivacyString);
        stringBuilder.append("<p>"+groupDescription+"</p>");
        stringBuilder.append("<p><b>"+printingMain+"</b></p>");
        stringBuilder.append("<p><b>"+instructionsHeader+"</b>");
        stringBuilder.append("<br>"+instruction1);
        stringBuilder.append("<br>"+instruction2);
        stringBuilder.append("<br>"+instruction3);
        stringBuilder.append("<br>"+instruction4);
        stringBuilder.append("<br>"+instructionEnd+"</p>");

        return  stringBuilder.toString();
    }

    public static String getHtmlOfLayout(Context context, String groupId, boolean groupPrivacy, String groupName, String groupDescription){

        //first get smaller qr code images
//        Bitmap groupQRCodeBitmap = getQRImage(userGroupModel.getGroupId(),2, 8.2, 6.2);
        Bitmap groupQRCodeBitmap = QRCodeHelper.getQRImage(context,groupId,2, 8.2, 6.2);


//        Bitmap appQRCodeBitmap = getQRImage("http://www.kaboome.com", 2, 20, 16);
        Bitmap appQRCodeBitmap = QRCodeHelper.getQRImage(context,"http://www.kaboome.com", 2, 20, 16);

        // Convert bitmap to Base64 encoded image for web
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        groupQRCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String image = "data:image/png;base64," + imgageBase64;

        byteArrayOutputStream = new ByteArrayOutputStream();
        appQRCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
        String appImgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String appImage = "data:image/png;base64," + appImgageBase64;


        String groupPrivacyString = groupPrivacy ? "Private Group":"Public Group";
        String printingMain = context.getString(R.string.group_qr_label_1);
        String instructionsHeader = context.getString(R.string.group_qr_label_2);
        String instruction1 = context.getString(R.string.group_qr_label_3);
        String instruction2 = context.getString(R.string.group_qr_label_4);
        String instruction3 = context.getString(R.string.group_qr_label_5);
        String instruction4 = context.getString(R.string.group_qr_label_6);
        String instructionEnd = context.getString(R.string.group_qr_label_7);
        String helperAppInstall = context.getString(R.string.group_qr_label_8);
        String appLink = context.getString(R.string.group_qr_app_link);

        StringBuilder stringBuilder = new StringBuilder().append("<html><body>");
        stringBuilder.append("<h1 style='text-align: center'>"+groupName+"</h1>");
        stringBuilder.append("<br>"+groupPrivacyString);
        stringBuilder.append("<p>"+groupDescription+"</p>");
        stringBuilder.append("<p><b>"+printingMain+"</b></p>");
        stringBuilder.append("<p style='text-align: center'>"+"<img src='"+image+"' /></p>");
        stringBuilder.append("<p><b>"+instructionsHeader+"</b>");
        stringBuilder.append("<br>"+instruction1);
        stringBuilder.append("<br>"+instruction2);
        stringBuilder.append("<br>"+instruction3);
        stringBuilder.append("<br>"+instruction4);
        stringBuilder.append("<br>"+instructionEnd+"</p>");
        stringBuilder.append("<p><b>"+helperAppInstall+"</b>");
        stringBuilder.append("<br><h4 style='color:DodgerBlue;'>"+appLink+"</h4></p>");
        stringBuilder.append("<p style='text-align: right'>"+"<img src='"+appImage+"' /></p>");
        stringBuilder.append("</body></html>");

        return stringBuilder.toString();

    }
}
