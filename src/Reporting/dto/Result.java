package Reporting.dto;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Result<T> {
    private final boolean success;
    private final String message;
    private final T data;

    private Result(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(true, null, data);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(false, message, null);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }

    public <R> Result<R> map(Function<? super T, ? extends R> mapper) {
        if (!success) return Result.fail(message);
        return Result.ok(mapper.apply(data));
    }

    public Result<T> ifSuccess(Consumer<? super T> action) {
        if (success) action.accept(data);
        return this;
    }

    public T orElseThrow(Supplier<? extends RuntimeException> exceptionSupplier) {
        if (success) return data;
        throw exceptionSupplier.get();
    }
}
