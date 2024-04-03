package courses.concordia.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "concordia")
public class ConcordiaApiProperties {
    private String apiUser;
    private String apiKey;
}
