package com.sapmedicines.sapmedicines;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 0;
    public Uri imageUri;
    private static final int FILECHOOSER_RESULTCODE   = 1;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
   // private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
    }
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode,
//                                    Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//         if(requestCode==FILECHOOSER_RESULTCODE)
//        {
//
//            if (null == this.uploadMessage ) {
//                return;
//
//            }
//
//            Uri result=null;
//
//            try{
//                if (resultCode != RESULT_OK) {
//
//                    result = null;
//
//                } else {
//
//                    // retrieve from the private variable if the intent is null
//                    result = intent == null ? mCapturedImageURI : intent.getData();
//                }
//            }
//            catch(Exception e)
//            {
//                Toast.makeText(getApplicationContext(), "activity :"+e,
//                        Toast.LENGTH_LONG).show();
//            }
//            uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
//            uploadMessage = null;
//            mUploadMessage.onReceiveValue(result);
//            mUploadMessage = null;
//
//        }
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        //super.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            Uri result=null;
            if (null == mUploadMessage && null == uploadMessage)
            return;
            if(resultCode != RESULT_OK)
                return;
            result = data == null ? mCapturedImageURI : data.getData();
              if(uploadMessage!=null) {
                  onActivityResultAboveL(requestCode, resultCode, data);

              }
            else
            {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode,
                                        Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE && uploadMessage != null)
        {
            Uri[] results=null;
            if (resultCode == RESULT_OK) {
                if (intent != null) {
                    String dataString= intent.getDataString();
                    ClipData clipData=intent.getClipData();
                    if(clipData!=null)
                    {
                        results=new Uri[clipData.getItemCount()];
                        for(int i=0;i<clipData.getItemCount();i++)
                        {
                            ClipData.Item item=clipData.getItemAt(i);
                            results[i]=item.getUri();
                        }
                    }
                   if(dataString!=null)
                       results= new Uri[]{Uri.parse(dataString)};

                }
                else if(mCapturedImageURI!=null)
                {
                    results=new Uri[]{mCapturedImageURI};
                }
                assert uploadMessage != null;
                uploadMessage.onReceiveValue(results);
                uploadMessage=null;

            }
        }
//            return
//        if (resultCode == RESULT_OK) {
//            if (intent != null) {
//                intent.getDataString()
//                val clipData = intent.clipData
//                if (clipData != null) {
//                    results = Array<Uri>(clipData.itemCount) { _->return Unit}
//                    for (i in 0 until clipData.itemCount) {
//                        val item = clipData.getItemAt(i)
//                        results[i] = item.uri
//                    }
//                }
//                if (dataString != null)
//                    results = arrayOf(Uri.parse(dataString))
//            }else if (myChromeClient.mCapturedImageURI!=null)
//                results = arrayOf(myChromeClient.mCapturedImageURI)
//        }
//        myChromeClient.mUploadMessageAboveL?.onReceiveValue(results)
//        myChromeClient.mUploadMessageAboveL = null
    }
    WebView wv;
    ProgressBar progressBar;

    private void initViews() {
        checkAndRequestPermissions();
        wv = findViewById(R.id.wv);

//        WebSettings webSettings = wv.getSettings();
//        webSettings.setDefaultFontSize(24);
        progressBar = findViewById(R.id.progressBar);





        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setAllowFileAccessFromFileURLs(true);
        wv.getSettings().setAllowContentAccess(true);
        wv.getSettings().setLoadWithOverviewMode(true);

        //webView.getSettings().setUseWideViewPort(true);

        //Other webview settings
        wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        wv.setScrollbarFadingEnabled(false);
//        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setPluginState(WebSettings.PluginState.ON);
        wv.getSettings().setAllowFileAccess(true);
//        wv.getSettings().setSupportZoom(true);
//        wv.loadUrl(url);
        wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                // Return the app name after finish loading
                if (progress == 100) {
                    if (progressBar.getVisibility() == View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    if (progressBar.getVisibility() != View.VISIBLE) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            }


            public void openFileChooser(ValueCallback<Uri> uploadMsgAboveL, ValueCallback<Uri[]> uploadMsg, String acceptType){

                // Update message
                mUploadMessage = uploadMsgAboveL;
                uploadMessage = uploadMsg;
                try{

                    // Create AndroidExampleFolder at sdcard

                    File imageStorageDir = new File(
                            Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES)
                            , "AndroidExampleFolder");

                    if (!imageStorageDir.exists()) {
                        // Create AndroidExampleFolder at sdcard
                        imageStorageDir.mkdirs();
                    }

                    // Create camera captured image file path and name
                    File file = new File(
                            imageStorageDir + File.separator + "IMG_"
                                    + String.valueOf(System.currentTimeMillis())
                                    + ".jpg");

                    mCapturedImageURI = Uri.fromFile(file);

                    // Camera capture image intent
                    final Intent captureIntent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);

                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");

                    // Create file chooser intent
                    Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

                    // Set camera intent to file chooser
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                            , new Parcelable[] { captureIntent });

                    // On select image call onActivityResult method of activity
                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

                }
                catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Chrome Exception:"+e,
                            Toast.LENGTH_LONG).show();
                }

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//                if (uploadMessage != null) {
//                    uploadMessage.onReceiveValue(null);
//                    uploadMessage = null;
//                }
                openFileChooser(null, filePathCallback, "");
                return  true;
//                uploadMessage = filePathCallback;
//
//             //   Intent intent = fileChooserParams.createIntent();
//                try
//                {
//                    File imageStorageDir = new File(
//                            Environment.getExternalStoragePublicDirectory(
//                                    Environment.DIRECTORY_PICTURES)
//                            , "AndroidExampleFolder");
//
//                    if (!imageStorageDir.exists()) {
//                        // Create AndroidExampleFolder at sdcard
//                        imageStorageDir.mkdirs();
//                    }
//
//                    // Create camera captured image file path and name
//                    File file = new File(
//                            imageStorageDir + File.separator + "IMG_"
//                                    + String.valueOf(System.currentTimeMillis())
//                                    + ".jpg");
//
//                    mCapturedImageURI = Uri.fromFile(file);
//
//                    // Camera capture image intent
//                    final Intent captureIntent = new Intent(
//                            android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//
//                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//
//                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                    i.addCategory(Intent.CATEGORY_OPENABLE);
//                    i.setType("image/*");
//
//                    // Create file chooser intent
//                    Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
//
//                    // Set camera intent to file chooser
//                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
//                            , new Parcelable[] { captureIntent });
//
//                    // On select image call onActivityResult method of activity
//                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
//                    //startActivityForResult(intent, REQUEST_SELECT_FILE);
//                } catch (ActivityNotFoundException e)
//                {
//                    uploadMessage = null;
//                    Toast.makeText(getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
//                    return false;
//                }
//                return true;

                //return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);


        }


    });
       wv.setWebViewClient(new WebViewClient()
       {
        //   @RequiresApi(api = Build.VERSION_CODES.KITKAT)
           @Override
           public void onPageFinished(WebView view, String url_new) {
               
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                   wv.evaluateJavascript("document.getElementsByTagName('footer')[0].style.display='none';",null);
               }
               super.onPageFinished(view, url);
           }


       });

////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
////            wv.evaluateJavascript(removeHeader,notify());
////        }
   //     new WebLoadTask().execute();
        wv.loadUrl(url);


    }

    String url="https://sapmedicines.com";
    private  class WebLoadTask extends AsyncTask<Void,Void,String>
    {
        Document document;
        String title="";
        @Override
        protected String doInBackground(Void... voids) {
            try {
                document = Jsoup.connect(url).get();
                ///Elements element=document.getElementsByClass("about");
                //document.getElementsByClass("topbar").remove();
                document.getElementsByTag("footer").get(0).remove();
//                document.getElementsByTag("header").remove();
//                document.getElementsByTag("footer").remove();
                title=document.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return title;
        }

        @Override
        protected void onPostExecute(String result) {

            if(document!=null) {
                wv.loadDataWithBaseURL(url, document.toString(), "text/html", "utf-8", "");
                wv.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
               // wv.loadData(document.html(), "text/html", "UTF-8");
                //wv.loadDataWithBaseURL(url, result, "text/html", "utf-8", "");
            }
            else
                wv.loadUrl(url);
            super.onPostExecute(result);
        }
    }
//    public static final int REQUEST_SELECT_FILE = 100;
    @Override
    public void onBackPressed() {

        if(wv.canGoBack()) {
            wv.goBack();

        } else {
            super.onBackPressed();
        }
    }
    private void checkAndRequestPermissions() {
        int camerapermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int phonestate = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (phonestate != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();

                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    ) {


                    } else {
                        Log.d("TAG", "Some permissions are not granted ask again ");
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showDialogOK("Some Permissions are required for Open Camera",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    dialog.dismiss();
                                                    break;
                                            }
                                        }
                                    });
                        } else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        dialog.create().dismiss();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        dialog.create().dismiss();
                        finish();
                    }
                });
        dialog.show();
    }
}