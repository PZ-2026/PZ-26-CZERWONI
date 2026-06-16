package pl.edu.ur.teachly.lesson.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.ur.teachly.lesson.dto.request.LessonRequest;
import pl.edu.ur.teachly.lesson.dto.response.LessonResponse;
import pl.edu.ur.teachly.lesson.entity.Lesson;

/**
 * Mapper MapStruct konwertujący między encją {@link Lesson} a jej DTO.
 *
 * <p>Mapuje zagnieżdżone pola korepetytora, ucznia i przedmiotu na płaskie pola odpowiedzi {@link
 * LessonResponse}.
 */
@Mapper(componentModel = "spring")
public interface LessonMapper {
    @Mapping(source = "tutor.userId", target = "tutorId")
    @Mapping(source = "tutor.user.firstName", target = "tutorFirstName")
    @Mapping(source = "tutor.user.lastName", target = "tutorLastName")
    @Mapping(source = "tutor.user.avatarUrl", target = "tutorAvatarUrl")
    @Mapping(source = "tutor.city", target = "tutorCity")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.firstName", target = "studentFirstName")
    @Mapping(source = "student.lastName", target = "studentLastName")
    @Mapping(source = "student.avatarUrl", target = "studentAvatarUrl")
    @Mapping(source = "subject.id", target = "subjectId")
    @Mapping(source = "subject.subjectName", target = "subjectName")
    LessonResponse toResponse(Lesson lesson);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tutor", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "lessonStatus", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tutorNotes", ignore = true)
    Lesson toEntity(LessonRequest request);
}
