package it.unipi.CellMap.database.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServerService {

    @Autowired
    private ServerRepository serverRepository;

    // FIXME: MA serve sta classe?

    //@Autowired
    //private RedisTemplate<String, String> redisTemplate;

    private static final String ACTIVE_SERVERS_KEY = "servers:active";

    public Boolean existsById(String id) {
        return serverRepository.existsById(id);
    }

    public List<Server> getServersByIds(List<String> ids) {
        return serverRepository.findAllById(ids); // Should be pipelined by default
    }

    /*
    public boolean isActive(String id) {
        Double score = redisTemplate.opsForZSet().score(ACTIVE_SERVERS_KEY, id);
        return score != null;
    }
     */
}
