package dev.stratospheric.person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

  Optional<Person> findByUsername(String username);

  Optional<Person> findByEmail(String email);

  List<Person> findByUsernameNot(String username);

  List<Person> findByEmailNot(String email);
}
