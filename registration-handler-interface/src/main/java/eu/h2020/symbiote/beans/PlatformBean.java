package eu.h2020.symbiote.beans;

/**
 * Created by jose on 26/09/16.
 */
public class PlatformBean extends NameIdBean{

    private String owner;
    private String type;
    private String resourceAccessProxyUrl;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResourceAccessProxyUrl() {
        return resourceAccessProxyUrl;
    }

    public void setResourceAccessProxyUrl(String resourceAccessProxyUrl) {
        this.resourceAccessProxyUrl = resourceAccessProxyUrl;
    }
}
