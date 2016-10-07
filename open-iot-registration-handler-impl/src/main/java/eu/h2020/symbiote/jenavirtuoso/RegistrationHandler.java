/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.jenavirtuoso;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

/**
 *
 * @author Aleksandar
 */
public class RegistrationHandler {

    public static void main(String[] args) {

        String url;
        if (args.length == 0) {
            url = "jdbc:virtuoso://161.53.19.121:1111";
        } else {
            url = args[0];
        }

        /*			STEP 1			*/
        VirtGraph set = new VirtGraph(url, "dba", "dba");

        /*			STEP 2			*/
 /*		Select data in virtuoso	*/
//get all sensor Ids that have at least one Observation 
        String initQuery = "SELECT distinct ?sensorId \n"
                + "from <http://lsm.deri.ie/OpenIoT/sensordata#> \n"
                + "where { "
                + "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> ?sensorId ."
                + " }";
        Query sparql = QueryFactory.create(initQuery);

        /*			STEP 3			*/
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, set);

        Set<String> ids = new HashSet<>();
        ResultSet results = vqe.execSelect();
        while (results.hasNext()) {
            QuerySolution result = results.nextSolution();
            RDFNode sensorId = result.get("sensorId");
            ids.add(sensorId.toString());
            //System.out.println(" " + sensorId);

        }

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

        /*			STEP 3			*/
        vqe = VirtuosoQueryExecutionFactory.create(sparql, set);

        Map<String, Sensor> sensorCollection = new HashMap<>();//<sensorId, Sensor>
        results = vqe.execSelect();
        while (results.hasNext()) {
            QuerySolution result = results.nextSolution();
            RDFNode sensorId = result.get("sensorId");

            //sensorId is associated with some existing observations
            if (ids.contains(sensorId.toString())) {
                RDFNode sensorName = result.get("SensorName");
                RDFNode obsProp = result.get("ObservedProperty");
                RDFNode location = result.get("GeoLocation");
                RDFNode type = result.get("type");

                Sensor sensor = sensorCollection.get(sensorId.toString());
                if (sensor != null) {//sensor is already created, it needs only to be extended with additional Observation Proprerty
                    //if (!(obsProp.toString().contains("Longitude") || obsProp.toString().contains("Latitude"))) {
                        sensor.addObservedProperty(obsProp.toString());
                    //}
                    //System.err.println(sensor);
                } else {//create new sensor
                    sensor = new Sensor(sensorId.toString(), sensorName.toString(), type.toString(), "UNIZG-FER", obsProp.toString(), new Location(sensorName.toString().substring(3), "MGRS cell", location.toString()));
                    sensorCollection.put(sensorId.toString(), sensor);

                    //System.out.println(" " + sensorId + " " + sensorName + " " + obsProp + " " + location.asLiteral().getString() + " " + type);
                    //System.err.println(sensor);
                }
            }
        }

        for (String sensorId : sensorCollection.keySet()) {
            System.out.println(sensorCollection.get(sensorId));
        }
    }

    private static class Sensor {

        String nativeId;//platform-specific Id
        String sensorName;
        String description;
        String owner;
        List<String> observedProperty;
        Location location;

        public Sensor(String nativeId, String sensorName, String description, String owner, String observedProperty, Location location) {
            this.nativeId = nativeId;
            this.sensorName = sensorName;
            this.description = description;
            this.owner = owner;
            this.observedProperty = new LinkedList<>();
            this.observedProperty.add(observedProperty);
            this.location = location;

        }

        @Override
        public String toString() {
            return "Sensor{ " + " \n\t nativeId=" + nativeId + ", \n\t sensorName=" + sensorName + ", \n\t description=" + description + ", \n\t owner=" + owner + ", \n\t observedProperty=" + observedProperty + ", \n\t location=" + location + " \n}";
        }

        public boolean addObservedProperty(String observedProperty) {
            return this.observedProperty.add(observedProperty);
        }

    }

    private static class Location {

        String name;
        String description;
        double longitude;
        double latitude;
        double altitude;

        public Location(String name, String description, double longitude, double latitude, double altitude) {
            this.name = name;
            this.description = description;
            this.longitude = longitude;
            this.latitude = latitude;
            this.altitude = altitude;
        }

        //expected format for location is: POINT(<longitude> <latitude>)
        public Location(String name, String description, String location) {
            this.name = name;
            this.description = description;
            String[] coord = location.substring(location.indexOf("(") + 1, location.indexOf(")")).split(" ");
            this.longitude = Double.parseDouble(coord[0]);
            this.latitude = Double.parseDouble(coord[1]);
            this.altitude = 0;
        }

        @Override
        public String toString() {
            return "Location{" + "name=" + name + ", description=" + description + ", longitude=" + longitude + ", latitude=" + latitude + ", altitude=" + altitude + '}';
        }

    }
}
