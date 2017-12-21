package nl.cerios.cdbt.mask;

import nl.cerios.cdbt.data.TableData;

import java.util.LinkedHashMap;

/**
 * Created by dwhelan on 15/11/2017.
 */
public abstract class AbstractMask {
    protected LinkedHashMap<String, String> mask;

    public void addRule(String match, String action) {
        mask.put(match, action);
    }

    public abstract TableData applyMask(TableData tableData);

    public int getRuleCount() {
        return (mask != null ? mask.keySet().size() : -1);
    }
}