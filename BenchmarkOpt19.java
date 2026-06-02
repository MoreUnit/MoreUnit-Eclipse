import java.util.regex.Pattern;

public class BenchmarkOpt19 {
    public static void main(String[] args) {
        String testProjTemplate = "prefix-${srcProject}-suffix";
        String SRC_PROJECT_VARIABLE = "${srcProject}";

        System.out.println(testProjTemplate.replaceFirst(Pattern.quote(SRC_PROJECT_VARIABLE), "\\\\E(.*)\\\\Q"));
        System.out.println(testProjTemplate.replace(SRC_PROJECT_VARIABLE, "\\E(.*)\\Q"));
    }
}
