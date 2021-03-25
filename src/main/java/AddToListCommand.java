import lombok.AllArgsConstructor;

import javax.management.ConstructorParameters;
import java.util.ArrayList;
import java.util.List;
//todo: add additional callbacks, like updating UI
public class AddToListCommand<T> implements Command {

    private List<T> list;
    private T item;
    private List<Runnable> undoCallbacks;
    private List<Runnable> redoCallbacks;

    public AddToListCommand(List<T> listToEdit, T item) {
        this.list = listToEdit;
        this.item = item;
        this.undoCallbacks = new ArrayList<>();
        this.redoCallbacks = new ArrayList<>();
    }

    @Override
    public void undo() {
        list.remove(item);
        undoCallbacks.forEach(Runnable::run);
    }

    @Override
    public void redo() {
        list.add(item);
        redoCallbacks.forEach(Runnable::run);
    }

    public void addUndoCallback(Runnable r) {
        this.undoCallbacks.add(r);
    }

    public void addRedoCallback(Runnable r) {
        this.redoCallbacks.add(r);
    }
}
