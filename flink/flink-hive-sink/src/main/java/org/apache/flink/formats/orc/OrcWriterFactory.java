/*
package org.apache.flink.formats.orc;

import org.apache.flink.api.common.serialization.BulkWriter;
import org.apache.flink.core.fs.FSDataOutputStream;
import org.apache.flink.utils.ReflectGetFieldUtil;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.orc.OrcFile;
import org.apache.orc.TypeDescription;

import java.io.IOException;
import java.util.Collection;

public class OrcWriterFactory<T> implements BulkWriter.Factory<T> {
    private static FileSystem fileSystem;
    private static String fsdataoutputstream = "org.apache.flink.runtime.fs.hdfs.HadoopRecoverableFsDataOutputStream";
    private OrcSchema orcSchema;
    private Collection assignerColumnNumber;

    public OrcWriterFactory(OrcSchema orcSchema, Collection assignerColumnNumber) {
        this.orcSchema = orcSchema;
        this.assignerColumnNumber = assignerColumnNumber;
    }

    @Override
    public BulkWriter<T> create(FSDataOutputStream out) throws IOException {
        Object tempFile = ReflectGetFieldUtil.getField(out, fsdataoutputstream, "tempFile");
        Path tmpPath = new Path(tempFile.toString());
        if (fileSystem == null) {
            Object fs = ReflectGetFieldUtil.getField(out, fsdataoutputstream, "fs");
            fileSystem = (FileSystem) fs;
        }
        String orcSchemaStr = this.orcSchema.toString();
        // 不把分区数据写到真实orc数据中
        String orcSchema1 = orcSchema.skipAssignerColumn(assignerColumnNumber);

        TypeDescription schema = TypeDescription.fromString(orcSchemaStr);
        TypeDescription schema1 = TypeDescription.fromString(orcSchema1);
        OrcFile.WriterOptions opts = OrcFile.writerOptions(fileSystem.getConf())
                .setSchema(schema1)
                .fileSystem(fileSystem)
                .overwrite(true);
        out.close();
        org.apache.orc.Writer orcWriter = OrcFile.createWriter(tmpPath, opts);

        return new OrcBulkWriter(orcWriter, schema, schema1, assignerColumnNumber);
    }
}
*/
