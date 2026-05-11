public class test_perf_3 {
    public static void main(String[] args) {
        String str = "\\QMyClassTest\\E.*";

        java.util.regex.Pattern QUOTE_SEPARATORS_AND_WILDCARDS = java.util.regex.Pattern.compile("(?:\\\\Q|\\\\E|\\.\\*)");

        System.out.println("Regex result: " + QUOTE_SEPARATORS_AND_WILDCARDS.matcher(str).replaceAll(""));
        System.out.println("Replace result: " + str.replace("\\Q", "").replace("\\E", "").replace(".*", ""));
    }
}
