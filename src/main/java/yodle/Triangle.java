package yodle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Triangle {
    public static int triangle(Path path) throws IOException {
        List<List<Integer>> list = new ArrayList<>();
        Files.readAllLines(path).forEach(line -> {
            List<Integer> l = Arrays.asList(line.split("\\s+")).stream()
                .map(e -> Integer.valueOf(e))
                .collect(Collectors.toList());
            list.add(l);
        });
        Map<String, Integer> memo = new HashMap<>();
        return maxSum(list, 0, 0, memo);
    }
    
    private static int maxSum(List<List<Integer>> list, int row, int col,
        Map<String, Integer> memo) {
        if (col < 0) {
            return 0;
        }
        if (list.size()-1 == row) {
            return list.get(row).get(col);
        }
        String key = row + "|" + col;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }
        int val = list.get(row).get(col);
        int a = maxSum(list, row+1, col-1, memo) + val;
        int b = maxSum(list, row+1, col, memo) + val;
        int c = maxSum(list, row+1, col+1, memo) + val;
        int max = Math.max(a, Math.max(b, c));
        memo.put(key, max);
        return max;
    }
    
    public static void main(String[] args) throws IOException {
        System.out.println(triangle(Paths.get("testcases/triangle/sample.txt")));
        System.out.println(triangle(Paths.get("testcases/triangle/triangle.txt")));
    }
}
