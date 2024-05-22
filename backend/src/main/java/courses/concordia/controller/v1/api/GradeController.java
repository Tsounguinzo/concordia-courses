package courses.concordia.controller.v1.api;

import courses.concordia.dto.response.Response;
import courses.concordia.service.GradeDistributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/grades")
public class GradeController {
    private final GradeDistributionService gradeDistributionService;

    @GetMapping("/distribution")
    public Response<?> getGradeDistribution(@RequestParam(name = "course_id") String courseId) {
        return Response
                .ok()
                .setPayload(gradeDistributionService.getGradeDistribution(courseId));
    }
}
