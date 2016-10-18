package eu.h2020.symbiote;

import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;
import eu.h2020.symbiote.exceptions.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jose on 27/09/16.
 */
@RestController
public class RegistrationHandlerRestService {

  @Autowired
  private PlatformInformationManager infoManager;

  @RequestMapping(method = RequestMethod.GET, path = "/platform")
  public PlatformBean getPlatformInfo() throws NotFoundException {
    return infoManager.getPlatformInfo();
  }

  @RequestMapping(method = RequestMethod.GET, path = "/resource")
  public List<ResourceBean> getResources() {
    return infoManager.getResources();
  }

  @RequestMapping(method = RequestMethod.POST, path = "/resource")
  public ResourceBean addResources(@RequestBody ResourceBean resource) {
    return infoManager.addOrUpdateResource(resource);
  }

  @RequestMapping(method = RequestMethod.PUT, path = "/resource")
  public ResourceBean updateResources(@RequestBody ResourceBean resource) {
    return infoManager.addOrUpdateResource(resource);
  }

  @RequestMapping(method = RequestMethod.PUT, path = "/platform")
  public PlatformBean updatePlatformInfo(@RequestBody PlatformBean platform) {
    return infoManager.updatePlatformInfo(platform);
  }

  @RequestMapping(method = RequestMethod.POST, path = "/platform/publish")
  public PlatformBean publishPlatform() throws NotFoundException {
    return infoManager.registerPlatform();
  }

  @RequestMapping(method = RequestMethod.POST, path = "/resource/publish/{resourceId}")
  public ResourceBean publishResource(@PathVariable String resourceId) throws NotFoundException {
    List<ResourceBean> result = infoManager.registerResources(
        Arrays.asList(new String[]{resourceId}));
    if (!result.isEmpty()) {
      return result.get(0);
    }

    return null;
  }

  @RequestMapping(method = RequestMethod.POST, path = "/resource/publishAll",
      consumes = "application/json")
  public List<ResourceBean> publishResources(@RequestBody List<String> resourceIds)
      throws NotFoundException {
    return infoManager.registerResources(resourceIds);
  }

}
