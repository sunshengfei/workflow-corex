package com.fuwafuwa.workflow.agent;


import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.bean.Kwags;
import com.fuwafuwa.workflow.plugins.ibase.MapFormDict;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeaconFinderRemark {

    public static String get() {
        HashMap<String, List<Kwags>> mapper = MapFormDict.optionsBeaconMaker();
        StringBuilder stringBuilder = new StringBuilder();
        if (RegexHelper.isNotEmpty(mapper)) {
            stringBuilder.append("备注：\n\n");
            for (Map.Entry<String, List<Kwags>> entry : mapper.entrySet()) {
                stringBuilder.append(String.format("『%s』\n", entry.getKey()));
                List<Kwags> list = entry.getValue();
                if (list != null) {
                    for (Kwags item : list) {
                        stringBuilder.append("\t")
                                .append(item.getKey())
                                .append("\t")
                                .append(item.getValue())
                                .append("\n");
                    }
                }
            }
        }
        return stringBuilder.toString();
    }
}
