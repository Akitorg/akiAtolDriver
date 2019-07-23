package com.ex.akiatol.print;

/**
 * Ошибка печати чека
 * Created by Leo on 06.04.17.
 */

public class PrintResult {

    public final static int STATUS_DONE = 0;
    public final static int STATUS_ERROR = 1;

    private final int result_status;
    private final String result;

    PrintResult (String error){
        result_status = STATUS_ERROR;
        result = error;
    }

    PrintResult (int number){
        result_status = STATUS_DONE;
        result = String.valueOf(number);
    }

    public int getResult_status() {
        return result_status;
    }

    public String getResult() {
        return result;
    }
}
//© Все права на распостранение и модификацию модуля принадлежат ООО "АКИП" (www.akitorg.ru)