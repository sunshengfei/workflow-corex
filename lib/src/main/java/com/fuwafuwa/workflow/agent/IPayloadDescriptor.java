package com.fuwafuwa.workflow.agent;

import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;

public interface IPayloadDescriptor {

    int payloadType();

    /**
     * TODO 可能需要改进为Class.forName
     *
     * @return
     */
    Class<? extends IPayload> payloadClass();
}
