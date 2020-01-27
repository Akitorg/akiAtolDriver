package com.ex.akiatol.print;

import android.content.Context;
import android.preference.PreferenceManager;



import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;

import static com.ex.akiatol.Const.FPTR_PREFERENCES;
import static com.ex.akiatol.print.PrintType.*;

/**
 * Печать чека через драйвер Атол 9 версии
 * Created by Leo on 09/04/2019.
 */
public class PrintAtol9AsyncTask extends PrintAsyncTask {

    @Override
    public void setListener(PrintResponseListener listener) {

    }

    @Override
    void registerPosition(String name, double price, double quantity, double positionSum, int taxNumber, double taxSum, ChequeType chequeType, String type, double discount, boolean isImport, String country, String decNumber) throws Exception {

    }

    @Override
    int getVat(double tax_sum, int tax) {
        return 0;
    }

    @Override
    protected PrintResult doInBackground(String... strings) {
        return null;
    }

//    private WeakReference<Context> contextWeakReference;
//
//    private PrintType printType;
//    private HashMap<Integer, PrintObjects> printObjectsSNO;
//    private IFptr fptr;
//    private String result;
//
//    private final String fptr_settings;
//    private int doc_number;
//
//    private PrintResponseListener printResponseListener;
//
    public PrintAtol9AsyncTask(HashMap<Integer, PrintObjects> printObjectsSNO,
                               PrintType printType, Context context) {

//        this.printType = printType;
//        this.printObjectsSNO = printObjectsSNO;
//
//        contextWeakReference = new WeakReference<>(context);
//
//        this.fptr_settings = context.getSharedPreferences(FPTR_PREFERENCES,
//                Context.MODE_PRIVATE).getString(SettingsActivity.DEVICE_SETTINGS, null);
    }

//    public void setListener(PrintResponseListener printResponseListener) {
//        this.printResponseListener = printResponseListener;
//    }
//
//    private void checkError(int result) throws Exception {
//
//        if (result < 0) {
//
//            int rc = fptr.get_ResultCode();
//            if (rc < 0) {
//
//                String rd = fptr.get_ResultDescription(), bpd = null;
//                if (rc == -6) {
//                    bpd = fptr.get_BadParamDescription();
//                }
//
//                if (bpd != null)
//                    throw new Exception(String.format(Locale.getDefault(), "[%d] %s (%s)", rc, rd, bpd));
//                else throw new Exception(String.format(Locale.getDefault(), "[%d] %s", rc, rd));
//
//            }
//
//        }
//
//    }
//
//    private void openCheck(int type) throws Exception {
//
//        try {
//
//            checkError(fptr.put_Mode(IFptr.MODE_REGISTRATION));
//            checkError(fptr.SetMode());
//            checkError(fptr.put_CheckType(type));
//            checkError(fptr.OpenCheck());
//
//        } catch (Exception e) {
//
//            // Проверка на превышение смены
//            if (fptr.get_ResultCode() == -3822) {
//                reportZ();
//                openCheck(type);
//            } else {
//                throw e;
//            }
//
//        }
//
//    }
//
//    private void closeCheck(int typeClose) throws Exception {
//
//        checkError(fptr.put_TypeClose(typeClose));
//        checkError(fptr.CloseCheck());
//
//    }
//
//    @Override
//    void registerPosition(String name, double price, double quantity, double positionSum,
//                          int taxNumber, ChequeType chequeType, String type, double discount,
//                          boolean isImport, String country, String decNumber) throws Exception {
//
//        checkError(fptr.put_TaxNumber(taxNumber));
//        checkError(fptr.put_PositionSum(positionSum));
//        checkError(fptr.put_Quantity(quantity));
//        checkError(fptr.put_Price(price));
//        checkError(fptr.put_TextWrap(IFptr.WRAP_WORD));
//        checkError(fptr.put_Name(name));
//
//        switch (chequeType) {
//
//            case FULL_PAY:
//                checkError(fptr.put_PositionPaymentType(4)); // Полная оплата в момент передачи предмета
//                //checkError(fptr.put_PositionType(1)); // о реализуемом товаре
//                break;
//
//            case FULL_PRE_PAY:
//                checkError(fptr.put_PositionPaymentType(1)); // Полная предварительная оплата
//                //checkError(fptr.put_PositionType(10)); // об авансе, задатке, предоплате, кредите
//                break;
//
//            case PART_PRE_PAY:
//                checkError(fptr.put_PositionPaymentType(2)); // Полная предварительная оплата
//                //checkError(fptr.put_PositionType(10)); // об авансе, задатке, предоплате, кредите
//                break;
//
//            case CHECK_OF_SHIPMENT:
//                checkError(fptr.put_PositionPaymentType(4)); // Передача предмета расчета
//                //checkError(fptr.put_PositionType(1)); // о реализуемом товаре
//                break;
//
//            default:
//                break;
//        }
//
//        if (type != null && type.equals("SERVICE")) {
//            checkError(fptr.put_PositionType(4));
//        } else {
//            checkError(fptr.put_PositionType(1));
//        }
//
//        checkError(fptr.Registration());
//    }
//
//    @Override
//    int getVat(double tax_sum, int tax) {
//        int tax_vat = IFptr.TAX_VAT_NO;
//        if (tax_sum > 0) {
//            if (tax == 10)
//                tax_vat = IFptr.TAX_VAT_10;
//            else
//                tax_vat = IFptr.TAX_VAT_18;
//        }
//
//        return tax_vat;
//    }
//
//    private void payment(double sum, int type) throws Exception {
//
//        checkError(fptr.put_Summ(sum));
//        checkError(fptr.put_TypeClose(type));
//        checkError(fptr.Payment());
//
//    }
//
//    private void reportZ() throws Exception {
//
//        checkError(fptr.put_Mode(IFptr.MODE_REPORT_CLEAR));
//        checkError(fptr.SetMode());
//        checkError(fptr.put_ReportType(IFptr.REPORT_Z));
//        checkError(fptr.Report());
//
//    }
//
//    private void reportX() throws Exception {
//
//        checkError(fptr.put_Mode(IFptr.MODE_REPORT_NO_CLEAR));
//        checkError(fptr.SetMode());
//        checkError(fptr.put_ReportType(IFptr.REPORT_X));
//        checkError(fptr.Report());
//
//    }
//
//    private void printIncome(double summ) throws Exception {
//
//        checkError(fptr.put_Mode(IFptr.MODE_REGISTRATION));
//        checkError(fptr.SetMode());
//        checkError(fptr.put_Summ(summ));
//        checkError(fptr.CashIncome());
//
//    }
//
//    private void printOutcome(double summ) throws Exception {
//
//        checkError(fptr.put_Mode(IFptr.MODE_REGISTRATION));
//        checkError(fptr.SetMode());
//        checkError(fptr.put_Summ(summ));
//        checkError(fptr.CashOutcome());
//
//    }
//
//    private void putPhoneEmail(String e_mail) throws Exception {
//
//        if (e_mail.trim().length() == 0)
//            return;
//
//        checkError(fptr.put_FiscalPropertyNumber(1008));
//        checkError(fptr.put_FiscalPropertyType(IFptr.FISCAL_PROPERTY_TYPE_STRING));
//        checkError(fptr.put_FiscalPropertyValue(e_mail));
//        checkError(fptr.WriteFiscalProperty());
//
//    }
//
//    private void putUser(String user) throws Exception {
//
//        if (user.trim().length() == 0)
//            return;
//
//        checkError(fptr.put_FiscalPropertyNumber(1021));
//        checkError(fptr.put_FiscalPropertyPrint(true));
//        checkError(fptr.put_FiscalPropertyType(IFptr.FISCAL_PROPERTY_TYPE_STRING));
//        checkError(fptr.put_FiscalPropertyValue(user));
//        checkError(fptr.WriteFiscalProperty());
//
//    }
//
//    private void putInn(String inn) throws Exception {
//
//        if (inn.trim().length() == 0)
//            return;
//
//        checkError(fptr.put_FiscalPropertyNumber(1203));
//        checkError(fptr.put_FiscalPropertyPrint(true));
//        checkError(fptr.put_FiscalPropertyType(IFptr.FISCAL_PROPERTY_TYPE_STRING));
//        checkError(fptr.put_FiscalPropertyValue(inn));
//        checkError(fptr.WriteFiscalProperty());
//
//    }
//
//    private void putSNO(int sno) throws Exception {
//
//        if (sno == 0)
//            return;
//
//        checkError(fptr.put_FiscalPropertyNumber(1055));
//        checkError(fptr.put_FiscalPropertyType(IFptr.FISCAL_PROPERTY_TYPE_BYTE));
//        checkError(fptr.put_FiscalPropertyValue(String.valueOf(sno)));
//        checkError(fptr.WriteFiscalProperty());
//
//    }
//
//    private void connectToKKM() throws Exception {
//
//        fptr.create(contextWeakReference.get());
//
//        publishProgress("Загрузка настроек...");
//        checkError(fptr.put_DeviceSettings(fptr_settings));
//
//        publishProgress("Установка соединения...");
//        checkError(fptr.put_DeviceEnabled(true));
//
//        publishProgress("Проверка связи...");
//        checkError(fptr.GetStatus());
//
//        // Отменяем чек, если уже открыт. Ошибки "Неверный режим" и "Чек уже закрыт"
//        // не являются ошибками, если мы хотим просто отменить чек
//        try {
//
//            checkError(fptr.CancelCheck());
//
//        } catch (Exception e) {
//
//            int rc = fptr.get_ResultCode();
//
//            if (rc != -16 && rc != -3801) {
//                throw e;
//            }
//
//        }
//
//    }
//
//    @Override
//    protected void onPreExecute() {}
//
//    @Override
//    protected void onProgressUpdate(String... values) {
//
//        if (values == null || values.length == 0) {
//            return;
//        }
//
//        if (printResponseListener != null)
//            printResponseListener.onUpdateListener(values);
//
//    }
//
//    @Override
//    protected void onPostExecute(PrintResult result) {
//
//        if (printResponseListener != null)
//            printResponseListener.onPostExecute(result);
//
//    }
//
//
//    @Override
//    protected PrintResult doInBackground(String... params) {
//
//        fptr = new Fptr();
//
//        Context context = contextWeakReference.get();
//
//        String user = PreferenceManager.getDefaultSharedPreferences(context)
//                .getString(context.getString(R.string.prefs_user_name), "Иванов Иван Иванович");
//
//        String inn = PreferenceManager.getDefaultSharedPreferences(context)
//                .getString(context.getString(R.string.prefs_user_inn), "");
//
//        try {
//
//            connectToKKM();
//
//            if (!checkLicense(context, fptr.get_Date()))
//                return new PrintResult("Лицензия данного устройства истекла! Пожалуйста, произведите оплату в личном кабинете");
//
//            switch (printType) {
//
//                case ZREP:
//
//                    doc_number = fptr.get_DocNumber();
//                    reportZ();
//                    return new PrintResult(doc_number);
//
//                case XREP:
//
//                    doc_number = fptr.get_DocNumber();
//                    reportX();
//                    return new PrintResult(doc_number);
//
//                case INCOME:
//
//                    PrintObjects.InOutcome income = (PrintObjects.InOutcome) printObjectsSNO.get(0);
//
//                    try {
//
//                        if (income == null)
//                            throw new Exception("Объект печати null");
//
//                        doc_number = fptr.get_DocNumber();
//                        printIncome((income).sum);
//
//                    } catch (Exception e) {
//
//                        // Проверка на превышение смены
//                        if (fptr.get_ResultCode() == -3822) {
//
//                            reportZ();
//
//                            doc_number = fptr.get_DocNumber();
//
//                            if (income == null)
//                                throw new Exception("Объект печати null");
//
//                            printIncome(income.sum);
//
//                        } else {
//
//                            throw e;
//
//                        }
//
//                    }
//
//                    return new PrintResult(doc_number);
//
//                case OUTCOME:
//
//                    PrintObjects.InOutcome outcome = (PrintObjects.InOutcome) printObjectsSNO.get(0);
//
//                    try {
//
//                        if (outcome == null)
//                            throw new Exception("Объект печати null");
//
//                        doc_number = fptr.get_DocNumber();
//                        printOutcome(outcome.sum);
//
//                    } catch (Exception e) {
//
//                        // Проверка на превышение смены
//                        if (fptr.get_ResultCode() == -3822) {
//
//                            reportZ();
//
//                            doc_number = fptr.get_DocNumber();
//
//                            if (outcome == null)
//                                throw new Exception("Объект печати null");
//
//                            printOutcome(outcome.sum);
//                        } else {
//                            throw e;
//                        }
//                    }
//
//                    return new PrintResult(doc_number);
//
//                case CORRECTION:
//
//                    for (HashMap.Entry<Integer, PrintObjects> entry : printObjectsSNO.entrySet()) {
//
//                        PrintObjects.Correction correctionObject = (PrintObjects.Correction) entry.getValue();
//
//                        int checkType = IFptr.CHEQUE_TYPE_SELL_CORRECTION;
//                        if (correctionObject.doc_type == PrintObjects.Correction.DOC_TYPE_RETORDER)
//                            checkType = IFptr.CHEQUE_TYPE_SELL_RETURN_CORRECTION;
//
//                        // Открываем чек продажи, попутно обработав превышение смены
//                        publishProgress("Открытие чека...");
//                        openCheck(checkType);
//
//                        doc_number = fptr.get_DocNumber();
//
//                        registerPosition("Коррекция",  correctionObject.sum,1,
//                                correctionObject.sum, IFptr.TAX_VAT_NO, null,
//                                "NORMAL", 0, false, "", "");
//
//                        // Оплачиваем
//                        publishProgress("Оплата...");
//
//                        if (correctionObject.pay_type == PrintObjects.Correction.PAY_TYPE_CASH)
//                            payment(correctionObject.sum, 0);
//                        else if (correctionObject.pay_type == PrintObjects.Correction.PAY_TYPE_CARD)
//                            payment(correctionObject.sum, 1);
//
//                        // Закрываем чек
//                        publishProgress("Закрытие чека...");
//                        closeCheck(0);
//                    }
//
//                    return new PrintResult(doc_number);
//
//                case OPEN_SESSION:
//
//                    checkError(fptr.put_Mode(IFptr.MODE_REGISTRATION));
//                    checkError(fptr.SetMode());
//                    checkError(fptr.OpenSession());
//
//                    doc_number = fptr.get_DocNumber();
//
//                    return new PrintResult(doc_number);
//            }
//
//            // Печать чека продажи или возврата
//            if (printType != PrintType.ORDER_CASH
//                    && printType != ORDER_CARD
//                    && printType != PrintType.RETORDER_CASH
//                    && printType != RETORDER_CARD
//                    && printType != ORDER_COMBO)
//
//                return new PrintResult("Неверный тип печати!");
//
//            int checkType = IFptr.CHEQUE_TYPE_SELL;
//            if (printType == PrintType.RETORDER_CASH || printType == RETORDER_CARD) {
//                checkType = IFptr.CHEQUE_TYPE_RETURN;
//            }
//
//            for (HashMap.Entry<Integer, PrintObjects> entry : printObjectsSNO.entrySet()) {
//
//                //Переподключаемся к ККМ?!
//                fptr.destroy();
//
//                fptr = new Fptr();
//                connectToKKM();
//
//                // Открываем чек продажи, попутно обработав превышение смены
//                publishProgress("Открытие чека...");
//                openCheck(checkType);
//
//                doc_number = fptr.get_DocNumber();
//
//                PrintObjects.Order orderObject = (PrintObjects.Order) entry.getValue();
//
//                putUser(user);
//                putInn(inn);
//
//                if (orderObject.e_mail != null && orderObject.e_mail.trim().length() > 0)
//                    putPhoneEmail(orderObject.e_mail);
//
//                //Налогооблажение
//                if (entry.getKey() > 0)
//                    putSNO(getByteSNO(orderObject.sno));
//
//                publishProgress("Регистрация позиций чека...");
//                registerPositions(orderObject, printType);
//
//                publishProgress("Оплата...");
//
//                if (orderObject.type == ChequeType.CHECK_OF_SHIPMENT) {
//                    payment(orderObject.get_sum, 2);
//                } else {
//                    switch (printType) {
//                        case ORDER_CASH:
//                            payment(orderObject.get_sum, 0);
//                            break;
//                        case ORDER_CARD:
//                            payment(orderObject.get_sum, 1);
//                            break;
//                        case RETORDER_CASH:
//                            payment(orderObject.full_sum, 0);
//                            break;
//                        case RETORDER_CARD:
//                            payment(orderObject.full_sum, 1);
//                            break;
//                        case ORDER_COMBO:
//                            payment(orderObject.get_sum, 0);
//                            payment(orderObject.full_sum - orderObject.get_sum, 1);
//                            break;
//                    }
//                }
//
//                // Закрываем чек
//                publishProgress("Закрытие чека...");
//                closeCheck(0);
//
//            }
//
//        } catch (Exception e) {
//            result = e.getMessage();
//        } finally {
//            fptr.destroy();
//        }
//
//        if (result == null)
//            return new PrintResult(doc_number);
//        else
//            return new PrintResult(result);
//    }
//
//    static KKM_Information getKKMInformation(Context context) throws Exception {
//
//        Fptr fptr = new Fptr();
//
//        fptr.create(context);
//
//        String fptr_settings = context.getSharedPreferences(FPTR_PREFERENCES,
//                Context.MODE_PRIVATE).getString(SettingsActivity.DEVICE_SETTINGS, null);
//
//        checkError(fptr, fptr.put_DeviceSettings(fptr_settings));
//        checkError(fptr, fptr.put_DeviceEnabled(true));
//        checkError(fptr, fptr.GetStatus());
//
//        fptr.destroy();
//
//        return new KKM_Information(0,
//                0,
//                0,
//                0,
//                0,
//                0);
//    }
//
//    private static void checkError(Fptr fptr, int result) throws Exception {
//
//        if (result < 0) {
//
//            int rc = fptr.get_ResultCode();
//            if (rc < 0) {
//
//                String rd = fptr.get_ResultDescription(), bpd = null;
//                if (rc == -6) {
//                    bpd = fptr.get_BadParamDescription();
//                }
//
//                if (bpd != null)
//                    throw new Exception(String.format(Locale.getDefault(), "[%d] %s (%s)", rc, rd, bpd));
//                else throw new Exception(String.format(Locale.getDefault(), "[%d] %s", rc, rd));
//
//            }
//
//        }
//
//    }

}