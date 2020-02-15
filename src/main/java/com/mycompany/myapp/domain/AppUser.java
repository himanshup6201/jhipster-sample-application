package com.mycompany.myapp.domain;


import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A AppUser.
 */
@Entity
@Table(name = "app_user")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "appuser")
public class AppUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "jhi_password")
    private String password;

    @Column(name = "adminflag")
    private Boolean adminflag;

    @Column(name = "deleteflag")
    private Boolean deleteflag;

    @ManyToMany
    @JoinTable(name = "app_user_role",
               joinColumns = @JoinColumn(name = "app_user_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public AppUser username(String username) {
        this.username = username;
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public AppUser password(String password) {
        this.password = password;
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isAdminflag() {
        return adminflag;
    }

    public AppUser adminflag(Boolean adminflag) {
        this.adminflag = adminflag;
        return this;
    }

    public void setAdminflag(Boolean adminflag) {
        this.adminflag = adminflag;
    }

    public Boolean isDeleteflag() {
        return deleteflag;
    }

    public AppUser deleteflag(Boolean deleteflag) {
        this.deleteflag = deleteflag;
        return this;
    }

    public void setDeleteflag(Boolean deleteflag) {
        this.deleteflag = deleteflag;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public AppUser roles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public AppUser addRole(Role role) {
        this.roles.add(role);
        role.getAppusers().add(this);
        return this;
    }

    public AppUser removeRole(Role role) {
        this.roles.remove(role);
        role.getAppusers().remove(this);
        return this;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppUser)) {
            return false;
        }
        return id != null && id.equals(((AppUser) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "AppUser{" +
            "id=" + getId() +
            ", username='" + getUsername() + "'" +
            ", password='" + getPassword() + "'" +
            ", adminflag='" + isAdminflag() + "'" +
            ", deleteflag='" + isDeleteflag() + "'" +
            "}";
    }
}
