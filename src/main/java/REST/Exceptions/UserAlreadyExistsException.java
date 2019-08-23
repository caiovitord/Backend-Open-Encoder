package REST.Exceptions;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String name) {
        super("A user with the name: \""+ name+ "\" is already registered ");
    }
}
