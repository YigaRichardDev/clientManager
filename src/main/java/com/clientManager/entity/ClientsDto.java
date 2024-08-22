package com.clientManager.entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class ClientsDto {
    @NotEmpty(message = "Name is required!")
    private String name;
    @NotEmpty(message = "Phone number is required!")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String phone;
    private MultipartFile profile;

    public @NotEmpty(message = "Name is required!") String getName() {
        return name;
    }

    public void setName(@NotEmpty(message = "Name is required!") String name) {
        this.name = name;
    }

    public @NotEmpty(message = "Phone number is required!") @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits") String getPhone() {
        return phone;
    }

    public void setPhone(@NotEmpty(message = "Phone number is required!") @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits") String phone) {
        this.phone = phone;
    }

    public MultipartFile getProfile() {
        return profile;
    }

    public void setProfile(MultipartFile profile) {
        this.profile = profile;
    }
}
