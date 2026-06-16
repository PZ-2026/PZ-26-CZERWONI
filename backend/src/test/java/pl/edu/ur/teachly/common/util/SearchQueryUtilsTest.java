package pl.edu.ur.teachly.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SearchQueryUtils – testy jednostkowe")
class SearchQueryUtilsTest {

    @Test
    @DisplayName("toLikePattern – null i puste zwracają null")
    void toLikePattern_blank_returnsNull() {
        assertThat(SearchQueryUtils.toLikePattern(null)).isNull();
        assertThat(SearchQueryUtils.toLikePattern("")).isNull();
        assertThat(SearchQueryUtils.toLikePattern("   ")).isNull();
    }

    @Test
    @DisplayName("toLikePattern – owija tekst i normalizuje wielkość liter")
    void toLikePattern_wrapsAndLowercases() {
        assertThat(SearchQueryUtils.toLikePattern("  Jan  ")).isEqualTo("%jan%");
    }

    @Test
    @DisplayName("toLikePattern – escapuje znaki specjalne LIKE")
    void toLikePattern_escapesWildcards() {
        assertThat(SearchQueryUtils.toLikePattern("100%")).isEqualTo("%100\\%%");
        assertThat(SearchQueryUtils.toLikePattern("a_b")).isEqualTo("%a\\_b%");
        assertThat(SearchQueryUtils.toLikePattern("a\\b")).isEqualTo("%a\\\\b%");
    }
}
