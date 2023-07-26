package hka.erhebungsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    Context context;
    ArrayList<Integer> numListe;
    ArrayList<String> startListe, zielListe, abgeschlossenListe;

    public RecyclerViewAdapter(Context context, ArrayList<Integer> numListe, ArrayList<String> startListe, ArrayList<String> zielListe, ArrayList<String> abgeschlossenListe) {
        this.context = context;
        this.numListe = numListe;
        this.startListe = startListe;
        this.zielListe = zielListe;
        this.abgeschlossenListe = abgeschlossenListe;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        int fahrtID = numListe.get(position);
        holder.tv_rv_num.setText(String.valueOf(numListe.get(position)));
        holder.tv_rv1.setText(String.valueOf(startListe.get(position)));
        holder.tv_rv2.setText(String.valueOf(zielListe.get(position)));
        if (String.valueOf(abgeschlossenListe.get(position)).equals("Ja")){
            holder.tv_rv_num.setTextColor(Color.rgb(0, 255, 0));
            holder.tv_rv1.setTextColor(Color.rgb(0, 255, 0));
            holder.tv_rv2.setTextColor(Color.rgb(0, 255, 0));
        } else {
            holder.tv_rv_num.setTextColor(Color.rgb(255, 0, 0));
            holder.tv_rv1.setTextColor(Color.rgb(255, 0, 0));
            holder.tv_rv2.setTextColor(Color.rgb(255, 0, 0));
        }
        holder.recyclerViewItem.setOnClickListener(view -> {
            Intent intent = new Intent (context, InfosFahrtActivity.class);
            intent.putExtra("FahrtID", fahrtID);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return startListe.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_rv_num, tv_rv1, tv_rv2;
        LinearLayout recyclerViewItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_rv_num = itemView.findViewById(R.id.tv_rv_num);
            tv_rv1 = itemView.findViewById(R.id.tv_rv1);
            tv_rv2 = itemView.findViewById(R.id.tv_rv2);
            recyclerViewItem = itemView.findViewById(R.id.item_recyclerview);
        }
    }
}
