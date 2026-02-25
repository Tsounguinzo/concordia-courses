package courses.concordia.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import courses.concordia.model.Instructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions.MongoConverterConfigurationAdapter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Configuration
@Slf4j
@EnableMongoAuditing
public class MongoClientConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.database}")
    private String database;
    @Value("${spring.data.mongodb.uri}")
    private String connectionString;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Bean
    public MongoClientSettings mongoClientSettings() {
        log.info("=> Creating the MongoClientSettings for MongoClient & MongoTemplate.");
        return MongoClientSettings.builder().applyConnectionString(new ConnectionString(connectionString)).build();
    }

    @Override
    protected void configureConverters(MongoConverterConfigurationAdapter adapter) {
        adapter.registerConverter(new StringToInstructorDepartmentConverter());
        adapter.registerConverter(new InstructorDepartmentToStringConverter());
        adapter.registerConverter(new StringToInstructorTagConverter());
        adapter.registerConverter(new InstructorTagToStringConverter());
        adapter.registerConverter(new DateToLocalDateTimeConverter());
        adapter.registerConverter(new LocalDateTimeToDateConverter());
        adapter.registerConverter(new StringToLocalDateTimeConverter());
        adapter.registerConverter(new DocumentToLocalDateTimeConverter());
    }

    @ReadingConverter
    private static class StringToInstructorDepartmentConverter implements Converter<String, Instructor.Department> {
        @Override
        public Instructor.Department convert(String source) {
            return Instructor.Department.fromString(source);
        }
    }

    @WritingConverter
    private static class InstructorDepartmentToStringConverter implements Converter<Instructor.Department, String> {
        @Override
        public String convert(Instructor.Department source) {
            return source.toValue();
        }
    }

    @ReadingConverter
    private static class StringToInstructorTagConverter implements Converter<String, Instructor.Tag> {
        @Override
        public Instructor.Tag convert(String source) {
            return Instructor.Tag.fromString(source);
        }
    }

    @WritingConverter
    private static class InstructorTagToStringConverter implements Converter<Instructor.Tag, String> {
        @Override
        public String convert(Instructor.Tag source) {
            return source.toValue();
        }
    }

    @ReadingConverter
    private static class DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {
        @Override
        public LocalDateTime convert(Date source) {
            return LocalDateTime.ofInstant(source.toInstant(), ZoneOffset.UTC);
        }
    }

    @WritingConverter
    private static class LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {
        @Override
        public Date convert(LocalDateTime source) {
            return Date.from(source.toInstant(ZoneOffset.UTC));
        }
    }

    @ReadingConverter
    private static class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
        @Override
        public LocalDateTime convert(String source) {
            if (source == null || source.isBlank()) {
                return null;
            }
            try {
                return OffsetDateTime.parse(source).toLocalDateTime();
            } catch (Exception ignored) {
                return LocalDateTime.parse(source);
            }
        }
    }

    @ReadingConverter
    private static class DocumentToLocalDateTimeConverter implements Converter<Document, LocalDateTime> {
        @Override
        public LocalDateTime convert(Document source) {
            if (source == null) {
                return null;
            }
            Object rawDate = source.get("$date");
            if (rawDate == null) {
                return null;
            }
            if (rawDate instanceof Date date) {
                return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
            }
            if (rawDate instanceof String dateString && !dateString.isBlank()) {
                try {
                    return OffsetDateTime.parse(dateString).toLocalDateTime();
                } catch (Exception ignored) {
                    return LocalDateTime.parse(dateString);
                }
            }
            return null;
        }
    }
}
