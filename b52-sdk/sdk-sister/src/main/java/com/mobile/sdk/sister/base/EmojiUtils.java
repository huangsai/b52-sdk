package com.mobile.sdk.sister.base;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

public class EmojiUtils {

    public static void disableEmoji(EditText editText) {
        InputFilter emojiFilter = new InputFilter() {
            @Override
            public CharSequence filter(
                    CharSequence source,
                    int start,
                    int end,
                    Spanned dest,
                    int dstart,
                    int dend
            ) {
                for (int index = start; index < end - 1; index++) {
                    int type = Character.getType(source.charAt(index));
                    if (type == Character.SURROGATE) {
                        return "";
                    }
                }
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{emojiFilter});
    }
}
