package com.ex.akiatol.print;

import java.io.Serializable;

/**
 * Created by Leo on 2019-07-22.
 */
public enum PrintType implements Serializable {
    ORDER_CASH,
    ORDER_CARD,
    ORDER_ADVANCE,
    ORDER_CREDIT,
    RETORDER_CARD,
    RETORDER_CASH,
    RETORDER_ADVANCE,
    RETORDER_CREDIT,
    ORDER_COMBO,
    INCOME,
    OUTCOME,
    ZREP,
    XREP,
    CORRECTION,
    OPEN_SESSION
}
