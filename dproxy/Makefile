JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java

# Input Java files (components).
CLASSES = \
	Automobile.java \
	Motorcycle.java \
	Sedan.java \
	SportsCar.java \
	Vehicle.java \
	Visitor.java \
	DynamicPrinterProxy.java
	
default: classes

classes: $(CLASSES:.java=.class)

dproxy: classes
	java DynamicPrinterProxy
	
clean:
	$(RM) *.class