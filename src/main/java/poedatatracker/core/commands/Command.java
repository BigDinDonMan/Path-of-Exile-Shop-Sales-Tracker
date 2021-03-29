package poedatatracker.core.commands;

public interface Command {
    void undo();
    void redo();
}
