package com.pedromoreirareisgmail.rmvendas.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatting;
import com.pedromoreirareisgmail.rmvendas.db.SearchDB;

import static com.pedromoreirareisgmail.rmvendas.db.Contract.EntryClient;

public class ClientAdapter extends CursorAdapter {

    public ClientAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_list_client, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ClientViewHolder holder = new ClientViewHolder(view);

        long id = cursor.getInt(cursor.getColumnIndex(EntryClient._ID));
        String name = cursor.getString(cursor.getColumnIndex(EntryClient.COLUMN_NAME));
        String fone = cursor.getString(cursor.getColumnIndex(EntryClient.COLUMN_FONE));
        Double receivable = SearchDB.receivable(
                context,
                id
        );

        holder.tvName.setText(name);
        holder.tvFone.setText(fone);

        if (receivable < 0) {

            //Cliente em debito - fonte do registro do valor fica vermelha
            holder.tvValueReceive.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.tvValueReceive.setText(Formatting.doubleToCurrency(receivable));

        } else {

            // Cliente com credito ou zerados - fonte do regitro do valor fica azul
            holder.tvValueReceive.setTextColor(context.getResources().getColor(R.color.colorBlue));
            holder.tvValueReceive.setText(Formatting.doubleToCurrency(receivable));
        }
    }

    class ClientViewHolder {

        private final TextView tvName;
        private final TextView tvFone;
        private final TextView tvValueReceive;

        public ClientViewHolder(View view) {

            tvName = view.findViewById(R.id.tv_client_list_name);
            tvFone = view.findViewById(R.id.tv_client_list_fone);
            tvValueReceive = view.findViewById(R.id.tv_client_value_receive_list);
        }
    }
}
