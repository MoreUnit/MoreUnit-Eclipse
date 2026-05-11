public class test_perf_4 {
    public static void main(String[] args) {
        String patternString = "${srcFile}Test";
        String srcFileName = "TheWorld";

        java.util.regex.Pattern SRC_FILE_VARIABLE_PATTERN = java.util.regex.Pattern.compile("\\$\\{srcFile\\}");

        System.out.println("Regex result: " + SRC_FILE_VARIABLE_PATTERN.matcher(patternString).replaceAll(java.util.regex.Matcher.quoteReplacement(srcFileName)));
        System.out.println("Replace result: " + patternString.replace("${srcFile}", srcFileName));
    }
}
