package pl.edu.ur.teachly.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.edu.ur.teachly.common.enums.UserRole;

/**
 * Encja reprezentująca konto użytkownika w systemie Teachly.
 *
 * <p>Implementuje {@link UserDetails}, dzięki czemu jest bezpośrednio obsługiwana przez Spring
 * Security. Hasło przechowywane jest jako hash BCrypt. Zablokowane konto ({@code isActive = false})
 * jest nieuwierzytelniane przez Spring Security.
 */
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true, length = 10)
    private String phoneNumber;

    /** Hash hasła użytkownika (BCrypt). */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;

    /** Flaga aktywności konta — zablokowane konto nie może się zalogować. */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Zwraca uprawnienia użytkownika na podstawie jego roli (np. {@code ROLE_STUDENT}).
     *
     * @return lista uprawnień Spring Security
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
    }

    /** Zwraca hash hasła — używane przez Spring Security do uwierzytelnienia. */
    @Override
    public String getPassword() {
        return passwordHash;
    }

    /** Zwraca adres e-mail jako nazwę użytkownika w Spring Security. */
    @Override
    public String getUsername() {
        return email;
    }

    /** Konto jest odblokowane, gdy {@code isActive == true}. */
    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    /** Konto jest włączone, gdy {@code isActive == true}. */
    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
