package com.focela.platform.framework.common.utils.json.databind;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Timestamp-based LocalDateTime serializer
 */
@Slf4j
public class TimestampLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    public static final TimestampLocalDateTimeSerializer INSTANCE = new TimestampLocalDateTimeSerializer();

    private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE = new ConcurrentHashMap<>();

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Case 1: if a custom JsonFormat annotation is present, use it.
        String fieldName = gen.getOutputContext().getCurrentName();
        if (fieldName != null) {
            Object currentValue = gen.getOutputContext().getCurrentValue();
            if (currentValue != null) {
                Class<?> clazz = currentValue.getClass();
                Map<String, Field> fieldMap = FIELD_CACHE.computeIfAbsent(clazz, this::buildFieldMap);
                Field field = fieldMap.get(fieldName);
                // Additional fix.
                if (field != null && field.isAnnotationPresent(JsonFormat.class)) {
                    JsonFormat jsonFormat = field.getAnnotation(JsonFormat.class);
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(jsonFormat.pattern());
                        gen.writeString(formatter.format(value));
                        return;
                    } catch (Exception ex) {
                        log.warn("[serialize][({}#{}) use JsonFormat pattern failed, fallback to default Long timestamp]",
                                clazz.getName(), fieldName, ex);
                    }
                }
            }
        }

        // Case 2: by default, convert the LocalDateTime to a Long timestamp
        gen.writeNumber(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    /**
     * Build the field map (cached)
     *
     * @param clazz class
     * @return field map
     */
    private Map<String, Field> buildFieldMap(Class<?> clazz) {
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field field : ReflectUtil.getFields(clazz)) {
            String fieldName = field.getName();
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            if (jsonProperty != null) {
                String value = jsonProperty.value();
                if (StrUtil.isNotEmpty(value) && ObjUtil.notEqual("\u0000", value)) {
                    fieldName = value;
                }
            }
            fieldMap.put(fieldName, field);
        }
        return fieldMap;
    }

}
