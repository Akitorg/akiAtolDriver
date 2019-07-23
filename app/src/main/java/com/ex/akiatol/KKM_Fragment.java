package com.ex.akiatol;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import com.ex.akiatol.print.KKM_Information;
import com.ex.akiatol.print.PrintAtol10AsyncTask;
import com.ex.akiatol.print.PrintObjects;
import com.ex.akiatol.print.PrintType;

import static com.ex.akiatol.PrintChequeActivity.PRINT_RESPONSE_CODE;
import static ru.atol.drivers10.fptr.IFptr.LIBFPTR_SS_CLOSED;

/**
 * Информация по кассе
 * Created by Leo on 12/04/2019.
 */
public class KKM_Fragment extends Fragment implements View.OnClickListener {

    private final KKM_Information[] kkm_information = {null};
    private boolean isAtol10 = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), PrintChequeActivity.getAppTheme());
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View rootView = localInflater.inflate(R.layout.fragment_kkm_info, container, false);

        rootView.findViewById(R.id.btn_check_ofd).setOnClickListener(this);
        rootView.findViewById(R.id.btn_open).setOnClickListener(this);
        rootView.findViewById(R.id.btn_income).setOnClickListener(this);
        rootView.findViewById(R.id.btn_outcome).setOnClickListener(this);
        rootView.findViewById(R.id.btn_history).setOnClickListener(this);
        rootView.findViewById(R.id.btn_x).setOnClickListener(this);
        rootView.findViewById(R.id.btn_correction).setOnClickListener(this);

        isAtol10 = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getBoolean(getString(R.string.prefs_kkm_use_10_driver), true);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getKKMInformation();
    }

    @SuppressLint({"SetTextI18n"})
    private void showKKMInformation() {

        View view = getView();
        if (view == null || kkm_information[0] == null ) {
            return;
        }

        ((TextView) view.findViewById(R.id.tvCashSum)).setText("" + kkm_information[0].cashSum);
        ((TextView) view.findViewById(R.id.tvSell)).setText("Продажи: " + kkm_information[0].sellSum);
        ((TextView) view.findViewById(R.id.tvReturn)).setText("Возвраты: " + kkm_information[0].returnSum);
        ((TextView) view.findViewById(R.id.tvIncome)).setText("Внесения: " + kkm_information[0].incomeSum);
        ((TextView) view.findViewById(R.id.tvOutcome)).setText("Изъятия: " + kkm_information[0].outcomeSum);

        Button btnShift = view.findViewById(R.id.btn_open);
        if (kkm_information[0].shiftState == LIBFPTR_SS_CLOSED) { // Смена закрыта
            btnShift.setBackgroundColor(getResources().getColor(R.color.blue_grey_500));
            btnShift.setText("Открыть смену");
        } else {
            btnShift.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btnShift.setText("Закрыть смену");
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void getKKMInformation() {

        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Получение информации");

        WaitTask wt = new WaitTask(pd) {
            @Override
            public void Run() throws Exception{

                if (isAtol10) {
                    kkm_information[0] = PrintAtol10AsyncTask.getKKMInformation(getContext());
                } else {
                    //kkm_information[0] = PrintAtol9AsyncTask.getKKMInformation(getContext());
                    throw new Exception("Для отображения информации включите настройку \"Драйвер v10\"");
                }
            }

            @Override
            public void onFinish(String result) {
                if (!result.equals("") && getContext() != null) {
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                } else
                    showKKMInformation();
            }
        };

        wt.execute();
    }

    @Override
    public void onClick(View v) {

        if (getActivity() == null)
            return;

        if (kkm_information[0] == null && isAtol10)
            return;

        Bundle bundle = new Bundle();
        bundle.clear();

        if (v.getId() == R.id.btn_income) {

            new CashOperationDialog(getActivity(), CashOperationDialog.OperationType.INCOME, null).show();

        } else if(v.getId() == R.id.btn_outcome) {

            new CashOperationDialog(getActivity(), CashOperationDialog.OperationType.OUTCOME, null).show();

        } else if(v.getId() == R.id.btn_x) {

            Intent intent = new Intent(getContext(), PrintChequeActivity.class);

            intent.putExtra("printType", PrintType.XREP);
            intent.putExtra("printObject", new PrintObjects.XRep());

            getActivity().startActivityForResult(intent, PRINT_RESPONSE_CODE);

        } else if(v.getId() == R.id.btn_open) {

            if (kkm_information[0] != null && kkm_information[0].shiftState != LIBFPTR_SS_CLOSED) {

                Intent intent = new Intent(getContext(), PrintChequeActivity.class);

                intent.putExtra("printType", PrintType.ZREP);
                intent.putExtra("printObject", new PrintObjects.ZRep());

                getActivity().startActivityForResult(intent, PRINT_RESPONSE_CODE);

            } else {

                Intent intent = new Intent(getContext(), PrintChequeActivity.class);

                intent.putExtra("printType", PrintType.OPEN_SESSION);
                intent.putExtra("printObject", new PrintObjects.OPEN_SEESION());

                getActivity().startActivityForResult(intent, PRINT_RESPONSE_CODE);

            }

        } else if (v.getId() == R.id.btn_correction) {

            print_correction();

        }
    }

    private void print_correction() {

        final PrintObjects.Correction correction = new PrintObjects.Correction();

        final AlertDialog.Builder dialog_sum = new AlertDialog.Builder(getContext());
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        dialog_sum.setTitle("Введите сумму коррекции");
        dialog_sum.setView(input);
        dialog_sum.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString().trim();
                try {

                    correction.sum = Double.parseDouble(value);

                    Intent intent = new Intent(getContext(), PrintChequeActivity.class);
                    intent.putExtra("printType", PrintType.CORRECTION);
                    intent.putExtra("printObject", correction);

                    getActivity().startActivityForResult(intent, PRINT_RESPONSE_CODE);

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Введена неверная сумма", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog_sum.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        final AlertDialog.Builder adb_pay_type = new AlertDialog.Builder(getContext());

        String[] data_pay_type = {"Наличными", "Картой"};
        adb_pay_type.setSingleChoiceItems(data_pay_type, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog_sum.show();
                correction.pay_type = which;
                dialog.dismiss();
            }
        });
        adb_pay_type.setTitle("Тип оплаты");

        final AlertDialog.Builder adb_type = new AlertDialog.Builder(getContext());

        String[] data_type = {"Продажа", "Возврат"};
        adb_type.setSingleChoiceItems(data_type, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adb_pay_type.show();
                correction.doc_type = which;
                dialog.dismiss();
            }
        });
        adb_type.setTitle("Тип чека коррекции");
        adb_type.show();

    }

}
