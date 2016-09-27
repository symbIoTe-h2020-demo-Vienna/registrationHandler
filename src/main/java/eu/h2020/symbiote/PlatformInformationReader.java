package eu.h2020.symbiote;

import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;

/**
 * Created by jose on 26/09/16.
 */

@Component
public class PlatformInformationReader implements CommandLineRunner {

    @Autowired
    private PlarformInfoReader platformReader;


    @Value("${symbiote.core.endpoint}")
    private String coreUrl;


    @Override
    public void run(String... args) throws Exception {

        PlatformBean platformInfo = platformReader.getPlatformInformation();
        List<ResourceBean> resourcesInfo = platformReader.getResourcesToRegister();


        CoreRegistryClient coreClient = Feign.builder().
                encoder(new GsonEncoder()).decoder(new GsonDecoder()).
                target(CoreRegistryClient.class,coreUrl);

        String platformId = coreClient.registerPlatform(platformInfo);
        if (platformId != null) {
            for (ResourceBean resource : resourcesInfo) {
                coreClient.registerResource(platformId,resource);
            }
        }

    }
}
