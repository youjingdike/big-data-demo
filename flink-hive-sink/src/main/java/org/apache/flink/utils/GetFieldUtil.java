package org.apache.flink.utils;

import org.apache.avro.generic.GenericRecord;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.types.Row;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.io.File;
import java.io.Serializable;
import java.sql.Date;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class GetFieldUtil<IN> implements Serializable {
    private static transient DateTimeFormatter dateTimeFormatter;

    public Object getFieldByColumnNumber(final IN element, int columnNum) {
        Object bucketStr = null;
        if (element instanceof Row) {
            Row row = (Row) element;
            bucketStr = row.getField(columnNum);
        } else if (element instanceof GenericRecord) {
            GenericRecord genericRecord = (GenericRecord) element;
            bucketStr = genericRecord.get(columnNum);
        } else if (element instanceof Tuple2) {
            Tuple2<LongWritable, Text> tuple = (Tuple2<LongWritable, Text>) element;
            String[] queue = tuple.f1.toString().split("\001");
            bucketStr = queue[columnNum];
        }
        return bucketStr;
    }

    public String getFieldByColumnNumber(final IN element, int[][] columnNum, String formatString) {
        if (dateTimeFormatter == null) {
            dateTimeFormatter = DateTimeFormatter.ofPattern(formatString);
        }

        StringBuffer bucketSb = new StringBuffer();
        for (int i = 0; i < columnNum.length; i++) {
            String column = getFieldByColumnNumber(element, columnNum[i][0]).toString();
            if (columnNum[i][1] == 1) {
                Date date = Date.valueOf(column);
                column = dateTimeFormatter.format(Instant.ofEpochMilli(date.getTime()));
            }
            bucketSb.append(column);
            if (i != columnNum.length - 1) {
                bucketSb.append(File.separator);
            }
        }

        return bucketSb.toString();
    }

    public String getFieldByColumnNumberAndColumnName(final IN element, int[][] columnNum, String[] columnNames, String formatString) {
        if (dateTimeFormatter == null) {
            dateTimeFormatter = DateTimeFormatter.ofPattern(formatString);
        }

        StringBuffer bucketSb = new StringBuffer();
        for (int i = 0; i < columnNum.length; i++) {
            String column = getFieldByColumnNumber(element, columnNum[i][0]).toString();
            if (columnNum[i][1] == 1) {
                Date date = Date.valueOf(column);
                column = dateTimeFormatter.format(Instant.ofEpochMilli(date.getTime()));
            }
            bucketSb.append(columnNames[i] + "=" + column);
            if (i != columnNum.length - 1) {
                bucketSb.append(File.separator);
            }
        }

        return bucketSb.toString();
    }

}
