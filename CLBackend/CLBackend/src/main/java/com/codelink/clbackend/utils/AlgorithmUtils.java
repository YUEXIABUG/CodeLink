package com.codelink.clbackend.utils;

import java.util.List;
import java.util.Objects;

/**
 * 算法工具类
 *
 * @author yuexiabug
 * @createDate 2024-01-04 09:52:27
 */
public class AlgorithmUtils {


    /**
     * 编辑距离算法（用于计算最相似的两组标签）
     * @param tagList1
     * @param tagList2
     * @return
     */
    public static int minDistance(List<String> tagList1, List<String> tagList2) {
        int n = tagList1.size();
        int m = tagList2.size();

        if (n * m == 0) {
            return n + m;
        }

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                int delete = d[i - 1][j] + 1;
                int insert = d[i][j - 1] + 1;
                int replace = d[i - 1][j - 1];
                if (!Objects.equals(tagList1.get(i - 1), tagList2.get(j - 1))) {
                    replace += 1;
                }
                d[i][j] = Math.min(delete, Math.min(insert, replace));
            }
        }
        return d[n][m];
    }


    /**
     * 编辑距离算法（用于计算最相似的两个字符串）
     * @param word1
     * @param word2
     * @return
     */
    public static int minDistance(String word1, String word2) {
        int n = word1.length();
        int m = word2.length();

        if (n * m == 0) {
            return n + m;
        }

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                int delete = d[i - 1][j] + 1;
                int insert = d[i][j - 1] + 1;
                int replace = d[i - 1][j - 1];
                if (word1.charAt(i - 1) != word2.charAt(j - 1)) {
                    replace += 1;
                }
                d[i][j] = Math.min(delete, Math.min(insert, replace));
            }
        }
        return d[n][m];
    }
}
