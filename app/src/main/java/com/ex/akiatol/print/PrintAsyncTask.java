package com.ex.akiatol.print;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import com.ex.akiatol.Const;
import com.ex.akiatol.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.ex.akiatol.Const.round;

/**
 * Created by Leo on 09/04/2019.
 */
public abstract class PrintAsyncTask extends AsyncTask<String, String, PrintResult> {

    public abstract void setListener(PrintResponseListener listener);

    // Перечень оснований для присвоения реквизиту «признак способа расчета» (тег 1214) соответствующего значения реквизита
    // 1 Полная предварительная оплата до момента передачи предмета расчета «ПРЕДОПЛАТА 100%» или «1» или может не печататься
    // 2 Частичная предварительная оплата до момента передачи предмета расчета «ПРЕДОПЛАТА» или «2» или может непечататься
    // 3 Аванс «АВАНС» или «3» или может не печататься
    // 4 Полная оплата, в том числе с учетом аванса (предварительной оплаты) в момент передачи предмета расчета «ПОЛНЫЙ РАСЧЕТ» или «4» или может не печататься
    // 5 Частичная оплата предмета расчета в момент его передачи с последующей оплатой в кредит «ЧАСТИЧНЫЙ РАСЧЕТ И КРЕДИТ» или «5» или может не печататься
    // 6 Передача предмета расчета без его оплаты в момент его передачи с последующей оплатой в кредит «ПЕРЕДАЧА В КРЕДИТ» или «7» или может не печататься
    // 7 Оплата предмета расчета после его передачи с оплатой в кредит (оплата кредита) «ОПЛАТА КРЕДИТА» или «9» или может не печататься
    // Примечание : В случае если в состав реквизита кассового чека (БСО) «предмет расчета» (тег 1059) входит реквизит «признак способа расчета» (тег 1214), имеющий
    // значение «7», такой кассовый чек (БСО) не может содержать иные реквизиты «предмет расчета» (тег 1059).

    // Перечень оснований для присвоения реквизиту «признак предмета расчета» (тег 1212) соответствующего значения реквизита
    // 1 о реализуемом товаре, за исключением подакцизного товара (наименование и иные сведения, описывающие товар)
    // 2 о реализуемом подакцизном товаре (наименование и иные сведения, описывающие товар)
    // 3 о выполняемой работе (наименование и иные сведения, описывающие работу)
    // 4 об оказываемой услуге (наименование и иные сведения, описывающие услугу)
    // 5 о приеме ставок при осуществлении деятельности по проведению азартных игр
    // 6 о выплате денежных средств в виде выигрыша при осуществлении деятельности по проведению азартных игр
    // 7 о приеме денежных средств при реализации лотерейных билетов, электронных лотерейных билетов, приеме лотерейных ставок при осуществлении деятельности по проведению лотерей
    // 8 о выплате денежных средств в виде выигрыша при осуществлении деятельности по проведению лотерей
    // 9 о предоставлении прав на использование результатов интеллектуальной деятельности или средств индивидуализации
    // 10 об авансе, задатке, предоплате, кредите, взносе в счет оплаты, пени, штрафе, вознаграждении, бонусе и ином аналогичном предмете расчета
    // 11 о вознаграждении пользователя, являющегося платежным агентом (субагентом), банковским платежным агентом (субагентом), комиссионером, поверенным или иным агентом
    // 12 о предмете расчета, состоящем из предметов, каждому из которых может быть присвоено значение от "1" до "11"
    // 13 о предмете расчета, не относящемуся к предметам расчета, которым может быть присвоено значение от "1" до "12" и от "14" до "18"
    // 14 о передаче имущественных прав
    // 15 о внереализационном доходе
    // 16 о суммах расходов, уменьшающих сумму налога (авансовых платежей) в соответствии с пунктом 3.1 статьи 346.21 Налогового кодекса Российской Федерации
    // 17 о суммах уплаченного торгового сбора
    // 18 о курортном сборе

    abstract void registerPosition(String name,
                                   double price,
                                   double quantity,
                                   double positionSum,
                                   int taxNumber,
                                   double taxSum,
                                   boolean recountVatSum,
                                   ChequeType chequeType,
                                   String type,
                                   double discount,
                                   boolean isImport,
                                   String country,
                                   String decNumber) throws Exception;

    void registerPositions(PrintObjects.Order orderObject, PrintType printType, boolean recountVatSum) throws Exception {

        double positions_sum = 0;

        //Состав чека
        for (int i = 0; i < orderObject.goods.length; i++) {

            PrintObjects.OrderGood item = (PrintObjects.OrderGood) orderObject.goods[i].clone();

            int tax_vat = getVat(item.vat_sum, item.vat);

            double position_sum;
            boolean isCombo = printType == PrintType.ORDER_COMBO;
            if (isCombo) {
                position_sum = round(item.price * item.count, 2);
            } else {
                position_sum = round((item.price * item.count) / orderObject.full_sum * orderObject.get_sum, 2);
            }

            //Подсчет скидки
            double discount = 0;
            if (item.discount > 0) {
                discount = round(item.discount * position_sum / 100, 2);
                position_sum = round(position_sum - discount, 2);
            }

            double position_price = round(position_sum / item.count, 2);

            if (orderObject.type == ChequeType.CHECK_OF_SHIPMENT || orderObject.type == ChequeType.PART_PRE_PAY) {
                item.name = "Предоплата за " + item.name;
            }

            item.price = position_price;
            item.dsum = position_sum;

            positions_sum += item.dsum;

            if ((positions_sum != orderObject.get_sum && !isCombo)) {
                if (i + 1 == orderObject.goods.length) { //Последняя позиция в списке - занесем на нее остаток (если нужно)
                    item.dsum += round(orderObject.get_sum - positions_sum, 2);
                    item.price = round(item.dsum / item.count, 2);
                } else if (i + 2 == orderObject.goods.length) { // Предпоследняя позиция - проверим чтобы сумма уже не превышала сумму дока
                    if (positions_sum > orderObject.get_sum) {
                        item.dsum -= round(positions_sum - orderObject.get_sum, 2) - 0.1;
                        item.price = round(item.dsum / item.count, 2);
                    }
                }
            }

            registerPosition(item.name, item.price, item.count, item.dsum, tax_vat, item.vat_sum, recountVatSum,
                    orderObject.type, item.type, discount, item.isImport, item.country, item.decNumber);

        }

    }

    int getByteSNO(int sno) {
        switch (sno) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 4;
            case 4:
                return 8;
            case 5:
                return 16;
            case 6:
                return 32;
            default:
                return 1;
        }
    }

    boolean checkLicense(Context context, Date kkm_date) {

        SharedPreferences prefs = context.getSharedPreferences("prefs", MODE_PRIVATE);
        String con_user = prefs.getString(context.getString(R.string.prefs_login), "demou");
        if (con_user.equals("demou")) //Демо режим
            return true;

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String l_date = Const.encode(prefs.getString("sjt", ""), "sjt");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = format.parse(l_date);
            if (date.getTime() < kkm_date.getTime())
                return false;
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    abstract int getVat(double tax_sum, int tax);

}
