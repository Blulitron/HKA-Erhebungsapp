package hka.erhebungsapp.ui.export;

import static java.lang.String.format;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.opencsv.CSVWriter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import hka.erhebungsapp.BuildConfig;
import hka.erhebungsapp.DatabaseHelper;
import hka.erhebungsapp.R;
import hka.erhebungsapp.databinding.FragmentExportBinding;

public class ExportFragment extends Fragment {

    private FragmentExportBinding binding;
    String timeStamp, timeStampFormatted;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ExportViewModel exportViewModel =
                new ViewModelProvider(this).get(ExportViewModel.class);

        binding = FragmentExportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textExport;
        exportViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Button exportButton = root.findViewById(R.id.buttonExport);
        Button sendenButton = root.findViewById(R.id.buttonSenden);

        sendenButton.setEnabled(false);

        exportButton.setOnClickListener(view -> {
            createCSVFile(DatabaseHelper.TABLE_NAME_FAHRTEN);
            createCSVFile(DatabaseHelper.TABLE_NAME_EREIGNISSE);
            createCSVFile(DatabaseHelper.TABLE_NAME_EVALUATION);
            File dataFileDir = new File(Environment.getExternalStorageDirectory() + "/Documents/HKA-Erhebungsapp/Data");
            zipFolder(dataFileDir);
            sendenButton.setEnabled(true);
        });

        sendenButton.setOnClickListener(view -> sendMail());
        return root;
    }

    public void createCSVFile(String tableName){
        //Dateipfad definieren:
        DatabaseHelper dbh = new DatabaseHelper(getContext());
        File dbFileDir = new File(Environment.getExternalStorageDirectory() + "/Documents/HKA-Erhebungsapp/Data/Tabellen");
        if (!dbFileDir.exists()){
            dbFileDir.mkdirs();
        }

        //Datum-Format für Datei erstellen:

        String timeStamp = getDate();
        String timeStampFormatted = timeStamp.replace(":", "-");

        //Datei erstellen:
        File dbFile = new File(dbFileDir,  tableName + "_" + timeStampFormatted + ".csv");
        try {
            dbFile.createNewFile();
            CSVWriter csvWriter = new CSVWriter(new FileWriter(dbFile));
            SQLiteDatabase db = dbh.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
            csvWriter.writeNext(cursor.getColumnNames());
            while (cursor.moveToNext()){
                List<String> list = new ArrayList<>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    list.add(cursor.getString(i));
                }
                String arrString[] = new String[list.size()];
                for (int i = 0; i < list.size(); i++){
                    arrString[i] = list.get(i);
                }
                csvWriter.writeNext(arrString);
            }
            csvWriter.close();
            cursor.close();
        } catch (Exception sqlException) {
            Log.e("ExportFragment", sqlException.getMessage(), sqlException);
            Toast.makeText(getContext(), "Fehler beim Export der Tabelle " + tableName, Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Zips a Folder to "[Folder].zip"
     * @param toZipFolder Folder to be zipped
     * @return the resulting ZipFile
     */
    public File zipFolder(File toZipFolder) {
        timeStamp = getDate();
        timeStampFormatted = timeStamp.replace(":", "-");
        timeStamp = timeStampFormatted;
        timeStampFormatted = timeStamp.replace(" ", "_");
        File ZipFile = new File(toZipFolder.getParent(), format("%s_" + timeStampFormatted + ".zip", toZipFolder.getName()));
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(ZipFile));
            zipSubFolder(out, toZipFolder, toZipFolder.getPath().length());
            out.close();
            return ZipFile;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Main zip Function
     * @param out Target ZipStream
     * @param folder Folder to be zipped
     * @param basePathLength Length of original Folder Path (for recursion)
     */
    private static void zipSubFolder(ZipOutputStream out, File folder, int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];

                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath.substring(basePathLength + 1);

                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(file.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
                out.closeEntry();
            }
        }
    }
    public void sendMail() {
        try {
            String filename = "Data_" + timeStampFormatted + ".zip";
            File filelocation = new File(Environment.getExternalStorageDirectory() + "/Documents/HKA-Erhebungsapp", filename);
            Uri path = FileProvider.getUriForFile(Objects.requireNonNull(getContext()), BuildConfig.APPLICATION_ID + ".provider", filelocation);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            // set the type to 'email'
            emailIntent .setType("vnd.android.cursor.dir/email");
            String to[] = {"email@example.com"};
            emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
            // the attachment
            emailIntent .putExtra(Intent.EXTRA_STREAM, path);
            // the mail subject
            emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Datensatz" );
            startActivity(Intent.createChooser(emailIntent , "Email senden"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Datei kann nicht gesendet werden...", Toast.LENGTH_SHORT).show();
        }
    }
    public String getDate(){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(date);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}