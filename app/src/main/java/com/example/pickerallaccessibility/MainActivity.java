package com.example.pickerallaccessibility;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NumberPicker p = findViewById(R.id.picker);
        p.setMinValue(0);
        p.setMaxValue(10);
        p.setWrapSelectorWheel(false);

        EditText mSelectedText = p.findViewById(Resources.getSystem().getIdentifier("numberpicker_input", "id", "android"));

        mSelectedText.setEnabled(false);
        mSelectedText.setFocusable(false);
        mSelectedText.setImeOptions(EditorInfo.IME_ACTION_NONE);
        mSelectedText.setAccessibilityDelegate(new View.AccessibilityDelegate(){
            @Override
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                //super.onInitializeAccessibilityNodeInfo(host, info);
                info.setContentDescription("Current value: " + p.getValue());
            }
        });

        final String[] focussedThing = {""};

        p.setAccessibilityDelegate(new View.AccessibilityDelegate(){
            @Override
            public void sendAccessibilityEvent(View host, int eventType) {
                Log.i("TG", "sendAccessibilityEvent: " + eventType);
                super.sendAccessibilityEvent(host, eventType);
            }

            @Override
            public boolean performAccessibilityAction(View host, int action, Bundle args) {
                Log.i("TG", "performAccessibilityAction: " + action);
                return super.performAccessibilityAction(host, action, args);
                //return false;
            }

            @Override
            public void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
                Log.i("TG", "sendAccessibilityEventUnchecked: " + event.toString());
                super.sendAccessibilityEventUnchecked(host, event);
            }

            @Override
            public boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                Log.i("TG", "dispatchPopulateAccessibilityEvent: " + event.toString() );
                return super.dispatchPopulateAccessibilityEvent(host, event);
                //return false;
            }

            @Override
            public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                Log.i("TG", "onPopulateAccessibilityEvent: " + event.toString());
                super.onPopulateAccessibilityEvent(host, event);
            }

            @Override
            public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
                Log.i("TG", "onInitializeAccessibilityEvent: " + event.toString());
                super.onInitializeAccessibilityEvent(host, event);
            }

            @Override
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                Log.i("TG", "onInitializeAccessibilityNodeInfo: " +info.toString());
                super.onInitializeAccessibilityNodeInfo(host, info);
            }

            @Override
            public void addExtraDataToAccessibilityNodeInfo(@NonNull View host, @NonNull AccessibilityNodeInfo info, @NonNull String extraDataKey, @Nullable Bundle arguments) {
                Log.i("TG", "addExtraDataToAccessibilityNodeInfo: " + info.toString());
                super.addExtraDataToAccessibilityNodeInfo(host, info, extraDataKey, arguments);
            }

            @Override
            public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
                Log.i("TG", "onRequestSendAccessibilityEvent: " + event.toString());
                if(event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED){
                    host.announceForAccessibility("Blah replaced blah.");
                    return false;
                }

                if(event.getEventType() == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED){
                    Log.i("TG", event.getClassName().toString());
                    Log.i("TG", event.getText().get(0).toString() + p.getValue());

                    if(event.getClassName() == "android.widget.EditText"){
                        focussedThing[0] = "current";
                    } else {
                        int i = Integer.parseInt(event.getText().get(0).toString());
                        if(i > p.getValue()){
                            event.setContentDescription("Blah");
                            host.announceForAccessibility("Tap to select next value in the picker");
                            focussedThing[0] = "after";
                            return true;
                        } else if(i < p.getValue()) {
                            host.announceForAccessibility("Tap to select the previous value in the picker");
                            focussedThing[0] = "before";
                        }
                    }
                }
                return super.onRequestSendAccessibilityEvent(host, child, event);
            }

            @Override
            public AccessibilityNodeProvider getAccessibilityNodeProvider(View host) {
                Log.i("TG", "getAccessibilityNodeProvider");
                return super.getAccessibilityNodeProvider(host);
            }
        });
    }
}