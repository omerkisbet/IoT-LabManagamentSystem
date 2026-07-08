package com.example.demo.service;

import com.example.demo.dto.ActivityRequest;
import com.example.demo.dto.ActivityResponse;
import com.example.demo.entity.Activity;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ActivityMapper;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final StudentRepository studentRepository;

    public ActivityService(
            ActivityRepository activityRepository,
            StudentRepository studentRepository
    ) {
        this.activityRepository = activityRepository;
        this.studentRepository = studentRepository;
    }

    public List<ActivityResponse> getActivitiesByStudentId(
            String studentId
    ) {
        validateStudentExists(studentId);

        return activityRepository
                .findByStudentIdOrderByActivityDateDesc(studentId)
                .stream()
                .map(ActivityMapper::toResponse)
                .toList();
    }

    public ActivityResponse getActivityById(
            String activityId
    ) {
        return ActivityMapper.toResponse(
                findActivityEntityById(activityId)
        );
    }

    public ActivityResponse addActivity(
            String studentId,
            ActivityRequest request
    ) {
        validateStudentExists(studentId);

        Activity activity =
                ActivityMapper.toEntity(
                        request,
                        studentId
                );

        if (activity.getActivityDate() == null) {
            activity.setActivityDate(
                    LocalDateTime.now()
            );
        }

        Activity savedActivity =
                activityRepository.save(activity);

        return ActivityMapper.toResponse(savedActivity);
    }

    public ActivityResponse updateActivity(
            String activityId,
            ActivityRequest request
    ) {
        Activity existingActivity =
                findActivityEntityById(activityId);

        ActivityMapper.updateEntity(
                existingActivity,
                request
        );

        Activity savedActivity =
                activityRepository.save(existingActivity);

        return ActivityMapper.toResponse(savedActivity);
    }

    public void deleteActivity(
            String activityId
    ) {
        Activity activity =
                findActivityEntityById(activityId);

        activityRepository.delete(activity);
    }

    private Activity findActivityEntityById(
            String activityId
    ) {
        return activityRepository.findById(activityId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Activity not found. ID: "
                                        + activityId
                        )
                );
    }

    private void validateStudentExists(
            String studentId
    ) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException(
                    "Student not found. ID: "
                            + studentId
            );
        }
    }
}