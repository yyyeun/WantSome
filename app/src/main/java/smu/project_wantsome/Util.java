package smu.project_wantsome;

import android.app.Activity;
import android.widget.Toast;

public class Util {
    public Util(){/**/}

    public static final String INTENT_PATH = "path";

    public static void showToast(Activity activity, String msg){
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public static String storageUriToName(String url) {
        return url.split("\\?")[0].split("%2F")[url.split("\\?")[0].split("%2F").length-1];
    }
}
