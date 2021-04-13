package dev.stratospheric.todoapp.tracing;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableScan
public interface TraceRepository extends CrudRepository<Trace, String> {
  Optional<Trace> findById(String id);
}
