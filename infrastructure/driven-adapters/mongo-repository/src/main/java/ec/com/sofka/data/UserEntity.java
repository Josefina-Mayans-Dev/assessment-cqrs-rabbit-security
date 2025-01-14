package ec.com.sofka.data;

import ec.com.sofka.user.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Document(collection = "_users")
public class UserEntity implements UserDetails {

    @Id
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role;

    public UserEntity(String id, String firstname, String lastname, String email, String password, Role role) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

   public UserEntity(){}

    public UserEntity(String firstname, String lastname, String email, String password, Role role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
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
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }


    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    // Builder
    public static class Builder {
        private String firstname;
        private String lastname;
        private String email;
        private String password;
        private Role role;


        public Builder setFirstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public Builder setLastname(String lastname){
            this.lastname = lastname;
            return  this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPassword(String password){
            this.password = password;
            return this;
        }

        public Builder setRole(Role role){
            this.role = role;
            return  this;
        }

        public static Builder builder(){
            return new Builder();
        }

        public UserEntity build() {
            return new UserEntity(firstname, lastname, email, password, role);
        }

    }
}