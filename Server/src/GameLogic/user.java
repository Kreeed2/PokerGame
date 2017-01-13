package GameLogic;

import java.io.*;

public class user {
    private String name;
    private String password;

    public user(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public void editTextFile() {
        try {
            String content = name + ":" + password;
            String path = "Userdata.txt";
            File file = new File(path);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            // write in file
            bw.write(content);
            // close connection
            bw.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
