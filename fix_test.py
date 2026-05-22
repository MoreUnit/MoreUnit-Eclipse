import re

with open('org.moreunit.core.test/test/org/moreunit/core/config/ModuleTest.java', 'r') as f:
    content = f.read()

content = content.replace("boolean prepareCalled = false;", "boolean prepareCalled;")
content = content.replace("boolean cleanCalled = false;", "boolean cleanCalled;")

with open('org.moreunit.core.test/test/org/moreunit/core/config/ModuleTest.java', 'w') as f:
    f.write(content)
