package pl.edu.ur.teachly.review.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.review.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByTutor_UserId(Integer tutorId);

    List<Review> findByStudent_Id(Integer studentId);
}
