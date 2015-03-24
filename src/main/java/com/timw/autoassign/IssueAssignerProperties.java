package com.timw.autoassign;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by twebster on 18/01/14.
 */
public class IssueAssignerProperties {

    private static final Logger LOG = LoggerFactory.getLogger(IssueAssignerProperties.class);
    protected static final String PROPS_FILE = "/autoassigner.properties";
    protected static final String SONAR_URL_KEY = "sonar.url";
    protected static final String SONAR_ADMIN_USERNAME_KEY = "sonar.admin.username";
    protected static final String SONAR_ADMIN_PASSWORD_KEY = "sonar.admin.password";
    protected static final String ASSIGNEE_OVERRIDE_KEY = "assignee.override";
    protected static final String ASSIGNEE_DEFAULT_KEY = "assignee.default";
    protected static final String INCLUDE_PROJECTS_KEY = "include.projects";
    private static final String PROP_DELIM_REGEX = "\\s*,\\s*";
    private Properties props;

    public IssueAssignerProperties() throws Exception {
        loadProperties();
    }

    private void loadProperties() throws Exception {
        final InputStream resource = this.getClass().getResourceAsStream(PROPS_FILE);
        if (resource == null) {
            throw new IllegalArgumentException(String.format("Error opening: " + PROPS_FILE));
        }
        props = new Properties();
        props.load(resource);
        //this.logProperties();
    }

    public String getSonarUrl() {
        return this.getProperty(SONAR_URL_KEY);
    }

    public String getSonarAdminUsername() {
        return this.getProperty(SONAR_ADMIN_USERNAME_KEY);
    }

    public String getSonarAdminPassword() {
        return this.getProperty(SONAR_ADMIN_PASSWORD_KEY);
    }

    public String[] getIncludeProjects() {
        return this.getMultiValuedProperty(INCLUDE_PROJECTS_KEY);
    }

    public String getOverrideAssignee() {
        return this.getProperty(ASSIGNEE_OVERRIDE_KEY);
    }

    public String getDefaultAssignee() {
        return this.getProperty(ASSIGNEE_DEFAULT_KEY);
    }

    private String[] getMultiValuedProperty(final String key) {
        return getProperty(key).split(PROP_DELIM_REGEX);
    }

    private String getProperty(final String key) {

        String propertyValue = this.getSystemProperty(key);

        if (StringUtils.isEmpty(propertyValue)) {
            propertyValue = this.props.getProperty(key);
            LOG.info("No system property found for key [" + key + "]. Using configured value [" + propertyValue + "]");
        }
        else {
            LOG.info("Found system property [" + key + "] value [" + propertyValue + "]");
        }

        return propertyValue;
    }

    private String getSystemProperty(final String key) {
        return System.getProperty(key);
    }

    private void logProperties() {

        final Iterator<Object> keys = this.props.keySet().iterator();
        LOG.info("Settings: ");

        while (keys.hasNext()) {
            final String key = (String)keys.next();
            LOG.info(key + ": [" + this.props.getProperty(key) + "]");
        }
    }
}
