package eu.h2020.symbiote;

import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;

import feign.RequestLine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jose on 7/10/16.
 */
@Component("openUwedatPlatformInfoReader")
public class NetworkPlatformInfoReader implements PlatformInfoReader {

  @Value("${symbiote.openuwedat.location}")
  private String openUwedatLocation;

  private class ResourcesWrapper {

    private List<ResourceBean> resources;

    public List<ResourceBean> getResources() {
      return resources;
    }

    public void setResources(List<ResourceBean> resources) {
      this.resources = resources;
    }
  }

  private interface RemoteFileClient {

    @RequestLine("GET /system.json")
    PlatformBean getPlatformInformation();

    @RequestLine("GET /resources.json")
    ResourcesWrapper getResources();

  }

  private RemoteFileClient getClient() {
    return RegistrationHandlerApplication.
        createFeignClient(RemoteFileClient.class, openUwedatLocation);
  }

  @Override
  public PlatformBean getPlatformInformation() {
    return getClient().getPlatformInformation();
  }

  @Override
  public List<ResourceBean> getResourcesToRegister() {
    return getClient().getResources().getResources();
  }
}