/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.user.management.authentication;


import com.google.gson.JsonObject;
import org.apache.shiro.authc.*;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.storage.couchdb.utils.Utils;

import java.util.List;


/**
 * Principals are a subjects identifying attributes  such as first name, last name or user name.
 * Credentials are usually secret values known only by the subject
 */
public class StreamPipesRealm implements Realm {

  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesRealm.class);

  private StreamPipesCredentialsMatcher credentialsMatcher;

  public StreamPipesRealm() {
    this.credentialsMatcher = new StreamPipesCredentialsMatcher();
  }

  @Override
  public String getName() {
    return "StreamPipeRealm";
  }

  @Override
  /**
   * Check if type of AuthenticationToken is supported.
   * So far we only support UsernamePasswordToken.
   */
  public boolean supports(AuthenticationToken authenticationToken) {
    return authenticationToken instanceof UsernamePasswordToken;
  }

  @Override
  /**
   * Checks if token is correct. See class JavaDoc for creating the password view.
   */
  public AuthenticationInfo getAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

    if (authenticationToken instanceof UsernamePasswordToken) {
      CouchDbClient dbClient = Utils.getCouchDbUserClient();
      try {
        String email = ((UsernamePasswordToken) authenticationToken).getUsername();
        List<JsonObject> users = dbClient.view("users/password").key(email).includeDocs(true).query(JsonObject.class);
        if (users.size() != 1) {
          throw new AuthenticationException("None or too many users " +
                  "with matching username");
        }
        JsonObject user = users.get(0);
        String password = user.get("password").getAsString();

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo();
        SimplePrincipalCollection principals = new SimplePrincipalCollection();
        principals.add(email, this.getName());

        LOG.info(principals.toString());
        info.setPrincipals(principals);
        info.setCredentials(password);

        if (credentialsMatcher.doCredentialsMatch(authenticationToken, info)) {
          System.out.println("User successfully authenticated");
        } else {
          throw new AuthenticationException("Could not authenticate");
        }
        return info;

      } catch (CouchDbException e) {
        e.printStackTrace();
      } catch (NullPointerException e) {
        e.printStackTrace();
      }
    }

    return null;
  }
}