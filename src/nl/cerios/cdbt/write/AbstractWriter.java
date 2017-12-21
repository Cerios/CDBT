package nl.cerios.cdbt.write;

import nl.cerios.cdbt.data.TableData;
import java.io.IOException;

/**
 * Created by dwhelan on 15/11/2017.
 */
public abstract class AbstractWriter implements java.lang.AutoCloseable  {
    public abstract void openFile(String filename) throws IOException;

    public abstract void close() throws IOException;

    public abstract boolean writeItem(TableData data) throws IOException;

    public abstract int getItemCount();
}
