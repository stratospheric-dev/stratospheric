package dev.stratospheric.todo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
  List<Todo> findAllByOwnerEmailOrderByIdAsc(String email);

  List<Todo> findAllByCollaboratorsEmailOrderByIdAsc(String email);

  Optional<Todo> findByIdAndOwnerEmail(Long todoId, String todoOwnerEmail);
}
