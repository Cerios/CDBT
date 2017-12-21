package nl.cerios.cdbt.mask;

import nl.cerios.cdbt.data.TableData;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by dwhelan on 21/12/2017.
 */
public class AliasMask extends AbstractMask {

    public AliasMask() {
        mask = new LinkedHashMap<String, String>();
    }

    @Override
    public void addRule(String col, String data) {
        super.addRule(col, data);
    }

    //DataMask: Match col name, replace data
    @Override
    public TableData applyMask(TableData tableData) {
        //Create output
        TableData local = new TableData(tableData);

        //Read input
        Set<String> cols = tableData.getColumns();

        String outAlias = tableData.getAlias() != null ? tableData.getAlias() : "";

        //For every data column
        for (String c : cols)
        {
            //Check against mask columns
            for (String m : mask.keySet())
            {
                //Perform adjustment on match
                if (c.equals(m)) {

                    //Check if we've applied two rules to a single col
                    if (outAlias != tableData.getAlias() && outAlias != "") {
                        System.out.println("2for1 AliasMask: " + outAlias + "lost");
                    }

                    outAlias = tableData.getData(c);
                }
            }

            local.setAlias(outAlias);
        }

        return local;
    }
}
