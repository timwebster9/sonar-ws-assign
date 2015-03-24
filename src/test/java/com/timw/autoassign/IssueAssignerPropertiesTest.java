package com.timw.autoassign;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.Properties;

import static org.easymock.EasyMock.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by twebster on 20/01/14.
 */
public class IssueAssignerPropertiesTest {

    private static final String DEFAULT_URL = "http://localhost:9000";
    private static final String REAL_URL = "http://devdoc:9000";

    @Test
    public void testGetIncludeProjects() throws Exception {
        final IssueAssignerProperties classUnderTest = new IssueAssignerProperties();
        final Properties mockProperties = createMock(Properties.class);

        Whitebox.setInternalState(classUnderTest, Properties.class, mockProperties);
        expect(mockProperties.getProperty(IssueAssignerProperties.INCLUDE_PROJECTS_KEY)).andReturn("value1 , value2 ,  value3");
        replay(mockProperties);

        final String[] result = classUnderTest.getIncludeProjects();

        assertThat(result).contains("value1", "value2", "value3");
    }

    @Test
    public void testSystemPropertyOverridesFilePropertyMultiValue() throws Exception {

        final IssueAssignerProperties classUnderTest = new IssueAssignerProperties();

        // this mock represents the file
        final Properties mockProperties = createMock(Properties.class);

        Whitebox.setInternalState(classUnderTest, Properties.class, mockProperties);
        expect(mockProperties.getProperty(IssueAssignerProperties.INCLUDE_PROJECTS_KEY)).andReturn("value1 , value2 ,  value3");
        replay(mockProperties);

        System.setProperty(IssueAssignerProperties.INCLUDE_PROJECTS_KEY, "sys1, sys2, sys3");

        final String[] result = classUnderTest.getIncludeProjects();
        assertThat(result).contains("sys1", "sys2", "sys3");
    }

    @Test
    public void testSystemPropertyOverridesFilePropertySingleValue() throws Exception {

        final IssueAssignerProperties classUnderTest = new IssueAssignerProperties();

        // this mock represents the file
        final Properties mockProperties = createMock(Properties.class);

        Whitebox.setInternalState(classUnderTest, Properties.class, mockProperties);
        expect(mockProperties.getProperty(IssueAssignerProperties.SONAR_URL_KEY)).andReturn(DEFAULT_URL);
        replay(mockProperties);

        System.setProperty(IssueAssignerProperties.SONAR_URL_KEY, REAL_URL);

        final String result = classUnderTest.getSonarUrl();
        assertThat(result).isEqualTo(REAL_URL);
    }

    @Test
    public void testEmptySystemPropertyDoesntOverrideFilePropertyMultiValue() throws Exception {

        final String DEFAULT_URL = "http://localhost:9000";

        final IssueAssignerProperties classUnderTest = new IssueAssignerProperties();

        // this mock represents the file
        final Properties mockProperties = createMock(Properties.class);

        Whitebox.setInternalState(classUnderTest, Properties.class, mockProperties);
        expect(mockProperties.getProperty(IssueAssignerProperties.SONAR_URL_KEY)).andReturn(DEFAULT_URL);
        replay(mockProperties);

        System.setProperty(IssueAssignerProperties.SONAR_URL_KEY, "");

        final String result = classUnderTest.getSonarUrl();
        assertThat(result).isEqualTo(DEFAULT_URL);
    }

    @Test
    public void testEmptySystemPropertyDoesntOverridesilePropertySingleValue() throws Exception {

        final IssueAssignerProperties classUnderTest = new IssueAssignerProperties();

        // this mock represents the file
        final Properties mockProperties = createMock(Properties.class);

        Whitebox.setInternalState(classUnderTest, Properties.class, mockProperties);
        expect(mockProperties.getProperty(IssueAssignerProperties.SONAR_URL_KEY)).andReturn(DEFAULT_URL);
        replay(mockProperties);

        System.setProperty(IssueAssignerProperties.SONAR_URL_KEY, "");

        final String result = classUnderTest.getSonarUrl();
        assertThat(result).isEqualTo(DEFAULT_URL);
    }
}
