package com.google.riosport;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import java.util.HashMap;

/**
 * Created by pierre-alexandremaury on 06/11/2014.
 */
public class CustomAutoComplete extends AutoCompleteTextView {
    private static final String DEFAULT_SEPARATOR = " ";
    private String mSeparator = DEFAULT_SEPARATOR;

    public CustomAutoComplete(Context context){
        super(context);
    }

    public CustomAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //Returns the place description corresponding to the selected item

    //Here Map argument are int (inflateParams)
    @SuppressWarnings("unchecked")
    public CustomAutoComplete(Context context, AttributeSet attrs,int inflateParams){
        super(context, attrs, inflateParams);
    }

    @SuppressWarnings("unchecked")
    public CustomAutoComplete(Context context, AttributeSet attrs,int inflateParams, int defStyle){
        super(context, attrs, inflateParams, defStyle);
    }

    /**
     * Gets the separator used to delimit multiple words. Defaults to " "
     if
     * never specified.
     */
    public String getSeparator(){
        return mSeparator;
    }

    /**
     * Sets the separator used to delimit multiple words. Defaults to " "
     if
     * never specified.
     *
     * @param separator
     */
    public void setSeparator(String separator){
        mSeparator = separator;
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode){
        String newText = text.toString();
        if (newText.indexOf(mSeparator) != -1){
            int lastIndex = newText.lastIndexOf(mSeparator);
            if (lastIndex != newText.length() - 1){
                newText = newText.substring(lastIndex + 1).trim();
                if (newText.length() >= getThreshold()){
                    text = newText;
                }
            }
        }
        super.performFiltering(text, keyCode);
    }

    @Override
    protected void replaceText(CharSequence text){
        String newText = getText().toString();
        if (newText.indexOf(mSeparator) != -1){
            int lastIndex = newText.lastIndexOf(mSeparator);
            newText = newText.substring(0, lastIndex + 1) + text.toString();
        }else{
            newText = text.toString();
        }
        super.replaceText(newText);
    }

    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
        // Each item in the autocompetetextview suggestion list is a hashmap object
        HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
        return hm.get("description");
    }
}
