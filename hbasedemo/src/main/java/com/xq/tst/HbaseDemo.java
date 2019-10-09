package com.xq.tst;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.RegionSplitter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HbaseDemo {
    private static final String HEXSTRINGSPLIT = "HexStringSplit";
    private static final String UNIFORMSPLIT = "UniformSplit";
    private static Connection connection;
    private static Admin admin;
    private static Configuration conf;

    static {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.property.clientPort","2181");
        conf.set("hbase.zookeeper.quorum",",,,");
        System.setProperty("HADOOP_USER_NAME","xq");
        try {
            connection = ConnectionFactory.createConnection(conf);
            admin = connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Map<String, String> map = new ConcurrentHashMap<String, String>();
        for (int i = 0; i < 100; i++) {
            map.put("" + i, "test"+i);
        }
        for(Iterator<Map.Entry<String, String>> iterator=map.entrySet().iterator();iterator.hasNext();){
            Map.Entry<String, String> next = iterator.next();
            System.out.println(next.getValue());

        }

//        new HbaseDemo().excute();
    }

    private void excute() {
        String nameSpace = "nameSpace";
        String tableName = nameSpace + "test";
        String hbaseCF = "cf";
        try {
            deleteTable(admin,tableName);
            create(admin,tableName,hbaseCF,HEXSTRINGSPLIT,128,"");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (admin != null) {
                try {
                    admin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void deleteTable(Admin admin, String tableName) throws IOException {
        if (admin.tableExists(TableName.valueOf(tableName))) {
            admin.disableTable(TableName.valueOf(tableName));
            admin.deleteTable(TableName.valueOf(tableName));
        }
    }

    private void create(Admin admin, String tableName,String hbaseCF,String splitType,int splitNum,String splitTxt) throws IOException {
        HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
        hTableDescriptor.addFamily(new HColumnDescriptor(hbaseCF.getBytes()).setMaxVersions(1).setTimeToLive(1000000));
        byte[][] split = getSplit(splitType, splitNum, splitTxt);
        admin.createTable(hTableDescriptor,split);

    }

    private void scan() {
        Table table =  null;
        ResultScanner scanner = null;
        try {
            table = connection.getTable(TableName.valueOf("test"));
            Scan scan = new Scan();
            scan.setBatch(1);
            scan.setCaching(1000);
            scan.setColumnFamilyTimeRange(Bytes.toBytes("cf"), 123214214232L, 4353252352L);
            scan.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("time"));
            scanner = table.getScanner(scan);
            for (Result result : scanner) {
                for (Cell cell : result.rawCells()) {
                    System.out.println("rowkey："+Bytes.toString(cell.getRowArray(),cell.getRowOffset(),cell.getRowLength()));
//                    cell.getRow()
                    byte[] rowKey = CellUtil.cloneRow(cell);
                    System.out.println("rowkey："+Bytes.toString(rowKey));
                    System.out.println("列名："+Bytes.toString(cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength()));
//                    cell.getQualifier();
                    byte[] qualifier = CellUtil.cloneQualifier(cell);
                    System.out.println("列值："+Bytes.toString(cell.getValueArray(),cell.getValueOffset(),cell.getValueLength()));
//                    cell.getValue();
                    byte[] value = CellUtil.cloneValue(cell);
                    System.out.println("列族："+Bytes.toString(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength()));
//                    cell.getFamily();
                    byte[] family = CellUtil.cloneFamily(cell);
                    System.out.println("stamp："+cell.getTimestamp());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (scanner!=null) {
                scanner.close();
            }
            if (table!=null) {
                try {
                    table.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private byte[][] getSplit(String splitType,int splitNum,String splitTxt) {
        byte[][] split = null;
        if (HEXSTRINGSPLIT.equalsIgnoreCase(splitType)) {
            RegionSplitter.HexStringSplit hexStringSplit = new RegionSplitter.HexStringSplit();
            split = hexStringSplit.split(splitNum);
        } else if (UNIFORMSPLIT.equalsIgnoreCase(splitType)) {
            RegionSplitter.UniformSplit uniformSplit = new RegionSplitter.UniformSplit();
            split = uniformSplit.split(splitNum);
        } else {
            String[] splitArr = splitTxt.split(",");
            split = new byte[splitArr.length][];
            for (int i = 0; i < splitArr.length; i++) {
                split[i] = Bytes.toBytes(splitArr[i]);
            }
        }
        return  split;
    }

    private Long scan1() {
        Table hTable = null;
        try {
            hTable = connection.getTable(TableName.valueOf("T_REVIEW_MODULE"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        LongColumnInterpreter columnInterpreter = new LongColumnInterpreter();
        AggregationClient aggregationClient = new AggregationClient(conf);

        Scan scan = new Scan( Bytes.toBytes("2018-07-01 12:12:12"), Bytes.toBytes("2018-07-27 12:12:12"));
        Long count = null;
        try {
            count = aggregationClient.rowCount(hTable, columnInterpreter, scan);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return count;
    }
}
