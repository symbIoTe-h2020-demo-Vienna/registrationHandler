package eu.h2020.symbiote.db;

import eu.h2020.symbiote.beans.NameIdBean;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by jose on 27/09/16.
 */
public interface NameIdRepository<T extends NameIdBean> extends MongoRepository<T, String> {

    T findByName(String name);

}
