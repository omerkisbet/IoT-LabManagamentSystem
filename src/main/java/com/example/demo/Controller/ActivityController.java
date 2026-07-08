package com.example.demo.Controller;

import com.example.demo.dto.ActivityRequest;
import com.example.demo.dto.ActivityResponse;
import com.example.demo.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(
            ActivityService activityService
    ) {
        this.activityService = activityService;
    }

    @GetMapping("/students/{studentId}/activities")
    public List<ActivityResponse> getStudentActivities(
            @PathVariable String studentId
    ) {
        return activityService
                .getActivitiesByStudentId(studentId);
    }

    @PostMapping("/students/{studentId}/activities")
    @ResponseStatus(HttpStatus.CREATED)
    public ActivityResponse addActivity(
            @PathVariable String studentId,
            @Valid @RequestBody ActivityRequest request
    ) {
        return activityService.addActivity(
                studentId,
                request
        );
    }

    @GetMapping("/activities/{activityId}")
    public ActivityResponse getActivityById(
            @PathVariable String activityId
    ) {
        return activityService
                .getActivityById(activityId);
    }

    @PutMapping("/activities/{activityId}")
    public ActivityResponse updateActivity(
            @PathVariable String activityId,
            @Valid @RequestBody ActivityRequest request
    ) {
        return activityService.updateActivity(
                activityId,
                request
        );
    }

    @DeleteMapping("/activities/{activityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivity(
            @PathVariable String activityId
    ) {
        activityService.deleteActivity(activityId);
    }
}