package REST.Controllers;

import Persistence.Entities.SimpleUser;
import REST.Exceptions.UserAlreadyExistsException;
import REST.Repositories.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController  {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/users")
    SimpleUser newUser(@RequestBody SimpleUser newUser) {
        try{
            return repository.save(newUser);
        }catch (Exception e){
            throw  new UserAlreadyExistsException(newUser.getUsername());
        }
    }

    @GetMapping("/users")
    List<SimpleUser> findAll() {
        return repository.findAll();
    }

}
