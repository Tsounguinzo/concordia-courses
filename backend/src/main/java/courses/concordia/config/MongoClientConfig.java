package courses.concordia.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

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

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(
                new StringToDateConverter()
        ));
    }
}
