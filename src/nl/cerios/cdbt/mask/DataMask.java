package nl.cerios.cdbt.mask;

import nl.cerios.cdbt.data.TableData;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by dwhelan on 20/12/2017.
 */
public class DataMask extends AbstractMask {
    public DataMask() {
        mask = new LinkedHashMap<String, String>();
    }

    //DataMask: Match column name, replace data with mask input
    @Override
    public TableData applyMask(TableData tableData) {
        //Create output
        TableData local = new TableData(tableData.getDataType());

        //Read input
        Set<String> cols = tableData.getColumns();

        String outRow = "";

        //For every data column
        for (String c : cols)
        {
            outRow = tableData.getData(c);

            //Check against mask columns
            for (String m : mask.keySet())
            {
                //Perform adjustment on match
                if (c.equals(m)) {

                    //Check if we've applied two rules to a single col
                    if (outRow != tableData.getData(c)) {
                        System.out.println("2for1 DataMask: " + outRow + "lost");
                    }

                    outRow = mask.get(m);
                }
            }

            local.addData(c, outRow);
        }

        return local;
    }
}
