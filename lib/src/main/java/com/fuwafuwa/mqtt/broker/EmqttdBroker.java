package com.fuwafuwa.mqtt.broker;

import android.content.Context;

import com.annimon.stream.Stream;
import com.fuwafuwa.mqtt.IMQTTBrokerDelegate;
import com.fuwafuwa.mqtt.event.MQTTStateEvent;
import com.fuwafuwa.mqtt.event.MQTTTraceEvent;
import com.fuwafuwa.za.LiveDataBus;
import com.fuwafuwa.mqtt.bean.ConnectActionState;
import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.utils.Loger;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.utils.StringUtils;
import com.fuwafuwa.workflow.agent.event.MQTTMessageEvent;
import com.fuwafuwa.workflow.plugins.mqtt.payload.EventType;
import com.fuwafuwa.workflow.plugins.mqtt.payload.MQTTMessage;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.ExtendedListener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.fusesource.mqtt.client.Tracer;
import org.fusesource.mqtt.codec.CONNACK;
import org.fusesource.mqtt.codec.CONNECT;
import org.fusesource.mqtt.codec.DISCONNECT;
import org.fusesource.mqtt.codec.MQTTFrame;
import org.fusesource.mqtt.codec.PINGREQ;
import org.fusesource.mqtt.codec.PINGRESP;
import org.fusesource.mqtt.codec.PUBACK;
import org.fusesource.mqtt.codec.PUBCOMP;
import org.fusesource.mqtt.codec.PUBLISH;
import org.fusesource.mqtt.codec.PUBREC;
import org.fusesource.mqtt.codec.PUBREL;
import org.fusesource.mqtt.codec.SUBACK;
import org.fusesource.mqtt.codec.SUBSCRIBE;
import org.fusesource.mqtt.codec.UNSUBACK;
import org.fusesource.mqtt.codec.UNSUBSCRIBE;

import java.net.URISyntaxException;
import java.util.Locale;

public class EmqttdBroker implements IMQTTBrokerDelegate {

    private Context context;
    private MQTT mqtt;
    private CallbackConnection callbackConnection;
    private boolean callbackConnected;

    private MQTTMessageEvent mQTTConnectEvent;
    private MQTTConnectUserEntity connectPoint;

    public EmqttdBroker(Context context, MQTTConnectUserEntity connectPoint) {
        this.connectPoint = connectPoint;
        this.context = context;
    }

    @Override
    public void prepared() {
        mqtt = new MQTT();
        try {
            String serverURI = String.format(Locale.ENGLISH, "%s://%s:%s",
                    connectPoint.isWebSocket() ?
                            (connectPoint.isUseSSL() ? "wss" : "ws") :
                            (connectPoint.isUseSSL() ? "ssl" : "tcp"),
                    RegexHelper.isIPv6(connectPoint.getHost()) ?
                            String.format(Locale.ENGLISH, "[%s]", connectPoint.getHost())
                            : connectPoint.getHost()
                    , connectPoint.isWebSocket() ?
                            (connectPoint.isUseSSL() ?
                                    connectPoint.getWebSocketSSLPort() :
                                    connectPoint.getWebSocketPort()) :
                            (connectPoint.isUseSSL() ?
                                    connectPoint.getSslPort() :
                                    connectPoint.getPort()));
            Loger.e("🎈serverURI:", serverURI);
            mqtt.setHost(serverURI);//设置连接主机IP
            mqtt.setCleanSession(connectPoint.isClearSession());//设置不清除Session连接
//            mqtt.setReconnectAttemptsMax(-1);     //设置最大重连次数
            mqtt.setReconnectDelay(connectPoint.getConnectTimeout());              //设置重连间隔时间
            if (connectPoint.getTickTime() < Short.MAX_VALUE) {
                mqtt.setKeepAlive((short) connectPoint.getTickTime());//设置心跳时间
            }
            mqtt.setSendBufferSize(2 * 1024 * 1024);                   //设置缓冲大小
            mqtt.setClientId(connectPoint.getClientId());                                //设置连接的客户端ID
            if (RegexHelper.isAllNotEmpty(connectPoint.getUserName(), connectPoint.getUserPasswort())) {
                mqtt.setUserName(connectPoint.getUserName()); //设置用户名
                mqtt.setPassword(connectPoint.getUserPasswort()); //设置密码
            }
            mqtt.setTracer(tracer);
            if (RegexHelper.isNotEmpty(connectPoint.getLwt())) {
                mqtt.setWillTopic(connectPoint.getLwt().getTopic());
                mqtt.setWillMessage(connectPoint.getLwt().getMessage());
                mqtt.setWillQos(int2QosEnum(connectPoint.getLwt().getQos()));// 设置“遗嘱”消息的QoS，默认为QoS.ATMOSTONCE
                mqtt.setWillRetain(connectPoint.getLwt().isRetained());// 若想要在发布“遗嘱”消息时拥有retain选项，则为true
                mqtt.setVersion(connectPoint.getVersion() == 3 ? "3.1" : "3.1.1");
            }
            // 失败重连接设置说明
            mqtt.setConnectAttemptsMax(-1);// 客户端首次连接到服务器时，连接的最大重试次数，超出该次数客户端将返回错误。-1意为无重试上限，默认为-1//原示例:10L
            mqtt.setReconnectAttemptsMax(-1);// 客户端已经连接到服务器，但因某种原因连接断开时的最大重试次数，超出该次数客户端将返回错误。-1意为无重试上限，默认为-1//原示例：3L
            mqtt.setReconnectDelay(10L);// 首次重连接间隔毫秒数，默认为10ms
            mqtt.setReconnectDelayMax(1000L);// 重连接间隔毫秒数，默认为30000ms//原示例：30000L
            mqtt.setReconnectBackOffMultiplier(2);// 设置重连接指数回归。设置为1则停用指数回归，默认为2
            // Socket设置说明
            mqtt.setReceiveBufferSize(65536);// 设置socket接收缓冲区大小，默认为65536（64k）
            mqtt.setSendBufferSize(65536);// 设置socket发送缓冲区大小，默认为65536（64k）
            mqtt.setTrafficClass(8);// 设置发送数据包头的流量类型或服务类型字段，默认为8，意为吞吐量最大化传输
            // 带宽限制设置说明
            mqtt.setMaxReadRate(0);// 设置连接的最大接收速率，单位为bytes/s。默认为0，即无限制
            mqtt.setMaxWriteRate(0);// 设置连接的最大发送速率，单位为bytes/s。默认为0，即无限制
            // 选择消息分发队列
//            mqtt.setDispatchQueue(Dispatch.createQueue("foo"));// 若没有调用方法setDispatchQueue，客户端将为连接新建一个队列。如果想实现多个连接使用公用的队列，显式地指定队列是一个非常方便的实现方法
            callbackConnection = mqtt.callbackConnection();
            callbackConnection.listener(extendedListener);
        } catch (URISyntaxException e) {
            //抛出异常也算网络连接失败
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        if (mqtt == null) {
            prepared();
        }
        if (callbackConnection == null) {
            callbackConnection = mqtt.callbackConnection();
        }
        callbackConnection.connect(connectCallback);
    }

    @Override
    public void disconnect() {
        try {
            if (callbackConnection == null) return;
            if (callbackConnection.getDispatchQueue().isExecuting()) {
                callbackConnection.disconnect(new Callback<Void>() {
                    @Override
                    public void onSuccess(Void value) {
                        if (connectPoint != null) {
                            connectPoint.setConnected(ConnectActionState.DISCONNECTED);
                            LiveDataBus.post(MQTTStateEvent.class, new MQTTStateEvent(connectPoint.get_id(), connectPoint.getConnected()));
                        }
                    }

                    @Override
                    public void onFailure(Throwable value) {

                    }
                });
            } else {
                callbackConnection.kill(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            if (connectPoint != null) {
//                connectPoint.setConnected(ConnectActionState.DISCONNECTED);
//                LiveDataBus.post(MQTTStateEvent.class, new MQTTStateEvent(connectPoint.get_id(), connectPoint.getConnected()));
//            }
        } finally {
        }
    }

    @Override
    public void subscribe(MQTTMessage mqttMessage) {
        subscribeUnion(new String[]{mqttMessage.getTopic()}, new int[]{mqttMessage.getQos()}, mqttMessage);
    }

    @Override
    public void subscribeAll(String[] topics, int[] qos) {
        subscribeUnion(topics, qos, null);
    }

    private void subscribeUnion(String[] topics, int[] qos, MQTTMessage mqttMessage) {
        if (callbackConnection == null) return;
        if (topics.length != qos.length) return;
        Topic[] topicsArray = Stream.of(topics).mapIndexed((index, topic) -> {
            int qosInt = qos[index];
            QoS qoS = int2QosEnum(qosInt);
            return new Topic(topic, qoS);
        }).toArray(Topic[]::new);
        try {
            callbackConnection.subscribe(topicsArray, new SubCallback(topics, mqttMessage));
        } catch (Error e) {
            e.printStackTrace();
        }

    }

    @Override
    public void unsubscribe(String topic) {
        unsubscribeAll(new String[]{topic});
    }

    @Override
    public void unsubscribeAll(String[] topics) {
        if (callbackConnection == null) return;
        UTF8Buffer[] topicsArray = Stream.of(topics).map(UTF8Buffer::new)
                .toArray(UTF8Buffer[]::new);
        callbackConnection.unsubscribe(topicsArray, new UnSubCallback(topics));
    }

    @Override
    public void publish(MQTTMessage mqttMessage) {
        if (callbackConnection == null) return;
        callbackConnection.publish(mqttMessage.getTopic(),
                mqttMessage.getMessage().getBytes(),
                int2QosEnum(mqttMessage.getQos()),
                mqttMessage.isRetained(), new PubCallback(mqttMessage));
    }

    @Override
    public boolean isConnected() {
        return callbackConnection != null && callbackConnected;
    }

    @Override
    public void release() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        if (callbackConnection != null) {
            callbackConnection.kill(new Callback<Void>() {
                @Override
                public void onSuccess(Void value) {
                }

                @Override
                public void onFailure(Throwable value) {
                }
            });
        }
    }

    private QoS int2QosEnum(int qos) {
        QoS qoS = QoS.AT_MOST_ONCE;
        if (qos == 0) {
            qoS = QoS.AT_MOST_ONCE;
        } else if (qos == 1) {
            qoS = QoS.AT_LEAST_ONCE;
        } else if (qos == 2) {
            qoS = QoS.EXACTLY_ONCE;
        }
        return qoS;
    }

    private ExtendedListener extendedListener = new ExtendedListener() {
        @Override
        public void onPublish(UTF8Buffer topic, Buffer body, Callback<Callback<Void>> ack) {
            mQTTConnectEvent = new MQTTMessageEvent();
            mQTTConnectEvent.setType(EventType.messageArrived);
            mQTTConnectEvent.setTopic(topic.toString());
            mQTTConnectEvent.setEmqttd(true);
            mQTTConnectEvent.setDate(System.currentTimeMillis());
            if (body != null) {
                mQTTConnectEvent.setPayload(body.data);
                mQTTConnectEvent.setMessage(Buffer.ascii(body).toString());
            }
            LiveDataBus.post(MQTTMessageEvent.class, mQTTConnectEvent);
            Loger.e("🔥ExtendedListener-onPublish:", "" + topic);
        }

        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected() {
        }

        @Override
        public void onPublish(UTF8Buffer topic, Buffer body, Runnable ack) {
            ack.run();
            Loger.e("🔥ExtendedListener-onPublish2:", "" + topic);
            mQTTConnectEvent = new MQTTMessageEvent();
            mQTTConnectEvent.setType(EventType.messageArrived);
            mQTTConnectEvent.setEmqttd(true);
            mQTTConnectEvent.setTopic(topic.toString());
            mQTTConnectEvent.setDate(System.currentTimeMillis());
            if (body != null) {
                mQTTConnectEvent.setPayload(body.data);
                mQTTConnectEvent.setMessage(Buffer.ascii(body).toString());
            }
            LiveDataBus.post(MQTTMessageEvent.class, mQTTConnectEvent);
        }

        @Override
        public void onFailure(Throwable value) {
            Loger.e("🔥ExtendedListener-onFailure:", "" + value);
        }
    };


    private Callback<Void> connectCallback = new Callback<Void>() {
        @Override
        public void onSuccess(Void value) {
            Loger.e("🔥onSuccess:", "" + value);
            callbackConnected = true;
            connectPoint.setConnected(ConnectActionState.CONNECTED);
            LiveDataBus.post(MQTTStateEvent.class, new MQTTStateEvent(connectPoint.get_id(), connectPoint.getConnected()));
        }

        @Override
        public void onFailure(Throwable exception) {
            callbackConnected = false;
            connectPoint.setConnected(ConnectActionState.DISCONNECTED);
            LiveDataBus.post(MQTTStateEvent.class, new MQTTStateEvent(connectPoint.get_id(), connectPoint.getConnected()));
        }
    };

    class PubCallback implements Callback<Void> {

        private MQTTMessage message;

        public PubCallback(MQTTMessage message) {
            this.message = message;
        }

        @Override
        public void onSuccess(Void value) {
            mQTTConnectEvent = new MQTTMessageEvent();
            mQTTConnectEvent.setType(EventType.deliveryComplete);
            mQTTConnectEvent.setTopic(message.getTopic());
//            mQTTConnectEvent.setId(token.getMessageId());
//            mQTTConnectEvent.setQos(token.getMessage().getQos());
//            mQTTConnectEvent.setServer(token.getClient().getServerURI());
//            mQTTConnectEvent.setPayload(token.getMessage().getPayload());
            mQTTConnectEvent.setDate(System.currentTimeMillis());
            LiveDataBus.post(MQTTMessageEvent.class, mQTTConnectEvent);
            Loger.e("🆗deliveryComplete:", "" + value);
        }

        @Override
        public void onFailure(Throwable value) {

        }
    }

    class SubCallback implements Callback<byte[]> {

        private MQTTMessage userContext;
        private String[] topic;
        private MQTTMessageEvent mQTTConnectEvent;

        public SubCallback(String[] topic, MQTTMessage mqttMessage) {
            this.topic = topic;
            this.userContext = mqttMessage;
        }

        @Override
        public void onSuccess(byte[] value) {
            mQTTConnectEvent = new MQTTMessageEvent();
            mQTTConnectEvent.setType(EventType.subCallBack);
            mQTTConnectEvent.setTopic(StringUtils.join(topic, ","));
            mQTTConnectEvent.setDate(System.currentTimeMillis());
            mQTTConnectEvent.setEntryId(connectPoint.get_id());
            if (userContext != null) {
                mQTTConnectEvent.setId(userContext.getId());
            }
//            mQTTConnectEvent.setMessage(message);
            Loger.e("✉subCallBack:", topic + "");
            LiveDataBus.post(MQTTMessageEvent.class, mQTTConnectEvent);
        }

        @Override
        public void onFailure(Throwable value) {

        }
    }


    class UnSubCallback implements Callback<Void> {

        private String[] topic;
        private MQTTMessageEvent mQTTConnectEvent;

        public UnSubCallback(String[] topic) {
            this.topic = topic;
        }

        @Override
        public void onSuccess(Void value) {
            mQTTConnectEvent = new MQTTMessageEvent();
            mQTTConnectEvent.setType(EventType.unsubCallBack);
            mQTTConnectEvent.setTopic(StringUtils.join(topic, ","));
            mQTTConnectEvent.setDate(System.currentTimeMillis());
            mQTTConnectEvent.setEntryId(connectPoint.get_id());
//            mQTTConnectEvent.setMessage(message);
            Loger.e("✉UnSubCallback:", topic + "");
            LiveDataBus.post(MQTTMessageEvent.class, mQTTConnectEvent);
        }

        @Override
        public void onFailure(Throwable value) {

        }
    }

    private Tracer tracer = new Tracer() {
        @Override
        public void debug(String message, Object... args) {
            super.debug(message, args);
            Loger.e("🎈traceException:", "" + message + args);
            LiveDataBus.post(MQTTTraceEvent.class, new MQTTTraceEvent(MQTTTraceEvent.Type.DEBUG, "", message));
        }

        @Override
        public void onSend(MQTTFrame frame) {
            super.onSend(frame);
            LiveDataBus.post(MQTTTraceEvent.class, new MQTTTraceEvent(MQTTTraceEvent.Type.DEBUG, messageType(frame.messageType()), frame.toString()));
        }

        @Override
        public void onReceive(MQTTFrame frame) {
            super.onReceive(frame);
            LiveDataBus.post(MQTTTraceEvent.class, new MQTTTraceEvent(MQTTTraceEvent.Type.DEBUG, messageType(frame.messageType()), frame.toString()));
        }
    };

    private static String messageType(byte messType) {
        String type = "";
        switch (messType) {
            case CONNECT.TYPE:
                type = "CONNECT";
                break;
            case CONNACK.TYPE:
                type = "CONNACK";
                break;
            case DISCONNECT.TYPE:
                type = "DISCONNECT";
                break;
            case PINGREQ.TYPE:
                type = "PINGREQ";
                break;
            case PINGRESP.TYPE:
                type = "PINGRESP";
                break;
            case SUBSCRIBE.TYPE:
                type = "SUBSCRIBE";
                break;
            case UNSUBSCRIBE.TYPE:
                type = "UNSUBSCRIBE";
                break;
            case UNSUBACK.TYPE:
                type = "UNSUBACK";
                break;
            case PUBLISH.TYPE:
                type = "PUBLISH";
                break;
            case SUBACK.TYPE:
                type = "SUBACK";
                break;
            case PUBACK.TYPE:
                type = "PUBACK";
                break;
            case PUBREC.TYPE:
                type = "PUBREC";
                break;
            case PUBREL.TYPE:
                type = "PUBREL";
                break;
            case PUBCOMP.TYPE:
                type = "PUBCOMP";
                break;
            default:
                break;
        }
        return type;
    }
}
