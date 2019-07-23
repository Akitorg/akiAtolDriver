package com.ex.akiatol.print;

import com.ex.akiatol.Const;

import java.io.Serializable;

/**
 * Объекты печати
 * Created by Leo on 06.04.17.
 */
@SuppressWarnings("WeakerAccess")
public abstract class PrintObjects implements Serializable {

    public static class Order extends PrintObjects {

        public String extid;
        public OrderGood[] goods;
        public double full_sum;
        public double get_sum;
        public int discount;
        public String e_mail;
        public int sno;
        public ChequeType type;

        public String client_name;
        public String client_inn;

        public boolean needCopy;

        public Order (String extid, OrderGood[] goods, double sum, double get_sum, ChequeType type,
                      String e_mail, String client_name, String client_inn, boolean needCopy) {

            this.extid = extid;
            this.goods = goods;
            this.full_sum = sum;
            this.get_sum = get_sum;
            this.type = type;

            this.e_mail = e_mail;
            this.client_name = client_name;
            this.client_inn = client_inn;
            this.needCopy = needCopy;

        }

        public void set_get_sum (double payback) {
            this.get_sum = payback;
            if (get_sum > full_sum) {
                this.get_sum = full_sum;
            }
        }

        public void set_e_mail(String e_mail){
            this.e_mail = e_mail;
        }

        public void set_discount(int discount){
            this.discount = discount;
            //this.full_sum = Const.countDiscout(full_sum, discount);

            for (OrderGood g:goods) {
                g.dsum = Const.countDiscout(g.dsum, discount);
                full_sum+=g.dsum;
            }
        }

        public void set_sno(int sno){
            this.sno = sno;
        }

        public void set_client_info (String name, String inn){
            client_name = name;
            client_inn = inn;
        }

        public void set_need_copy() {
            needCopy = true;
        }

    }

    public static class OrderGood implements Cloneable, Serializable {

        public String extid;
        public String name;
        public String unitname;
        public String type;
        public double count;
        public double price;
        public double discount;
        public int vat;
        public double vat_sum;
        public double dsum;
        public int sno;

        public boolean isImport;
        public String country;
        public String decNumber;

        public OrderGood (String extid, String name, double price, double count, double discount,
                          String unitname, int vat, double vat_sum, double dsum, int sno, String type,
                          boolean isImport, String country, String decNumber){

            this.extid = extid;
            this.name = name;
            this.unitname = unitname;
            this.count = count;
            this.price = price;
            this.discount = discount;
            this.vat = vat;
            this.vat_sum = vat_sum;
            this.dsum = dsum;
            this.sno = sno;
            this.type = type;

            this.isImport = isImport;
            this.country = country;
            this.decNumber = decNumber;
        }

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

    }

    public static class InOutcome extends PrintObjects {

        public double sum;
        public String person;
        public String reason;

        public InOutcome (double sum, String reason, String person){

            this.sum = sum;
            this.person = person;
            this.reason = reason;

        }

    }

    public static class ZRep extends PrintObjects {}
    public static class XRep extends PrintObjects {}

    public static class Correction extends PrintObjects {

        public final static int PAY_TYPE_CASH = 0;
        public final static int PAY_TYPE_CARD = 1;

        public final static int DOC_TYPE_ORDER = 0;
        public final static int DOC_TYPE_RETORDER = 1;

        public double sum;
        public int doc_type;
        public int pay_type;

    }

    public static class OPEN_SEESION extends PrintObjects {}

}
//© Все права на распостранение и модификацию модуля принадлежат ООО "АКИП" (www.akitorg.ru)