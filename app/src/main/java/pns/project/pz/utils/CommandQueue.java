package pns.project.pz.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pns.project.pz.commands.Command;

public class CommandQueue{
    private static final String TAG = CommandQueue.class.getCanonicalName();

    private CommandQueue(){}

    private static final CommandQueue instanceOf = new CommandQueue();

    public static CommandQueue getInstance() {
        return instanceOf;
    }

    private static final List<Command> commands = new ArrayList<>();

    public void push(Command command) {
        try{
            commands.add(command);
        } catch (Exception e){
            Log.d("Exception : ",e.getMessage());
        }
    }

    public Command pop() {
       Command cmd = Command.noOp();
        try{
           if(commands.size() > 0) {
               cmd = commands.remove(0);
           }
       } catch(Exception e){
            Log.d("Exception : ",e.getMessage());
       }
        return cmd;
    }


}

