package pl.edu.ur.teachly.tutor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.review.repository.ReviewRepository;
import pl.edu.ur.teachly.subject.entity.Subject;
import pl.edu.ur.teachly.subject.repository.SubjectRepository;
import pl.edu.ur.teachly.tutor.dto.request.TutorSelfProfileRequest;
import pl.edu.ur.teachly.tutor.dto.request.TutorSubjectRequest;
import pl.edu.ur.teachly.tutor.dto.response.TutorResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorSearchResultResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorSubjectResponse;
import pl.edu.ur.teachly.tutor.entity.Tutor;
import pl.edu.ur.teachly.tutor.entity.TutorSubject;
import pl.edu.ur.teachly.tutor.mapper.TutorMapper;
import pl.edu.ur.teachly.tutor.mapper.TutorSubjectMapper;
import pl.edu.ur.teachly.tutor.repository.TutorRepository;
import pl.edu.ur.teachly.tutor.repository.TutorSubjectRepository;
import pl.edu.ur.teachly.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("TutorService - testy jednostkowe")
class TutorServiceTest {

    @Mock private TutorRepository tutorRepository;
    @Mock private TutorMapper tutorMapper;
    @Mock private TutorSubjectRepository tutorSubjectRepository;
    @Mock private TutorSubjectMapper tutorSubjectMapper;
    @Mock private SubjectRepository subjectRepository;
    @Mock private ReviewRepository reviewRepository;

    @InjectMocks private TutorService tutorService;

    @Test
    @DisplayName("getAllTutors - zwraca listę aktywnych korepetytorów")
    void getAllTutors_returnsList() {
        Tutor t1 = Tutor.builder().userId(1).user(User.builder().isActive(true).build()).build();
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
                        true,
                        "Kraków");

        when(tutorRepository.searchActiveTutors(null, null, null)).thenReturn(List.of(t1));
        when(tutorMapper.toResponse(t1)).thenReturn(r1);

        List<TutorResponse> result = tutorService.getAllTutors(null);

        assertThat(result).containsExactly(r1);
    }

    @Test
    @DisplayName("searchTutors - zwraca wyniki z przedmiotami i statystykami opinii")
    void searchTutors_returnsEnrichedResults() {
        Tutor t1 = Tutor.builder().userId(1).user(User.builder().isActive(true).build()).build();
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
                        true,
                        "Kraków");
        TutorSubject ts = TutorSubject.builder().tutor(t1).build();
        TutorSubjectResponse tsr =
                new TutorSubjectResponse(
                        1, 1, "Matematyka", "Kat", true, false, false, false, false);

        when(tutorRepository.searchActiveTutors("%jan%", null, null)).thenReturn(List.of(t1));
        when(tutorMapper.toResponse(t1)).thenReturn(r1);
        when(tutorSubjectRepository.findByTutor_UserIdIn(List.of(1))).thenReturn(List.of(ts));
        when(tutorSubjectMapper.toResponse(ts)).thenReturn(tsr);
        when(reviewRepository.findRatingStatsByTutorIds(List.of(1)))
                .thenReturn(
                        Collections.singletonList(
                                new Object[] {1, java.math.BigDecimal.valueOf(4.5), 2L}));

        List<TutorSearchResultResponse> result = tutorService.searchTutors("Jan", null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).tutor()).isEqualTo(r1);
        assertThat(result.get(0).subjects()).containsExactly(tsr);
        assertThat(result.get(0).averageRating()).isEqualTo(4.5);
        assertThat(result.get(0).reviewCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("searchTutors - pusta lista gdy brak wyników")
    void searchTutors_empty_returnsEmptyList() {
        when(tutorRepository.searchActiveTutors(null, null, null)).thenReturn(List.of());

        assertThat(tutorService.searchTutors(null, null, null)).isEmpty();
    }

    @Test
    @DisplayName("searchTutors - brak statystyk opinii zwraca zera")
    void searchTutors_noStats_returnsZeros() {
        Tutor t1 = Tutor.builder().userId(1).user(User.builder().isActive(true).build()).build();
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
                        true,
                        "Kraków");

        when(tutorRepository.searchActiveTutors(null, null, null)).thenReturn(List.of(t1));
        when(tutorMapper.toResponse(t1)).thenReturn(r1);
        when(tutorSubjectRepository.findByTutor_UserIdIn(List.of(1))).thenReturn(List.of());
        when(reviewRepository.findRatingStatsByTutorIds(List.of(1))).thenReturn(List.of());

        List<TutorSearchResultResponse> result = tutorService.searchTutors(null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).averageRating()).isZero();
        assertThat(result.get(0).reviewCount()).isZero();
    }

    @Test
    @DisplayName("getTutorById - sukces: zwraca korepetytora")
    void getTutorById_found_returnsResponse() {
        Tutor t1 = Tutor.builder().user(User.builder().isActive(true).build()).build();
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
                        true,
                        "Kraków");

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
    @DisplayName("getTutorById - błąd: korepetytor nieaktywny")
    void getTutorById_inactive_throwsException() {
        Tutor inactive = Tutor.builder().user(User.builder().isActive(false).build()).build();
        when(tutorRepository.findById(1)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> tutorService.getTutorById(1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getTutorSubjects - sukces: zwraca przedmioty korepetytora")
    void getTutorSubjects_success() {
        Tutor t1 = Tutor.builder().user(User.builder().isActive(true).build()).build();
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
                        "Bio", java.math.BigDecimal.valueOf(100), true, true, "Kraków");
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
                        true,
                        "Kraków");

        when(tutorRepository.findById(1)).thenReturn(Optional.of(tutor));
        when(tutorRepository.save(tutor)).thenReturn(tutor);
        when(tutorMapper.toResponse(tutor)).thenReturn(response);

        TutorResponse result = tutorService.adminUpdateTutor(1, req);

        assertThat(result).isEqualTo(response);
        verify(tutorMapper).updateFromRequest(req, tutor);
        verify(tutorRepository).save(tutor);
    }

    @Test
    @DisplayName("adminUpdateTutor - błąd: korepetytor nie istnieje")
    void adminUpdateTutor_notFound_throwsException() {
        pl.edu.ur.teachly.tutor.dto.request.TutorRequest req =
                new pl.edu.ur.teachly.tutor.dto.request.TutorRequest(
                        "Bio", java.math.BigDecimal.valueOf(100), true, true, "Kraków");

        when(tutorRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tutorService.adminUpdateTutor(99, req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("adminUpdateTutor - błąd: stacjonarnie bez miasta")
    void adminUpdateTutor_inPersonWithoutCity_throwsException() {
        pl.edu.ur.teachly.tutor.dto.request.TutorRequest req =
                new pl.edu.ur.teachly.tutor.dto.request.TutorRequest(
                        "Bio", java.math.BigDecimal.valueOf(100), false, true, null);

        assertThatThrownBy(() -> tutorService.adminUpdateTutor(1, req))
                .isInstanceOf(pl.edu.ur.teachly.common.exception.BusinessValidationException.class)
                .hasMessageContaining("miasto");
    }

    @Test
    @DisplayName("updateMyProfile - sukces")
    void updateMyProfile_success() {
        User currentUser = User.builder().id(1).build();
        TutorSelfProfileRequest req =
                new TutorSelfProfileRequest(
                        "Nowe bio", java.math.BigDecimal.valueOf(120), true, false, null);
        Tutor tutor = Tutor.builder().userId(1).build();
        TutorResponse response =
                new TutorResponse(
                        1,
                        "A",
                        "B",
                        "a@b.com",
                        "123",
                        "url",
                        "Nowe bio",
                        java.math.BigDecimal.valueOf(120),
                        true,
                        false,
                        null);

        when(tutorSubjectRepository.findByTutor_UserId(1))
                .thenReturn(List.of(TutorSubject.builder().id(10).build()));
        when(tutorRepository.findById(1)).thenReturn(Optional.of(tutor));
        when(tutorRepository.save(tutor)).thenReturn(tutor);
        when(tutorMapper.toResponse(tutor)).thenReturn(response);

        TutorResponse result = tutorService.updateMyProfile(req, currentUser);

        assertThat(result).isEqualTo(response);
        assertThat(tutor.getBio()).isEqualTo("Nowe bio");
    }

    @Test
    @DisplayName("updateMyProfile - błąd: zajęcia stacjonarne bez miasta")
    void updateMyProfile_inPersonWithoutCity_throwsException() {
        User currentUser = User.builder().id(1).build();
        TutorSelfProfileRequest req =
                new TutorSelfProfileRequest(
                        "Bio", java.math.BigDecimal.valueOf(100), false, true, "  ");

        assertThatThrownBy(() -> tutorService.updateMyProfile(req, currentUser))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("miasto");
    }

    @Test
    @DisplayName("updateMyProfile - sukces: zajęcia stacjonarne z miastem (trim)")
    void updateMyProfile_inPersonWithCity_setsTrimmedCity() {
        User currentUser = User.builder().id(1).build();
        TutorSelfProfileRequest req =
                new TutorSelfProfileRequest(
                        "Bio", java.math.BigDecimal.valueOf(100), false, true, "  Kraków  ");
        Tutor tutor = Tutor.builder().userId(1).build();

        when(tutorSubjectRepository.findByTutor_UserId(1))
                .thenReturn(List.of(TutorSubject.builder().id(10).build()));
        when(tutorRepository.findById(1)).thenReturn(Optional.of(tutor));
        when(tutorRepository.save(tutor)).thenReturn(tutor);
        when(tutorMapper.toResponse(tutor)).thenReturn(null);

        tutorService.updateMyProfile(req, currentUser);

        assertThat(tutor.getCity()).isEqualTo("Kraków");
        assertThat(tutor.getOffersInPerson()).isTrue();
    }

    @Test
    @DisplayName("updateMyProfile - błąd: brak formy zajęć")
    void updateMyProfile_noLessonFormat_throwsException() {
        User currentUser = User.builder().id(1).build();
        TutorSelfProfileRequest req =
                new TutorSelfProfileRequest(
                        "Bio", java.math.BigDecimal.valueOf(100), false, false, null);

        assertThatThrownBy(() -> tutorService.updateMyProfile(req, currentUser))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("formę zajęć");
    }

    @Test
    @DisplayName("updateMyProfile - błąd: brak przedmiotów")
    void updateMyProfile_noSubjects_throwsException() {
        User currentUser = User.builder().id(1).build();
        TutorSelfProfileRequest req =
                new TutorSelfProfileRequest(
                        "Bio", java.math.BigDecimal.valueOf(100), true, false, null);

        when(tutorSubjectRepository.findByTutor_UserId(1)).thenReturn(List.of());

        assertThatThrownBy(() -> tutorService.updateMyProfile(req, currentUser))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("przedmiot");
    }

    @Test
    @DisplayName("addMySubject - sukces")
    void addMySubject_success() {
        User currentUser = User.builder().id(1).build();
        Tutor tutor = Tutor.builder().userId(1).build();
        Subject subject = Subject.builder().id(2).subjectName("Chemia").build();
        TutorSubjectRequest request = new TutorSubjectRequest(2, true, false, false, false, false);
        TutorSubject saved = TutorSubject.builder().id(10).tutor(tutor).subject(subject).build();
        TutorSubjectResponse response =
                new TutorSubjectResponse(10, 2, "Chemia", "Kat", true, false, false, false, false);

        when(tutorRepository.findById(1)).thenReturn(Optional.of(tutor));
        when(tutorSubjectRepository.existsByTutor_UserIdAndSubject_Id(1, 2)).thenReturn(false);
        when(subjectRepository.findById(2)).thenReturn(Optional.of(subject));
        when(tutorSubjectRepository.save(org.mockito.ArgumentMatchers.any(TutorSubject.class)))
                .thenReturn(saved);
        when(tutorSubjectMapper.toResponse(saved)).thenReturn(response);

        TutorSubjectResponse result = tutorService.addMySubject(request, currentUser);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("removeMySubject - sukces")
    void removeMySubject_success() {
        User currentUser = User.builder().id(1).build();
        Tutor tutor = Tutor.builder().userId(1).build();
        TutorSubject tutorSubject = TutorSubject.builder().id(10).tutor(tutor).build();

        when(tutorSubjectRepository.findById(10)).thenReturn(Optional.of(tutorSubject));
        when(tutorSubjectRepository.findByTutor_UserId(1))
                .thenReturn(List.of(tutorSubject, TutorSubject.builder().id(11).build()));

        tutorService.removeMySubject(10, currentUser);

        verify(tutorSubjectRepository).delete(tutorSubject);
    }

    @Test
    @DisplayName("removeMySubject - błąd: brak uprawnień")
    void removeMySubject_accessDenied_throwsException() {
        User currentUser = User.builder().id(2).build();
        Tutor tutor = Tutor.builder().userId(1).build();
        TutorSubject tutorSubject = TutorSubject.builder().id(10).tutor(tutor).build();

        when(tutorSubjectRepository.findById(10)).thenReturn(Optional.of(tutorSubject));

        assertThatThrownBy(() -> tutorService.removeMySubject(10, currentUser))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("addMySubject - błąd: duplikat przedmiotu")
    void addMySubject_duplicate_throwsException() {
        User currentUser = User.builder().id(1).build();
        Tutor tutor = Tutor.builder().userId(1).build();
        TutorSubjectRequest request = new TutorSubjectRequest(2, true, false, false, false, false);

        when(tutorRepository.findById(1)).thenReturn(Optional.of(tutor));
        when(tutorSubjectRepository.existsByTutor_UserIdAndSubject_Id(1, 2)).thenReturn(true);

        assertThatThrownBy(() -> tutorService.addMySubject(request, currentUser))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("już przypisany");
    }

    @Test
    @DisplayName("adminAddSubject - błąd: przedmiot nie istnieje")
    void adminAddSubject_subjectNotFound_throwsException() {
        Tutor tutor = Tutor.builder().userId(1).build();
        TutorSubjectRequest request = new TutorSubjectRequest(99, true, false, false, false, false);

        when(tutorRepository.findById(1)).thenReturn(Optional.of(tutor));
        when(tutorSubjectRepository.existsByTutor_UserIdAndSubject_Id(1, 99)).thenReturn(false);
        when(subjectRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tutorService.adminAddSubject(1, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("przedmiotu");
    }

    @Test
    @DisplayName("adminAddSubject - sukces: dodaje przedmiot korepetytorowi")
    void adminAddSubject_success() {
        Tutor tutor = Tutor.builder().userId(1).build();
        Subject subject = Subject.builder().id(2).subjectName("Fizyka").build();
        TutorSubjectRequest request = new TutorSubjectRequest(2, true, false, false, false, false);
        TutorSubject saved =
                TutorSubject.builder()
                        .id(10)
                        .tutor(tutor)
                        .subject(subject)
                        .levelPrimary(true)
                        .build();
        TutorSubjectResponse response =
                new TutorSubjectResponse(10, 2, "Fizyka", "Kat", true, false, false, false, false);

        when(tutorRepository.findById(1)).thenReturn(Optional.of(tutor));
        when(tutorSubjectRepository.existsByTutor_UserIdAndSubject_Id(1, 2)).thenReturn(false);
        when(subjectRepository.findById(2)).thenReturn(Optional.of(subject));
        when(tutorSubjectRepository.save(org.mockito.ArgumentMatchers.any(TutorSubject.class)))
                .thenReturn(saved);
        when(tutorSubjectMapper.toResponse(saved)).thenReturn(response);

        TutorSubjectResponse result = tutorService.adminAddSubject(1, request);

        assertThat(result).isEqualTo(response);
        verify(tutorSubjectRepository).save(org.mockito.ArgumentMatchers.any(TutorSubject.class));
    }

    @Test
    @DisplayName("adminAddSubject - błąd: duplikat przedmiotu")
    void adminAddSubject_duplicate_throwsException() {
        Tutor tutor = Tutor.builder().userId(1).build();
        TutorSubjectRequest request = new TutorSubjectRequest(2, true, false, false, false, false);

        when(tutorRepository.findById(1)).thenReturn(Optional.of(tutor));
        when(tutorSubjectRepository.existsByTutor_UserIdAndSubject_Id(1, 2)).thenReturn(true);

        assertThatThrownBy(() -> tutorService.adminAddSubject(1, request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("już przypisany");
    }

    @Test
    @DisplayName("updateMyProfile - błąd: profil korepetytora nie istnieje")
    void updateMyProfile_notFound_throwsException() {
        User currentUser = User.builder().id(1).build();
        TutorSelfProfileRequest req =
                new TutorSelfProfileRequest(
                        "Bio", java.math.BigDecimal.valueOf(100), true, false, null);

        when(tutorSubjectRepository.findByTutor_UserId(1))
                .thenReturn(List.of(TutorSubject.builder().id(10).build()));
        when(tutorRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tutorService.updateMyProfile(req, currentUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("removeMySubject - błąd: przedmiot nie istnieje")
    void removeMySubject_notFound_throwsException() {
        User currentUser = User.builder().id(1).build();
        when(tutorSubjectRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tutorService.removeMySubject(99, currentUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("adminRemoveSubject - błąd: przedmiot należy do innego korepetytora")
    void adminRemoveSubject_wrongTutor_throwsException() {
        Tutor tutor = Tutor.builder().userId(2).build();
        TutorSubject tutorSubject = TutorSubject.builder().id(10).tutor(tutor).build();

        when(tutorSubjectRepository.findById(10)).thenReturn(Optional.of(tutorSubject));

        assertThatThrownBy(() -> tutorService.adminRemoveSubject(1, 10))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("adminRemoveSubject - sukces: usuwa przedmiot")
    void adminRemoveSubject_success() {
        Tutor tutor = Tutor.builder().userId(1).build();
        TutorSubject tutorSubject = TutorSubject.builder().id(10).tutor(tutor).build();

        when(tutorSubjectRepository.findById(10)).thenReturn(Optional.of(tutorSubject));
        when(tutorSubjectRepository.findByTutor_UserId(1))
                .thenReturn(List.of(tutorSubject, TutorSubject.builder().id(11).build()));

        tutorService.adminRemoveSubject(1, 10);

        verify(tutorSubjectRepository).delete(tutorSubject);
    }

    @Test
    @DisplayName("adminRemoveSubject - błąd: ostatni przedmiot")
    void adminRemoveSubject_lastSubject_throwsException() {
        Tutor tutor = Tutor.builder().userId(1).build();
        TutorSubject tutorSubject = TutorSubject.builder().id(10).tutor(tutor).build();

        when(tutorSubjectRepository.findById(10)).thenReturn(Optional.of(tutorSubject));
        when(tutorSubjectRepository.findByTutor_UserId(1)).thenReturn(List.of(tutorSubject));

        assertThatThrownBy(() -> tutorService.adminRemoveSubject(1, 10))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("co najmniej jeden");
    }
}
