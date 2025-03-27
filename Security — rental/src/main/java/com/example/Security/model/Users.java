    package com.example.Security.model;

    import com.example.Security.model.enums.Role;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import jakarta.persistence.*;
    import lombok.*;

    import java.util.UUID;

    @Entity
    @NoArgsConstructor
    @AllArgsConstructor
    public class Users {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private UUID id;
        @Column(unique = true, nullable = false)
        @JsonProperty("login")
        private String login;
        @JsonProperty("password")
        private String password;
        private boolean Oauth2;
        private String email;
        @Override
        public String toString() {
            return "Users{" +
                    "id=" + id +
                    ", username='" + login + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        // Обновленные геттеры и сеттеры
        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }



        public boolean isOauth2() {
            return Oauth2;
        }

        public void setOauth2(boolean oauth2) {
            Oauth2 = oauth2;
        }
    }
