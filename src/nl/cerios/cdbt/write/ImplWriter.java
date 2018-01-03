package nl.cerios.cdbt.write;

import nl.cerios.cdbt.data.TableData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by dwhelan on 16/11/2017.
 */
public class ImplWriter extends AbstractWriter {
    protected int itemCount_;
    protected BufferedWriter writer_;
    protected String indent_;
    protected String newLine_;

    public ImplWriter(){
        indent_ = "    ";
        newLine_ = System.getProperty("line.separator");
        reset();
    }

    //Resets to a blank state
    private void reset() {
        itemCount_ = 0;
        writer_ = null;
        indent_ = "    ";
    }

    //Attempts to open the file with Absolute Path
    @Override
    public void openFile(String filename) throws IOException {
        writer_ = new BufferedWriter(new FileWriter(filename, false));
    }

    //Attempts to close the file
    @Override
    public void close() throws IOException {
        writer_.close();
        reset();
    }

    //Writes one item to an open file
    @Override
    public boolean writeItem(TableData data) throws IOException {
        boolean success = false;

        if (data != null) {

            if (itemCount_ >= 1)
            {

                writer_.write(newLine_);
            }

            writer_.write(data.getDataType() +
                    ((data.getAlias() != null && data.getAlias() != "") ? "(" + data.getAlias() + ")" : "") +
                    ":" + newLine_);

            for (String c : data.getColumns())
            {
                writer_.write(indent_ + c + ": " + data.getData(c) + newLine_);
            }


            //writer_.write(data.toString() + newLine_);

            itemCount_ += 1;
            success = true;
        }

        return success;
    }

    //Gets a count of written items
    @Override
    public int getItemCount() { return itemCount_; }

    //Uses a simple toString to output the table data
    public boolean writeAsText(TableData data) {
        boolean written = false;
        try {
            writer_.write(data.toString() + newLine_ );
            written = true;
        }

        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

        return written;
    }
}