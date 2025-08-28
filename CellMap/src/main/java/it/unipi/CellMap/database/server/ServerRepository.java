package it.unipi.CellMap.database.server;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends KeyValueRepository<Server, String> {

}
