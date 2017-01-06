package Network;

public class DatabaseObject {
    String name;
    String pass;

    public DatabaseObject(String input) {
        String splitString[] = input.split(":");
        name = splitString[0];
        pass = splitString [1];
    }

    public String getName() {
        return name;
    }

    public String getPass() {
        return pass;
    }

    @Override
    public String toString() {
        return name + ":" + pass + "\n";
    }
}
