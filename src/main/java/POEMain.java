import poedatatracker.core.ApplicationDatabase;

public class POEMain {

    public static void main(String[] args) {
        ApplicationDatabase.initialize();
        TrackerApp.main(args);
    }

}
