package courses.concordia.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "resourceLinks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceLink {
    private String _id;
    private String userId;
    private String url;
    private String description;
    private LocalDateTime timestamp;
}
