package com.techLabs.nbpdcl.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
public class OtpViewHandler extends androidx.appcompat.widget.AppCompatEditText {

    private OnOtpBackspaceListener listener;

    public OtpViewHandler(Context context) {
        super(context);
    }

    public OtpViewHandler(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnOtpBackspaceListener(OnOtpBackspaceListener listener) {
        this.listener = listener;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection ic = super.onCreateInputConnection(outAttrs);

        return new InputConnectionWrapper(ic, true) {

            @Override
            public boolean deleteSurroundingText(int beforeLength, int afterLength) {
                if (beforeLength == 1 && getText() != null && getText().length() == 0) {
                    if (listener != null) {
                        listener.onBackspacePressed();
                    }
                    return true;
                }
                return super.deleteSurroundingText(beforeLength, afterLength);
            }

            @Override
            public boolean sendKeyEvent(KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_DEL) {

                    if (getText() != null && getText().length() == 0) {
                        if (listener != null) {
                            listener.onBackspacePressed();
                        }
                        return true;
                    }
                }
                return super.sendKeyEvent(event);
            }
        };
    }

    public interface OnOtpBackspaceListener {
        void onBackspacePressed();
    }
}

