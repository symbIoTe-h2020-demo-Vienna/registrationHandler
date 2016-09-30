package eu.h2020.symbiote;

import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;
import eu.h2020.symbiote.db.PlatformRepository;
import eu.h2020.symbiote.db.ResourceRepository;
import eu.h2020.symbiote.exceptions.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
            platform.setInternalId(existing.getInternalId());
            return platformRepository.save(platform);
        } catch (NotFoundException e) {
            return platformRepository.save(platform);
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/platform/publish")
    public PlatformBean publishPlatform() throws NotFoundException {
        PlatformBean platform = getPlatformInfo();
        String symbioteId = coreClient.registerPlatform(platform);
        platform.setSymbioteId(symbioteId);
        return platformRepository.save(platform);
    }

    private ResourceBean doPublishResource(String platformId, String resourceId) {
        ResourceBean resource = resourceRepository.findOne(resourceId);
        if (resource != null) {
            ArrayList<ResourceBean> toRegister = new ArrayList<>();
            toRegister.add(resource);
            List<ResourceBean> registered = coreClient.registerResource(platformId,toRegister);
            if (registered != null && registered.size() > 0) {
                ResourceBean result = registered.get(0);

                resource.setSymbioteId(result.getId());

                return resourceRepository.save(resource);
            }
        }

        return null;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/resource/publish/{resourceId}")
    public ResourceBean publishResource(@PathVariable String resourceId) throws NotFoundException {
        PlatformBean platform = getPlatformInfo();
        if (platform.getSymbioteId() != null) {
            return doPublishResource(platform.getSymbioteId(), resourceId);
        } else {
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/resource/publishAll",
            consumes = "application/json")
    public List<ResourceBean> publishResources(@RequestBody List<String> resourceIds)
            throws NotFoundException {

        List<ResourceBean> result = new ArrayList<>();
        if (resourceIds != null) {
            for (String resourceId : resourceIds) {
                ResourceBean registerResult = publishResource(resourceId);
                if (registerResult != null) {
                    result.add(registerResult);
                }
            }
        }
        return result;
    }

}
