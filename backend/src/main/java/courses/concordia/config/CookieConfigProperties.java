package courses.concordia.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "cookie")
@Component
public class CookieConfigProperties {
    private boolean secure;
    private boolean httpOnly;
}
