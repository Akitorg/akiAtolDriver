package com.ex.akiatol.print;

/**
 * Created by Leo on 12/04/2019.
 */
 public class KKM_Information {

    public long shiftState;
    public double cashSum;
    public double incomeSum;
    public double outcomeSum;
    public double sellSum;
    public double returnSum;

    KKM_Information(long shiftState,
                    double cashSum,
                    double incomeSum,
                    double outcomeSum,
                    double sellSum,
                    double returnSum) {

        this.shiftState = shiftState;
        this.cashSum = cashSum;
        this.incomeSum = incomeSum;
        this.outcomeSum = outcomeSum;
        this.sellSum = sellSum;
        this.returnSum = returnSum;

    }

}
