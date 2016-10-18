package eu.h2020.symbiote;

import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;
import eu.h2020.symbiote.db.PlatformRepository;
import eu.h2020.symbiote.db.ResourceRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

/**
 * Created by jose on 6/10/16.
 */
@Component
public class PlatformInformationManager {

  private static final Log logger = LogFactory.getLog(PlatformInformationManager.class);

  @Value("${symbiote.core.endpoint}")
  private String coreUrl;

  @Autowired
  private PlatformRepository platformRepository;

  @Autowired
  private ResourceRepository resourceRepository;

  private CoreRegistryClient coreClient;

  @PostConstruct
  private void init() {
    coreClient = RegistrationHandlerApplication.
        createFeignClient(CoreRegistryClient.class, coreUrl);
  }

  public PlatformBean updatePlatformInfo(PlatformBean platformInfo) {

    if (platformInfo != null) {
      List<PlatformBean> platforms = platformRepository.findAll();
      String symbioteId = null;
      if (platforms != null) {
        for (PlatformBean platform : platforms) {
          if (platform.getSymbioteId() != null) {
            symbioteId = platform.getSymbioteId();
          }
          platformRepository.delete(platform);
        }
      }

      platformInfo.setSymbioteId(symbioteId);
      return platformRepository.save(platformInfo);
    }

    return null;

  }

  public ResourceBean addOrUpdateResource(ResourceBean resource) {

    if (resource != null) {

      if (resource.getId() != null) {
        resource.setSymbioteId(resource.getId());
      }

      String resourceId = resource.getResourceURL();
      if (resourceId != null) {
        ResourceBean existing = resourceRepository.getByResourceURL(resourceId);
        if (existing != null) {
          resource.setInternalId(existing.getInternalId());
          if (resource.getSymbioteId() == null) {
            resource.setSymbioteId(existing.getSymbioteId());
          }
        }
      }

      return resourceRepository.save(resource);
    }
    return null;
  }

  public List<ResourceBean> addOrUpdateResources(List<ResourceBean> resources) {
    return resources.stream().map(resource -> addOrUpdateResource(resource))
        .filter(resource -> resource != null).collect(Collectors.toList());
  }

  public PlatformBean getPlatformInfo() {
    List<PlatformBean> platforms = platformRepository.findAll();
    if (platforms != null && !platforms.isEmpty()) {
      return platforms.get(0);
    } else {
      return null;
    }
  }

  public List<ResourceBean> getResources() {
    return resourceRepository.findAll();
  }

  public PlatformBean registerPlatform() {
    PlatformBean info = getPlatformInfo();
    if (info != null) {
      PlatformBean newPlatform = coreClient.registerPlatform(info);
      if (newPlatform != null && newPlatform.getId() != null) {
        info.setSymbioteId(newPlatform.getId());
        return platformRepository.save(info);
      }
    }
    return info;
  }

  public List<ResourceBean> registerResources(List<String> resourceIds) {

    PlatformBean platformInfo = getPlatformInfo();
    List<ResourceBean> resources = new ArrayList<>();

    if (platformInfo != null) {
      if (platformInfo.getSymbioteId() != null) {

        for (String id : resourceIds) {
          ResourceBean resource = resourceRepository.findOne(id);
          if (resource != null) {
            resources.add(resource);
          }
        }

        resources = coreClient.registerResource(platformInfo.getSymbioteId(), resources);

        return addOrUpdateResources(resources);
      }
    }

    return resources;
  }

}
