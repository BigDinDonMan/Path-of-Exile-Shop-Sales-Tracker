import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class AddToListCommand<T> implements Command {

    private List<T> list;
    private T item;

    @Override
    public void undo() {
        list.remove(item);
    }

    @Override
    public void redo() {
        list.add(item);
    }
}
