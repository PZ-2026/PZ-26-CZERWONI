package pl.edu.ur.teachly.user.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.user.entity.User;

/**
 * Repozytorium JPA dla encji {@link User}.
 *
 * <p>Zawiera zapytania JPQL do wyszukiwania użytkowników po e-mailu, numerze telefonu oraz
 * filtrowania z obsługą wzorca LIKE (case-insensitive).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber")
    Optional<User> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.phoneNumber = :phoneNumber")
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT u FROM User u WHERE u.email = :email OR u.phoneNumber = :phoneNumber")
    Optional<User> findByEmailOrPhoneNumber(
            @Param("email") String email, @Param("phoneNumber") String phoneNumber);

    @Query("SELECT COUNT(u) FROM User u WHERE u.userRole = :role")
    int countByUserRole(@Param("role") UserRole role);

    @Query("SELECT u.userRole, COUNT(u) FROM User u GROUP BY u.userRole")
    List<Object[]> countGroupedByRole();

    @Query(
            """
                    SELECT u FROM User u WHERE
                    (:pattern IS NULL OR
                     LOWER(u.firstName) LIKE :pattern ESCAPE '\\' OR
                     LOWER(u.lastName) LIKE :pattern ESCAPE '\\' OR
                     LOWER(u.email) LIKE :pattern ESCAPE '\\')
                    AND (:role IS NULL OR u.userRole = :role)
                    AND (:active IS NULL OR u.isActive = :active)
                    ORDER BY u.lastName, u.firstName
                    """)
    List<User> searchUsers(
            @Param("pattern") String pattern,
            @Param("role") UserRole role,
            @Param("active") Boolean active);
}
