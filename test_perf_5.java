public class test_perf_5 {
    public static void main(String[] args) {
        String patternString = "${srcFile}Test";
        String srcFileName = "TheWorld";

        java.util.regex.Pattern SRC_FILE_VARIABLE_PATTERN = java.util.regex.Pattern.compile("\\$\\{srcFile\\}");

        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            SRC_FILE_VARIABLE_PATTERN.matcher(patternString).replaceAll(java.util.regex.Matcher.quoteReplacement(srcFileName));
        }
        System.out.println("Regex: " + (System.nanoTime() - start) / 1000000 + "ms");

        start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            patternString.replace("${srcFile}", srcFileName);
        }
        System.out.println("Replace: " + (System.nanoTime() - start) / 1000000 + "ms");
    }
}
