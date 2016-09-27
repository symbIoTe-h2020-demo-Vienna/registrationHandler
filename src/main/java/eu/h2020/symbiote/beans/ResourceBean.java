package eu.h2020.symbiote.beans;

/**
 * Created by jose on 26/09/16.
 */
public class ResourceBean extends NameIdBean{

    private String owner;
    private String description;
    private LocationBean location;
    private String observedProperty;

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

    public String getObservedProperty() {
        return observedProperty;
    }

    public void setObservedProperty(String observedProperty) {
        this.observedProperty = observedProperty;
    }
}
