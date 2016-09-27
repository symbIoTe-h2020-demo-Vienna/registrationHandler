package eu.h2020.symbiote;

import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jose on 27/09/16.
 */
@Component("filePlatformInfoReader")
public class FilePlatformInfoReader implements PlatformInfoReader{


    @Override
    public PlatformBean getPlatformInformation() {
        return null;
    }

    @Override
    public List<ResourceBean> getResourcesToRegister() {
        return null;
    }
}
