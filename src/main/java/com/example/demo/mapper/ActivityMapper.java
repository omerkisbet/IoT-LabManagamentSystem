package com.example.demo.mapper;

import com.example.demo.dto.ActivityRequest;
import com.example.demo.dto.ActivityResponse;
import com.example.demo.entity.Activity;

public final class ActivityMapper {

    private ActivityMapper() {
    }

    public static Activity toEntity(
            ActivityRequest request,
            String studentId
    ) {
        Activity activity = new Activity();

        activity.setStudentId(studentId);
        activity.setTitle(request.title());
        activity.setDescription(request.description());
        activity.setActivityDate(request.activityDate());

        return activity;
    }

    public static void updateEntity(
            Activity activity,
            ActivityRequest request
    ) {
        activity.setTitle(request.title());
        activity.setDescription(request.description());

        if (request.activityDate() != null) {
            activity.setActivityDate(
                    request.activityDate()
            );
        }
    }

    public static ActivityResponse toResponse(
            Activity activity
    ) {
        return new ActivityResponse(
                activity.getId(),
                activity.getStudentId(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getActivityDate()
        );
    }
}