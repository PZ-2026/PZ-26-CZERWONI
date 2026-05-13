package pl.edu.ur.teachly.report;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.edu.ur.teachly.user.entity.User;
import pl.edu.ur.teachly.user.repository.UserRepository;

@SpringBootTest
public class ReportServiceTest {

    @Autowired private ReportService reportService;

    @Autowired private UserRepository userRepository;

    @Test
    public void testTutorReport() {
        // user_id = 2 is Marek Nowak (Tutor) in seed data
        User tutor = userRepository.findById(2).orElseThrow();
        System.out.println("Tutor: " + tutor.getFirstName());
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusYears(1);
        try {
            reportService.generateReport(tutor, startDate, endDate);
            System.out.println("TUTOR REPORT SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
