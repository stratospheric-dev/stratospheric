package com.aws101.todo;

import org.springframework.data.repository.CrudRepository;

public interface TodoRepository extends CrudRepository<Todo, Long> {
}
