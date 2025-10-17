package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class User {
    public Long id;
    public String email;
    public String doc_number;
    public String password;
    public String username;
    public String full_name;
    public Boolean loggedin;
    public java.time.LocalDate created_at;
    public java.time.LocalDateTime updated_at;


    public User(Long id, String name, String email, String password, String doc_number, String username, String full_name, Boolean loggedin, java.time.LocalDate created_at, java.time.LocalDateTime updated_at) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.doc_number = doc_number;
        this.username = username;
        this.full_name = full_name;
        this.loggedin = loggedin;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDoc_number() {
        return doc_number;
    }

    public void setDoc_number(String doc_number) {
        this.doc_number = doc_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public Boolean getLoggedin() {
        return loggedin;
    }

    public void setLoggedin(Boolean loggedin) {
        this.loggedin = loggedin;
    }

    public LocalDate getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDate created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "model.User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", doc_number='" + doc_number + '\'' +
                ", username='" + username + '\'' +
                ", full_name='" + full_name + '\'' +
                ", loggedin='" + loggedin + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}
