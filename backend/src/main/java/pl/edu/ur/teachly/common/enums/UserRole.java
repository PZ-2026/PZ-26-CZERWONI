package pl.edu.ur.teachly.common.enums;

/**
 * Rola użytkownika w systemie Teachly.
 *
 * <p>Rola decyduje o uprawnieniach: uczeń może rezerwować lekcje, korepetytor zarządza swoją
 * dostępnością i ofertą, administrator ma pełny dostęp do panelu zarządzania.
 */
public enum UserRole {
    /** Uczeń — może rezerwować lekcje i wystawiać opinie korepetytorów. */
    STUDENT,

    /** Korepetytor — zarządza dostępnością, ofertą i prowadzi lekcje. */
    TUTOR,

    /** Administrator — pełny dostęp do zarządzania systemem. */
    ADMIN
}
