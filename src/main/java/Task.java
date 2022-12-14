import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Task {

    public static final String SEG_A_FILE = "testfile_a.s";
    public static final String SEG_B_FILE = "testfile_b.s";

    public static final String FUN_A_FILE = "testfile_a.f";
    public static final String FUN_B_FILE = "testfile_b.f";

    public static void main(String[] args) {

        Task task = new Task();

        List<int[]> seg_a = task.readSegment(SEG_A_FILE);
        List<int[]> seg_b = task.readSegment(SEG_B_FILE);

        List<Double> fun_a = task.readFunction(FUN_A_FILE);
        List<Double> fun_b = task.readFunction(FUN_B_FILE);


        //test (example in the document)
//        int[] a = {1, 2};
//        int[] b = {3, 6};
//        int[] c = {0, 1};
//        int[] d = {1, 5};
//        List<int[]> seg_a = new ArrayList<int[]>();
//        seg_a.add(a);
//        seg_a.add(b);
//        List<int[]> seg_b = new ArrayList<int[]>();
//        seg_b.add(c);
//        seg_b.add(d);
//        Double[] x = {10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0};
//        Double[] y = {10.5, 11.5, 12.0, 13.0, 13.5, 15.0, 14.0};
//        List<Double> fun_a = Arrays.asList(x);
//        List<Double> fun_b = Arrays.asList(y);

        // Task 1
        ArrayList<Integer> overlaps = task.findOverlaps(seg_a, seg_b);
        System.out.println("overlap: " + overlaps.size());

        // Task 2
        double cor = task.computeCorrelation(fun_a,fun_b);
        System.out.println(cor);

        // Task 3
        ArrayList<Integer> index = task.findIndexFromSegment(seg_a);
        double mean = task.computeMeanofCoveredNums(index, fun_b);
        System.out.println(mean);


    }

    // for task 1
    private ArrayList<Integer> findOverlaps(List<int[]> seg_a, List<int[]> seg_b) {

        DecimalFormat formatter = new DecimalFormat("#0.00000");

        //Running time: 0.35000 seconds (find overlap values in small segment file from big segment file)
        //Running time: 0.08000 seconds (find overlap values in big segment file from small segment file, which is about 77% faster)

        ArrayList<Integer> overlaps = new ArrayList<Integer>();
        int pivot = 0;
        List<int[]> first = seg_a;
        List<int[]> second = seg_b;
        if (seg_a.size() > seg_b.size()) {
            first = seg_b;
            second = seg_a;
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < first.size(); i++) {
            int length_a = first.get(i)[1] - first.get(i)[0];
            int[] values_a = new int[length_a];
            values_a[0] = first.get(i)[0];
            for (int t = 1; t < length_a; t++) {
                values_a[t] = values_a[t - 1] + 1;
            }
            for (int j = pivot; j < second.size(); j++) {
                if (values_a[length_a - 1] >= second.get(j)[0]) {
                    for (int t = 0; t < length_a; t++) {
                        if (values_a[t] < second.get(j)[1] && values_a[t] >= second.get(j)[0]) {
                            overlaps.add(values_a[t]);
                        }
                    }

                } else {
                    if (j > 1) {
                        pivot = j - 1;
                    }
                    break;
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Running time: " + formatter.format((end - start) / 1000d) + " seconds");
        return overlaps;
    }

    // for task 2
    private double computeCorrelation(List<Double> a, List<Double> b) {
        if (a.size() != b.size()) return -1d;

        double mean_a = mean(a);
        double mean_b = mean(b);
        double numerator = 0d;
        double denominator_a = 0d;
        double denominator_b = 0d;

        for (int i = 0; i < a.size(); i++) {
            numerator = numerator + (a.get(i) - mean_a) * (b.get(i) - mean_b);
            denominator_a = denominator_a + Math.pow((a.get(i) - mean_a), 2);
            denominator_b = denominator_b + Math.pow((b.get(i) - mean_b), 2);
        }
        if (denominator_a == 0 || denominator_b == 0) return -1d;

        return numerator / (Math.sqrt(denominator_a) * Math.sqrt(denominator_b));
    }

    public static double mean(List<Double> a) {
        double sum = 0d;
        for (int i = 0; i < a.size(); i++) {
            sum = sum + a.get(i);
        }
        return sum / a.size();
    }

    // for task 3
    private ArrayList<Integer> findIndexFromSegment(List<int[]> seg_a) {
        ArrayList<Integer> index = new ArrayList<Integer>();
        for (int i = 0; i < seg_a.size(); i++) {
            int length = seg_a.get(i)[1] - seg_a.get(i)[0];
            index.add(seg_a.get(i)[0]);
            for (int t = 1; t < length; t++) {
                index.add(seg_a.get(i)[0] + t);
            }
        }
        return index;
    }

    // for task 3
    private double computeMeanofCoveredNums(List<Integer> index, List<Double> fun) {
        if (index.size() < 1) return -1d;
        double sum = 0;
        for (int i = 0; i < index.size(); i++) {
            sum = sum + fun.get(index.get(i));
        }
        return sum / index.size();
    }

    private List<int[]> readSegment(String file) {
        List<int[]> segment = new ArrayList<int[]>();
        String line = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            line = in.readLine();
            while (line != null) {
                String[] itemStr = line.split("\t");
                int[] itemNum = new int[itemStr.length];
                itemNum[0] = Integer.parseInt(itemStr[0]);
                itemNum[1] = Integer.parseInt(itemStr[1]);
                segment.add(itemNum);
                line = in.readLine();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return segment;
    }

    private List<Double> readFunction(String file) {
        List<Double> function = new ArrayList<Double>();
        String line = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            line = in.readLine();
            while (line != null) {
                function.add(Double.parseDouble(line));
                line = in.readLine();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return function;
    }
}