public class BenchmarkOpt20 {
    public static void main(String[] args) {
        String codePathWithinSrcFolder = "cleanSrcPath";
        String tstPathTpl = "tstPathTpl";
        String cleanTestPath = "cleanTestPath";

        System.out.println(cleanTestPath.replaceFirst(tstPathTpl, ""));
    }
}
