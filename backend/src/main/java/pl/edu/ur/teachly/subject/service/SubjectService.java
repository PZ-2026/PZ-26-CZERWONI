package pl.edu.ur.teachly.subject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final SubjectCategoryRepository categoryRepository;
    private final SubjectMapper subjectMapper;
    private final SubjectCategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(subjectMapper::toResponse)
                .toList();
    }

    @Transactional
    public SubjectResponse addSubject(SubjectRequest request) {
        SubjectCategory category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono szukanej kategorii"));

        Subject subject = subjectMapper.toEntity(request);
        subject.setCategory(category);
        return subjectMapper.toResponse(subjectRepository.save(subject));
    }

    @Transactional
    public void deleteSubject(Integer id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie znaleziono przedmiotu do usunięcia");
        }
        subjectRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SubjectCategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Transactional
    public SubjectCategoryResponse addSubjectCategory(SubjectCategoryRequest request) {
        SubjectCategory category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deleteSubjectCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie znaleziono kategorii do usunięcia");
        }
        if (!subjectRepository.findByCategory_Id(id).isEmpty()) {
            throw new BusinessValidationException("Nie można usunąć kategorii, ponieważ przypisane są do niej przedmioty");
        }
        categoryRepository.deleteById(id);
    }
}

