package indi.vicliu.juaner.authorization.exception;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import indi.vicliu.juaner.common.core.exception.ErrorType;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CustomOAuthExceptionJacksonSerializer extends StdSerializer<CustomOAuth2Exception> {

    protected CustomOAuthExceptionJacksonSerializer() {
        super(CustomOAuth2Exception.class);
    }

    @Override
    public void serialize(CustomOAuth2Exception e, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        log.debug(" customOAuth2Exception message {} info {}" , e.getMessage(), JSONObject.toJSONString(e));
        jsonGenerator.writeStartObject();
        //jsonGenerator.writeObjectField("status", e.getHttpErrorCode());
        jsonGenerator.writeObjectField("code", ErrorType.OAUTH_ERROR.getCode());
        jsonGenerator.writeStringField("message", ErrorType.OAUTH_ERROR.getMessage());
        jsonGenerator.writeObjectField("timestamp", ZonedDateTime.now().toInstant().plusMillis(TimeUnit.HOURS.toMillis(8)));
        jsonGenerator.writeObjectField("data", e.getMessage());

        if (e.getAdditionalInformation()!=null) {
            for (Map.Entry<String, String> entry : e.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                log.info("json before -> key:{},value:{}",key,add);
                jsonGenerator.writeStringField(key, add);
            }
        }
        jsonGenerator.writeEndObject();
    }
}
