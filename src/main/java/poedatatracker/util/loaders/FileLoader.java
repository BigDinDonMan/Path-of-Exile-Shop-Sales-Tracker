package poedatatracker.util.loaders;

import java.util.List;

public interface FileLoader<T> {
    List<T> loadLog(String logPath);
}
