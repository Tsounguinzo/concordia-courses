package courses.concordia.json;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.time.OffsetDateTime;
import java.util.Date;

public class IsoOffsetDateTimeToDateConverter implements Converter<String, Date> {
    @Override
    public Date convert(MappingContext<String, Date> mappingContext) {
        String source = mappingContext.getSource();
        if (source == null) {
            return null;
        }
        return convertIsoOffsetDateTimeToDate(source);
    }

    private Date convertIsoOffsetDateTimeToDate(String isoDateTime) {
        OffsetDateTime odt = OffsetDateTime.parse(isoDateTime);
        return Date.from(odt.toInstant());
    }
}
