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

package eu.openanalytics.rsb.jaxrs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.StringContains.containsString;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import eu.openanalytics.rsb.Constants;

/**
 * @author "Open Analytics <rsb.development@openanalytics.eu>"
 */
public class IllegalArgumentExceptionMapperTestCase {
    @Test
    public void toResponse() {
        final IllegalArgumentExceptionMapper iaeMapper = new IllegalArgumentExceptionMapper();

        final Response response = iaeMapper.toResponse(new IllegalArgumentException("test_err"));
        assertThat(response.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));
        assertThat(response.getMetadata().get(Constants.REASON_PHRASE_HEADER).get(0).toString(), containsString("test_err"));
        assertThat(response.getEntity(), nullValue());
    }
}