/*
 *   R Service Bus
 *   
 *   Copyright (c) Copyright of Open Analytics NV, 2010-2019
 *
 *   ===========================================================================
 *
 *   This file is part of R Service Bus.
 *
 *   R Service Bus is free software: you can redistribute it and/or modify
 *   it under the terms of the Apache License as published by
 *   The Apache Software Foundation, either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Apache License for more details.
 *
 *   You should have received a copy of the Apache License
 *   along with R Service Bus.  If not, see <http://www.apache.org/licenses/>.
 *
 */

package eu.openanalytics.rsb.stats;

import java.util.Calendar;
import java.util.Map;

import eu.openanalytics.rsb.message.Job;

/**
 * Defines a handler for RSB job statistics.
 * 
 * @author "Open Analytics &lt;rsb.development@openanalytics.eu&gt;"
 */
public interface JobStatisticsHandler
{
    void setConfiguration(Map<String, Object> configuration);

    void initialize();

    void destroy();

    void storeJobStatistics(Job job,
                            Calendar jobCompletionTime,
                            long millisecondsSpentProcessing,
                            String rServiAddress);
}
