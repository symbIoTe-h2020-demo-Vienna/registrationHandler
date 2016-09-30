package eu.h2020.symbiote;

import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;

import java.util.List;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * Created by jose on 26/09/16.
 */
public interface CoreRegistryClient {

    @RequestLine("POST /cloud_api/platforms")
    @Headers("Content-Type: application/json")
    String registerPlatform(PlatformBean platformInfo);

    @RequestLine("POST /cloud_api/platforms/{platformId}/resources")
    @Headers("Content-Type: application/json")
    List<ResourceBean> registerResource(@Param("platformId") String platformId, List<ResourceBean> resourceInfo);

}
