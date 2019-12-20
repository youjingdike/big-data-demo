package com.xq.tst;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

import java.io.IOException;

public class HdfsDemo {
    private final String rootPathStr = "/root/";
    private String tmpPathStr = "tmp";
    private String currentPathStr = "current";

    private Path tmpPath = null;
    private Path currentPath = null;

    public static void main(String[] args) {
        Configuration conf = new Configuration(true);
        conf.set("fs.defaultFS","hdfs://127.0.0.1:9000");
        FileSystem fs = null;
        try {
            fs = FileSystem.get(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HdfsDemo hdfsDemo = new HdfsDemo();
        hdfsDemo.remaneFile(fs);

    }

    /**
     * 重命名
     * @param fs
     */
    private void remaneFile(FileSystem fs) {
        tmpPath = new Path(rootPathStr + tmpPathStr);
        try {
            if (fs.exists(tmpPath)) {
                if (fs.isDirectory(tmpPath)) {
                    RemoteIterator<LocatedFileStatus> fileList = fs.listFiles(tmpPath, false);
                    while (fileList.hasNext()) {
                        LocatedFileStatus file = fileList.next();
                        if (file.isFile()) {
                            Path fPath = file.getPath();
                            String name = fPath.getName();
System.out.println("path:"+fPath.toString()+";name:"+name);
                            if (name.endsWith("complex.txt")) {
                                currentPath = new Path(rootPathStr + currentPathStr + "/" + name);
                                if (fs.exists(currentPath)) {
                                    fs.delete(currentPath, true);
                                }
                                fs.rename(fPath, currentPath);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 重命名
     * @param fs
     */
    private void remaneFile1(FileSystem fs) {
        tmpPath = new Path(rootPathStr + tmpPathStr);
        try {
            if (fs.exists(tmpPath)) {
                if (fs.isDirectory(tmpPath)) {
                    FileStatus[] fileStatuses = fs.listStatus(tmpPath, new RegexPathFilter(".*\\.complex\\.txt"));
                    if (fileStatuses.length!=0) {
                        for (FileStatus fileStatus : fileStatuses) {
                            if (fileStatus.isFile()) {
                                currentPath = new Path(rootPathStr + currentPathStr + "/" + fileStatus.getPath().getName());
System.out.println("path:"+fileStatus.getPath().toString()+";name:"+fileStatus.getPath().getName());
                                if (fs.exists(currentPath)) {
                                    fs.delete(currentPath, true);
                                }
                                fs.rename(fileStatus.getPath(), currentPath);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 复制文件
     * @param fs
     */
    private void copyFile(FileSystem fs) {
        tmpPath = new Path(rootPathStr + tmpPathStr);
        try {
            if (fs.exists(tmpPath)) {
                if (fs.isDirectory(tmpPath)) {
                    FileStatus[] fileStatuses = fs.listStatus(tmpPath);
                    if (fileStatuses.length != 0) {
                        for (FileStatus fileStatus : fileStatuses) {
                            if (fileStatus.isFile()) {
                                currentPath = new Path(rootPathStr + "ceshi/" + fileStatus.getPath().getName());
System.out.println("path:"+fileStatus.getPath().toString()+";name:"+fileStatus.getPath().getName());
                                /*if (!fs.exists(currentPath)) {
                                    fs.create(currentPath);
                                }*/
                                //覆盖目标文件，不覆盖的话，如果存在的话会报错
                                FileContext.getFileContext(fs.getConf()).util().copy(fileStatus.getPath(), currentPath, false, true);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class RegexPathFilter implements PathFilter{
        private final String regex;

        public RegexPathFilter(String regex) {
            this.regex = regex;
        }

        @Override
        public boolean accept(Path path) {
            return path.getName().matches(regex);
        }
    }
}
