package kg.mega.library_app.models.entities;

import jakarta.persistence.*;
import kg.mega.library_app.models.constants.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)

@Entity
@Table(name = "users")
public class User implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;
    @Column(length = 30)
    String firstname;
    @Column(length = 30)
    String lastname;
    @Column(name = "phone_number", length = 50)
    String phoneNumber;
    @Column(name = "is_active")
    boolean isActive;
    @Column(length = 50, unique = true, updatable = false)
    String email;
    @Column(length = 1000)
    String password;
    @Enumerated(STRING)
    Role role;
    @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
    List<Book> books;
    @Column(name = "date_of_created")
    LocalDateTime dateOfCreated;
    @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
    List<Review> reviews;
    @ManyToOne
    @JoinColumn(name = "admin_created_by", referencedColumnName = "id")
    User adminCreatedBy;
    @ManyToMany(fetch = EAGER, cascade = {PERSIST, MERGE, REMOVE})
    @JoinTable(
            name = "user_favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    List<Book> favorites;

    public User(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public User(java.lang.String firstname, java.lang.String lastname, java.lang.String phoneNumber, boolean isActive, java.lang.String email, Role role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneNumber = phoneNumber;
        this.isActive = isActive;
        this.email = email;
        this.role = role;
    }

    public User(String firstname, String lastname, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public User(String email) {
        this.email = email;
    }

    @PrePersist
    protected void onCreate() {
        dateOfCreated = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
