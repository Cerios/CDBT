package nl.cerios.cdbt.mask;

import nl.cerios.cdbt.data.TableData;

import java.util.LinkedHashMap;

/**
 * Created by dwhelan on 20/12/2017.
 */
public class TypeMask extends AbstractMask {
    public TypeMask() {
        mask = new LinkedHashMap<String, String>();
    }

    //TypeMask: Match dataType, replace dataType with mask input
    @Override
    public TableData applyMask(TableData tableData) {
        String type = tableData.getDataType();
        String outType = type;

        for (String m : mask.keySet())
        {
            if (m.equals(type)) {
                if (outType != type) {
                    System.out.println("2for1 TypeMask: " + outType + "lost");
                }

                outType = mask.get(m);
            }
        }

        //Create output
        TableData local = new TableData(outType);
        local.copyData(tableData);

        return local;
    }
}
