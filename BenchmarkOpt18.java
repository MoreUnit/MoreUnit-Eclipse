public class BenchmarkOpt18 {
    public static void main(String[] args) {
        String cleanSrcPath = "src/main/java/com/example/MyClass.java";
        String srcPathTpl = "src/main/java/.*";

        System.out.println("Wait, if srcPathTpl is a regex, and I use String.replaceFirst(srcPathTpl, ''), it treats srcPathTpl as a regex.");
        System.out.println("If I replaced it with startsWith, it treats srcPathTpl as a literal. So they behave completely differently!");

        System.out.println("Original code: String codePathWithinSrcFolder = cleanSrcPath.replaceFirst(srcPathTpl, \"\");");
        System.out.println("If srcPathTpl = \"src/main/java/.*\"");
        System.out.println("cleanSrcPath.replaceFirst(srcPathTpl, \"\") = " + cleanSrcPath.replaceFirst(srcPathTpl, ""));
    }
}
