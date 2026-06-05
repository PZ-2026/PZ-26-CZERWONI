package pl.edu.ur.teachly.ui.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.model.TutorSubjectResponse

enum class TeachingLevel {
    PRIMARY,
    HIGH_SCHOOL,
    UNIVERSITY,
    EXAM,
    PROFESSIONAL;

    companion object {
        val displayOrder =
            listOf(PRIMARY, HIGH_SCHOOL, UNIVERSITY, EXAM, PROFESSIONAL)
    }
}

data class SubjectsByLevelGroup(
    val level: TeachingLevel,
    val subjectNames: List<String>
)

@Composable
fun TeachingLevel.displayLabel(): String =
    when (this) {
        TeachingLevel.PRIMARY -> stringResource(R.string.teaching_level_primary)
        TeachingLevel.HIGH_SCHOOL -> stringResource(R.string.teaching_level_high_school)
        TeachingLevel.UNIVERSITY -> stringResource(R.string.teaching_level_university)
        TeachingLevel.EXAM -> stringResource(R.string.teaching_level_exam)
        TeachingLevel.PROFESSIONAL -> stringResource(R.string.teaching_level_professional)
    }

@Composable
fun TeachingLevel.shortLabel(): String =
    when (this) {
        TeachingLevel.PRIMARY -> stringResource(R.string.teaching_level_primary_short)
        TeachingLevel.HIGH_SCHOOL -> stringResource(R.string.teaching_level_high_school_short)
        TeachingLevel.UNIVERSITY -> stringResource(R.string.teaching_level_university_short)
        TeachingLevel.EXAM -> stringResource(R.string.teaching_level_exam_short)
        TeachingLevel.PROFESSIONAL -> stringResource(R.string.teaching_level_professional_short)
    }

fun TutorSubjectResponse.activeTeachingLevels(): List<TeachingLevel> =
    TeachingLevel.displayOrder.filter { hasLevel(it) }

fun TutorSubjectResponse.hasLevel(level: TeachingLevel): Boolean =
    when (level) {
        TeachingLevel.PRIMARY -> levelPrimary == true
        TeachingLevel.HIGH_SCHOOL -> levelHighSchool == true
        TeachingLevel.UNIVERSITY -> levelUniversity == true
        TeachingLevel.EXAM -> levelExamPrep == true
        TeachingLevel.PROFESSIONAL -> levelProfessional == true
    }

fun List<TutorSubjectResponse>.groupByTeachingLevel(): List<SubjectsByLevelGroup> =
    TeachingLevel.displayOrder.mapNotNull { level ->
        val names =
            this.filter { it.hasLevel(level) }
                .map { it.subjectName }
                .distinct()
                .sorted()
        if (names.isNotEmpty()) SubjectsByLevelGroup(level, names) else null
    }
