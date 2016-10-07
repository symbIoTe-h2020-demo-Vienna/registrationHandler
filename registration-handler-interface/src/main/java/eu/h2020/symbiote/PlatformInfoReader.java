package eu.h2020.symbiote;

import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;

import java.util.List;

/**
 * Created by jose on 26/09/16.
 */
public interface PlatformInfoReader {

    PlatformBean getPlatformInformation();
    List<ResourceBean> getResourcesToRegister();

}
