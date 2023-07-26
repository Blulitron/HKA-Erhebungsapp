package hka.erhebungsapp.ui.start;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hka.erhebungsapp.DatabaseHelper;
import hka.erhebungsapp.MainActivity;
import hka.erhebungsapp.R;
import hka.erhebungsapp.NeueRouteActivity;
import hka.erhebungsapp.RecyclerViewAdapter;
import hka.erhebungsapp.databinding.FragmentStartBinding;

public class StartFragment extends Fragment {

    private FragmentStartBinding binding;
    RecyclerView recyclerview;
    DatabaseHelper dbh;
    ArrayList<Integer>numListe;
    ArrayList<String>startListe, zielListe, abgeschlossenListe;
    RecyclerViewAdapter recyclerViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StartViewModel startViewModel =
                new ViewModelProvider(this).get(StartViewModel.class);

        binding = FragmentStartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView textView = binding.textStart;

        recyclerview = root.findViewById(R.id.rV_Start);

        dbh = new DatabaseHelper(getContext());
        numListe = new ArrayList<>();
        startListe = new ArrayList<>();
        zielListe = new ArrayList<>();
        abgeschlossenListe = new ArrayList<>();
        storeDataInArrays();
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), numListe, startListe, zielListe, abgeschlossenListe);
        recyclerview.setAdapter(recyclerViewAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        startViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        binding.fab.setOnClickListener(view -> newJourneyActivity());
        Button newJourneyButton = root.findViewById(R.id.button_newJourney);
        newJourneyButton.setOnClickListener(view -> newJourneyActivity());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        clearDataInArrays();
        storeDataInArrays();
        recyclerViewAdapter.notifyDataSetChanged();
    }

    public void newJourneyActivity(){
        Intent intent = new Intent(getContext(), NeueRouteActivity.class);
        startActivity(intent);
    }

    void storeDataInArrays(){
        Cursor cursor = dbh.getData("SELECT * FROM " + DatabaseHelper.TABLE_NAME_FAHRTEN);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                numListe.add(cursor.getInt(0));
                startListe.add(cursor.getString(1));
                zielListe.add(cursor.getString(2));
                abgeschlossenListe.add(cursor.getString(8));
            }
        }
    }
    void clearDataInArrays(){
        numListe.clear();
        startListe.clear();
        zielListe.clear();
        abgeschlossenListe.clear();
    }
}