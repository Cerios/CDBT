package nl.cerios.cdbt.read;

import nl.cerios.cdbt.data.TableData;
import java.io.IOException;

/**
 * Created by dwhelan on 15/11/2017.
 */
public abstract class AbstractReader implements java.lang.AutoCloseable  {
    TableData dataTemplate_;

    public abstract void openFile(String filename) throws IOException;

    public abstract void close() throws IOException;

    public abstract TableData readItem() throws IOException;

    public abstract TableData getDataTemplate();

    public abstract int getItemCount();
    public abstract int getColCount();
}