package com.ex.akiatol;

import android.content.Context;
import android.graphics.Color;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.io.Serializable;

/**
 * Адаптер списка чеков
 * Created by Leo on 30.03.17.
 */

public class ChequeListAdapter extends ArrayAdapter<ChequeListAdapter.Cheque> {

    private final Context context;
    private final Cheque[] checks;

    public ChequeListAdapter(@NonNull Context context, Cheque[] values) {
        super(context, R.layout.list_item_cheque, values);
        this.context = context;
        this.checks = values;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = convertView;
        ViewHolder holder;

        if (convertView == null) {

            assert inflater != null;
            rowView = inflater.inflate(R.layout.list_item_cheque, parent, false);

            holder = new ViewHolder();
            holder.tv_client = rowView.findViewById(R.id.tv_client);
            holder.tv_price = rowView.findViewById(R.id.tv_price);
            holder.tv_date = rowView.findViewById(R.id.tv_date);
            holder.tv_time = rowView.findViewById(R.id.tv_time);

            rowView.setTag(holder);

        } else
            holder = (ViewHolder) rowView.getTag();

        holder.tv_client.setText(checks[position].client);
        holder.tv_price.setText(Const.priceDouble(checks[position].summ));
        holder.tv_date.setText(checks[position].date);
        holder.tv_time.setText(checks[position].time);

        if (checks[position].time.toString().contains("\u20BD") && !checks[position].time.toString().equals("0.00 \u20BD"))
            holder.tv_time.setTextColor(Color.RED);
        else
            holder.tv_time.setTextColor(ContextCompat.getColor(getContext(), R.color.txt_time));

        return rowView;
    }

    private class ViewHolder {

        TextView tv_client;
        TextView tv_price;
        TextView tv_date;
        TextView tv_time;

    }

    public static Cheque newCheque(String extid, String client, double summ, String date, Spanned time){
        return new Cheque(extid, client, summ, date, time);
    }

    public static Cheque newCheque(String extid, String client, double summ,
                                   String date, Spanned time, boolean is_cash, String sale){
        return new Cheque(extid, client, summ, date, time, is_cash, sale);
    }

    public static class Cheque implements Serializable{

        double summ;
        public String extid;
        public String client;
        String date;
        Spanned time;
        public boolean is_cash = false;
        public String sale;

        Cheque (String extid, String client, double summ, String date, Spanned time){
            this.extid = extid;
            this.client = client;
            this.date = date;
            this.time = time;
            this.summ = summ;
        }

        Cheque (String extid, String client, double summ, String date, Spanned time, boolean is_cash, String sale){
            this(extid, client, summ, date, time);
            this.is_cash = is_cash;
            this.sale = sale;
        }
    }


}
//© Все права на распостранение и модификацию модуля принадлежат ООО "АКИП" (www.akitorg.ru)