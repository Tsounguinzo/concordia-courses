package courses.concordia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ConcordiaCoursesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConcordiaCoursesApplication.class, args);
	}

}
