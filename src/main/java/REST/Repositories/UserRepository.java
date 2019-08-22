package REST.Repositories;

import Persistence.Entities.SimpleUser;
import org.springframework.data.jpa.repository.JpaRepository;

public class UserRepository  implements JpaRepository<SimpleUser, String> {
}
