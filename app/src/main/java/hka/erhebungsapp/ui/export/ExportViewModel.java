package hka.erhebungsapp.ui.export;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExportViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ExportViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Drücke den Button \"Daten exportieren\", um alle deine Fahrten zu exportieren.\n\n" +
                "Drücke danach den Button \"Daten senden\", um die Datei per Mail an uns zu senden.\n" +
                "Alternativ findest du die Daten in deinem Dateimanager unter:\n\"Documents → HKA-Erhebungsapp\"\n" +
                "Wähle dort die ZIP-Datei mit dem aktuellen Datum und sende uns diese manuell zu.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}