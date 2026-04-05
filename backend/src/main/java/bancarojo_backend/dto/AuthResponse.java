package bancarojo_backend.dto;

public record AuthResponse(
        String token,
        String tokenType,
        String email,
        String firstName,
        String lastName,
        String role
) {
    public static AuthResponse of(String token, String email,
                                  String firstName, String lastName,
                                  String role) {
        return new AuthResponse(token, "Bearer", email, firstName, lastName, role);
    }
}