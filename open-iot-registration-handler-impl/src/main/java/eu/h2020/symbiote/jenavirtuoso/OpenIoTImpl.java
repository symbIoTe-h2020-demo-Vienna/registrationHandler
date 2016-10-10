package eu.h2020.symbiote.jenavirtuoso;

import eu.h2020.symbiote.PlatformInfoReader;
import eu.h2020.symbiote.beans.LocationBean;
import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Aleksandar
 */
@Component("openIoTPlatformInfoReader")
public class OpenIoTImpl implements PlatformInfoReader {

    @Value("${symbiote.openiot.rap.url}")
    private String rapUrl;

    @Value("${symbiote.openiot.virtuoso.url}")
    private String virtuosoUrl;

    @Override
    public PlatformBean getPlatformInformation() {
        //NOTE/TODO: we should use some constructor to do initialization
        PlatformBean instance = new PlatformBean();
        instance.setOwner("UNIZG-FER");
        instance.setType("OpenIoT");
        instance.setName("OpenIoT");
        instance.setResourceAccessProxyUrl(rapUrl);

        return instance;
    }

    @Override
    public List<ResourceBean> getResourcesToRegister() {
        return getResources();
    }

    private List<ResourceBean> getResources() {
        LinkedList<ResourceBean> resources = new LinkedList<ResourceBean>();

        String url = virtuosoUrl;

        VirtGraph set = new VirtGraph(url, "dba", "dba");
        //get all sensor Ids that have at least one Observation 
        String initQuery = "SELECT distinct ?sensorId \n"
                + "from <http://lsm.deri.ie/OpenIoT/sensordata#> \n"
                + "where { "
                + "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> ?sensorId ."
                + " }";
        Query sparql = QueryFactory.create(initQuery);
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, set);

        Set<String> ids = new HashSet<>();
        ResultSet results = vqe.execSelect();
        while (results.hasNext()) {
            QuerySolution result = results.nextSolution();
            RDFNode sensorId = result.get("sensorId");
            ids.add(sensorId.toString());
        }

        //get sensor details from meta grapj
        String sensorDetailsQuery = "SELECT distinct ?sensorId ?SensorName ?ObservedProperty ?GeoLocation ?type \n"
                + "from <http://lsm.deri.ie/OpenIoT/sensormeta#> \n"
                + "where {?sensorId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type. \n"
                + "?sensorId <http://www.w3.org/2000/01/rdf-schema#label> ?SensorName . \n"
                + "?sensorId     <http://purl.oclc.org/NET/ssnx/ssn#observes> ?ObservedProp . \n"
                + "?ObservedProp ?c ?ObservedProperty .\n"
                + "?sensorId <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?loc.\n"
                + "?loc <http://www.w3.org/2003/01/geo/wgs84_pos#geometry> ?GeoLocation .  "
                + "}\n";

        sparql = QueryFactory.create(sensorDetailsQuery);
        vqe = VirtuosoQueryExecutionFactory.create(sparql, set);

        Map<String, ResourceBean> sensorCollection = new HashMap<>();//<sensorId, Resource>
        results = vqe.execSelect();
        while (results.hasNext()) {
            QuerySolution result = results.nextSolution();
            RDFNode sensorId = result.get("sensorId");

            //sensorId is associated with some existing observations, if not then just skip this sensor since it has no observations
            if (ids.contains(sensorId.toString())) {
                RDFNode sensorName = result.get("SensorName");
                RDFNode obsProp = result.get("ObservedProperty");
                RDFNode position = result.get("GeoLocation");
                RDFNode type = result.get("type");

                ResourceBean sensor = sensorCollection.get(sensorId.toString());

                if (sensor != null) {//sensor is already created, it needs only to be extended with additional Observation Proprerty
                    if (!(obsProp.toString().contains("Longitude") || obsProp.toString().contains("Latitude"))) {
                        sensor.addObservedProperty(obsProp.toString().substring(obsProp.toString().lastIndexOf("/") + 1));
                    }
                } else {//create new sensor
                    sensor = new ResourceBean();

                    sensor.setOwner("UNIZG-FER");

                    sensor.setDescription(type.toString());

                    LocationBean location = new LocationBean();
                    sensor.setLocation(locationParser(sensorName.toString().substring(3), "MGRS cell", position.toString()));

                    if (!(obsProp.toString().contains("Longitude") || obsProp.toString().contains("Latitude"))) {
                        sensor.addObservedProperty(obsProp.toString().substring(obsProp.toString().lastIndexOf("/") + 1));
                    }

                    sensor.setName(sensorId.toString().substring(sensorId.toString().lastIndexOf("/") + 1));
                    sensor.setResourceURL(rapUrl+"/Sensors('"+sensorId.toString().substring(sensorId.toString().lastIndexOf("/") + 1)+"')");

                    sensorCollection.put(sensorId.toString(), sensor);
                }
            }
        }

        for (String sensorId : sensorCollection.keySet()) {
            System.out.println(sensorCollection.get(sensorId));
            resources.add(sensorCollection.get(sensorId));
        }
        
        return resources;
    }

    //expected format for location is: POINT(<longitude> <latitude>)
    private LocationBean locationParser(String name, String description, String position) {
        String[] coord = position.substring(position.indexOf("(") + 1, position.indexOf(")")).split(" ");

        LocationBean location = new LocationBean();
        location.setName(name);
        location.setDescription(description);
        location.setLongitude(Double.parseDouble(coord[0]));
        location.setLatitude(Double.parseDouble(coord[1]));
        location.setAltitude(0d);
        return location;
    }
}
