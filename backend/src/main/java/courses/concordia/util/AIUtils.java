package courses.concordia.util;

import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Flow;

@Slf4j
public class AIUtils {
    private static final String OLLAMA_TEXT_GENERATION_END_POINT = "http://localhost:11434/api/generate";
    private static final String DEFAULT_MODEL = "gemma:2b";

    public static void main(String[] args) {
        //Before using this, download ollama from https://ollama.com/download and pull the model you want, e.g gemma:7b or gemma:2b or any other available at https://ollama.com/library
        String prompt = "Fundamentals of electric circuits: Kirchoff’s laws, voltage and current sources, Ohm’s law, series and parallel circuits. Nodal and mesh analysis of DC circuits. Superposition theorem, Thevenin and Norton Equivalents. Use of operational amplifiers. Transient analysis of simple RC, RL and RLC circuits. Steady state analysis: Phasors and impedances, power and power factor. Single and three phase circuits. Magnetic circuits and transformers. Power generation and distribution. Lectures: three hours per week. Tutorial: two hours per week. Laboratory: 15 hours total.Prerequisite: ENGR 213 previously or concurrently; PHYS 205. Course Prerequisite: PHYS205; Course Co-requisite: ENGR213 Separate the above in a json format as below, give a value of null where applicable {“description” : string “prerequisites”:  string “corequisites”: string “Restrictions”: string}";

        try {
            generateText(prompt, true);
        } catch (IOException | InterruptedException e) {
            log.error("An exception occurred: {}", e.getMessage(), e);
        }
    }

    public static void generateText(String prompt, boolean stream) throws IOException, InterruptedException {
        generateText(DEFAULT_MODEL, prompt, stream);
    }
    public static void generateText(String model, String prompt, boolean stream) throws IOException, InterruptedException {
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

        HttpResponse.BodyHandler<Void> streamingBodyHandler = HttpResponse.BodyHandlers.fromLineSubscriber(new Flow.Subscriber<>() {
            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                subscription.request(1); // Request the first piece of data
            }

            @Override
            public void onNext(String item) {
                OllamaResponse res = JsonUtils.getData(item, new TypeToken<OllamaResponse>(){});
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
