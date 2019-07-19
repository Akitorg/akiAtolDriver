package com.ex.akiatol.print;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.ex.akiatol.Const;
import com.ex.akiatol.R;

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
abstract public class PrintChequeFragment extends Fragment implements PrintResponseListener {

    private static PrintChequeFragment fragment;
    public void setFragment(PrintChequeFragment fragment){
        this.fragment = fragment;
    }
    public static PrintChequeFragment getFragment() {
        return fragment;
    }

    private static int CONTENT_FRAME = 0;
    public static void setContentFrame(int i) {
        CONTENT_FRAME = i;
    }
    public static int getContentFrame() {
        return CONTENT_FRAME;
    }

    public enum PrintType {
        ORDER_CASH, ORDER_CARD, RETORDER_CARD, RETORDER_CASH, INCOME, OUTCOME, ZREP, XREP, CORRECTION, OPEN_SESSION, ORDER_COMBO
    }

    private PrintType printType;
    private HashMap<Integer, PrintObjects> printObjectsSNO = new HashMap<>();

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        PrintObjects printObject = null;

        if (getArguments()!= null) {

            printType = (PrintType) getArguments().getSerializable("printType");
            printObject = (PrintObjects) getArguments().getSerializable("printObject");

            getArguments().remove("printType");
            getArguments().remove("printObject");
        }

        if (printType == null || printObject == null) {

            Toast.makeText(getContext(), R.string.print_no_value, Toast.LENGTH_SHORT).show();

            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        }

        rootView = inflater.inflate(R.layout.fragment_print, container, false);

        final ImageView img_animation = rootView.findViewById(R.id.img_cheque);

        final TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                0.0f, -1000.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation.setDuration(10000);  // animation duration
        animation.setRepeatCount(-1);  // animation repeat count

        img_animation.startAnimation(animation);

        rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null)
                    getFragmentManager().popBackStack();
            }
        });

        rootView.findViewById(R.id.btn_try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = getContext();
                if (context == null)
                    return;

                rootView.findViewById(R.id.ll_error).setVisibility(View.GONE);
                rootView.findViewById(R.id.tv_error).setVisibility(View.GONE);

                ((TextView)rootView.findViewById(R.id.tv_title)).setText(R.string.print_in_progress);
                ((TextView)rootView.findViewById(R.id.tv_title)).setTextColor(ContextCompat.getColor(context, R.color.bg_title));

                boolean isAtol10 = PreferenceManager.getDefaultSharedPreferences(getContext())
                        .getBoolean(getString(R.string.prefs_kkm_use_10_driver), true);

                PrintAsyncTask printTask;
                if (isAtol10)
                    printTask = new PrintAtol10AsyncTask(printObjectsSNO, printType, getContext());
                else
                    return;
                    //printTask = new PrintAtol9AsyncTask(printObjectsSNO, printType, getContext());

                printTask.setListener(PrintChequeFragment.this);
                printTask.execute();

                img_animation.startAnimation(animation);
            }
        });


        if (printObject instanceof PrintObjects.Order) {

            PrintObjects.OrderGood[] goods = ((PrintObjects.Order) printObject).goods;

            //don't use vat
            if (!PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getString(R.string.grant_vat), true)) {
                for (PrintObjects.OrderGood g: goods) {
                    g.vat = 0;
                    g.vat_sum = 0;
                }
            }

            //slice by sno
            if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getString(R.string.grant_sno), false)) {

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

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Context context = getContext();
        if (context == null)
            return;

        boolean emulate_kkm = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getBoolean(context.getString(R.string.prefs_kkm_emulate), false);

        if (emulate_kkm) {

            Message msg = printHandler.obtainMessage(STATUS_DONE, 0, 0);
            msg.obj = 5;
            printHandler.sendMessage(msg);

        } else {

            boolean isAtol10 = PreferenceManager.getDefaultSharedPreferences(getContext())
                    .getBoolean(getString(R.string.prefs_kkm_use_10_driver), true);

            PrintAsyncTask printTask;
            if (isAtol10)
                printTask = new PrintAtol10AsyncTask(printObjectsSNO, printType, getContext());
            else
                return;
            //printTask = new PrintAtol9AsyncTask(printObjectsSNO, printType, getContext());

            printTask.setListener(this);
            printTask.execute();

        }

    }

    @Override
    public void onUpdateListener(String... values) {
        ((TextView) rootView.findViewById(R.id.tv_title)).setText(values[0]);
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
                    onPrintDone();

                case STATUS_ERROR:

                    rootView.findViewById(R.id.ll_error).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.tv_error).setVisibility(View.VISIBLE);
                    ((TextView) rootView.findViewById(R.id.tv_error)).setText(String.valueOf(msg.obj));
                    ((TextView) rootView.findViewById(R.id.tv_title)).setText(getString(R.string.print_exception));
                    ((TextView) rootView.findViewById(R.id.tv_title)).setTextColor(Color.RED);
                    rootView.findViewById(R.id.img_cheque).clearAnimation();

                    break;
                default:
                    break;
            }
            return true;
        }
    });

    abstract boolean onPrintDone();
    abstract void sendDebugLogs();

}
//© Все права на распостранение и модификацию модуля принадлежат ООО "АКИП" (www.akitorg.ru)