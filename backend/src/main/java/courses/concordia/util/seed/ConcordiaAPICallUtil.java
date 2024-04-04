package courses.concordia.util.seed;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@Slf4j
public class ConcordiaAPICallUtil {
    /**
     * <a href="https://github.com/opendataConcordiaU/documentation/">...</a>
     */
    public static final String BASE_URL = "https://opendata.concordia.ca/API/v1/";
    /**
     * GET course/catalog/filter/{subject}/{catalog}/{carreer}
     */
    public static final String COURSE_CATALOG_ENDPOINT = "course/catalog/filter/";
    /**
     * GET course/description/filter/{courseID}
     */
    public static final String COURSE_DESCRIPTION_ENDPOINT = "course/description/filter/";
    /**
     * GET course/scheduleTerm/filter/{subject}/{termcode}
     */
    public static final String COURSE_SCHEDULE_TERM_ENDPOINT = "course/scheduleTerm/filter/";

    public static String getRequest(String requestUrl) throws IOException {

        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");


        String apiUser = "593";
        String apiKey = "45903278e7da59e5c3e9568f7070244c";
        String encoded = Base64.getEncoder().encodeToString(
                (apiUser + ":" + apiKey).getBytes());
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


