package com.example.demo.mapper;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;

final class MongoTimestampUtil {

    private MongoTimestampUtil() {
    }

    static LocalDateTime resolveCreatedAt(
            String id,
            LocalDateTime createdAt
    ) {
        if (createdAt != null) {
            return createdAt;
        }

        if (id == null || !ObjectId.isValid(id)) {
            return null;
        }

        return LocalDateTime.ofInstant(
                new ObjectId(id).getDate().toInstant(),
                ZoneId.systemDefault()
        );
    }
}
