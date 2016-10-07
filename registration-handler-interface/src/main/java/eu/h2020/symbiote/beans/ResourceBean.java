package eu.h2020.symbiote.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 26/09/16.
 */
public class ResourceBean extends NameIdBean{

    private String owner;
    private String description;
    private LocationBean location;
    private List<String> observedProperties = new ArrayList<String>();
    private String resourceURL;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocationBean getLocation() {
        return location;
    }

    public void setLocation(LocationBean location) {
        this.location = location;
    }

    public List<String> getObservedProperties() {
        return observedProperties;
    }

    public void setObservedProperties(List<String> observedProperties) {
        this.observedProperties = observedProperties;
    }

    public String getResourceURL() {
        return resourceURL;
    }

    public void setResourceURL(String resourceURL) {
        this.resourceURL = resourceURL;
    }

    public void addObservedProperty(String property) {
      observedProperties.add(property);
    }
}
