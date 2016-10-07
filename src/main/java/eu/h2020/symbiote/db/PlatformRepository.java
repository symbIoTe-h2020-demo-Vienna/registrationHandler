package eu.h2020.symbiote.db;

import eu.h2020.symbiote.beans.PlatformBean;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by jose on 27/09/16.
 */
public interface PlatformRepository extends MongoRepository<PlatformBean, String> {

}
