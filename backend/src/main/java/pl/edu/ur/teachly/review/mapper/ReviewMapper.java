package pl.edu.ur.teachly.review.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.ur.teachly.review.dto.request.ReviewRequest;
import pl.edu.ur.teachly.review.dto.response.ReviewResponse;
import pl.edu.ur.teachly.review.entity.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(source = "tutor.userId", target = "tutorId")
    @Mapping(source = "student.id", target = "studentId")
    ReviewResponse toResponse(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tutor", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Review toEntity(ReviewRequest request);
}
