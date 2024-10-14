package veci.veciback.repository;

import veci.veciback.model.Transaccion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaccionRepository extends MongoRepository<Transaccion, String> {}