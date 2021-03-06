package com.ex.akiatol.print;

import java.io.Serializable;

/**
 * Тип чека для печати
 * Created by Leo on 24.08.2018.
 */

public enum ChequeType implements Serializable {

    FULL_PAY, // Обычная оплата
    FULL_PRE_PAY, // Предоплата 100%
    PART_PRE_PAY, // Предоплата частичная
    CHECK_OF_SHIPMENT; // Чек отгрузки

    public int getTag()  {

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

        switch (this){
            case FULL_PAY:
                return 4;
            case FULL_PRE_PAY:
                return 1;
            case PART_PRE_PAY:
                return 2;
            case CHECK_OF_SHIPMENT:
                return 6;
            default:
                return 0;
        }
    }

    public static ChequeType getByTag(int tag) {

        switch (tag) {
            case 4:
                return FULL_PAY;
            case 1:
                return FULL_PRE_PAY;
            case 2:
                return PART_PRE_PAY;
            case 6:
                return CHECK_OF_SHIPMENT;
            default:
                return FULL_PAY;
        }

    }

    @Override
    public String toString() {
        switch (this) {
            case FULL_PAY:
                return "";
            case FULL_PRE_PAY:
                return "Аванс 100%";
            case PART_PRE_PAY:
                return "Частичная предоплата";
            case CHECK_OF_SHIPMENT:
                return "Отгрузка";
            default:
                return "";
        }
    }
}
