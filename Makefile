JAVA_HOME  ?= $(shell /usr/libexec/java_home 2>/dev/null || echo "/Users/oxesoft/Library/Java/JavaVirtualMachines/jbr-21.0.11/Contents/Home")
JAVAC      := $(if $(wildcard $(JAVA_HOME)/bin/javac),$(JAVA_HOME)/bin/javac,javac)
JAVA       := $(if $(wildcard $(JAVA_HOME)/bin/java),$(JAVA_HOME)/bin/java,java)
JFLAGS     := -encoding UTF-8
SRC_DIR    := src
BIN_DIR    := bin
MAIN_CLASS := kosmos.Main

SOURCES    := $(shell find $(SRC_DIR) -name "*.java")

.PHONY: all compile run clean

all: compile

compile:
	@mkdir -p $(BIN_DIR)
	$(JAVAC) $(JFLAGS) -d $(BIN_DIR) -sourcepath $(SRC_DIR) $(SOURCES)

run: compile
	$(JAVA) -cp $(BIN_DIR) $(MAIN_CLASS)

clean:
	rm -rf $(BIN_DIR)
