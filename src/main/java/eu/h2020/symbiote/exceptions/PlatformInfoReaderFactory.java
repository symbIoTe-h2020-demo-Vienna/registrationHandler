package eu.h2020.symbiote.exceptions;

import eu.h2020.symbiote.PlatformInfoReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by jose on 5/10/16.
 */
@Component
public class PlatformInfoReaderFactory {

  @Autowired
  private ApplicationContext ctx;

  public PlatformInfoReader getPlatformInfoReader(String type) {
    String finalType = (type != null) ? type : "filePlatformInfoReader";
    return ctx.getBean(type, PlatformInfoReader.class);
  }

}
