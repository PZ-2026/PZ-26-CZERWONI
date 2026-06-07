package pl.edu.ur.teachly.subject.service;

import java.util.List;
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

/**
 * Serwis zarządzający przedmiotami i ich kategoriami.
 *
 * <p>Przedmioty są przypisane do kategorii. Usunięcie kategorii jest zablokowane, gdy posiada ona
 * przypisane przedmioty.
 */
@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final SubjectCategoryRepository categoryRepository;
    private final SubjectMapper subjectMapper;
    private final SubjectCategoryMapper categoryMapper;

    /**
     * Zwraca listę wszystkich przedmiotów.
     *
     * @return lista przedmiotów
     */
    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAll().stream().map(subjectMapper::toResponse).toList();
    }

    /**
     * Dodaje nowy przedmiot przypisany do wskazanej kategorii.
     *
     * @param request dane nowego przedmiotu
     * @return zapisany przedmiot
     * @throws ResourceNotFoundException gdy kategoria nie istnieje
     */
    @Transactional
    public SubjectResponse addSubject(SubjectRequest request) {
        SubjectCategory category =
                categoryRepository
                        .findById(request.categoryId())
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanej kategorii"));

        Subject subject = subjectMapper.toEntity(request);
        subject.setCategory(category);
        return subjectMapper.toResponse(subjectRepository.save(subject));
    }

    /**
     * Aktualizuje dane przedmiotu.
     *
     * @param id identyfikator przedmiotu
     * @param request nowe dane przedmiotu
     * @return zaktualizowany przedmiot
     * @throws ResourceNotFoundException gdy przedmiot lub kategoria nie istnieje
     */
    @Transactional
    public SubjectResponse updateSubject(Integer id, SubjectRequest request) {
        Subject subject =
                subjectRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono przedmiotu do edycji"));
        SubjectCategory category =
                categoryRepository
                        .findById(request.categoryId())
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanej kategorii"));
        subject.setSubjectName(request.subjectName());
        subject.setCategory(category);
        return subjectMapper.toResponse(subjectRepository.save(subject));
    }

    /**
     * Usuwa przedmiot o podanym identyfikatorze.
     *
     * @param id identyfikator przedmiotu
     * @throws ResourceNotFoundException gdy przedmiot nie istnieje
     */
    @Transactional
    public void deleteSubject(Integer id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie znaleziono przedmiotu do usunięcia");
        }
        subjectRepository.deleteById(id);
    }

    /**
     * Zwraca listę wszystkich kategorii przedmiotów.
     *
     * @return lista kategorii
     */
    @Transactional(readOnly = true)
    public List<SubjectCategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::toResponse).toList();
    }

    /**
     * Dodaje nową kategorię przedmiotów.
     *
     * @param request dane nowej kategorii
     * @return zapisana kategoria
     */
    @Transactional
    public SubjectCategoryResponse addSubjectCategory(SubjectCategoryRequest request) {
        SubjectCategory category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    /**
     * Aktualizuje nazwę kategorii przedmiotów.
     *
     * @param id identyfikator kategorii
     * @param request nowa nazwa kategorii
     * @return zaktualizowana kategoria
     * @throws ResourceNotFoundException gdy kategoria nie istnieje
     */
    @Transactional
    public SubjectCategoryResponse updateSubjectCategory(
            Integer id, SubjectCategoryRequest request) {
        SubjectCategory category =
                categoryRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono kategorii do edycji"));
        category.setCategoryName(request.categoryName());
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    /**
     * Usuwa kategorię przedmiotów. Usunięcie jest blokowane, gdy kategoria posiada przypisane
     * przedmioty.
     *
     * @param id identyfikator kategorii
     * @throws ResourceNotFoundException gdy kategoria nie istnieje
     * @throws BusinessValidationException gdy do kategorii są przypisane przedmioty
     */
    @Transactional
    public void deleteSubjectCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie znaleziono kategorii do usunięcia");
        }
        if (!subjectRepository.findByCategory_Id(id).isEmpty()) {
            throw new BusinessValidationException(
                    "Nie można usunąć kategorii, ponieważ przypisane są do niej przedmioty");
        }
        categoryRepository.deleteById(id);
    }
}
