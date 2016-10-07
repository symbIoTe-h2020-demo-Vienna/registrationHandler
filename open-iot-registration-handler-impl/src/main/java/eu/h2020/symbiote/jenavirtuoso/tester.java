/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.jenavirtuoso;


/**
 *
 * @author Aleksandar
 */
public class tester {

    public static void main(String[] args) {

        OpenIoTImpl o = new OpenIoTImpl();
        o.getResourcesToRegister();
        
//        String url;
//        if (args.length == 0) {
//            url = "jdbc:virtuoso://161.53.19.121:1111";
//        } else {
//            url = args[0];
//        }
//
//        /*			STEP 1			*/
//        VirtGraph set = new VirtGraph(url, "dba", "dba");
//
//        /*			STEP 2			*/
// /*			STEP 3			*/
// /*		Select all data in virtuoso	*/
//        Query sparql = QueryFactory.create("SELECT *"
//
//
//                + "from <http://lsm.deri.ie/OpenIoT/sensormeta#>\n" 
//                + "where {?sensorId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type.\n" 
//                + "?sensorId <http://www.w3.org/2000/01/rdf-schema#label> ?SensorName .\n" 
//                + "?sensorId     <http://purl.oclc.org/NET/ssnx/ssn#observes> ?ObservedProp .\n" 
//                + "?ObservedProp ?c ?ObservedProperty .\n" 
//                + "?sensorId <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?loc.\n" 
//                + "?loc <http://www.w3.org/2003/01/geo/wgs84_pos#geometry> ?GeoLocation .  "
//                + "}\n" 
//
//        );
//
//        /*			STEP 4			*/
//        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, set);
//
//        ResultSet results = vqe.execSelect();
//        while (results.hasNext()) {
//            QuerySolution result = results.nextSolution();
//            RDFNode graph = result.get("obs");
//            RDFNode s = result.get("sensorId");
//            //RDFNode p = result.get("p");
//            //RDFNode o = result.get("o");
//            System.out.println(graph + " { " + s +  " . }");
//        }
    }
}