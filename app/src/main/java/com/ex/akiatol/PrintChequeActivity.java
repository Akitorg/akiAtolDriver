package com.ex.akiatol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.ex.akiatol.print.*;

import java.util.ArrayList;
import java.util.HashMap;

import static com.ex.akiatol.Const.round;
import static com.ex.akiatol.print.PrintResult.STATUS_DONE;
import static com.ex.akiatol.print.PrintResult.STATUS_ERROR;

/**
 * Экран печати чека
 * Created by Leo on 06.04.17.
 */

@SuppressLint("UseSparseArrays")
public class PrintChequeActivity extends Activity implements PrintResponseListener {

    private PrintType printType;
    private HashMap<Integer, PrintObjects> printObjectsSNO = new HashMap<>();

    public static final int PRINT_RESPONSE_CODE = 1021;

    private static int theme = R.style.AppTheme;
    public static void setDarkTheme() { theme = R.style.AtolThemeDark; }
    public static void setLightTheme() { theme = R.style.AtolTheme; }
    public static int getAppTheme() { return theme; }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setTheme(theme);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fragment_print);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        PrintObjects printObject = null;

        Bundle extras = getIntent().getExtras();
        if ( extras != null) {

            printType = (PrintType) extras.getSerializable("printType");
            printObject = (PrintObjects) extras.getSerializable("printObject");

            extras.remove("printType");
            extras.remove("printObject");
        }

        if (printType == null || printObject == null) {

            Toast.makeText(this, R.string.print_no_value, Toast.LENGTH_SHORT).show();

            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        }

       final ImageView img_animation = findViewById(R.id.img_cheque);

        final TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                0.0f, -1000.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation.setDuration(10000);  // animation duration
        animation.setRepeatCount(-1);  // animation repeat count

        img_animation.startAnimation(animation);

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findViewById(R.id.ll_error).setVisibility(View.GONE);
                findViewById(R.id.tv_error).setVisibility(View.GONE);

                ((TextView) findViewById(R.id.tv_title)).setText(R.string.print_in_progress);
                ((TextView) findViewById(R.id.tv_title)).setTextColor(ContextCompat.getColor(PrintChequeActivity.this, R.color.bg_title));

                boolean isAtol10 = PreferenceManager.getDefaultSharedPreferences(PrintChequeActivity.this)
                        .getBoolean(getString(R.string.prefs_kkm_use_10_driver), true);

                PrintAsyncTask printTask;
                if (isAtol10)
                    printTask = new PrintAtol10AsyncTask(printObjectsSNO, printType, PrintChequeActivity.this);
                else
                    printTask = new PrintAtol9AsyncTask(printObjectsSNO, printType, PrintChequeActivity.this);

                printTask.setListener(PrintChequeActivity.this);
                printTask.execute();

                //img_animation.startAnimation(animation);
            }
        });


        if (printObject instanceof PrintObjects.Order) {

            PrintObjects.OrderGood[] goods = ((PrintObjects.Order) printObject).goods;

            //don't use vat
            if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.grant_vat), true)) {
                for (PrintObjects.OrderGood g: goods) {
                    g.vat = 0;
                    g.vat_sum = 0;
                }
            }

            //slice by sno
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.grant_sno), false)) {

                HashMap<Integer, ArrayList<PrintObjects.OrderGood>> sno_good = new HashMap<>();

                //Разобьем товары по сно
                for (PrintObjects.OrderGood g: goods) {

                    if (!sno_good.containsKey(g.sno))
                        sno_good.put(g.sno, new ArrayList<PrintObjects.OrderGood>());

                    ArrayList<PrintObjects.OrderGood> orderGoods = sno_good.get(g.sno);

                    if (orderGoods != null)
                        orderGoods.add(g);
                }

                double full_sum = 0;
                int i = 0;
                for (HashMap.Entry<Integer, ArrayList<PrintObjects.OrderGood>> entry : sno_good.entrySet()) {

                    i++;
                    int sno = entry.getKey();
                    ArrayList<PrintObjects.OrderGood> glist = entry.getValue();
                    double sum = 0;
                    for (PrintObjects.OrderGood g: glist) {
                        sum+=g.dsum;
                    }

                    double getSum = round(sum/((PrintObjects.Order) printObject).full_sum * ((PrintObjects.Order) printObject).get_sum, 2);
                    full_sum += getSum;

                    if ((full_sum != ((PrintObjects.Order) printObject).get_sum) && sno_good.size() == i) {
                        getSum = round(getSum - full_sum, 2);
                    }

                    PrintObjects.OrderGood[] dsf = new PrintObjects.OrderGood[glist.size()];

                    PrintObjects.Order co = (PrintObjects.Order) printObject;
                    PrintObjects.Order order = new PrintObjects.Order(co.extid, glist.toArray(dsf),
                            sum, getSum, co.type, co.e_mail, co.client_name, co.client_inn, co.needCopy);

                    printObjectsSNO.put(sno, order);

                    PrintObjects.Order printOrder = ((PrintObjects.Order) printObjectsSNO.get(sno));

                    if (printOrder != null)
                        printOrder.set_sno(sno);

                }

            } else
                printObjectsSNO.put(0, printObject);

        } else
            printObjectsSNO.put(0, printObject);

        //send debug logs
        sendDebugLogs();

    }

    @Override
    public void onStart() {
        super.onStart();

        boolean emulate_kkm = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.prefs_kkm_emulate), false);

        if (emulate_kkm) {

            Message msg = printHandler.obtainMessage(STATUS_DONE, 0, 0);
            msg.obj = 5;
            printHandler.sendMessage(msg);

        } else {

            boolean isAtol10 = PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(getString(R.string.prefs_kkm_use_10_driver), true);

            PrintAsyncTask printTask;
            if (isAtol10)
                printTask = new PrintAtol10AsyncTask(printObjectsSNO, printType, PrintChequeActivity.this);
            else
                printTask = new PrintAtol9AsyncTask(printObjectsSNO, printType, PrintChequeActivity.this);

            printTask.setListener(this);
            printTask.execute();

        }

    }

    @Override
    public void onUpdateListener(String... values) {
        ((TextView) findViewById(R.id.tv_title)).setText(values[0]);
    }

    @Override
    public void onPostExecute(PrintResult result) {

        Message msg = printHandler.obtainMessage(result.getResult_status(), 0, 0);
        msg.obj = result.getResult();
        printHandler.sendMessage(msg);

    }

    private Handler printHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {

                case STATUS_DONE:

                    onPrintDone(msg);

                    break;
                case STATUS_ERROR:

                    findViewById(R.id.ll_error).setVisibility(View.VISIBLE);
                    findViewById(R.id.tv_error).setVisibility(View.VISIBLE);

                    ((TextView) findViewById(R.id.tv_error)).setText(String.valueOf(msg.obj));
                    ((TextView) findViewById(R.id.tv_title)).setText(getString(R.string.print_exception));
                    ((TextView) findViewById(R.id.tv_title)).setTextColor(Color.RED);

                    findViewById(R.id.img_cheque).clearAnimation();

                    break;
                default:
                    break;
            }

            return true;
        }
    });

    public void onPrintDone(Message msg) {

        Intent intent = new Intent();

        intent.putExtra("printType", printType);
        intent.putExtra("printObject", printObjectsSNO);
        intent.putExtra("cheque_number", Long.parseLong("" + msg.obj));

        setResult(RESULT_OK, intent);

        PrintChequeActivity.this.finish();
    }

    public void sendDebugLogs() {

    }

    @Override
    public void onBackPressed() {}
}
//© Все права на распостранение и модификацию модуля принадлежат ООО "АКИП" (www.akitorg.ru)