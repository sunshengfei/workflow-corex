package com.fuwafuwa.workflow.plugins.evaluatejs;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import static org.mozilla.javascript.Context.javaToJS;

public class RunScript {

    private Class<RunScript> clazz;

    public RunScript() {
        this.clazz = RunScript.class;
    }

    public String runScript(String script, String functionName, Object... params) {
        ContextFactory factory = ContextFactory.getGlobal();
        Context context = factory.enterContext();
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_1_8);
        try {
            Scriptable scope = context.initStandardObjects();
            //配置属性 javaContext:当前类RunScript的上下文
            ScriptableObject.putProperty(scope, "javaContext", javaToJS(this, scope));
            //配置属性 javaLoader:当前类的JSEngine的类加载器
            ScriptableObject.putProperty(scope, "javaLoader", javaToJS(clazz.getClassLoader(), scope));
            context.evaluateString(scope, script, clazz.getSimpleName(), 1, null);
            Function function = (Function) scope.get(functionName, scope);
            Object result = function.call(context, scope, scope, params);
            if (result instanceof String) {
                return (String) result;
            } else if (result instanceof NativeJavaObject) {
                return (String) ((NativeJavaObject) result).getDefaultValue(String.class);
            } else if (result instanceof NativeObject) {
                return (String) ((NativeObject) result).getDefaultValue(String.class);
            }
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            Context.exit();
        }
    }
}
