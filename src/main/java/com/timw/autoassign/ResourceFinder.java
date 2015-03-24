package com.timw.autoassign;

import com.timw.autoassign.exception.AuthorNotFoundException;
import com.timw.autoassign.exception.ResourceNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.issue.Issue;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by twebster on 18/01/14.
 */
public class ResourceFinder {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceFinder.class);
    private static final Map<String, Resource> RESOURCE_CACHE = new HashMap<String, Resource>();
    final Sonar sonar;

    public ResourceFinder(final IssueAssignerProperties issueAssignerProperties) {
        this.sonar = Sonar.create(issueAssignerProperties.getSonarUrl(),
                                  issueAssignerProperties.getSonarAdminUsername(),
                                  issueAssignerProperties.getSonarAdminPassword());
    }

    public String getIssueAuthor(Issue issue) throws AuthorNotFoundException, ResourceNotFoundException {
        final Resource resource = this.findWithScmAuthorByKey(issue.componentKey());
        final Measure measure = resource.getMeasure(CoreMetrics.SCM_AUTHORS_BY_LINE_KEY);
        String author;

        try {
            final Map<String, String> data = measure.getDataAsMap(";");
            author = data.get(String.valueOf(issue.line()));
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            final String msg = "SCM measure data not found for issue: [" + issue.key() + "], " + "on component: [" + issue.componentKey() + "]";
            LOG.error(msg);
            throw new AuthorNotFoundException();
        }

        if (StringUtils.isEmpty(author)) {
            final String msg = "Author not found for component: [" + issue.componentKey() + "].";
            LOG.error(msg);
            throw new AuthorNotFoundException();
        }

        return author;
    }

    private Resource findWithScmAuthorByKey(final String resourceKey) throws ResourceNotFoundException {
        final Resource resource = this.getResource(resourceKey);

        if (resource == null) {
            final String msg = "Resource not found for component: [" + resourceKey + "].";
            LOG.error(msg);
            throw new ResourceNotFoundException(msg);
        }

        return resource;
    }

    private Resource getResource(final String resourceKey) {
        Resource resource = RESOURCE_CACHE.get(resourceKey);

        if (resource == null) {
            resource = this.sonar.find(ResourceQuery.createForMetrics(resourceKey, CoreMetrics.SCM_AUTHORS_BY_LINE_KEY));
            LOG.info("Found resource with key: [" + resourceKey + "]");
            RESOURCE_CACHE.put(resourceKey, resource);
        }
        else {
            LOG.info("Using cached resource: [" + resourceKey + "]");
        }

        return resource;
    }
}
