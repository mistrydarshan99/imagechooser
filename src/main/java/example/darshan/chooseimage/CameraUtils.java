package example.darshan.chooseimage;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;


import java.io.File;
import java.io.IOException;

public class CameraUtils {

    /**
     * Check conditions like SD card Mount,Camera availability and space on SD
     * card accordingly messages will be shown
     *
     * @param context
     * @return
     */
    public static boolean checkAvailaility(Context context) {
        boolean isSuccess = true;
        Activity activity = (Activity) context;
        if (!sdCardMounted(context)) {
            isSuccess = false;
            Notify.dialogOK(
                    context.getString(R.string.alert_sd_card_not_mounted),
                    activity, false);
        } else if (!isCameraExists(context)) {
            isSuccess = false;
            Notify.dialogOK(
                    context.getString(R.string.alert_device_not_support_camera),
                    activity, false);
        } else if (isSpaceAvailbale() <= 0) {
            isSuccess = false;
            Notify.dialogOK(context
                            .getString(R.string.alert_no_space_available_on_sd_card),
                    activity, false);
        }
        return isSuccess;
    }

    /**
     * Storage state if the media is present and mounted at its mount point with
     * read/write access.
     *
     * @param context
     * @return
     */
    public static boolean sdCardMounted(Context context) {
        boolean isMediaMounted = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            isMediaMounted = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            isMediaMounted = false;
        } else if (Environment.MEDIA_CHECKING.equals(state)) {
            isMediaMounted = false;
        } else if (Environment.MEDIA_NOFS.equals(state)) {
            isMediaMounted = false;
        } else if (Environment.MEDIA_REMOVED.equals(state)) {
            isMediaMounted = false;
        } else if (Environment.MEDIA_SHARED.equals(state)) {
            isMediaMounted = false;
        } else if (Environment.MEDIA_UNMOUNTABLE.equals(state)) {
            isMediaMounted = false;
        } else if (Environment.MEDIA_UNMOUNTED.equals(state)) {
            isMediaMounted = false;
        }
        return isMediaMounted;
    }

    /**
     * Checks whether given charSequence is empty or not. It will check whether
     * String is null, or empty or having "null" as a value
     *
     * @param charSequence : charSequence to check.
     * @return <code>true</code> in case of given charSequence is null, empty or
     * having "null" as a value
     */
    public static boolean isEmpty(CharSequence charSequence) {
        return TextUtils.isEmpty(charSequence)
                || charSequence.toString().equalsIgnoreCase("null");
    }

    /**
     * @param context
     * @param dir     : Directory name, if directory name is specified then image is
     *                saved in that location otherwise image is saved in root
     *                directory
     * @param img     : Image name , if image name is specified then same image name
     *                is saved otherwise image name is given as img_millisec.jpg
     * @return
     */
    public static File createDirFile(Context context, String dir, String img) {
        String root = null;
        File createDir = null;
        File file = null;
        try {
            if (CameraUtils.checkAvailaility(context)) {
                root = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + File.separator;

                // Both are not defined , file will be saved to root folder
                if (isEmpty(dir) && isEmpty(img)) {
                    createDir = new File(root);
                    img = "img_" + System.currentTimeMillis() + ".jpg";

                } // directory name is given and image name is not given.
                else if (!isEmpty(dir) && isEmpty(img)) {
                    createDir = new File(root + dir);
                    if (!createDir.exists()) {
                        createDir.mkdirs();
                    }
                    img = "img_" + System.currentTimeMillis() + ".jpg";

                } // directory name is not given , image name is given
                else if (isEmpty(dir) && !isEmpty(img)) {
                    createDir = new File(root);

                } // both given
                else {
                    createDir = new File(root + dir);
                    if (!createDir.exists()) {
                        createDir.mkdirs();
                    }
                }

                file = new File(createDir.getAbsolutePath(), img);
                if (file.exists())
                    file.delete();
                else
                    file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Check space available on SD card or not
     *
     * @return
     */

    private static long isSpaceAvailbale() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
                .getPath());
        long bytesAvailable = (long) stat.getBlockSize()
                * (long) stat.getBlockCount();
        long megAvailable = bytesAvailable / 1048576;
        return megAvailable;
    }

    /**
     * Check if camera is available on the device or not.
     *
     * @param context
     * @return
     */
    private static boolean isCameraExists(Context context) {
        PackageManager pm = context.getPackageManager();
        boolean isCamera = true;
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            isCamera = false;
        }
        return isCamera;
    }


    public static Bitmap SetImageOrientaion(String path) {
        Bitmap correctBmp = null;
        try {
            File f = new File(path);
            ExifInterface exif = new ExifInterface(f.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }

            Matrix mat = new Matrix();
            mat.postRotate(angle);

            Bitmap bmp1 = decodeFile(path);
            correctBmp = Bitmap.createBitmap(bmp1, 0, 0, bmp1.getWidth(), bmp1.getHeight(), mat, true);

//			 Matrix mat = new Matrix();
//		        mat.postRotate(angle);
//		        BitmapFactory.Options options = new BitmapFactory.Options();
//		        options.inSampleSize = 2;
//
//		        Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f),null, options);
//		        correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),bmp.getHeight(), mat, true);
//		        ByteArrayOutputStream outstudentstreamOutputStream = new ByteArrayOutputStream();
//		        correctBmp.compress(Bitmap.CompressFormat.PNG, 100,outstudentstreamOutputStream);
        } catch (IOException e) {
            Logger.e("TAG", "-- Error in setting image");
        } catch (OutOfMemoryError oom) {
            Logger.e("TAG", "-- OOM Error in setting image");
        }
        return correctBmp;


    }


    public static Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 512;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }
}
