package zkforge.geolocation;

// Enum to represent error codes and descriptions
enum ErrorType {
    PERMISSION_DENIED(1, "The acquisition of the geolocation information failed because the page didn't have the necessary permissions, for example because it is blocked by a Permissions Policy."),
    POSITION_UNAVAILABLE(2, "The acquisition of the geolocation failed because at least one internal source of position returned an internal error."),
    TIMEOUT(3, "The time allowed to acquire the geolocation was reached before the information was obtained.");

    private final int code;
    private final String description;

    // Constructor for enum
    ErrorType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    // Getter for code
    public int getCode() {
        return code;
    }

    // Getter for description
    public String getDescription() {
        return description;
    }

    // Method to convert enum to GeolocationPositionError
    public GeolocationPositionError toGeolocationPositionError() {
        return new GeolocationPositionError(this.code, this.description);
    }

    // Static method to get ErrorType by code
    public static ErrorType fromCode(int code) {
        for (ErrorType error : values()) {
            if (error.getCode() == code) {
                return error;
            }
        }
        throw new IllegalArgumentException("Invalid error code: " + code);
    }
}
