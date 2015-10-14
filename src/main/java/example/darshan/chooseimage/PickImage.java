package example.darshan.chooseimage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PickImage {
    private String TAG = PickImage.class.getSimpleName();
    private Context context;
    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_GALLERY = 2;
    private Uri fileUri;

    private String picturePath = "";

    public PickImage(Context context) {
        this.context = context;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    /**
     * Open up the pop up to choose image from gallery or camera.
     */
    public void createImageChooser() {
        List<String> list = Arrays.asList(context.getResources().getStringArray(R.array.chooser));
        CharSequence[] cs = list.toArray(new CharSequence[list.size()]);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(context.getString(R.string.app_name));
        dialog.setSingleChoiceItems(cs, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (which == 0) {
                    //if(Util.checkAvailaility(context));
                    generateCameraPickerIntent();
                } else {
                    generateGalleryPickerIntent();
                }
            }
        });

        final AlertDialog alertDialog = dialog.create();
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    /**
     * Opening camera
     */
    public void generateCameraPickerIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri();
        setFileUri(fileUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * Opening gallery
     */
    public void generateGalleryPickerIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }


    public Bitmap handleCameraIntent() {
        try {
            Bitmap bmp = null;
            bmp = CameraUtils.SetImageOrientaion(fileUri.getPath());

            Log.d(TAG, "Image path = " + fileUri.getPath());
            picturePath = fileUri.getPath();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                MediaScannerConnection.scanFile(context, new String[]
                                {Environment.getExternalStorageDirectory().toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {

                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });
            } else {
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://" + Environment.getExternalStorageDirectory())));
            }

            return bmp;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @return imageUri
     */
    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }


    private static File getOutputMediaFile() {
        File mediaFile;

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "temp");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        if (!mediaFile.exists())
            try {
                mediaFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        return mediaFile;
    }

    /**
     * @param data : Intent we got from activity in order to fetch the bitmap
     * @return Bitmap
     */
    public Bitmap handleGalleryIntent(Intent data) {
        Uri selectedImage = data.getData();
        Bitmap bmp = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
        picturePath = cursor.getString(columnIndex);
        cursor.close();

        Log.d(TAG, "Image path = " + picturePath);
        if (!CameraUtils.isEmpty(picturePath)) {
            bmp = CameraUtils.decodeFile(picturePath);
        } else {
            Notify.dialogOK(context.getString(R.string.alert_invalid_file), (Activity) context, false);
        }
        return bmp;
    }

    public String getImagePath() {
        if (picturePath != null && picturePath.length() > 0)
            return picturePath;
        else
            return null;
    }


    public String getCameraImagePath() {

        picturePath = fileUri.getPath();

        return picturePath;
    }

    public String getGalleryImagePath(Intent data) {

        Uri selectedImage = data.getData();

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
        picturePath = cursor.getString(columnIndex);

        return picturePath;
    }

}
