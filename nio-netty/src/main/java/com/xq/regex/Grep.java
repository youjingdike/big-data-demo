package com.xq.regex;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Grep {
    private Pattern pattern;

    public Grep(Pattern pattern) {
        this.pattern = pattern;
    }

    public Grep(String regex,boolean ignoreCase) {
        this.pattern = Pattern.compile(regex,(ignoreCase)?Pattern.CASE_INSENSITIVE:0);
    }

    public Grep(String regex) {
        this(regex, false);
    }

    public MathchedLine[] grep(File file) throws IOException {
        List<MathchedLine> list = grepList(file);
        MathchedLine[] mathches = new MathchedLine[list.size()];
        list.toArray(mathches);
        return mathches;
    }

    public MathchedLine[] grep(String fileName) throws IOException {
        return grep(new File(fileName));
    }

    public MathchedLine[] grep(File[] files) {
        LinkedList<MathchedLine> aggregate = new LinkedList<>();

        for (int i = 0; i < files.length; i++) {
            try {
                List<MathchedLine> temp = grepList(files[i]);
                aggregate.addAll(temp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MathchedLine[] mathches = new MathchedLine[aggregate.size()];
        aggregate.toArray(mathches);
        return mathches;
    }

    public static class MathchedLine {
        private File file;
        private int lineNumber;
        private String lineText;
        private int start;
        private int end;

        public MathchedLine(File file, int lineNumber, String lineText, int start, int end) {
            this.file = file;
            this.lineNumber = lineNumber;
            this.lineText = lineText;
            this.start = start;
            this.end = end;
        }

        public File getFile() {
            return file;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public String getLineText() {
            return lineText;
        }

        public int start() {
            return start;
        }

        public int end() {
            return end;
        }
    }

    private List<MathchedLine> grepList(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("Does not exist:" + file);
        }
        if (!file.isFile()) {
            throw new IOException("Not a regular file:" + file);
        }
        if (!file.canRead()) {
            throw new IOException("Unreadable file:" + file);
        }

        LinkedList<MathchedLine> list = new LinkedList();
        FileReader fr = new FileReader(file);
        LineNumberReader lnr = new LineNumberReader(fr);
        Matcher matcher = this.pattern.matcher("");
        String line;
        while ((line = lnr.readLine()) != null) {
            matcher.reset(line);
            if (matcher.find()) {
                list.add(new MathchedLine(file, lnr.getLineNumber(), line, matcher.start(), matcher.end()));
            }
        }

        lnr.close();
        return list;
    }

    public static void main(String[] args) {
        boolean ignoreCase = false;
        boolean onebyone = false;
        LinkedList<String> argList = new LinkedList();//采集变量
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if (args[i].equals("-i") || args[i].equals("--ignore-case")) {
                    ignoreCase = true;
                }

                if (args[i].equals("-l")) {
                    onebyone = true;
                }
                continue;
            }
            argList.add(args[i]);
        }

        if (argList.size()<2) {
            System.err.println("usage：[options] pattern filename ...");
            return;
        }

        Grep grep = new Grep(argList.remove(0), ignoreCase);
        if (onebyone) {
            Iterator<String> it = argList.iterator();
            while (it.hasNext()) {
                String filename = it.next();
                System.out.println("Filename:" + filename);

                MathchedLine[] mathches = null;
                try {
                    mathches = grep.grep(filename);
                } catch (IOException e) {
                    System.err.println("\t***:" + e);
                    continue;
                }
                for (int i = 0; i < mathches.length; i++) {
                    MathchedLine mathch = mathches[i];
                    System.out.println(mathch.getLineNumber() + "[" + mathch.start() + "-"
                            + (mathch.end() - 1) + "]:" + mathch.getLineText());
                }
            }
        } else {
            File[] files = new File[argList.size()];
            for (int i = 0; i < files.length; i++) {
                files[i] = new File(argList.get(i));
            }

            MathchedLine[] mathches = grep.grep(files);
            for (int i = 0; i < mathches.length; i++) {
                MathchedLine mathch = mathches[i];
                System.out.println(mathch.getLineNumber() + "[" + mathch.start() + "-"
                        + (mathch.end() - 1) + "]:" + mathch.getLineText());
            }
        }
    }
}
