package example.darshan.chooseimage;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

/**
 * Created by vaibhav.jani on 5/27/2015.
 */
public class Notify {

    public static void toast(int stringId, Activity Activity) {

        toast(Activity.getString(stringId), Activity);
    }

    public static void toast(String text, Context Activity) {

        try {

            Toast.makeText(Activity, text, Toast.LENGTH_LONG).show();

        } catch (Exception e) {

            Logger.e(e);
        }
    }

    public static void dialogOK(String message, final Activity activity, final boolean finish){

        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(message);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    if(finish) {

                        activity.finish();
                    }
                }
            });
            builder.show();

        } catch (Exception e) {

            Logger.e(e);
        }
    }

    public static void dialogOK(String message, Activity activity){

        dialogOK(message, activity, false);
    }

    public static void dialogOK(int message, Activity activity){

        dialogOK(activity.getString(message), activity, false);
    }

    public static void dialogOK(int message, Activity activity, boolean finish){

        dialogOK(activity.getString(message), activity, finish);
    }

}
