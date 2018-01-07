package nl.cerios.cdbt.read;

import nl.cerios.cdbt.data.TableData;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/**
 * Created by dwhelan on 16/11/2017.
 */
public class LineReader extends AbstractReader {
    protected int itemCount_ ;
    protected LineIterator iterator_;
    protected TableData dataTemplate_;

    public LineReader() {
        reset();
    }

    //Resets to blank state
    private void reset() {
        itemCount_ = 0;
        iterator_ = null;
        dataTemplate_ = null;
    }

    //Attempts to open the file
    @Override
    public void openFile(String filename) throws IOException {
        File f = new File(filename);

        if (!f.exists()) throw new IOException("Error: File not found: " + filename);

        iterator_ = FileUtils.lineIterator(f);

        if (iterator_.hasNext()) {

            //TODO: Make this not gaping
            //Simple way of accounting for forward and back slashes
            int x = filename.lastIndexOf('/');
            int y = filename.lastIndexOf('\\');

            //We take the later mark as correct
            int z = (x > y ? x : y);

            String type = filename.substring(z + 1, filename.lastIndexOf('.'));
            String line = iterator_.nextLine();

            setupTemplate(type, line);
        }

        else throw new IOException("Error configuring table data from first line in .csv");
    }

    //Attempts to close the file
    @Override
    public void close() throws IOException {
        iterator_.close();
        reset();
    }

    //Returns the next line, or an empty string
    @Override
    public TableData readItem() throws IOException {
        if (iterator_ == null) throw new IOException("Error: Attempted to read while reader was closed");

        String line =  null;

        if (iterator_.hasNext()) {
            line = iterator_.nextLine();
            itemCount_ += 1;
        }

        return convertLine(line);
    }

    //Returns the line count
    @Override
    public int getItemCount() { return itemCount_; }

    //Returns the column count of the data
    @Override
    public int getColCount() {
        return dataTemplate_.getColumnCount();
    }

    //Returns the data template
    @Override
    public TableData getDataTemplate() { return dataTemplate_; }

    //Pulls column names from the first line of a .csv file
    protected void setupTemplate(String type, String firstLine) {
        dataTemplate_ = new TableData(type);

        String[] data = firstLine.split("\",\"");

        for (String s : data) {
            dataTemplate_.addData(stripQuotes(s), "");
        }
    }

    //Parse data out of .csv line using splits
    protected TableData convertLine(String line) {

        //Return null for null
        if (line == null) return null;

        String[] data = line.split("\",\"");
        if (data.length == dataTemplate_.getColumnCount()) {
            TableData td = new TableData(dataTemplate_);

            int i = 0;
            for (String s : td.getColumns()) {
                td.setData(stripQuotes(s), stripQuotes(data[i++]));
            }
            return td;
        }

        else
            System.out.println(data.length + "!=" + dataTemplate_.getColumnCount());return null;

    }

    //Strips " from start and end of string
    protected String stripQuotes(String string) {
        return (string != null ? string.replaceAll("^\"|\"$", "") : null);
    }
}
