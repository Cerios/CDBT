package nl.cerios.cdbt.mask;

import nl.cerios.cdbt.data.TableData;

import java.util.LinkedHashMap;

/**
 * Created by dwhelan on 15/11/2017.
 */
public abstract class AbstractMask {
    //List of rules: Left is matching strategy, right is replacement.
    protected LinkedHashMap<String, String> mask;

    //Add a new rule
    public void addRule(String match, String action) {
        mask.put(match, action);
    }

    //Returns a TableData with the mask applied
    public abstract TableData applyMask(TableData tableData);

    //Get a count of rules in this mask
    public int getRuleCount() {
        return (mask != null ? mask.keySet().size() : -1);
    }
}