package org.myec3.socle.core.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

public class DockerSecretsDatabasePasswordProcessor implements EnvironmentPostProcessor {

    private static final Log logger = LogFactory.getLog(DockerSecretsDatabasePasswordProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        System.out.println("jthjthjth");
        if (StringUtils.isBlank(environment.getProperty("dataSource.password"))) {
            logger.info("property dataSource.password presente, pas de generation de cette derniere depuis la property dataSource.password.path");
            return;
        }
        String pwdSecretPath = environment.getProperty("dataSource.password.path");
        Resource resource = new FileSystemResource(pwdSecretPath);
        if (resource.exists()) {
                if (logger.isInfoEnabled()) {
                    logger.info("Using database password from injected Docker secret file");
                }
            String dbPassword = null;
            try {
                dbPassword = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            } catch (IOException e) {
                throw new UtilTechException("Erreur lors de l'acces au fichier " + pwdSecretPath,e);
            }
            Properties props = new Properties();
                props.put("dataSource.password", dbPassword);
                environment.getPropertySources().addLast(new PropertiesPropertySource("pwd.properties", props));
        }
    }
}