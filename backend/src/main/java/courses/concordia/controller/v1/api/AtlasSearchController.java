package courses.concordia.controller.v1.api;

import com.mongodb.client.*;
import com.mongodb.client.model.search.SearchOperator;
import courses.concordia.model.Instructor;
import courses.concordia.model.Review;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.search.SearchOperator.text;
import static com.mongodb.client.model.search.SearchOptions.searchOptions;
import static com.mongodb.client.model.search.SearchPath.fieldPath;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/search")
@Slf4j
public class AtlasSearchController {
    @Value("${spring.data.mongodb.uri}")
    private String mongodbUri;
    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @GetMapping
    public ResponseEntity<List<Review>> FTS(@RequestParam String query) {

        try (MongoClient mongoClient = MongoClients.create(mongodbUri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("reviews");

            // Define the search stage
            SearchOperator contentSearchClause = text(fieldPath("content"), query);

            Bson searchStage = search(
                    contentSearchClause,
                    searchOptions().option("scoreDetails", false)
            );

            // Create a pipeline that searches, projects, and limits the number of results returned.
            AggregateIterable<Document> aggregationResults = collection.aggregate(Arrays.asList(
                    searchStage,
                    project(fields(excludeId(),
                            exclude(),
                            include("type", "content", "courseId", "instructorId", "timestamp", "_id", "difficulty", "experience", "rating", "tags"),
                            metaSearchScore("score"))),
                    limit(10)
            ));

            List<Review> reviews = new ArrayList<>();
            for (Document doc : aggregationResults) {
                Review review = new Review();
                review.set_id(doc.getString("_id"));
                review.setType(doc.getString("type"));
                review.setContent(doc.getString("content"));
                review.setCourseId(doc.getString("courseId"));
                review.setInstructorId(doc.getString("instructorId"));
                //review.setTimestamp(LocalDateTime.parse(doc.getDate("timestamp").toString()));
                review.setDifficulty(doc.getInteger("difficulty"));
                review.setExperience(doc.getInteger("experience"));
                review.setRating(doc.getInteger("rating"));
                review.setTags(((ArrayList<String>) doc.get("tags")).stream().map(Instructor.Tag::valueOf).collect(Collectors.toSet()));
                reviews.add(review);
            }

            return ResponseEntity.ok(reviews);
        }
    }
}
