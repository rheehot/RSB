/*
 *   R Service Bus
 *   
 *   Copyright (c) Copyright of OpenAnalytics BVBA, 2010-2011
 *
 *   ===========================================================================
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.openanalytics.rsb.component;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import eu.openanalytics.rsb.Constants;
import eu.openanalytics.rsb.Util;
import eu.openanalytics.rsb.rest.types.Directory;
import eu.openanalytics.rsb.rest.types.FileType;

/**
 * @author "OpenAnalytics &lt;rsb.development@openanalytics.eu&gt;"
 */
@Component("dataDirectoriesResource")
@Path("/" + Constants.DATA_DIR_PATH)
@Produces({ Constants.RSB_XML_CONTENT_TYPE, Constants.RSB_JSON_CONTENT_TYPE })
public class DataDirectoriesResource extends AbstractComponent {

    private final Map<String, File> rootMap = new HashMap<String, File>();

    @PostConstruct
    public void setupRootMap() {
        final List<File> dataDirectoryRoots = getConfiguration().getDataDirectories();
        if (dataDirectoryRoots != null) {
            for (final File dataDirectoryRoot : dataDirectoryRoots) {
                final String rootKey = Base64.encodeBase64URLSafeString(DigestUtils.md5Digest(dataDirectoryRoot.getPath().getBytes()));
                rootMap.put(rootKey, dataDirectoryRoot);
            }
        }
    }

    @Path("/")
    @GET
    public Directory browseRoots(@Context final HttpHeaders httpHeaders, @Context final UriInfo uriInfo) throws URISyntaxException {
        final Directory roots = Util.REST_OBJECT_FACTORY.createDirectory();
        roots.setPath("/");
        roots.setUri(Util.buildDataDirectoryUri(httpHeaders, uriInfo, "/").toString());

        for (final Entry<String, File> rootEntry : rootMap.entrySet()) {
            final Directory root = Util.REST_OBJECT_FACTORY.createDirectory();
            roots.setPath(rootEntry.getValue().getPath());
            roots.setUri(Util.buildDataDirectoryUri(httpHeaders, uriInfo, rootEntry.getKey()).toString());
            roots.getDirectories().add(root);
        }

        return roots;
    }

    @Path("/{rootId}{b64extension : (/b64extension)?}")
    @GET
    public Directory browsePath(@PathParam("rootId") final String rootId, @PathParam("b64extension") final String b64extension,
            @Context final HttpHeaders httpHeaders, @Context final UriInfo uriInfo) throws URISyntaxException {

        final File rootDataDir = rootMap.get(rootId);
        if (rootDataDir == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        final String extension = (b64extension != null ? new String(Base64.decodeBase64(b64extension)) : "");
        final File targetDataDir = new File(rootDataDir, extension);

        if (!targetDataDir.exists()) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        final Directory result = Util.REST_OBJECT_FACTORY.createDirectory();
        result.setPath(rootDataDir.getPath() + extension);
        result.setUri(Util.buildDataDirectoryUri(httpHeaders, uriInfo, rootId, b64extension).toString());

        for (final File child : targetDataDir.listFiles()) {
            if (child.isFile()) {
                final FileType fileType = Util.REST_OBJECT_FACTORY.createFileType();
                fileType.setPath(child.getPath());
                result.getFiles().add(fileType);
            } else if (child.isDirectory()) {
                final Directory childDir = Util.REST_OBJECT_FACTORY.createDirectory();
                childDir.setPath(child.getPath());
                final String childB64extension = Base64.encodeBase64URLSafeString(StringUtils.difference(rootDataDir.getPath(),
                        child.getPath()).getBytes());
                childDir.setUri(Util.buildDataDirectoryUri(httpHeaders, uriInfo, rootId, childB64extension).toString());
                result.getDirectories().add(childDir);
            } else {
                getLogger().warn("unsupported file type: " + child);
            }
        }

        return result;
    }
}
