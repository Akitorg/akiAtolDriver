package com.ex.akiatol;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import org.apache.commons.math3.util.Precision;

import java.util.Locale;

/**ReLinker
 * Some interesting usable static methods
 * Created by Leo on 30.03.17.
 */

public abstract class Const {

    public static final String FPTR_PREFERENCES = "FPTR_PREFERENCES";

    static void buttonEffect(View button) {
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }

    public static double round(double value, int places) {

        if (places < 0) throw new IllegalArgumentException();

        // Ебучая математика!!!! Приходиться округлять сначало до пред. числа
        // Решение проблемы с округлением числа 1332.0449999999998
        double valueNew = Precision.round(value, places + 1);

        return Precision.round(valueNew, places);
    }

    public static double countDiscout(double price, double discount) {
        return round(price - price * discount / 100, 2);
    }

    static Spanned priceDouble(Double price) {

        price = round(price, 2);
        String beforePoint = (String.format(Locale.getDefault(), "%,d", price.intValue())).replace(',', ' ');

        int afterPoint = (int) Const.round(price % 1 * 100, 0);
        if (afterPoint < 0)
            afterPoint = -afterPoint;

        String afterPointString = String.valueOf(afterPoint);
        if (afterPoint < 10)
            afterPointString = "0" + afterPointString;

        return Const.fromHtml("<b>" + beforePoint + "</b>." + afterPointString + " \u20BD");
    }

    static void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setTextColor(Color.GRAY);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    @SuppressWarnings("deprecation")
    private static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    public static String encode(String pText, String pKey) {
        byte[] txt = pText.getBytes();
        byte[] key = pKey.getBytes();
        byte[] res = new byte[txt.length];

        for (int i = 0; i < txt.length; i++) {
            res[i] = (byte) (txt[i] ^ key[i % key.length]);
        }

        return new String(res);

    }
}
//© Все права на распостранение и модификацию модуля принадлежат ООО "АКИП" (www.akitorg.ru)