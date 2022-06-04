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

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String[] focussedThing = {"", ""};

        NumberPicker p1 = findViewById(R.id.picker1);
        p1.setDisplayedValues(new String[]{"0", "1","2", "3","4"});
        p1.setMinValue(0);
        p1.setMaxValue(4);
        p1.setWrapSelectorWheel(false);

        EditText mSelectedText1 = p1.findViewById(Resources.getSystem().getIdentifier("numberpicker_input", "id", "android"));
        mSelectedText1.setEnabled(false);
        mSelectedText1.setFocusable(false);
        mSelectedText1.setImeOptions(EditorInfo.IME_ACTION_NONE);
        mSelectedText1.setAccessibilityDelegate(new View.AccessibilityDelegate(){
            @Override
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                //super.onInitializeAccessibilityNodeInfo(host, info);
                info.setContentDescription("Current value: " + p1.getDisplayedValues()[p1.getValue()]);
            }
        });

        p1.setAccessibilityDelegate(new View.AccessibilityDelegate(){
            @Override
            public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
                Log.i("TG", "onRequestSendAccessibilityEvent: " + event.toString());
                if(event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED){
                    String replacedText = event.getText().get(0) + " replaced " + event.getBeforeText() + ". ";
                    int indexOfNew = Arrays.asList(p1.getDisplayedValues()).indexOf(event.getText().get(0).toString());

                    if(indexOfNew == 0 && focussedThing[0].equals("before")){
                        replacedText+="No previous values to choose from";
                    } else if(indexOfNew == p1.getDisplayedValues().length -1 && focussedThing[0].equals("after")){
                        replacedText+="No subsequent values to choose from";
                    } else if(focussedThing[0].equals("before")){
                        replacedText+=p1.getDisplayedValues()[indexOfNew -1] + ". Button.";
                    } else if(focussedThing[0].equals("after")){
                        replacedText+=p1.getDisplayedValues()[indexOfNew + 1] + ". Button.";
                    } else if(focussedThing[0].equals("current")){
                        replacedText+=p1.getDisplayedValues()[indexOfNew -1] + ". Current value: " + event.getText().get(0);
                    }


                    host.announceForAccessibility(replacedText);
                    return false;
                }

                if(event.getEventType() == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED){
                    if(event.getClassName() == "android.widget.EditText"){
                        focussedThing[0] = "current";
                    } else {
                        int i = Arrays.asList(p1.getDisplayedValues()).indexOf(event.getText().get(0).toString());
                        if(i > p1.getValue()){
                            focussedThing[0] = "after";
                        } else if(i < p1.getValue()) {
                            host.announceForAccessibility("Tap to select the previous value in the picker");
                            focussedThing[0] = "before";
                        }
                    }
                }
                return super.onRequestSendAccessibilityEvent(host, child, event);
            }
        });
    }
}