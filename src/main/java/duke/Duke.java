package duke;

import java.io.IOException;

public class Duke {
    private final Ui ui;
    private final Parser parser;
    private boolean hasExited;
    private final String dest;

    /**
     * Constructor with specified directory of database.
     *
     * @param dest directory of database
     */
    public Duke(String dest) {
        this.dest = dest;
        ui = new Ui();
        parser = new Parser();
    }

    private void run() {
        System.setProperty("file.encoding", "UTF-8");
        Storage storage;
        TaskList taskList;
        try {
            storage = new Storage(dest);
            taskList = storage.loadFile();
        } catch (IOException e) {
            ui.printNicely(e.getMessage());
            return;
        }
        ui.greet();
        ui.listOut(taskList);
        while (!hasExited) {
            try {
                String input = ui.nextLine();
                switch (parser.parseInput(input)) {
                case ADD:
                    taskList.add(parser.parseInputTask(input));
                    ui.printNicely("Noted, I have added the following task:",
                            parser.parseInputTask(input).toString());
                    break;
                case DONE:
                    taskList.markDone(parser.parseDone(input));
                    ui.printNicely("Noted, I have marked the following task as done:",
                            taskList.get(parser.parseDone(input)).toString());
                    break;
                case DELETE:
                    ui.printNicely("Noted, I have deleted the following task:",
                            taskList.get(parser.parseDelete(input)).toString());
                    taskList.delete(parser.parseDelete(input));
                    ui.listOut(taskList);
                    break;
                case FIND:
                    TaskList newList = taskList.find(parser.parseFind(input));
                    ui.listOut(String.format("I have found the following %d task(s)", newList.size()),
                            newList);
                    break;
                case LIST:
                    ui.listOut(taskList);
                    break;
                case BYE:
                    ui.bye();
                    hasExited = true;
                    return;
                default:
                    throw new IOException("Cannot be understood.");
                }
                storage.writeFile(taskList);
            } catch (Exception e) {
                ui.printNicely(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new Duke("data/duke.txt").run();
    }
}
