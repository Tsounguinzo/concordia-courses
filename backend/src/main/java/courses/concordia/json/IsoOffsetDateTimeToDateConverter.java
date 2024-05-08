package courses.concordia.json;

import org.modelmapper.AbstractConverter;

import java.time.OffsetDateTime;
import java.util.Date;

public class IsoOffsetDateTimeToDateConverter extends AbstractConverter<String, Date> {
    @Override
    public Date convert(String value) {
        if (value != null && !value.isEmpty()) {
            OffsetDateTime odt = OffsetDateTime.parse(value);
            return Date.from(odt.toInstant());
        }
        return null;
    }
}
