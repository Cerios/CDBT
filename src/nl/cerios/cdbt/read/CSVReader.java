package nl.cerios.cdbt.read;

import nl.cerios.cdbt.data.TableData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dwhelan on 04/01/2018.
 */
public class CSVReader extends AbstractReader {
    protected int itemCount_;
    protected TableData dataTemplate_;

    protected LineIterator iterator_;

    public CSVReader() { reset(); }

    //Resets reader to a blank state
    public void reset() {
        itemCount_ = 0;
        dataTemplate_ = null;
    }

    //Attempts to open the file
    @Override
    public void openFile(String filename) throws IOException {
        File f = new File(filename);

        if (!f.exists()) throw new IOException("Error: File not found: " + filename);

        iterator_ = FileUtils.lineIterator(f);

        //If the file isn't empty
        if (iterator_.hasNext()) {
            //TODO: Make this not gaping
            //Simple way of accounting for forward and back slashes
            int fwd = filename.lastIndexOf('/');
            int back = filename.lastIndexOf('\\');

            //We take the later mark as correct
            int lastSlash = (fwd > back ? fwd : back);

            String type = filename.substring(lastSlash + 1, filename.lastIndexOf('.'));
            String line = iterator_.nextLine();

            setupTemplate(type, line);
        }

        else throw new IOException("Error configuring table data from first line in .csv");
    }

    //Attempts to close the file
    @Override
    public void close() throws IOException {
        iterator_.close();
    }

    @Override
    public TableData readItem() throws IOException {
        if (iterator_ == null) throw new IOException("Error: Attempted to read while reader was closed");

        if (iterator_.hasNext()) {
            String line = iterator_.nextLine();
            CSVRecord record = CSVParser.parse(line, CSVFormat.DEFAULT).getRecords().get(0);
            itemCount_ +=1;

            return convertLine(record);
        }

        else return null;
    }

    @Override
    public TableData getDataTemplate() { return dataTemplate_; }

    @Override
    public int getItemCount() { return itemCount_; }

    @Override
    public int getColCount() { return dataTemplate_.getColumnCount(); }

    protected void setupTemplate(String type, String firstLine) throws IOException {
        dataTemplate_ = new TableData(type);
        CSVRecord record = CSVParser.parse(firstLine, CSVFormat.DEFAULT).getRecords().get(0);

        System.out.println(record.toString());
        System.out.println(record.get(0));

        for (int i = 0, j = record.size(); i < j; ++i) {
            dataTemplate_.addData(record.get(i), "");
        }
    }

    //Parse data from a line of csv
    protected TableData convertLine(CSVRecord record) {
        //Return null for null
        if (record == null) return null;

        if (record.size() == dataTemplate_.getColumnCount()) {
            TableData td = new TableData(dataTemplate_);
            int i = 0;
            for (String col : td.getColumns()) {
                td.setData(col, record.get(i++));
            }
            return td;
        }
        else {
            System.out.println("Records: " + record.size() + "!= Template:" + dataTemplate_.getColumnCount());
            return null;
        }
    }
}
