package server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Some server logs that can be used for debugging.
 * Functionality can easily be added to monitor the different
 * users playing the game.
 */

public class ServerLogs {

    private final List<String> entries = Collections.synchronizedList(new ArrayList<>());

    public void appendEntry(String entry){
        entries.add(entry);
        System.out.print(entry);
    }
    //not used atm
    public String[] getLogs(){
        return entries.toArray(new String[0]);
    }
}
