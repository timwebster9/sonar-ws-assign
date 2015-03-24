package com.timw.autoassign;

import com.timw.autoassign.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.wsclient.SonarClient;
import org.sonar.wsclient.user.User;
import org.sonar.wsclient.user.UserClient;
import org.sonar.wsclient.user.UserQuery;

/**
 * Created by twebster on 18/01/14.
 */
public class UserClientWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(UserClientWrapper.class);
    private SonarClient sonarClient;
    private UserClient userClient;

    public UserClientWrapper(final SonarClient sonarClient) {
        this.sonarClient = sonarClient;
        this.userClient = sonarClient.userClient();
    }

    public User findUser(final String scmAuthor) throws UserNotFoundException {
        final UserQuery userQuery = UserQuery.create().logins(scmAuthor);

        try {
            return userClient.find(userQuery).get(0);
        }
        catch (final Exception e) {
            final String msg = "Sonar user not found for SCM author: [" + scmAuthor + "].";
            LOG.error(msg);
            throw new UserNotFoundException(e);
        }
    }
}
