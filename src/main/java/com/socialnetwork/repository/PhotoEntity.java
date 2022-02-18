package com.socialnetwork.repository;

import com.socialnetwork.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "profilepictures")
public class PhotoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profilepicture_seq_generator")
    @SequenceGenerator(name = "profilepicture_seq_generator", sequenceName = "profilepictures_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String path;

    private boolean validSelfie;
}
