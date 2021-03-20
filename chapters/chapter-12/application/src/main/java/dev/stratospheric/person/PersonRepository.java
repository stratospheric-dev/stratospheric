package dev.stratospheric.person;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
  Optional<Person> findByName(String name);

  Optional<Person> findByEmail(String email);

  List<Person> findByEmailNot(String email);
}
