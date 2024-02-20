package courses.concordia.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeedServiceImpl<T> implements SeedService<T> {
    private static final Logger logger = LoggerFactory.getLogger(SeedServiceImpl.class);
    private final Gson gson = new Gson();
    private final Class<T> typeParameterClass;

    @Override
    public List<T> readSeedFromFile(Path path) {

        if (Files.notExists(path)) {
            logger.error("Seed file does not exist at path: {}", path);
            return Collections.emptyList();
        }

        try (Reader reader = new FileReader(path.toFile())) {
            Type seedType = TypeToken.getParameterized(List.class, typeParameterClass).getType();
            return gson.fromJson(reader, seedType);
        } catch (JsonSyntaxException e) {
            logger.error("Failed to parse seed file at path: {}. The file format is incorrect.", path, e);
        } catch (Exception e) {
            logger.error("An error occurred while reading the seed file at path: {}", path, e);
        }
        return Collections.emptyList();

    }
}
