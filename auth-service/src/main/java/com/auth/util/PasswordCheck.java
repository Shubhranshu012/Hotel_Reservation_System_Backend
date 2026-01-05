package com.auth.util;

import com.auth.exception.BadRequestException;

public class PasswordCheck {

    public static void validate(String password){

        if (password.length() < 7) {
            throw new BadRequestException("Password must be at least 7 characters long");
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            } else {
                hasSpecial = true;
            }
        }

        if (!hasUppercase) {
            throw new BadRequestException("Password must contain at least one uppercase letter");
        }
        if (!hasLowercase) {
            throw new BadRequestException("Password must contain at least one lowercase letter");
        }
        if (!hasNumber) {
            throw new BadRequestException("Password must contain at least one number");
        }
        if (!hasSpecial) {
            throw new BadRequestException("Password must contain at least one special character");
        }
    }
}
