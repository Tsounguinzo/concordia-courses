package courses.concordia.controller.api.v1;

import com.mongodb.client.*;
import com.mongodb.client.model.search.SearchOperator;
import courses.concordia.dto.response.Response;
import courses.concordia.model.Course;
import courses.concordia.model.Instructor;
import courses.concordia.model.Review;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
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

    @Timed(value = "atlas.search", description = "Search for reviews using Atlas Search")
    @GetMapping
    public Response<?> FTSReviewContent(
            @RequestParam String query,
            @RequestParam(name = "limit", defaultValue = "15") int limit
    ) {

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
                    limit(limit)
            ));

            List<Review> reviews = new ArrayList<>();
            for (Document doc : aggregationResults) {
                Review review = new Review();
                review.set_id(doc.getString("_id"));
                review.setType(doc.getString("type"));
                review.setContent(doc.getString("content"));
                review.setCourseId(doc.getString("courseId"));
                review.setInstructorId(doc.getString("instructorId"));
                try {
                    String timestampStr = doc.getString("timestamp");
                    if (timestampStr != null) {
                        try {
                            LocalDateTime timestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ISO_DATE_TIME);
                            review.setTimestamp(timestamp);
                        } catch (DateTimeParseException e) {
                            System.err.println("Error parsing timestamp: " + timestampStr);
                        }
                    }
                } catch (ClassCastException e) {
                    Date date = doc.getDate("timestamp");
                    if (date != null) {
                        Instant instant = date.toInstant();
                        LocalDateTime timestamp = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                        review.setTimestamp(timestamp);
                    }
                }

                review.setDifficulty(doc.getInteger("difficulty"));
                review.setExperience(doc.getInteger("experience"));
                review.setRating(doc.getInteger("rating"));
                review.setTags(((ArrayList<String>) doc.get("tags")).stream().map(Instructor.Tag::valueOf).collect(Collectors.toSet()));
                reviews.add(review);
            }

            return Response.ok().setPayload(reviews);
        }
    }

    @GetMapping("/instructor")
    public Response<?> FTSInstructorName(
            @RequestParam String query,
            @RequestParam(name = "limit", defaultValue = "3") int limit
    ) {
        try (MongoClient mongoClient = MongoClients.create(mongodbUri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("instructors");

            // Define the search stage
            SearchOperator nameSearchClause = text(fieldPath("_id"), query);

            Bson searchStage = search(
                    nameSearchClause,
                    searchOptions().option("scoreDetails", false)
            );

            // Create a pipeline that searches, projects, and limits the number of results returned.
            AggregateIterable<Document> aggregationResults = collection.aggregate(Arrays.asList(
                    searchStage,
                    project(fields(excludeId(),
                            exclude(),
                            include("_id", "firstName", "lastName", "departments", "courses", "tags", "avgDifficulty", "avgRating", "reviewCount", "aiSummary"),
                            metaSearchScore("score"))),
                    limit(limit)
            ));

            List<Instructor> instructors = new ArrayList<>();
            for (Document doc : aggregationResults) {
                Instructor instructor = new Instructor();
                instructor.set_id(doc.getString("_id"));
                instructor.setFirstName(doc.getString("firstName"));
                instructor.setLastName(doc.getString("lastName"));
                instructor.setCourses(new HashSet<>(((ArrayList<Instructor.Course>) doc.get("courses"))));
                instructor.setDepartments(((ArrayList<String>) doc.get("departments")).stream()
                        .map(Instructor.Department::fromString)
                        .collect(Collectors.toSet()));
                instructor.setTags(((ArrayList<String>) doc.get("tags")).stream().map(Instructor.Tag::valueOf).collect(Collectors.toSet()));
                instructor.setAvgDifficulty(doc.getDouble("avgDifficulty"));
                instructor.setAvgRating(doc.getDouble("avgRating"));
                instructor.setReviewCount(doc.getInteger("reviewCount"));
                instructor.setAiSummary(doc.getString("aiSummary"));
                instructors.add(instructor);
            }

            return Response.ok().setPayload(instructors);
        }
    }

    @GetMapping("/course")
    public Response<?> FTSCourseTitle(
            @RequestParam String query,
            @RequestParam(name = "limit", defaultValue = "3") int limit
    ) {
        try (MongoClient mongoClient = MongoClients.create(mongodbUri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("courses");

            // Define the search stage
            SearchOperator nameSearchClause = text(fieldPath("title"), query);

            Bson searchStage = search(
                    nameSearchClause,
                    searchOptions().option("scoreDetails", false)
            );

            // Create a pipeline that searches, projects, and limits the number of results returned.
            AggregateIterable<Document> aggregationResults = collection.aggregate(Arrays.asList(
                    searchStage,
                    project(fields(excludeId(),
                            exclude(),
                            include("_id", "prerequisites", "subject", "catalog", "title", "classUnit", "terms", "instructors", "avgDifficulty", "avgExperience", "reviewCount", "description", "schedules"),
                            metaSearchScore("score"))),
                    limit(limit)
            ));

            List<Course> courses = new ArrayList<>();
            for (Document doc : aggregationResults) {
                Course course = new Course();
                course.set_id(doc.getString("_id"));
                course.setTerms((ArrayList<String>) doc.get("terms"));
                course.setInstructors((ArrayList<String>) doc.get("instructors"));
                course.setPrerequisites(doc.getString("prerequisites"));
                course.setDescription(doc.getString("description"));
                course.setSubject(doc.getString("subject"));
                course.setCatalog(doc.getString("catalog"));
                course.setTitle(doc.getString("title"));
                course.setClassUnit(doc.getDouble("classUnit"));
                course.setAvgDifficulty(doc.getDouble("avgDifficulty"));
                course.setAvgExperience(doc.getDouble("avgExperience"));
                course.setReviewCount(doc.getInteger("reviewCount"));
                course.setSchedules(((ArrayList<Course.Schedule>) doc.get("schedules")));
                courses.add(course);
            }

            return Response.ok().setPayload(courses);
        }
    }
}
