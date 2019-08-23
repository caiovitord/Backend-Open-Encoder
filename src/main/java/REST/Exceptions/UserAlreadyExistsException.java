package REST.Exceptions;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String name) {
        super("A user with this name is already registered " + name);
    }
}
