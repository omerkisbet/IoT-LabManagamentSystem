package com.example.demo.dto;

public record StudentStatisticsResponse(
        long projectCount,
        long publicationCount,
        long activityCount,
        long totalContributionCount
) {
}
