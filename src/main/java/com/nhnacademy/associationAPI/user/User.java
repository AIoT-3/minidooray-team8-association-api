package com.nhnacademy.associationAPI.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "users")
public class User {
    public enum Status {
        JOIN, WITHDRAW, DORMANT;

        public static Status fromString(String value){
            for(Status status : Status.values()){
                if(status.name().equalsIgnoreCase(value)){
                    return status;
                }
            }

            throw new IllegalArgumentException("일치하는 상태가 없습니다: " + value);
        }
    }

    public User(String id, String email, String password){
        this.id = id;
        this.email = email;
        this.password = password;
        this.status = Status.JOIN;
    }

    @Id
    @Column(name = "user_id")
    private String id;

    private String email;
    private String password;

    @Setter
    @Enumerated(value = EnumType.STRING)
    private Status status;
}
