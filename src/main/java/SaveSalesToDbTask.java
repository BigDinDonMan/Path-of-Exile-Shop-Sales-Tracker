import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SaveSalesToDbTask extends Task<Boolean> {

    private List<ShopSale> sales;

    public SaveSalesToDbTask(Collection<? extends ShopSale> c) {
        super();
        this.sales = new ArrayList<>(c);
    }

    @Override
    protected Boolean call() throws Exception {

        return null;
    }
}
