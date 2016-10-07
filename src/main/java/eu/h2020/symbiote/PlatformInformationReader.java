package eu.h2020.symbiote;

import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;
import eu.h2020.symbiote.exceptions.PlatformInfoReaderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

/**
 * Created by jose on 26/09/16.
 */

@Component
public class PlatformInformationReader implements CommandLineRunner {

  private static final Log logger = LogFactory.getLog(PlatformInformationReader.class);

  @Value("${reghandler.reader.impl}")
  private String readerImplementation;

  @Value("${reghandler.init.autoregister}")
  private boolean autoRegister;

  @Autowired
  private PlatformInfoReaderFactory platformReaderFactory;

  @Autowired
  private PlatformInformationManager platformManager;

  private PlatformInfoReader platformReader;

  @PostConstruct
  private void init() {
    platformReader = platformReaderFactory.getPlatformInfoReader(readerImplementation);
  }

  @Override
  public void run(String... args) throws Exception {

    PlatformBean platformInfo = platformReader.getPlatformInformation();
    List<ResourceBean> resourcesInfo = platformReader.getResourcesToRegister();

    if (platformInfo != null) {
      platformManager.updatePlatformInfo(platformInfo);
    }

    List<ResourceBean> updatedResources = new ArrayList<>();

    if (resourcesInfo != null) {
      updatedResources = platformManager.addOrUpdateResources(resourcesInfo);
    }

    if (autoRegister) {
      platformManager.registerPlatform();
      platformManager.registerResources(updatedResources.stream().map(
          resource -> resource.getInternalId()
      ).collect(Collectors.toList()));
    }

  }
}
