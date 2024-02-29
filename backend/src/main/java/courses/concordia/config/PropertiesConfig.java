package courses.concordia.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@PropertySource("classpath:custom.properties")
public class PropertiesConfig {

    private final Environment env;

    public String getConfigValue(String configKey) {
        return env.getProperty(configKey);
    }
}
