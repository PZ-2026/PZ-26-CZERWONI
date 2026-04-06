package pl.edu.ur.teachly.lesson.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.ur.teachly.lesson.dto.request.LessonRequest;
import pl.edu.ur.teachly.lesson.dto.response.LessonResponse;
import pl.edu.ur.teachly.lesson.entity.Lesson;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    @Mapping(source = "tutor.userId", target = "tutorId")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "subject.id", target = "subjectId")
    LessonResponse toResponse(Lesson lesson);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tutor", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "lessonStatus", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tutorNotes", ignore = true)
    Lesson toEntity(LessonRequest request);
}