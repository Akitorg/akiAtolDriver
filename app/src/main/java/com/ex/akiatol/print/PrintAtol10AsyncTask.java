package com.ex.akiatol.print;

import android.content.Context;
import android.preference.PreferenceManager;
import com.ex.akiatol.Const;
import com.ex.akiatol.R;
import ru.atol.drivers10.fptr.Fptr;
import ru.atol.drivers10.fptr.IFptr;
import ru.atol.drivers10.fptr.settings.SettingsActivity;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.ex.akiatol.Const.round;
import static com.ex.akiatol.print.ChequeType.FULL_PAY;
import static com.ex.akiatol.print.PrintType.*;
import static ru.atol.drivers10.fptr.IFptr.LIBFPTR_SS_CLOSED;
import static ru.atol.drivers10.fptr.IFptr.LIBFPTR_SS_EXPIRED;

/**
 * Печать чека через драйвер Атол 10 версии
 * Created by Leo on 09/04/2019.
 */
public class PrintAtol10AsyncTask extends PrintAsyncTask {

    private WeakReference<Context> contextWeakReference;

    private PrintType printType;
    private HashMap<Integer, PrintObjects> printObjectsSNO;
    private IFptr fptr;

    private String result;

    private final String fptr_settings;

    private PrintResponseListener printResponseListener;

    public PrintAtol10AsyncTask(HashMap<Integer, PrintObjects> printObjectsSNO,
                                PrintType printType, Context context) {

        this.printType = printType;
        this.printObjectsSNO = printObjectsSNO;

        contextWeakReference = new WeakReference<>(context);

        this.fptr_settings = context.getSharedPreferences(Const.FPTR_PREFERENCES,
                Context.MODE_PRIVATE).getString(SettingsActivity.DEVICE_SETTINGS, null);
    }

    public void setListener(PrintResponseListener listener) {
        this.printResponseListener = listener;
    }

    private void checkError(int result) throws Exception {

        if (result < 0) {
            throw new Exception(String.format(Locale.getDefault(),"%d [%s]",
                    fptr.errorCode(), fptr.errorDescription()));
        }

    }

    private static void checkError(IFptr fptr, int result) throws Exception {

        if (result < 0) {
            throw new Exception(String.format(Locale.getDefault(),"%d [%s]",
                    fptr.errorCode(), fptr.errorDescription()));
        }

    }

    private void connectToKKM() throws Exception {

        publishProgress("Соединение с кассой");
        checkError(fptr.setSettings(fptr_settings));
        checkError(fptr.open());

        fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_STATUS);
        checkError(fptr.queryData());

        long shiftState = fptr.getParamInt(IFptr.LIBFPTR_PARAM_SHIFT_STATE);

        if (shiftState == LIBFPTR_SS_EXPIRED) { // Смена превысила 24 часа, переоткроем смену

            publishProgress("Закрытие смены");

            fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_CLOSE_SHIFT);
            checkError(fptr.report());

            publishProgress("Открытие смены");
            checkError(fptr.openShift());

        } else if (shiftState == LIBFPTR_SS_CLOSED) { // Закрыта, откроем

            publishProgress("Открытие смены");
            checkError(fptr.openShift());
        }

        //Проверка состояния чека
        checkError(fptr.checkDocumentClosed());
        if (!fptr.getParamBool(IFptr.LIBFPTR_PARAM_DOCUMENT_CLOSED)) { // Документ не закрылся.
            publishProgress("Отмена чека");
            fptr.cancelReceipt();
        }

    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onProgressUpdate(String... values) {

        if (values == null || values.length == 0) {
            return;
        }

        if (printResponseListener != null)
            printResponseListener.onUpdateListener(values);

    }

    @Override
    protected void onPostExecute(PrintResult result) {

        if (printResponseListener != null)
            printResponseListener.onPostExecute(result);

    }

    @Override
    void registerPosition(String name, double price, double quantity, double positionSum,
                          int taxNumber, ChequeType chequeType, String type, double discount,
                          boolean isImport, String country, String decNumber) throws Exception {

        double difference = round(positionSum - price * quantity, 2);
        if (difference < 0) {
            price += difference;
            price = round(price, 2);
            difference = round(positionSum - price * quantity, 2);
        }

        fptr.setParam(IFptr.LIBFPTR_PARAM_COMMODITY_NAME, name);
        fptr.setParam(IFptr.LIBFPTR_PARAM_PRICE, price);
        fptr.setParam(IFptr.LIBFPTR_PARAM_QUANTITY, quantity);
        fptr.setParam(IFptr.LIBFPTR_PARAM_SUM, positionSum);
        fptr.setParam(IFptr.LIBFPTR_PARAM_TAX_TYPE, taxNumber);

        if (discount > 0){
            fptr.setParam(IFptr.LIBFPTR_PARAM_INFO_DISCOUNT_SUM, discount);
        }

        switch (chequeType) {
            case FULL_PAY:
                fptr.setParam(1214, 4);
                break;
            case FULL_PRE_PAY:
                fptr.setParam(1214, 1);
                break;
            case PART_PRE_PAY:
                fptr.setParam(1214, 2);
                break;
            case CHECK_OF_SHIPMENT:
                fptr.setParam(1214, 4);
                break;
            default:
                break;
        }

        if (type != null && type.equals("SERVICE")) {
            fptr.setParam(1212, 4);
        } else {
            fptr.setParam(1212, 1);
        }

        if (isImport) {

            fptr.setParam(1230, country);
            fptr.setParam(1231, decNumber);

        }

        checkError(fptr.registration());

        if (difference != 0)
            registerPosition(name, Math.abs(difference), 1, Math.abs(difference),
                    taxNumber, chequeType, type, 0, isImport, country, decNumber);

    }

    @Override
    int getVat(double tax_sum, int tax) {

        int tax_vat = IFptr.LIBFPTR_TAX_NO;
        if (tax_sum > 0) {
            if (tax == 10)
                tax_vat = IFptr.LIBFPTR_TAX_VAT10;
            else
                tax_vat = IFptr.LIBFPTR_TAX_VAT20;
        }

        return tax_vat;

    }

    private void registerPayment(double summ, int pay_type) throws Exception {

        fptr.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_TYPE, pay_type);
        fptr.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_SUM, summ);
        checkError(fptr.payment());

    }

    @Override
    protected PrintResult doInBackground(String... strings) {

        fptr = new Fptr(contextWeakReference.get());

        long doc_number = 0;
        Context context = contextWeakReference.get();

        String user = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.prefs_user_name), "Иванов Иван Иванович");

        String inn = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.prefs_user_inn), "");

        try {

            connectToKKM();

            publishProgress("Получение информации по кассе...");
            fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_STATUS);
            checkError(fptr.queryData());

            Date dateTime = fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME);
            doc_number  = fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENT_NUMBER);

            publishProgress("Проверка лицензии...");
            if (!checkLicense(context, dateTime))
                return new PrintResult("Лицензия данного устройства истекла! Пожалуйста, произведите оплату в личном кабинете");

            // Регистрация кассира
            publishProgress("Регистрация кассира...");
            if (user.trim().length() != 0)
                fptr.setParam(1021, user);
            if (inn.trim().length() != 0)
                fptr.setParam(1203, inn);

            checkError(fptr.operatorLogin());

            publishProgress("Печать чека...");
            switch (printType) {

                case ZREP:

                    fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_CLOSE_SHIFT);
                    checkError(fptr.report());

                    break;
                case XREP:

                    fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_X);
                    checkError(fptr.report());

                    break;
                case INCOME:

                    PrintObjects.InOutcome income = (PrintObjects.InOutcome) printObjectsSNO.get(0);

                    if (income == null)
                        throw new Exception("Объект печати null");

                    fptr.setParam(IFptr.LIBFPTR_PARAM_SUM, income.sum);
                    checkError(fptr.cashIncome());

                    break;
                case OUTCOME:

                    PrintObjects.InOutcome outcome = (PrintObjects.InOutcome) printObjectsSNO.get(0);

                    if (outcome == null)
                        throw new Exception("Объект печати null");

                    fptr.setParam(IFptr.LIBFPTR_PARAM_SUM, outcome.sum);
                    checkError(fptr.cashOutcome());

                    break;
                case CORRECTION:

                    for (HashMap.Entry<Integer, PrintObjects> entry : printObjectsSNO.entrySet()) {

                        PrintObjects.Correction correctionObject = (PrintObjects.Correction) entry.getValue();

                        int checkType = IFptr.LIBFPTR_RT_SELL_CORRECTION;
                        if (correctionObject.doc_type == PrintObjects.Correction.DOC_TYPE_RETORDER)
                            checkType = IFptr.LIBFPTR_RT_BUY_CORRECTION;

                        publishProgress("Открытие чека...");

                        Calendar c = Calendar.getInstance();
                        c.set(2018, 1, 2);
                        fptr.setParam(1178, c.getTime());
                        fptr.setParam(1177, "Самостоятельная коррекция");
                        fptr.setParam(1179, "-");
                        checkError(fptr.utilFormTlv());
                        byte[] correctionInfo = fptr.getParamByteArray(IFptr.LIBFPTR_PARAM_TAG_VALUE);
                        fptr.setParam(1174, correctionInfo);

                        fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, checkType);
                        fptr.setParam(1173, 0);

                        checkError(fptr.openReceipt());

                        publishProgress("Регистрации позиций...");

                        registerPosition("Коррекция", correctionObject.sum, 1,
                                correctionObject.sum, correctionObject.vat_rate, FULL_PAY,
                                "NORMAL", 0, false, "", "");


                        publishProgress("Оплата...");

                        if (correctionObject.pay_type == PrintObjects.Correction.PAY_TYPE_CASH)
                            registerPayment(correctionObject.sum, IFptr.LIBFPTR_PT_CASH);
                        else if (correctionObject.pay_type == PrintObjects.Correction.PAY_TYPE_CARD)
                            registerPayment(correctionObject.sum, IFptr.LIBFPTR_PT_ELECTRONICALLY);

                        publishProgress("Закрытие чека...");
                        checkError(fptr.closeReceipt());

                    }

                    break;
                case ORDER_CARD:
                case ORDER_CASH:
                case RETORDER_CARD:
                case RETORDER_CASH:
                case ORDER_COMBO:

                    int checkType = IFptr.LIBFPTR_RT_SELL;
                    if (printType == RETORDER_CARD || printType == RETORDER_CASH)
                        checkType = IFptr.LIBFPTR_RT_SELL_RETURN;

                    for (HashMap.Entry<Integer, PrintObjects> entry : printObjectsSNO.entrySet()) {

                        PrintObjects.Order orderObject = (PrintObjects.Order) entry.getValue();

                        publishProgress("Открытие чека...");

                        if (orderObject.e_mail != null && orderObject.e_mail.trim().length() > 0)
                            fptr.setParam(1008, orderObject.e_mail);

                        // Налогооблажение
                        if (entry.getKey() > 0)
                            fptr.setParam(1055, getByteSNO(orderObject.sno));

                        // Регистрация покупателя
                        if (orderObject.client_inn != null && orderObject.client_name != null) {
                            fptr.setParam(1227, orderObject.client_name);
                            fptr.setParam(1228, orderObject.client_inn);
                        }

                        fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, checkType);
                        checkError(fptr.openReceipt());

                        publishProgress("Регистрация позиций чека...");
                        registerPositions(orderObject, printType);

                        publishProgress("Регистрация Итога...");
//                        fptr.setParam(IFptr.LIBFPTR_PARAM_SUM, orderObject.get_sum);
//                        checkError(fptr.receiptTotal());

                        publishProgress("Оплата...");

                        if (orderObject.type == ChequeType.CHECK_OF_SHIPMENT) {
                            registerPayment(orderObject.get_sum, IFptr.LIBFPTR_PT_PREPAID);
                        } else {

                            if (printType == ORDER_CASH || printType == RETORDER_CASH) {
                                registerPayment(orderObject.get_sum, IFptr.LIBFPTR_PT_CASH);
                            } else if(printType == ORDER_COMBO) {
                                registerPayment(orderObject.get_sum, IFptr.LIBFPTR_PT_CASH);
                                registerPayment(round(orderObject.full_sum - orderObject.get_sum, 2), IFptr.LIBFPTR_PT_ELECTRONICALLY);
                            } else {
                                registerPayment(orderObject.get_sum, IFptr.LIBFPTR_PT_ELECTRONICALLY);
                            }

                        }

                        publishProgress("Закрытие чека...");
                        checkError(fptr.closeReceipt());

                        if (orderObject.needCopy) {
                            publishProgress("Печать копии чека...");
                            fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_LAST_DOCUMENT);
                            fptr.report();
                        }
                    }

                    break;
            }

            //Проверка состояния чека
            checkError(fptr.checkDocumentClosed());
            if (!fptr.getParamBool(IFptr.LIBFPTR_PARAM_DOCUMENT_CLOSED)) { // Документ не закрылся.
                publishProgress("Отмена чека...");
                fptr.cancelReceipt();
            }

        } catch (Exception e) {
            result = e.getMessage();
        }

        publishProgress("Закрытие соединения...");
        fptr.close();
        fptr.destroy();

        if (result == null)
            return new PrintResult((int) doc_number);
        else
            return new PrintResult(result);
    }

    public static KKM_Information getKKMInformation(Context context) throws Exception {

        Fptr fptr = new Fptr(context);

        String fptr_settings = context.getSharedPreferences(Const.FPTR_PREFERENCES,
                Context.MODE_PRIVATE).getString(SettingsActivity.DEVICE_SETTINGS, null);

        fptr.setSettings(fptr_settings);
        checkError(fptr, fptr.open());

        fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_STATUS);
        checkError(fptr, fptr.queryData());

        long shiftState = fptr.getParamInt(IFptr.LIBFPTR_PARAM_SHIFT_STATE);

        fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_CASH_SUM);
        checkError(fptr, fptr.queryData());

        double cashSum = fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM);

        fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_CASHIN_SUM);
        checkError(fptr, fptr.queryData());

        double incomeSum = fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM);

        fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_CASHOUT_SUM);
        checkError(fptr, fptr.queryData());

        double outcomeSum = fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM);

        fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_PAYMENT_SUM);
        fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, IFptr.LIBFPTR_RT_SELL);
        fptr.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_TYPE, IFptr.LIBFPTR_PT_CASH);
        checkError(fptr, fptr.queryData());

        double sellSum = fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM);

        fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_PAYMENT_SUM);
        fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, IFptr.LIBFPTR_RT_SELL_RETURN);
        fptr.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_TYPE, IFptr.LIBFPTR_PT_CASH);
        checkError(fptr, fptr.queryData());

        double returnSum = fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM);

        fptr.close();
        fptr.destroy();

        return new KKM_Information(shiftState,
                cashSum,
                incomeSum,
                outcomeSum,
                sellSum,
                returnSum);
    }

}
