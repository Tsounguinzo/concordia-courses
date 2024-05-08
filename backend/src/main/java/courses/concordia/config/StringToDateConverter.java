package courses.concordia.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.text.SimpleDateFormat;
import java.util.Date;

@ReadingConverter
public class StringToDateConverter implements Converter<String, Date> {
    private final SimpleDateFormat isoFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @Override
    public Date convert(String source) {
        try {
            return isoFormatter.parse(source);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format", e);
        }
    }
}