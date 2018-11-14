package server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerLogs {

    private final List<String> entries = Collections.synchronizedList(new ArrayList<>());

    public void appendEntry(String entry){
        entries.add(entry);
        System.out.print(entry);
    }
    public String[] getLogs(){
        return entries.toArray(new String[0]);
    }
}
