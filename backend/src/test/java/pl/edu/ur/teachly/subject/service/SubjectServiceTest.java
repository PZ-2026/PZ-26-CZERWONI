package pl.edu.ur.teachly.subject.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.subject.dto.request.SubjectCategoryRequest;
import pl.edu.ur.teachly.subject.dto.request.SubjectRequest;
import pl.edu.ur.teachly.subject.dto.response.SubjectCategoryResponse;
import pl.edu.ur.teachly.subject.dto.response.SubjectResponse;
import pl.edu.ur.teachly.subject.entity.Subject;
import pl.edu.ur.teachly.subject.entity.SubjectCategory;
import pl.edu.ur.teachly.subject.mapper.SubjectCategoryMapper;
import pl.edu.ur.teachly.subject.mapper.SubjectMapper;
import pl.edu.ur.teachly.subject.repository.SubjectCategoryRepository;
import pl.edu.ur.teachly.subject.repository.SubjectRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubjectService - testy jednostkowe")
class SubjectServiceTest {

    @Mock private SubjectRepository subjectRepository;
    @Mock private SubjectCategoryRepository categoryRepository;
    @Mock private SubjectMapper subjectMapper;
    @Mock private SubjectCategoryMapper categoryMapper;

    @InjectMocks
    private SubjectService subjectService;

    @Test
    @DisplayName("getAllSubjects - zwraca listę przedmiotów")
    void getAllSubjects_returnsList() {
        Subject s1 = new Subject();
        SubjectResponse r1 = new SubjectResponse(1, "Matematyka", 1, "Nauki ścisłe");
        
        when(subjectRepository.findAll()).thenReturn(List.of(s1));
        when(subjectMapper.toResponse(s1)).thenReturn(r1);

        List<SubjectResponse> result = subjectService.getAllSubjects();

        assertThat(result).containsExactly(r1);
    }

    @Test
    @DisplayName("addSubject - sukces: dodaje przedmiot")
    void addSubject_success() {
        SubjectRequest req = new SubjectRequest("Matematyka", 1);
        SubjectCategory category = new SubjectCategory();
        Subject subject = new Subject();
        SubjectResponse response = new SubjectResponse(1, "Matematyka", 1, "Nauki ścisłe");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(subjectMapper.toEntity(req)).thenReturn(subject);
        when(subjectRepository.save(subject)).thenReturn(subject);
        when(subjectMapper.toResponse(subject)).thenReturn(response);

        SubjectResponse result = subjectService.addSubject(req);

        assertThat(result).isEqualTo(response);
        verify(subjectRepository).save(subject);
    }

    @Test
    @DisplayName("addSubject - błąd: kategoria nie istnieje")
    void addSubject_categoryNotFound_throwsException() {
        SubjectRequest req = new SubjectRequest("Matematyka", 99);
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.addSubject(req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deleteSubject - sukces")
    void deleteSubject_success() {
        when(subjectRepository.existsById(1)).thenReturn(true);

        subjectService.deleteSubject(1);

        verify(subjectRepository).deleteById(1);
    }

    @Test
    @DisplayName("deleteSubject - błąd: nie istnieje")
    void deleteSubject_notFound_throwsException() {
        when(subjectRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> subjectService.deleteSubject(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getAllCategories - zwraca listę kategorii")
    void getAllCategories_returnsList() {
        SubjectCategory c1 = new SubjectCategory();
        SubjectCategoryResponse r1 = new SubjectCategoryResponse(1, "Nauki ścisłe");

        when(categoryRepository.findAll()).thenReturn(List.of(c1));
        when(categoryMapper.toResponse(c1)).thenReturn(r1);

        List<SubjectCategoryResponse> result = subjectService.getAllCategories();

        assertThat(result).containsExactly(r1);
    }

    @Test
    @DisplayName("addSubjectCategory - sukces")
    void addSubjectCategory_success() {
        SubjectCategoryRequest req = new SubjectCategoryRequest("Nauki ścisłe");
        SubjectCategory category = new SubjectCategory();
        SubjectCategoryResponse response = new SubjectCategoryResponse(1, "Nauki ścisłe");

        when(categoryMapper.toEntity(req)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(response);

        SubjectCategoryResponse result = subjectService.addSubjectCategory(req);

        assertThat(result).isEqualTo(response);
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("deleteSubjectCategory - sukces")
    void deleteSubjectCategory_success() {
        when(categoryRepository.existsById(1)).thenReturn(true);
        when(subjectRepository.findByCategory_Id(1)).thenReturn(List.of());

        subjectService.deleteSubjectCategory(1);

        verify(categoryRepository).deleteById(1);
    }

    @Test
    @DisplayName("deleteSubjectCategory - błąd: kategoria ma przypisane przedmioty")
    void deleteSubjectCategory_hasSubjects_throwsException() {
        when(categoryRepository.existsById(1)).thenReturn(true);
        when(subjectRepository.findByCategory_Id(1)).thenReturn(List.of(new Subject()));

        assertThatThrownBy(() -> subjectService.deleteSubjectCategory(1))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("przypisane są do niej przedmioty");
    }
}
