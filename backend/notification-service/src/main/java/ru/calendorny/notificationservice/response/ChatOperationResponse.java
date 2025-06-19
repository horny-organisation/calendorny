package ru.calendorny.notificationservice.response;

public record ChatOperationResponse<T>(boolean success, String message, T data) {

    public static <T> ChatOperationResponse<T> success(T data) {
        return new ChatOperationResponse<>(true, null, data);
    }

    public static <T> ChatOperationResponse<T> error(String message) {
        return new ChatOperationResponse<>(false, message, null);
    }
}
