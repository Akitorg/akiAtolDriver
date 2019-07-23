package com.ex.akiatol.print;

import java.io.Serializable;

/**
 * Created by Leo on 2019-07-22.
 */
public enum PrintType implements Serializable {
    ORDER_CASH,
    ORDER_CARD,
    RETORDER_CARD,
    RETORDER_CASH,
    INCOME,
    OUTCOME,
    ZREP,
    XREP,
    CORRECTION,
    OPEN_SESSION,
    ORDER_COMBO
}
