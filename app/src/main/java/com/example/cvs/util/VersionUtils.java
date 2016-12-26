package com.example.cvs.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by john on 11/13/16.
 */

public class VersionUtils {
    public static int getVersionCode(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo manager = context.getPackageManager().getPackageInfo(
                context.getPackageName(), 0);
        return manager.versionCode;
    }
}
