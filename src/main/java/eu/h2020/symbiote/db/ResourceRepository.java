package eu.h2020.symbiote.db;

import eu.h2020.symbiote.beans.ResourceBean;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by jose on 27/09/16.
 */
public interface ResourceRepository extends MongoRepository<ResourceBean, String> {

  ResourceBean getByResourceURL(String resourceId);

}
