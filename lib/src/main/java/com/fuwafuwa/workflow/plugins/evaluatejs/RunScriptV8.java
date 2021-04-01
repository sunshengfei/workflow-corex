package com.fuwafuwa.workflow.plugins.evaluatejs;//package com.fuwafuwa.workflows.workflow.plugins.evaluatejs;
//
//import com.eclipsesource.v8.V8;
//import com.eclipsesource.v8.V8Array;
//
//public class RunScriptV8 {
//
//    public RunScriptV8() {
//    }
//
//    public String runScript(String script, String functionName, String... params) {
//        V8 v8 = V8.createV8Runtime();
//        v8.executeVoidScript(script);
//        V8Array paramArr = new V8Array(v8);
//        for (int i = 0; i < params.length; i++) {
//            paramArr.push(params[i]);
//        }
//        String result = v8.executeStringFunction(functionName, paramArr);
//        paramArr.close();
//        v8.close();
//        return result;
//    }
//}
