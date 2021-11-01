package com.myapp.nativePackages.toast;

import static com.myapp.nativePackages.toast.ModuleNames.TOAST_MODULE;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class MyToastModule extends ReactContextBaseJavaModule {

    ReactContext context;

    public MyToastModule(@Nullable ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return TOAST_MODULE;
    }

    /**
     * Shows a Toast Message with default time, Toast.LENGTH_SHORT
     * @param message
     */
    @ReactMethod
    public void showToast(String message) {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }


}
