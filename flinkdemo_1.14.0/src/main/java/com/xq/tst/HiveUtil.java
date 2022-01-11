/*
package com.xq.tst;

import org.apache.flink.api.java.hadoop.mapred.utils.HadoopUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connectors.hive.util.JobConfUtils;
import org.apache.flink.table.catalog.CatalogTable;
import org.apache.flink.table.catalog.ObjectIdentifier;
import org.apache.flink.table.catalog.exceptions.CatalogException;
import org.apache.flink.table.filesystem.FileSystemConnectorOptions;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.mapred.JobConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import static org.apache.flink.table.catalog.hive.util.HiveTableUtil.getHadoopConfiguration;
import static org.apache.flink.util.StringUtils.isNullOrWhitespaceOnly;

public class HiveUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HiveUtil.class);
    public static final String HIVE_SITE_FILE = "hive-site.xml";

    public static HiveTableSinkNew getHiveTableSink(Configuration conf, Map<String,String> options) {
        Integer configuredParallelism =
                Configuration.fromMap(options)
                        .get(FileSystemConnectorOptions.SINK_PARALLELISM);
        HiveConf hiveConf = createHiveConf(options.get("hive-conf-dir"),options.get("hadoop-conf-dir"));
        JobConf jobConf = JobConfUtils.createJobConfWithCredentials(hiveConf);
        return new HiveTableSinkNew(
                conf,
                jobConf,
                ObjectIdentifier.of("catalogName", "databaseName", "objectName"),
                new CatalogTable(),
                configuredParallelism);
    }

    static HiveConf createHiveConf(@Nullable String hiveConfDir, @Nullable String hadoopConfDir) {
        // create HiveConf from hadoop configuration with hadoop conf directory configured.
        org.apache.hadoop.conf.Configuration hadoopConf = null;
        if (isNullOrWhitespaceOnly(hadoopConfDir)) {
            for (String possibleHadoopConfPath :
                    HadoopUtils.possibleHadoopConfPaths(
                            new org.apache.flink.configuration.Configuration())) {
                hadoopConf = getHadoopConfiguration(possibleHadoopConfPath);
                if (hadoopConf != null) {
                    break;
                }
            }
        } else {
            hadoopConf = getHadoopConfiguration(hadoopConfDir);
            if (hadoopConf == null) {
                String possiableUsedConfFiles =
                        "core-site.xml | hdfs-site.xml | yarn-site.xml | mapred-site.xml";
                throw new CatalogException(
                        "Failed to load the hadoop conf from specified path:" + hadoopConfDir,
                        new FileNotFoundException(
                                "Please check the path none of the conf files ("
                                        + possiableUsedConfFiles
                                        + ") exist in the folder."));
            }
        }
        if (hadoopConf == null) {
            hadoopConf = new org.apache.hadoop.conf.Configuration();
        }
        // ignore all the static conf file URLs that HiveConf may have set
        HiveConf.setHiveSiteLocation(null);
        HiveConf.setLoadMetastoreConfig(false);
        HiveConf.setLoadHiveServer2Config(false);
        HiveConf hiveConf = new HiveConf(hadoopConf, HiveConf.class);

        LOG.info("Setting hive conf dir as {}", hiveConfDir);

        if (hiveConfDir != null) {
            Path hiveSite = new Path(hiveConfDir, HIVE_SITE_FILE);
            if (!hiveSite.toUri().isAbsolute()) {
                // treat relative URI as local file to be compatible with previous behavior
                hiveSite = new Path(new File(hiveSite.toString()).toURI());
            }
            try (InputStream inputStream = hiveSite.getFileSystem(hadoopConf).open(hiveSite)) {
                hiveConf.addResource(inputStream, hiveSite.toString());
                // trigger a read from the conf so that the input stream is read
                isEmbeddedMetastore(hiveConf);
            } catch (IOException e) {
                throw new CatalogException(
                        "Failed to load hive-site.xml from specified path:" + hiveSite, e);
            }
        } else {
            // user doesn't provide hive conf dir, we try to find it in classpath
            URL hiveSite =
                    Thread.currentThread().getContextClassLoader().getResource(HIVE_SITE_FILE);
            if (hiveSite != null) {
                LOG.info("Found {} in classpath: {}", HIVE_SITE_FILE, hiveSite);
                hiveConf.addResource(hiveSite);
            }
        }
        return hiveConf;
    }

    public static boolean isEmbeddedMetastore(HiveConf hiveConf) {
        return isNullOrWhitespaceOnly(hiveConf.getVar(HiveConf.ConfVars.METASTOREURIS));
    }

}
*/
