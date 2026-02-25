package courses.concordia.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import courses.concordia.model.Instructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions.MongoConverterConfigurationAdapter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

@Configuration
@Slf4j
@EnableMongoAuditing //enable the automatic setting of @CreatedDate
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
}
