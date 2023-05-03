/*
 * Copyright 2023 The Project Authors.
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
 */
package org.spdx.sbom.gradle.uri;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import org.gradle.api.artifacts.ModuleVersionIdentifier;

public class URIs {
  public static URI toDownloadLocation(
      URI repoUri, ModuleVersionIdentifier moduleId, String filename) {
    if (!repoUri.toString().endsWith("/")) {
      repoUri = URI.create(repoUri.toString().concat("/"));
    }
    String modulePath;
    try {
      modulePath =
          moduleId.getGroup().replace(".", "/")
              + "/"
              + moduleId.getName()
              + "/"
              + moduleId.getVersion()
              + "/"
              + URLEncoder.encode(filename, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    return repoUri.resolve(modulePath);
  }

  public static String toPurl(URI repoUri, ModuleVersionIdentifier moduleId) {
    String locator =
        "pkg:maven/" + moduleId.getGroup() + "/" + moduleId.getName() + "@" + moduleId.getVersion();
    String repo = repoUri.toString();
    repo = trimTrailingSlashes(repo);
    if (!repo.equals("https://repo.maven.org/maven2")) {
      repo = trimPrefix(repo, "http://");
      repo = trimPrefix(repo, "https://");
      try {
        locator = locator + ("?repository_url=" + URLEncoder.encode(repo, "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    }
    return locator;
  }

  private static String trimPrefix(String str, String prefix) {
    return str.replaceFirst("^" + prefix, "");
  }

  private static String trimTrailingSlashes(String str) {
    return str.replaceFirst("/*$", "");
  }
}
