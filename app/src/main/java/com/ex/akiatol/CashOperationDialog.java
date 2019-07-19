package com.ex.akiatol;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.ex.akiatol.print.PrintChequeFragment;
import com.ex.akiatol.print.PrintObjects;

/**
 * Created by Leo on 12/04/2019.
 */
public class CashOperationDialog extends Dialog {

    public CashOperationDialog(@NonNull final Context context,
                               final CashOperationDialog.OperationType operationType,
                               final CashOperation cashOperation) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_income_reason);

        final EditText et_reason = (findViewById(R.id.et_reason));
        final EditText et_person = (findViewById(R.id.et_person));
        final EditText et_sum = (findViewById(R.id.et_sum));

        if (operationType == null) {
            dismiss();
            return;
        } else if (operationType == CashOperationDialog.OperationType.OUTCOME) {
            ((TextView) findViewById(R.id.tv_person)).setText(R.string.to_who);
            et_person.setHint(R.string.to_who);
        }

        if (cashOperation != null) { // Открываем сохраненный документ

            Const.disableEditText(et_reason);
            Const.disableEditText(et_person);
            Const.disableEditText(et_sum);

            findViewById(R.id.btn_cancel).setVisibility(View.INVISIBLE);
            ((Button) findViewById(R.id.btn_add)).setText(R.string.close);

            et_reason.setText(cashOperation.reason);
            et_person.setText(cashOperation.person);
            et_sum.setText(cashOperation.dsum);

            findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

        } else { //Создаем новый

            findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    double summ;

                    try {
                        summ = Const.round(Double.parseDouble(et_sum.getText().toString()), 2);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), R.string.incorrect_summ, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (et_person.getText().length() == 0 || et_reason.getText().length() == 0) {
                        Toast.makeText(getContext(), R.string.edit_info, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Bundle bundle = new Bundle();

                    if (operationType == CashOperationDialog.OperationType.INCOME)
                        bundle.putSerializable("printType", PrintChequeFragment.PrintType.INCOME);
                    else
                        bundle.putSerializable("printType", PrintChequeFragment.PrintType.OUTCOME);

                    bundle.putSerializable("printObject", new PrintObjects.InOutcome(summ,
                            et_reason.getText().toString(), et_person.getText().toString()));

                    Fragment fragment = PrintChequeFragment.getFragment();
                    fragment.setArguments(bundle);

                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(PrintChequeFragment.getContentFrame(),
                            fragment, "KKM_Fragment").addToBackStack(null).commit();


                    dismiss();
                }
            });
        }


        Const.buttonEffect(findViewById(R.id.btn_cancel));
        Const.buttonEffect(findViewById(R.id.btn_add));

    }

    public class CashOperation {

        String reason;
        String person;
        String dsum;

        public CashOperation (String reason, String person, String dsum) {
            this.reason = reason;
            this.person = person;
            this.dsum = dsum;
        }

    }

    public enum OperationType {
        ZREP, INCOME, OUTCOME
    }

}
