package com.socialnetwork.repository;

import com.socialnetwork.domain.Photo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_generator")
    @SequenceGenerator(name = "user_seq_generator", sequenceName = "users_id_seq", allocationSize = 1)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    private boolean emailVerified;

    private String passwordHash;

    @OneToOne
    @JoinColumn(name = "profilepicture_id")
    private PhotoEntity profilePicture;

    public UserEntity(Long id) {
        this.id = id;
    }
}
