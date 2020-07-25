package dev.aws101.person;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

  Optional<Person> findByName(String name);

  Optional<Person> findByEmail(String email);
}
