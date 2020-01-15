package com.ex.akiatol.print;

import java.io.Serializable;

/**
 *
 * Created by Leo on 2019-07-22.
 */
public enum PrintType implements Serializable {
    ORDER_CASH,
    ORDER_CARD,
    ORDER_ADVANCE,
    ORDER_CREDIT,
    ORDER_OTHER,
    RETORDER_CARD,
    RETORDER_CASH,
    RETORDER_ADVANCE,
    RETORDER_CREDIT,
    RETORDER_OTHER,
    ORDER_COMBO,
    INCOME,
    OUTCOME,
    ZREP,
    XREP,
    CORRECTION,
    OPEN_SESSION
}
