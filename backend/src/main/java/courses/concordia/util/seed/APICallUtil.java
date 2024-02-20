package courses.concordia.util.seed;

import com.google.gson.reflect.TypeToken;
import courses.concordia.util.JsonUtil;
import courses.concordia.util.seed.model.CourseWithDescription;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.List;

@Slf4j
public class APICallUtil {
    private static String BASE_URL = "https://opendata.concordia.ca/API/v1/";
    private static String COURSE_CATALOG_ENDPOINT = "course/catalog/filter/";
    private static String COURSE_DESCRIPTION_ENDPOINT = "course/description/filter/";

    public static <T> List<T> fetchCourseByTerm(String subject, String catalogue,String term) {
        String urlStr = BASE_URL + COURSE_CATALOG_ENDPOINT + "*/*/" + term;
        try {
            String response = getRequest(urlStr);
            return JsonUtil.getData(response, new TypeToken<List<T>>(){});

        } catch (Exception e) {
            log.error("Failed to fetch data by term. URL: {}, Error: {}", urlStr, e.getMessage(), e);
        }

        return null;
    }


    public static List<CourseWithDescription> fetchCourseDescription() {
        String urlStr = BASE_URL + COURSE_DESCRIPTION_ENDPOINT + "*";
        try {
            String response = getRequest(urlStr);
            return JsonUtil.getData(response, new TypeToken<List<CourseWithDescription>>(){});

        } catch (Exception e) {
            log.error("Failed to fetch course descriptions. URL: {}, Error: {}", urlStr, e.getMessage(), e);
        }
        return null;
    }

    private static String getRequest(String requestUrl) throws MalformedURLException, IOException {
        /*String apiUser = System.getenv("CONCORDIA_API_USER");
        String apiKey = System.getenv("CONCORDIA_API_KEY");

        if (apiUser == null || apiKey == null) {
            throw new IllegalStateException("API_USER or API_KEY environment variables are not set.");
        }*/

        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        // Encode user and key for basic authentication
        //String credentials = apiUser + ":" + apiKey;
        //String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        String encoded = Base64.getEncoder().encodeToString(("593:45903278e7da59e5c3e9568f7070244c").getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encoded);

        if (conn.getResponseCode() != 200) {
            log.error("HTTP request failed. URL: {}, HTTP Error Code: {}", requestUrl, conn.getResponseCode());
            return null;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output;
        StringBuilder response = new StringBuilder();
        while ((output = br.readLine()) != null) {
            response.append(output);
        }

        conn.disconnect();

        return response.toString();
    }
}


