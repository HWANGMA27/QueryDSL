package study.querydsl.dto;

import lombok.Data;

@Data
public class UserDTO {

    private String name;
    private int age;

    public UserDTO() {
    }

    public UserDTO(String username, int age) {
        this.name = username;
        this.age = age;
    }
}
