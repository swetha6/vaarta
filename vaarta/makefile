JCC = javac
JFLAGS = -g -classpath $(JAVA_HOME)/jre/lib/ext/jfxrt.jar:src
PKGA = src/appui
PKGB = src/logic

default: Controller.class\
	Data.class\
	Main.class\
	popupController.class\
	CommonPool.class\
	Feed.class\
	Item.class\
	Merge.class\
	ParallelMerge.class\
	Parser.class\
	Sort.class\
	Strategy.class

Controller.class: $(PKGA)/Controller.java
	$(JCC) $(JFLAGS) $(PKGA)/Controller.java

Data.class: $(PKGA)/Data.java
	$(JCC) $(JFLAGS) $(PKGA)/Data.java

Main.class: $(PKGA)/Main.java
	$(JCC) $(JFLAGS) $(PKGA)/Main.java

popupController.class: $(PKGA)/popupController.java
	$(JCC) $(JFLAGS) $(PKGA)/popupController.java

CommonPool.class: $(PKGB)/CommonPool.java
	$(JCC) $(JFLAGS) $(PKGB)/CommonPool.java

Feed.class: $(PKGB)/Feed.java
	$(JCC) $(JFLAGS) $(PKGB)/Feed.java

Item.class: $(PKGB)/Item.java
	$(JCC) $(JFLAGS) $(PKGB)/Item.java

Merge.class: $(PKGB)/Merge.java
	$(JCC) $(JFLAGS) $(PKGB)/Merge.java

ParallelMerge.class: $(PKGB)/ParallelMerge.java
	$(JCC) $(JFLAGS) $(PKGB)/ParallelMerge.java

Parser.class: $(PKGB)/Parser.java
	$(JCC) $(JFLAGS) $(PKGB)/Parser.java

Sort.class: $(PKGB)/Sort.java
	$(JCC) $(JFLAGS) $(PKGB)/Sort.java

Strategy.class: $(PKGB)/Strategy.java
	$(JCC) $(JFLAGS) $(PKGB)/Strategy.java
clean:
	$(RM) *.class
	$(RM) $(PKGA)/*.class
	$(RM) $(PKGB)/*.class
