package eu.h2020.symbiote;

import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;
import eu.h2020.symbiote.db.PlatformRepository;
import eu.h2020.symbiote.db.ResourceRepository;
import eu.h2020.symbiote.exceptions.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.PostConstruct;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;

/**
 * Created by jose on 27/09/16.
 */
@RestController
public class RegistrationHandlerRestService {

    @Value("${symbiote.core.endpoint}")
    private String coreUrl;

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    private CoreRegistryClient coreClient;

    @PostConstruct
    private void init() {
        coreClient = Feign.builder().
                encoder(new GsonEncoder()).decoder(new GsonDecoder()).
                target(CoreRegistryClient.class,coreUrl);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/platform")
    public PlatformBean getPlatformInfo() throws NotFoundException {
        List<PlatformBean> platformBean = platformRepository.findAll();
        if (!platformBean.isEmpty()) {
            return platformBean.get(0);
        } else {
            throw new NotFoundException();
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/resource")
    public List<ResourceBean> getResources() {
        return resourceRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/resource")
    public ResourceBean addResources(ResourceBean resource) {
        return resourceRepository.save(resource);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/resource")
    public ResourceBean updateResources(ResourceBean resource) {
        return resourceRepository.save(resource);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/platform")
    public PlatformBean updatePlatformInfo(PlatformBean platform) {
        try {
            PlatformBean existing = getPlatformInfo();
            platform.setId(existing.getId());
            return platformRepository.save(platform);
        } catch (NotFoundException e) {
            return platformRepository.save(platform);
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/platform")
    public String publishPlatform() throws NotFoundException {
        PlatformBean platform = getPlatformInfo();
        return coreClient.registerPlatform(platform);
    }
}
