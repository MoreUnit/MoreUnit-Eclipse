## 2026-05-09 - Avoid Pre-compiled Regex for Literal Replacements
**Learning:** In modern JDKs (Java 21 used here), `String.replace()` without regex compilation overhead is significantly faster and cleaner for exact string matches than `Pattern.compile().matcher().replaceAll()`.
**Action:** When finding micro-optimizations, look for regex replacements that use literal escaping (e.g., `\Q...\E` or literal `.*` replacements) and convert them to simple `String.replace()`.
