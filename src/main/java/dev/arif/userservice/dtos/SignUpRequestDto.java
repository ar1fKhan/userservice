package dev.arif.userservice.dtos;

public class SignUpRequestDto {
    private String email;
    private String password;

    public SignUpRequestDto() {
    }

    public SignUpRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public SignUpRequestDto(SignUpRequestDto signUpRequestDto) {
        this.email = signUpRequestDto.getEmail();
        this.password = signUpRequestDto.getPassword();
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
