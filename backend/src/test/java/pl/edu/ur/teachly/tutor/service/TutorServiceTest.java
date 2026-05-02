package pl.edu.ur.teachly.tutor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.tutor.dto.response.TutorResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorSubjectResponse;
import pl.edu.ur.teachly.tutor.entity.Tutor;
import pl.edu.ur.teachly.tutor.entity.TutorSubject;
import pl.edu.ur.teachly.tutor.mapper.TutorMapper;
import pl.edu.ur.teachly.tutor.mapper.TutorSubjectMapper;
import pl.edu.ur.teachly.tutor.repository.TutorRepository;
import pl.edu.ur.teachly.tutor.repository.TutorSubjectRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("TutorService - testy jednostkowe")
class TutorServiceTest {

    @Mock private TutorRepository tutorRepository;
    @Mock private TutorMapper tutorMapper;
    @Mock private TutorSubjectRepository tutorSubjectRepository;
    @Mock private TutorSubjectMapper tutorSubjectMapper;

    @InjectMocks private TutorService tutorService;

    @Test
    @DisplayName("getAllTutors - zwraca listę wszystkich korepetytorów")
    void getAllTutors_returnsList() {
        Tutor t1 = new Tutor();
        TutorResponse r1 =
                new TutorResponse(
                        1,
                        "Test",
                        "Test",
                        "test@test.com",
                        "123",
                        "url",
                        "Bio",
                        java.math.BigDecimal.TEN,
                        true,
                        true);

        when(tutorRepository.findAll()).thenReturn(List.of(t1));
        when(tutorMapper.toResponse(t1)).thenReturn(r1);

        List<TutorResponse> result = tutorService.getAllTutors();

        assertThat(result).containsExactly(r1);
    }

    @Test
    @DisplayName("getTutorById - sukces: zwraca korepetytora")
    void getTutorById_found_returnsResponse() {
        Tutor t1 = new Tutor();
        TutorResponse r1 =
                new TutorResponse(
                        1,
                        "Test",
                        "Test",
                        "test@test.com",
                        "123",
                        "url",
                        "Bio",
                        java.math.BigDecimal.TEN,
                        true,
                        true);

        when(tutorRepository.findById(1)).thenReturn(Optional.of(t1));
        when(tutorMapper.toResponse(t1)).thenReturn(r1);

        TutorResponse result = tutorService.getTutorById(1);

        assertThat(result).isEqualTo(r1);
    }

    @Test
    @DisplayName("getTutorById - błąd: nie istnieje")
    void getTutorById_notFound_throwsException() {
        when(tutorRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tutorService.getTutorById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getTutorSubjects - sukces: zwraca przedmioty korepetytora")
    void getTutorSubjects_success() {
        Tutor t1 = new Tutor();
        TutorSubject ts = new TutorSubject();
        TutorSubjectResponse r1 =
                new TutorSubjectResponse(
                        1, 1, "Matematyka", "Kategoria", true, false, false, false, false);

        when(tutorRepository.findById(1)).thenReturn(Optional.of(t1));
        when(tutorSubjectRepository.findByTutor_UserId(1)).thenReturn(List.of(ts));
        when(tutorSubjectMapper.toResponse(ts)).thenReturn(r1);

        List<TutorSubjectResponse> result = tutorService.getTutorSubjects(1);

        assertThat(result).containsExactly(r1);
    }

    @Test
    @DisplayName("getTutorSubjects - błąd: tutor nie istnieje")
    void getTutorSubjects_tutorNotFound_throwsException() {
        when(tutorRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tutorService.getTutorSubjects(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("adminUpdateTutor - sukces")
    void adminUpdateTutor_success() {
        Tutor tutor = new Tutor();
        pl.edu.ur.teachly.tutor.dto.request.TutorRequest req =
                new pl.edu.ur.teachly.tutor.dto.request.TutorRequest(
                        "Bio", java.math.BigDecimal.valueOf(100), true, true);
        TutorResponse response =
                new TutorResponse(
                        1,
                        "A",
                        "B",
                        "a@b.com",
                        "123",
                        "url",
                        "Bio",
                        java.math.BigDecimal.valueOf(100),
                        true,
                        true);

        when(tutorRepository.findById(1)).thenReturn(Optional.of(tutor));
        when(tutorRepository.save(tutor)).thenReturn(tutor);
        when(tutorMapper.toResponse(tutor)).thenReturn(response);

        TutorResponse result = tutorService.adminUpdateTutor(1, req);

        assertThat(result).isEqualTo(response);
        verify(tutorMapper).updateFromRequest(req, tutor);
        verify(tutorRepository).save(tutor);
    }
}
