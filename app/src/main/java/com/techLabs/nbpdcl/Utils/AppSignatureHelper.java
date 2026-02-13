package com.techLabs.nbpdcl.Utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.util.Log;

import com.techLabs.nbpdcl.R;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class AppSignatureHelper extends ContextWrapper {

    private static final String TAG = "NorthBihar";

    public AppSignatureHelper(Context context) {
        super(context);
    }

    public List<String> getAppSignatures() {
        List<String> appCodes = new ArrayList<>();

        try {
            String packageName = getPackageName();
            PackageManager pm = getPackageManager();

            PackageInfo packageInfo =
                    pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES);

            byte[] signature;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                assert packageInfo.signingInfo != null;
                signature = packageInfo.signingInfo.getApkContentsSigners()[0].toByteArray();
            } else {
                assert packageInfo.signatures != null;
                signature = packageInfo.signatures[0].toByteArray();
            }

            String hash = generateHash(packageName, signature);

            if (hash != null) {
                appCodes.add(hash);
                Log.d(TAG, "App Signature: " + hash);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error", e);
        }

        return appCodes;
    }

    private String generateHash(String packageName, byte[] signature) {

        final String appInfo = packageName + " " + Base64.encodeToString(signature, Base64.NO_WRAP);

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(appInfo.getBytes());
            byte[] hashSignature = md.digest();

            byte[] truncated = new byte[9];
            System.arraycopy(hashSignature, 0, truncated, 0, 9);

            String base64Hash =
                    Base64.encodeToString(truncated, Base64.NO_WRAP | Base64.NO_PADDING);

            return base64Hash.substring(0, 11);

        } catch (Exception e) {
            return null;
        }
    }
}