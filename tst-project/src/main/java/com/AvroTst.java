package com;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class AvroTst {
    public static void main(String[] args) throws IOException {
        Schema schema = new Schema.Parser().parse(new File("/Users/xingqian/Downloads/EC.ACPDR.avsc"));

        schema.getFields().forEach(new Consumer<Schema.Field>() {
            @Override
            public void accept(Schema.Field field) {
                System.out.println(field.name()+":"+field.schema().toString());
            }
        });

        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>(schema);
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(new File("/Users/xingqian/Downloads/delete.avro"), datumReader);
        GenericRecord user = null;
        while (dataFileReader.hasNext()) {
            user = dataFileReader.next(user);
            System.out.println(user);
        }
    }
}
