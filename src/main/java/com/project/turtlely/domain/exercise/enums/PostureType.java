package com.project.turtlely.domain.exercise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostureType {
    ALL("전체"),
    TURTLE_NECK("거북목"),
    STRAIGHT_NECK("일자목"),
    REVERSE_C("역C자목"),
    NORMAL("정상");

    private final String description;
}
