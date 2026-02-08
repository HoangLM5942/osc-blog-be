package app.onestepcloser.blog.exception;

import lombok.Getter;

public final class ErrorMessages {

    private ErrorMessages() {}

    @Getter
    public enum ERROR {
        AUTH_E000("Access denied"),
        AUTH_E001("Invalid token");

        private final String message;

        ERROR(String message) {
            this.message = message;
        }

    }
}
