/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.jenavirtuoso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
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
public class RAP {

    public static void main(String[] args) {
        //2016-09-29T11:31:29.563
        SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

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
        String sensorId = "http://lsm.deri.ie/resource/406576468110395";
        String query = "select ?s ?unit ?value ?label ?time\n"
                + "from <http://lsm.deri.ie/OpenIoT/sensordata#>\n"
                + "where{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#ObservationValue>.\n";
        if (sensorId != null) {
            query = query + "?s <http://openiot.eu/ontology/ns/isObservedValueOf> ?obs. \n"
                    + "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <" + sensorId + "> .\n";
        }
        query = query + "?s <http://openiot.eu/ontology/ns/unit> ?unit .\n"
                + "?s <http://openiot.eu/ontology/ns/value> ?value.\n"
                + "?s <http://www.w3.org/2000/01/rdf-schema#label> ?label.\n"
                + "?s <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time.\n"
                + "}\n"
                + "order by desc(?time)";
        Query sparql = QueryFactory.create(query);

        /*			STEP 3			*/
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, set);

        Map<Long, Double> latitude = new HashMap<>();//timestamp, coordinate
        Map<Long, Double> longitude = new HashMap<>();//timestamp, coordinate
        Set<Observation> observations = new HashSet<>();

        ResultSet results = vqe.execSelect();
        while (results.hasNext()) {
            QuerySolution result = results.nextSolution();
            RDFNode id = result.get("s");
            RDFNode UOMSymbol = result.get("unit");
            RDFNode obsval = result.get("value");
            RDFNode obsProp = result.get("label");
            RDFNode time = result.get("time");
            try {
                if (obsProp.toString().equals("Longitude")) {
                    longitude.put(parserSDF.parse(time.asLiteral().getString()).getTime(), (Double) obsval.asLiteral().getValue());
                } else if (obsProp.toString().equals("Latitude")) {
                    latitude.put(parserSDF.parse(time.asLiteral().getString()).getTime(), (Double) obsval.asLiteral().getValue());
                } else {

                    Observation o = new Observation(id.toString(), parserSDF.parse(time.asLiteral().getString()).getTime(), parserSDF.parse(time.asLiteral().getString()).getTime(), "", obsProp.toString(), (Double) obsval.asLiteral().getValue(), "UOMName", UOMSymbol.toString());
                    observations.add(o);
                }

            } catch (ParseException e) {
                e.printStackTrace();
                break;
            }
        }

        for (Observation o : observations) {
            Double lon = longitude.get(o.resultTime);
            Double lat = latitude.get(o.resultTime);

            if (lon == null || lat == null) {
                System.out.println(o.toString());
            } else {
                o.setLocation(new Location(longitude.get(o.resultTime), latitude.get(o.resultTime), 0));
                //System.out.println(o.toString());
            }
        }
        
        System.out.println(observations.size());

    }

    private static class Observation {

        String Id;//platform-specific Id
        long samplingTime;
        long resultTime;
        Location location;
        String FOI;
        String observationProp;
        Object observationValue;
        String UOMName;
        String UOMSymbol;

        public Observation(String Id, long samplingTime, long resultTime, Location location, String FOI, String observationProp, Object observationValue, String UOMName, String UOMSymbol) {
            this.Id = Id;
            this.samplingTime = samplingTime;
            this.resultTime = resultTime;
            this.location = location;
            this.FOI = FOI;
            this.observationProp = observationProp;
            this.observationValue = observationValue;
            this.UOMName = UOMName;
            this.UOMSymbol = UOMSymbol;
        }

        public Observation(String Id, long samplingTime, long resultTime, String FOI, String observationProp, Object observationValue, String UOMName, String UOMSymbol) {
            this.Id = Id;
            this.samplingTime = samplingTime;
            this.resultTime = resultTime;
            this.FOI = FOI;
            this.observationProp = observationProp;
            this.observationValue = observationValue;
            this.UOMName = UOMName;
            this.UOMSymbol = UOMSymbol;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        @Override
        public String toString() {
            return "Observation{" + "Id=" + Id + ", samplingTime=" + samplingTime + ", resultTime=" + resultTime + ", location=" + location + ", FOI=" + FOI + ", observationProp=" + observationProp + ", observationValue=" + observationValue + ", UOMName=" + UOMName + ", UOMSymbol=" + UOMSymbol + '}';
        }

    }

    private static class Location {

        double longitude;
        double latitude;
        double altitude;

        public Location(double longitude, double latitude, double altitude) {
            this.longitude = longitude;
            this.latitude = latitude;
            this.altitude = altitude;
        }

        @Override
        public String toString() {
            return "Location{" + "longitude=" + longitude + ", latitude=" + latitude + ", altitude=" + altitude + '}';
        }

    }
}
