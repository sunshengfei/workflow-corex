package com.fuwafuwa.workflow.plugins.beacon.template;

import com.fuwafuwa.utils.RegexHelper;

import java.util.Locale;

public class BeaconFinderTemplateDelegate {

    public static String getTemplate(String condition) {
        return String.format(Locale.CHINESE,
                "扫描Beacon设备，直到满足&nbsp;<a href='#condition'>%s</a>"
                , RegexHelper.isEmpty(condition) ? "条件" : condition);
    }

}
