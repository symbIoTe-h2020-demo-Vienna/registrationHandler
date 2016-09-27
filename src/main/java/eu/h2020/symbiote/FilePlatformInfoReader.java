package eu.h2020.symbiote;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 27/09/16.
 */
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
