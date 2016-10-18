# SymbIoTe Registration Handler Component

This project is part of the Interworking API for SymbIoTe H2020 project. It's main aim is to get resource data from different platforms and allow the registration of this resources into an instance of SymbIoTe Core.

## Data gathering

In order to read data from the platform, several methods are provided:

## Java Interface

For Java clients, it's easy to provide the data to the Registration Handler Component. 

The registration-handler-interface subproject provides an interface that should be implemented by the platform owner. It should provide the description of the platform and the available resources by implementing the PlatformInfoReader interface:

- `PlatformBean getPlatformInformation()`: Get the platform information such as name and Resource Access Proxy base URL
- `List<ResourceBean> getResourcesToRegister()`: Should return a list of resources available in the platform. Each resource should contain information like location, observed properties and Resource Access Proxy full URL

Taking advantage of Spring Boot, the implemented component can be annotated with `@Component` and given a name. Then it should be put inside the Registration Handler jar classpath so it can find it and it should be referenced in the properties configuration as the `reghandler.reader.impl` property.

For example, let's take the default provided component `FilePlatformInfoReader`

```java
@Component("filePlatformInfoReader")
public class FilePlatformInfoReader implements PlatformInfoReader{

    private static final Log logger = LogFactory.getLog(FilePlatformInfoReader.class);

    private static final String PLATFORM_FILE_NAME = "platform.json";
    private static final String RESOURCES_FILE_NAME = "resources.json";

    @Value("${symbiote.platform.file.location}")
    String fileLocation;

    @Override
    public PlatformBean getPlatformInformation() {

        File platformFile = new File(fileLocation+"/"+PLATFORM_FILE_NAME);

        Gson reader = new Gson();

        try {
            return reader.fromJson(new FileReader(platformFile),
                    PlatformBean.class);
        } catch (FileNotFoundException e) {
            logger.error("Error reading platform info file", e);
        }

        return null;
    }

    @Override
    public List<ResourceBean> getResourcesToRegister() {

        Gson reader = new Gson();
        File resourcesFile = new File(fileLocation+"/"+RESOURCES_FILE_NAME);

        Type listType = new TypeToken<ArrayList<ResourceBean>>(){}.getType();

        try {
            return reader.fromJson(new FileReader(resourcesFile), listType);
        } catch (FileNotFoundException e) {
            logger.error("Error reading resource file", e);
        }

        return new ArrayList<>();
    }
}
```

It implements the interface and reads two JSON files, one with the platform information and another one with the resource list. This files will can be stored in a configurable location defined in the `symbiote.platform.file.location` property.

The component is annotated and as such it will be loaded by Spring Boot under the name `filePlatformInfoReader`, now if we specify this value for the `reghandler.reader.impl` property as `filePlatformInfoReader` value, it will be picked up as the initial information feeder. 

Any other component can be loaded in the same way just implementing the interface and giving it a name.

## REST Interface

To allow interoperability with non Java implementations, the Registration Handler provides also a REST interface that can be used to add, remove and update resources information and to publish or unpublish them.

These are the operations:

- `GET /platform`: It will return the current information that was registered for the platform
- `GET /resource`: Get a list of available resources, both registered within SymbIoTe core and not registered ones
- `POST /resource`: Add information about a new resource. The resource information should be passed in the POST body payload. If successfully added, it will return the sensor information back with an internal id. Notice that it will NOT register this resource with SymbIoTe core yet.
- `PUT /resource`: Update the information about an existing resource. It will not check that the resource is pre-existing so in fact POST and PUT will behave the same by adding and updating the resource information depending on the pressence of an internal id in the request information.
- `PUT /platform`: Add or update the information about the platform. It expects the platform information in JSON in the message body.
- `POST /platform/publish`: Publish the information saved about the platform to SymbIoTe Core. It will return the platform information plus a symbioteId element, with the internal SymbIoTe identifier assigned by the core.
- `POST /resource/publish/{resourceId}`: Publish a particular resource to SymbIoTe core passing its internal identifier as a path parameter. If successful, it returns the resource information plus a symbioteId element.
- `POST /resource/publishAll`: Publish all the available resources to SymbIoTe core. It returns a list of resources with symbioteId for the successfully registered ones.

As data model, platform information should be provided as a JSON element like this:

```json
{
  "internalId": "5805f7a6b6a0ba3004bcd20b",
  "symbioteId": "5805f7a6b6a0ba438abc50cb",
  "name": "TestPlatform",
  "owner": "SomeOwner",
  "type": "SomeType",
  "resourceAccessProxyUrl": "http://rap.url:8080/rap"
}
```

As output, internalId will be the some internal id used by the REST service to identify the platform. Notice that only one platform instance is allowed per Registration Handler instance. 
Also, symbioteId, if present, will the the identifier assigned to the core for this platform.

As input, internalId will be used to identify the platform and update its data. symbioteId will be ignored.

Resource information follows this datamodel:

```json
{
    "internalId": "5805f7a7b6a0ba3004bcd20c",
    "symbioteId": "5805f7a6b6a0ba123499qwee",
    "name": "1234567890",
    "owner": "UNIZG-FER",
    "description": "http://openiot.eu/ontology/ns/Virtual_MGRS_Sensor",
    "location": {
      "name": "33TXL0982",
      "description": "MGRS cell",
      "longitude": 16.404726028442,
      "latitude": 45.882930755615,
      "altitude": 0
    },
    "observedProperties": [
      "CO",
      "BatterySensor",
      "Pressure",
      "Temperature",
      "BatteryMobilePhone",
      "SO2",
      "Humidity",
      "NO2"
    ],
    "resourceURL": "http://rap.url:8080/openiotRAP/Sensors('1234567890')"
}
```

As output, internalId it's the identifier assigned to the sensor information by the Registration Handler. It will be used as input for update and publish in their respective operations. Also, as with the platform information, symbioteId, if present, is the internal identifier assigned to the resource by the SymbIoTe core.
As input, only internalId will be used to update the sensor information which is saved in the Registration Handler instance.

## Configuration

Several properties are used to configure the Registration Handler and its backend. Notice that each backend (each implementation of `PlatformInfoReader`) can define its own properties and they should be provided in the same `.properties` file

- `spring.application.name`: Name under which the registration handler will register itself within Spring Boot.
- `symbiote.core.endpoint`: Endpoint of the SymbIoTe core registry component
- `reghandler.reader.impl`: Implementation of `PlatformInfoReader` that should be loaded and that will populate the Registration Handler database initially
- `reghandler.init.autoregister`: If set to true, it will register the platform information and every resource provided by the `reghandler.init.autoregister` component in the SymbIoTe core instance. If false it will just save the information for further registering.

### Default file reader:
- `symbiote.platform.file.location`: When using the default file reader for `PlatformInfoReader`, this property should point to the folder in which the `platform.json` and `resources.json` files are stored.

### Default network reader:
- `symbiote.network.information.location`: URL with the location of the `system.json` and `resources.json` files containing the platform and resources information. They will be read from an HTTP client.

### OpenIoT properties (reghandler.reader.impl=openIoTPlatformInfoReader):
- `symbiote.openiot.rap.url`: Resource Access Proxy base URL. Used in the composition of the resource URL and platform information.
- `symbiote.openiot.virtuoso.url`: JDBC URL of the Virtuoso database

### OpenUwedat (reghandler.reader.impl=networkPlatformInfoReader)
OpenUwedat is using the default network reader backend for the demo but it might change in the future.

## Packaging
Packaging on Spring Boot is extremely easy but this project depends on Gradle. As such, it should be installed in the machine that wants to build a runnable jar. Instructions to do so are at https://gradle.org/gradle-download/

Once Gradle is installed and running, getting a runnable jar compromises just personalizing the `src/main/resources/application.properties` file and running gradle clean build jar.

## Running
To run, it needs a MongoDB database running in the local host with the default configuration. Instructions to do so are at https://www.mongodb.com/download-center

Once the jar is generated and the database is running, the Registration Handler can be run by the command `java -jar <name_of_the_jar_file.jar>` 

The REST interface will be accessible at `http://localhost:8080` but the port can be configured by modifying the property `server.port` inside the `application.properties` file. 


