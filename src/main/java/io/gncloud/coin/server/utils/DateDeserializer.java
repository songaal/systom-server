package io.gncloud.coin.server.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateDeserializer implements JsonDeserializer<Date> {
    protected static Logger logger = LoggerFactory.getLogger(DateDeserializer.class);

    private TimeZone zone;
    private String pattern;

    public DateDeserializer(String pattern, TimeZone zone) {
        this.pattern = pattern;
        this.zone = zone;
    }
    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String date = json.getAsString();

        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setTimeZone(zone);

        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            logger.error("", e);
            return null;
        }
    }
}
