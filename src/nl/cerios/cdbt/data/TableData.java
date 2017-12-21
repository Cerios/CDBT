package nl.cerios.cdbt.data;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by dwhelan on 16/11/2017.
 */
public class TableData {
    private String dataType_;
    private String alias_;

    private HashMap<String, String> data_;

    public TableData(String dataType) {
        dataType_ = dataType;
        alias_ = null;

        data_ = new HashMap<String, String>();
    }

    public TableData(TableData tableData) {
        dataType_ = tableData.getDataType();
        alias_ = tableData.getAlias();

        data_ = new HashMap<String, String>();
        copyData(tableData);
    }

    //Copy data
    public void copyData(TableData tableData) {
        for (String s : tableData.getColumns())
        {
            data_.put(s, tableData.getData(s));
        }
    }

    //Add data
    public boolean addData(String col, String row) {
        //Only if the data doesn't already exist
        if (!data_.containsKey(col)) {
            data_.put(col, row);
            return true;
        }

        //If it's null allow us to set
        else if (data_.get(col) == null) {
            data_.replace(col, row);
            return true;
        }

        else return false;
    }

    public boolean setData(String col, String row) {
        //Only if the data already exists
        if (data_.containsKey(col)) {
            data_.replace(col, row);
            return true;
        }
        else return false;
    }

    //Get data by column name
    public String getData(String col) {
        if (data_.containsKey(col)) {
            String out = data_.get(col);

            return out;
        }

        else return null;
    }

    //Get all column names
    public Set<String> getColumns() {
        return data_.keySet();
    }

    public int getColumnCount() { return data_.keySet().size(); }

    public String getAlias() { return alias_; }
    public void setAlias(String alias) { alias_ = alias; }

    public String getDataType() { return dataType_; }

    public String toString() {
        String out = "[" + dataType_ + "]";

        if (alias_ != null){
            out += "(" + alias_ + ")";
        }

        //Data
        out += "{";

        String t = "";
        for (String s : data_.keySet())
        {
            out += t + s + "=" + data_.get(s);
            t = ", ";
        }

        out+="}";

        return out;
    }
}
