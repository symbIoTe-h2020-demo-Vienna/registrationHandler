package eu.h2020.symbiote;

import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;
import eu.h2020.symbiote.db.PlatformRepository;
import eu.h2020.symbiote.db.ResourceRepository;
import eu.h2020.symbiote.exceptions.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by jose on 27/09/16.
 */
@RestController
public class RegistrationHandlerRestService {

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/platformInfo")
    public PlatformBean getPlatformInfo() throws NotFoundException {
        List<PlatformBean> platformBean = platformRepository.findAll();
        if (!platformBean.isEmpty()) {
            return platformBean.get(0);
        } else {
            throw new NotFoundException();
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/resourceList")
    public List<ResourceBean> getResources() {
        return resourceRepository.findAll();
    }

}
