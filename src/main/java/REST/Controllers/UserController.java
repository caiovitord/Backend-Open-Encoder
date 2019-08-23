package REST.Controllers;

import Persistence.Entities.SimpleUser;
import REST.Repositories.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController  {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/users")
    SimpleUser newUser(@RequestBody SimpleUser newUser) {
        return repository.save(newUser);
    }


}
