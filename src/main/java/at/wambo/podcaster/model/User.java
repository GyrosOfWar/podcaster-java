package at.wambo.podcaster.model;

import lombok.Data;

import javax.persistence.*;

/**
 * @author Martin
 *         01.07.2016
 */
@Data
@Entity
@Table(name = "users")
public class User {
    // TODO http://kielczewski.eu/2014/12/spring-boot-security-application/

    @GeneratedValue
    @Id
    private int id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String pwHash;
}
