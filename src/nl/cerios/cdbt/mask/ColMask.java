package nl.cerios.cdbt.mask;

import nl.cerios.cdbt.data.TableData;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by dwhelan on 20/12/2017.
 */
public class ColMask extends AbstractMask {

    public ColMask() {
        mask = new LinkedHashMap<String, String>();
    }

    @Override
    public void addRule(String col, String replace) {
        super.addRule(col, replace);
    }

    //Match col name, rename col
    @Override
    public TableData applyMask(TableData tableData) {
        //Create output
        TableData local = new TableData(tableData.getDataType());

        //Read input
        Set<String> cols = tableData.getColumns();

        String outCol = "";

        //For every data column
        for (String c : cols)
        {
            outCol = c;

            //Check against mask columns
            for (String m : mask.keySet())
            {
                //Perform adjustment on match
                if (c.equals(m)) {

                    //Check if we've applied two rules to a single col
                    if (outCol != c) {
                        System.out.println("2for1 DataMask: " + outCol + "lost");
                        }

                    outCol = mask.get(m);
                }
            }

            local.addData(outCol, tableData.getData(c));
        }

        return local;
    }
}
