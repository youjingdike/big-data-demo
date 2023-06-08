/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xq.tst.lookup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.connectors.hive.HiveLookupTableSource;
import org.apache.flink.connectors.hive.util.JobConfUtils;
import org.apache.flink.table.catalog.CatalogTable;
import org.apache.flink.table.catalog.ObjectIdentifier;
import org.apache.flink.table.catalog.exceptions.TableNotExistException;
import org.apache.flink.table.catalog.hive.HiveCatalog;
import org.apache.flink.table.catalog.hive.factories.HiveCatalogFactoryOptions;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.factories.DynamicTableSourceFactory;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.util.Preconditions;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.mapred.JobConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.xq.tst.lookup.HiveConnectorOptions.DATABASE;
import static com.xq.tst.lookup.HiveConnectorOptions.HIVE_CONF;
import static com.xq.tst.lookup.HiveConnectorOptions.LOOKUP_JOIN_CACHE_TTL;
import static com.xq.tst.lookup.HiveConnectorOptions.TABLE_NAME;
import static org.apache.flink.table.factories.FactoryUtil.createTableFactoryHelper;

/** A dynamic table factory implementation for Hive catalog. */
public class HiveDynamicTableFactory implements DynamicTableSourceFactory {
    private static final Logger LOG = LoggerFactory.getLogger(HiveDynamicTableFactory.class);
    private static final String IDENTIFIER = "hive-ksy";

    @Override
    public String factoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        Set<ConfigOption<?>> set = new HashSet<>();
        set.add(HIVE_CONF);
        set.add(DATABASE);
        set.add(TABLE_NAME);
        return set;
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        Set<ConfigOption<?>> set = new HashSet<>();
        set.add(HIVE_CONF);
        set.add(DATABASE);
        set.add(TABLE_NAME);
        set.add(LOOKUP_JOIN_CACHE_TTL);
        return set;
    }

    @Override
    public DynamicTableSource createDynamicTableSource(Context context) {

        FactoryUtil.TableFactoryHelper helper = createTableFactoryHelper(this, context);
        helper.validate();
        CatalogTable catalogTable = Preconditions.checkNotNull(context.getCatalogTable());
//        TableSchema tableSchema = catalogTable.getSchema();
//        catalogTable.getOptions();
//        ResolvedSchema resolvedSchema = catalogTable.getResolvedSchema();
//        System.out.println(tableSchema);

        ReadableConfig options = helper.getOptions();//其实就是catalogTable.getOptions();

        HiveConf hiveConf = new HiveConf();
        String hiveConfStr = options.get(HIVE_CONF);
        JSONObject jsonObject = JSON.parseObject(hiveConfStr);
        jsonObject.forEach((key,vaule)->hiveConf.set(key,vaule+""));
        hiveConf.set(HiveCatalogFactoryOptions.HIVE_VERSION.key(),"3.1.2");

        JobConf jobConf = JobConfUtils.createJobConfWithCredentials(hiveConf);
        // hive table source that has not lookup ability

        HiveCatalog hiveCatalog = createHiveCatalog(hiveConf);
        ObjectIdentifier objectIdentifier = ObjectIdentifier.of("hive-lookup", options.get(DATABASE), options.get(TABLE_NAME));
        CatalogTable catalogHiveTable = null;
        try {
            catalogHiveTable = (CatalogTable)hiveCatalog.getTable(objectIdentifier.toObjectPath());
            Iterator<Map.Entry<String, String>> iterator = catalogTable.getOptions().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                catalogHiveTable.getOptions().put(next.getKey(),next.getValue());
            }
        } catch (TableNotExistException e) {
            LOG.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }

        // hive table source that has scan and lookup ability
        return new HiveLookupTableSource(
                jobConf,
                context.getConfiguration(),
                objectIdentifier.toObjectPath(),
                catalogHiveTable);
    }

    private HiveCatalog createHiveCatalog(HiveConf hiveConf) {
        return new HiveCatalog(
                "hive-lookup",
                null,
                hiveConf,
                hiveConf.get((HiveCatalogFactoryOptions.HIVE_VERSION.key())));
    }
}
