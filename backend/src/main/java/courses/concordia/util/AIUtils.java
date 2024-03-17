package courses.concordia.util;

import com.google.gson.reflect.TypeToken;
import courses.concordia.util.seed.model.Course;
import courses.concordia.util.seed.model.CourseDescriptionSegment;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class AIUtils {
    private static final String OLLAMA_TEXT_GENERATION_END_POINT = "http://localhost:11434/api/generate";
    private static final String DEFAULT_MODEL = "gemma:2b";

    public static void main(String[] args) {
        //Before using this, download ollama from https://ollama.com/download and pull the model you want, e.g gemma:7b or gemma:2b or any other available at https://ollama.com/library

         String SEED_FILENAME = "2023-2024-courses-v2.json";
         Path fileLocation = Paths.get("backend","src","main","resources","seeds", SEED_FILENAME);
         List<Course> courses = JsonUtils.getData(fileLocation, new TypeToken<List<Course>>(){});

         String task = "Into the json format below, give a value of null where applicable {“description” : string “prerequisites”:  string “corequisites”: string “Restrictions”: string}";
         for (Course course : courses) {
             String prompt = String.format("Convert the following:%n%s%n%s%n%s", course.getDescription(), course.getPrerequisites(), task).replace("\n","").replace("\r","");
             try {
                 String response = generateText(prompt);
                 Pattern pattern = Pattern.compile("\\{\\s*\"[\\s\\S]*?\\}", Pattern.MULTILINE);
                 Matcher matcher = pattern.matcher(response);

                 String jsonString = null;
                 if (matcher.find()) {
                     jsonString = matcher.group();
                     System.out.println("Extracted JSON string:");
                     System.out.println(jsonString);
                 } else {
                     System.out.println("No JSON string found.");
                     System.out.println(new CourseDescriptionSegment());
                 }
                 System.out.println(JsonUtils.getData(jsonString, new TypeToken<CourseDescriptionSegment>(){}));
             } catch (IOException | InterruptedException e) {
                 log.error("An exception occurred: {}", e.getMessage(), e);
             }
             System.out.println();
         }
    }

    public static void generateText(String prompt, boolean stream) throws IOException, InterruptedException {
        generateText(DEFAULT_MODEL, prompt, stream);
    }
    public static String generateText(String prompt) throws IOException, InterruptedException {
        return generateText(DEFAULT_MODEL, prompt, false);
    }
    public static String generateText(String model, String prompt, boolean stream) throws IOException, InterruptedException {
        String requestBody = String.format("""
                {
                  "model": "%s",
                  "prompt": "%s",
                  "stream": %b
                }""", model, prompt, stream);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_TEXT_GENERATION_END_POINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        if (stream) {
            HttpResponse.BodyHandler<Void> streamingBodyHandler = HttpResponse.BodyHandlers.fromLineSubscriber(new Flow.Subscriber<>() {
                private Flow.Subscription subscription;

                @Override
                public void onSubscribe(Flow.Subscription subscription) {
                    this.subscription = subscription;
                    subscription.request(1); // Request the first piece of data
                }

                @Override
                public void onNext(String item) {
                    OllamaResponse res = JsonUtils.getData(item, new TypeToken<OllamaResponse>() {
                    });
                    System.out.print(res.getError() != null ? res.getError() : res.getResponse());
                    subscription.request(1); // Request the next piece of data
                }

                @Override
                public void onError(Throwable throwable) {
                    log.error("Error occurred: {}", throwable.getMessage(), throwable);
                }

                @Override
                public void onComplete() {
                    System.out.println("\n\nStream completed.");
                }
            });

            client.send(request, streamingBodyHandler);
            return null;
        } else {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            OllamaResponse res = JsonUtils.getData(response.body(), new TypeToken<OllamaResponse>() {});
            return res.getError() != null ? res.getError() : res.getResponse();
        }
    }

}

@Data
class OllamaResponse {
    //https://github.com/ollama/ollama/blob/main/docs/api.md
    private String model;
    private String created_at;
    private String response;
    private String error;
    private boolean done;
}
