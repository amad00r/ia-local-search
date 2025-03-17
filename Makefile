SRC_DIR = src
BIN_DIR = bin

CLASSES := \
	$(BIN_DIR)/RedSensoresEstado.class \
	$(BIN_DIR)/RedSensoresSuccessorFunction.class \
	$(BIN_DIR)/Main.class

CLASSPATH := $(BIN_DIR):lib/AIMA.jar:lib/RedSensores.jar

.PHONY: all
all: $(CLASSES)

$(BIN_DIR):
	mkdir -p $(BIN_DIR)

$(BIN_DIR)/%.class: $(SRC_DIR)/%.java | $(BIN_DIR)
	javac -cp $(CLASSPATH) -d $(BIN_DIR) $<

.PHONY: run
run: all
	java -ea -cp $(CLASSPATH) redsensores/Main

.PHONY: clean
clean:
	rm -rf $(BIN_DIR)
