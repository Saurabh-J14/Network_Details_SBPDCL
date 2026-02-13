package com.techLabs.nbpdcl.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.Status;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private final OtpReceiveListener listener;

    public SmsBroadcastReceiver(OtpReceiveListener listener) {
        this.listener = listener;
    }

    public static IntentFilter getIntentFilter() {
        return new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras == null) {
                if (listener != null) listener.onFailure();
                return;
            }

            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            if (status == null) {
                if (listener != null) listener.onFailure();
                return;
            }

            switch (status.getStatusCode()) {
                case com.google.android.gms.common.api.CommonStatusCodes.SUCCESS:
                    Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                    if (listener != null) listener.onOtpReceived(consentIntent);
                    break;
                case com.google.android.gms.common.api.CommonStatusCodes.TIMEOUT:
                default:
                    if (listener != null) listener.onFailure();
                    break;
            }
        }
    }

    public interface OtpReceiveListener {
        void onOtpReceived(Intent consentIntent);

        void onFailure();
    }

}