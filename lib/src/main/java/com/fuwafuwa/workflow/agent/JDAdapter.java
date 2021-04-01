package com.fuwafuwa.workflow.agent;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;

import java.lang.reflect.Type;

public class JDAdapter implements JsonDeserializer<IPayload> {

    private Gson gson;

    public JDAdapter() {
        this.gson = new Gson();
    }

    @Override
    public IPayload deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement.isJsonObject()) {
            JsonObject object = jsonElement.getAsJsonObject();
            int typeInt = object.get("type").getAsInt();
            Class<? extends IPayload> clazz = FlowFactory.classForTypeInt(typeInt);
            if (clazz != null) {
                return gson.fromJson(object, clazz);
            }
            return new IPayload();
        }
        return null;
    }
}