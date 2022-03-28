# Input Java files (components).
JAVA_FILES=TreeMain.java

ASPECTJRT=/home/andreas/aspectj1.9/lib/aspectjrt.jar

aspectJCompile: ${JAVA_FILES}
	@ajc -source 1.8 ${JAVA_FILES}

tree: aspectJCompile
	@echo "== Running with AspectJ =="
	@java -cp ${ASPECTJRT}:. TreeMain
