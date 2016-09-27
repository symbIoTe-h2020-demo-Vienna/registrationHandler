package eu.h2020.symbiote;

import eu.h2020.symbiote.beans.NameIdBean;
import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;
import eu.h2020.symbiote.db.NameIdRepository;
import eu.h2020.symbiote.db.PlatformRepository;
import eu.h2020.symbiote.db.ResourceRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 26/09/16.
 */

@Component
public class PlatformInformationReader implements CommandLineRunner {

    private static final Log logger = LogFactory.getLog(PlatformInformationReader.class);

    @Autowired
    private PlatformInfoReader platformReader;

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Value("${symbiote.core.endpoint}")
    private String coreUrl;


    private <T extends NameIdBean> T insertOrUpdate(NameIdRepository<T> repo, T bean) {

        T found = repo.findByName(bean.getName());
        if (found != null) {
            logger.info("Updating element "+bean.getName());
            bean.setId(found.getId());
        }
        logger.info("Saving element "+bean.getName());
        return repo.save(bean);

    }

    private <T extends NameIdBean> List<T> insertOrUpdate(NameIdRepository<T> repo,
                                                          Iterable<T> beans) {
        List<T> result = new ArrayList<T>();
        beans.forEach(bean -> result.add(insertOrUpdate(repo, bean)));
        return result;
    }

    @Override
    public void run(String... args) throws Exception {

        PlatformBean platformInfo = platformReader.getPlatformInformation();
        List<ResourceBean> resourcesInfo = platformReader.getResourcesToRegister();


        if (platformInfo != null) {
           insertOrUpdate(platformRepository, platformInfo);
        }

        insertOrUpdate(resourceRepository, resourcesInfo);

    }
}
