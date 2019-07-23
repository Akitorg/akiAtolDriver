package com.ex.akiatol;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;

/**
 * Кастомный диалог
 * Created by Leo on 20.04.17.
 */

class CustomAlertDialog extends Dialog{

    private Button btn_positive;
    private Button btn_negative;
    private TextView tv_title;
    private EditText et_message;

    CustomAlertDialog(@NonNull Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View content = View.inflate(context, R.layout.alert_dialog, null);
        setContentView(content);

        btn_negative = (content.findViewById(R.id.btn_cancel));
        btn_positive = (content.findViewById(R.id.btn_ok));
        tv_title = (content.findViewById(R.id.tv_title));
        et_message = (content.findViewById(R.id.et_message));

        Const.buttonEffect(btn_negative);
        Const.buttonEffect(btn_positive);
    }


    void setOnClickPositiveButton(View.OnClickListener listener){
        btn_positive.setOnClickListener(listener);
    }

    void setOnClickNegativeButton(View.OnClickListener listener){
        btn_negative.setOnClickListener(listener);
    }

    void setEt_message(String s){
        et_message.setVisibility(View.VISIBLE);
        et_message.setText(s);
    }

    void setEt_message_hint(String s){
        et_message.setVisibility(View.VISIBLE);
        et_message.setHint(s);
    }

    void setUncancelable(){
        setCancelable(false);
        btn_negative.setVisibility(View.GONE);
    }

    void setButtonText(String positiveButton, String negativeButton){
        btn_negative.setText(negativeButton);
        btn_positive.setText(positiveButton);
    }

    void setEt_message_Type(int type){
        et_message.setInputType(type);
    }

    String getEt_message(){
        return et_message.getText().toString();
    }

    void setMyTitle(String title){
        tv_title.setText(title);
    }
}
//© Все права на распостранение и модификацию модуля принадлежат ООО "АКИП" (www.akitorg.ru)