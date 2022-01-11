package com.xq.tst;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        set.add("5400-5411");
        set.add("5441-5482");
        set.add("5428-5439");
        System.out.println(getServerId(set,100));
    }

    private static String getServerId(Set<String> idSet, Integer sourceParallelism) {
        StringBuilder sb = new StringBuilder();
        if (idSet != null && idSet.size() != 0) {
            List<String> idSortList = idSet.stream().sorted().collect(Collectors.toList());
            getServerId(idSortList, sourceParallelism,sb);
        } else {
            sb.append(5400).append("-").append(5400 + sourceParallelism-1);
        }
        return sb.toString();
    }

    private static void getServerId(List<String> idSortList, Integer sourceParallelism,StringBuilder sb) {
        int start = 0;
        int end = 0;
        int listSize = idSortList.size();
        for (int i = 0; i < listSize; i++) {
            if (i == 0) {
                String[] start_end = idSortList.get(i).split("-");
                start = 5400;
                end = Integer.valueOf(start_end[0]) -1;
                if (end - start >= sourceParallelism-1) {
                    sb.append(start).append("-").append(start+sourceParallelism-1);
                    return;
                }
            }
            if (i == listSize-1) {
                String[] start_end = idSortList.get(i).split("-");
                start = Integer.valueOf(start_end[1]) + 1;
                sb.append(start).append("-").append(start+sourceParallelism-1);
                return;
            }
            String[] start_end = idSortList.get(i).split("-");
            String[] start_end_next = idSortList.get(i+1).split("-");
            start = Integer.valueOf(start_end[1]) + 1;
            end = Integer.valueOf(start_end_next[0]) - 1;
            if (end - start >= sourceParallelism-1) {
                sb.append(start).append("-").append(start+sourceParallelism-1);
                return;
            }
        }
    }
}
