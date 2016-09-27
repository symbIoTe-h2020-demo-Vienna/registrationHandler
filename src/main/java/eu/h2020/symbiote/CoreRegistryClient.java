package eu.h2020.symbiote;

import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;

import feign.Param;
import feign.RequestLine;
import feign.Response;

/**
 * Created by jose on 26/09/16.
 */
public interface CoreRegistryClient {

    @RequestLine("POST /register/platform")
    String registerPlatform(PlatformBean platformInfo);

    @RequestLine("POST /register/platform/{platformId}/resources")
    Response registerResource(@Param("platformId") String platformId, ResourceBean resourceInfo);

}
