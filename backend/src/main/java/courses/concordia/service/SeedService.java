package courses.concordia.service;

import java.nio.file.Path;
import java.util.List;

public interface SeedService<T> {
    List<T> readSeedFromFile(Path path);
}
