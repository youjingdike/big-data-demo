package com.xq.tst.app;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.hadoop.hive.ql.plan.ColStatistics;
import org.apache.hadoop.hive.ql.plan.Explain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Statistics implements Serializable {

    public enum State {
        COMPLETE, PARTIAL, NONE
    }

    private long numRows;
    private long runTimeNumRows;
    private long dataSize;
    private Statistics.State basicStatsState;
    private Map<String, ColStatistics> columnStats;
    private Statistics.State columnStatsState;

    public Statistics() {
        this(0, 0, -1);
    }

    public Statistics(long nr, long ds, long rnr) {
        this.setNumRows(nr);
        this.setDataSize(ds);
        this.setRunTimeNumRows(rnr);
        this.basicStatsState = Statistics.State.NONE;
        this.columnStats = null;
        this.columnStatsState = Statistics.State.NONE;
    }

    public long getNumRows() {
        return numRows;
    }

    public void setNumRows(long numRows) {
        this.numRows = numRows;
        updateBasicStatsState();
    }

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
        updateBasicStatsState();
    }

    private void updateBasicStatsState() {
        if (numRows <= 0 && dataSize <= 0) {
            this.basicStatsState = Statistics.State.NONE;
        } else if (numRows <= 0 || dataSize <= 0) {
            this.basicStatsState = Statistics.State.PARTIAL;
        } else {
            this.basicStatsState = Statistics.State.COMPLETE;
        }
    }

    public Statistics.State getBasicStatsState() {
        return basicStatsState;
    }

    public void setBasicStatsState(Statistics.State basicStatsState) {
        this.basicStatsState = basicStatsState;
    }

    public Statistics.State getColumnStatsState() {
        return columnStatsState;
    }

    public void setColumnStatsState(Statistics.State columnStatsState) {
        this.columnStatsState = columnStatsState;
    }

    @Override
    @Explain(displayName = "Statistics")
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Num rows: ");
        sb.append(numRows);
        if (runTimeNumRows >= 0) {
            sb.append("/" + runTimeNumRows);
        }
        sb.append(" Data size: ");
        sb.append(dataSize);
        sb.append(" Basic stats: ");
        sb.append(basicStatsState);
        sb.append(" Column stats: ");
        sb.append(columnStatsState);
        return sb.toString();
    }

    @Explain(displayName = "Statistics", explainLevels = { Explain.Level.USER })
    public String toUserLevelExplainString() {
        StringBuilder sb = new StringBuilder();
        sb.append("rows=");
        sb.append(numRows);
        if (runTimeNumRows >= 0) {
            sb.append("/" + runTimeNumRows);
        }
        sb.append(" width=");
        // just to be safe about numRows
        if (numRows != 0) {
            sb.append(dataSize / numRows);
        } else {
            sb.append("-1");
        }
        return sb.toString();
    }

    public String extendedToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" numRows: ");
        sb.append(numRows);
        sb.append(" dataSize: ");
        sb.append(dataSize);
        sb.append(" basicStatsState: ");
        sb.append(basicStatsState);
        sb.append(" colStatsState: ");
        sb.append(columnStatsState);
        sb.append(" colStats: ");
        sb.append(columnStats);
        return sb.toString();
    }

    @Override
    public Statistics clone() throws CloneNotSupportedException {
        Statistics clone = new Statistics(numRows, dataSize, runTimeNumRows);
        clone.setBasicStatsState(basicStatsState);
        clone.setColumnStatsState(columnStatsState);
        if (columnStats != null) {
            Map<String, ColStatistics> cloneColStats = Maps.newHashMap();
            for (Map.Entry<String, ColStatistics> entry : columnStats.entrySet()) {
                cloneColStats.put(entry.getKey(), entry.getValue().clone());
            }
            clone.setColumnStats(cloneColStats);
        }
        return clone;
    }

    public void addToNumRows(long nr) {
        numRows += nr;
        updateBasicStatsState();
    }

    public void addToDataSize(long rds) {
        dataSize += rds;
        updateBasicStatsState();
    }

    public void setColumnStats(Map<String, ColStatistics> colStats) {
        this.columnStats = colStats;
    }

    public void setColumnStats(List<ColStatistics> colStats) {
        columnStats = Maps.newHashMap();
        addToColumnStats(colStats);
    }

    public void addToColumnStats(List<ColStatistics> colStats) {

        if (columnStats == null) {
            columnStats = Maps.newHashMap();
        }

        if (colStats != null) {
            for (ColStatistics cs : colStats) {
                ColStatistics updatedCS = null;
                if (cs != null) {

                    String key = cs.getColumnName();
                    // if column statistics for a column is already found then merge the statistics
                    if (columnStats.containsKey(key) && columnStats.get(key) != null) {
                        updatedCS = columnStats.get(key);
                        updatedCS.setAvgColLen(Math.max(updatedCS.getAvgColLen(), cs.getAvgColLen()));
                        updatedCS.setNumNulls(updatedCS.getNumNulls() + cs.getNumNulls());
                        updatedCS.setCountDistint(Math.max(updatedCS.getCountDistint(), cs.getCountDistint()));
                        columnStats.put(key, updatedCS);
                    } else {
                        columnStats.put(key, cs);
                    }
                }
            }
        }
    }

    //                  newState
    //                  -----------------------------------------
    // columnStatsState | COMPLETE          PARTIAL      NONE    |
    //                  |________________________________________|
    //         COMPLETE | COMPLETE          PARTIAL      PARTIAL |
    //          PARTIAL | PARTIAL           PARTIAL      PARTIAL |
    //             NONE | COMPLETE          PARTIAL      NONE    |
    //                  -----------------------------------------
    public void updateColumnStatsState(Statistics.State newState) {
        if (newState.equals(Statistics.State.PARTIAL)) {
            columnStatsState = Statistics.State.PARTIAL;
        }

        if (newState.equals(Statistics.State.NONE)) {
            if (columnStatsState.equals(Statistics.State.NONE)) {
                columnStatsState = Statistics.State.NONE;
            } else {
                columnStatsState = Statistics.State.PARTIAL;
            }
        }

        if (newState.equals(Statistics.State.COMPLETE)) {
            if (columnStatsState.equals(Statistics.State.PARTIAL)) {
                columnStatsState = Statistics.State.PARTIAL;
            } else {
                columnStatsState = Statistics.State.COMPLETE;
            }
        }
    }

    public long getAvgRowSize() {
        if (numRows != 0) {
            return dataSize / numRows;
        }

        return dataSize;
    }

    public ColStatistics getColumnStatisticsFromColName(String colName) {
        if (columnStats == null) {
            return null;
        }
        for (ColStatistics cs : columnStats.values()) {
            if (cs.getColumnName().equalsIgnoreCase(colName)) {
                return cs;
            }
        }
        return null;
    }

    public List<ColStatistics> getColumnStats() {
        if (columnStats != null) {
            return Lists.newArrayList(columnStats.values());
        }
        return null;
    }

    public long getRunTimeNumRows() {
        return runTimeNumRows;
    }

    public void setRunTimeNumRows(long runTimeNumRows) {
        this.runTimeNumRows = runTimeNumRows;
    }
}
