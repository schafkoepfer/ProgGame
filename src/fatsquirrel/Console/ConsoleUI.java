package fatsquirrel.Console;

import fatsquirrel.UI;
import fatsquirrel.core.BoardView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ConsoleUI implements UI {

    PrintStream outputStream = System.out;
    BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public void render(BoardView view) {
        for(int y=0;y<view.getSize().Y;y++){
            for(int x=0;x<view.getSize().X;x++){
                switch (view.getEntityType(x,y).getType()){
                    case None:
                        System.out.print(" ");
                        break;
                    case Wall:
                        System.out.print("#");
                        break;
                    case BadBeast:
                        System.out.print("B");
                        break;
                    case GoodBeast:
                        System.out.print("b");
                        break;
                    case BadPlant:
                        System.out.print("X");
                        break;
                    case GoodPlant:
                        System.out.print("O");
                        break;
                    case HandOperatedMasterSquirrel:
                        System.out.print("M");
                        break;
                    case MiniSquirrel:
                        System.out.print("m");
                        break;
                    case StandardMiniSquirrel:
                        System.out.print("m");
                        break;
                }
            }
            System.out.println();
        }
    }

    @Override
    public MoveCommand getCommand() throws IOException {
        CommandScanner commandScanner = new CommandScanner(GameCommandType.values(), inputReader);

        while (true) { // the loop over all commands with one input line for every command

            Command command = commandScanner.next();
            if(command != null) {
                Class[] params = new Class[command.getParams().length];
                for (int i = 0; i < params.length; i++)
                    params[i] = command.getParams()[i].getClass();

                Method method = null;
                try {
                    method = this.getClass().getDeclaredMethod(command.getCommandType().getName(), params);
                    if (method.getReturnType() == MoveCommand.class)                                                //Evtl noch abstrakter machen
                        return (MoveCommand) (method.invoke(this, command.getParams()));
                    else
                        method.invoke(this, null);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //in andre klasse, damit scanner unabhängig von UI ist

    private void exit(){
        System.exit(0);
    }

    private void help(){
        for(GameCommandType commandType: GameCommandType.values()){
            outputStream.println("<" +commandType.getName() + "> - " + commandType.getHelpText());
        }
    }

    private MoveCommand all(){
        return new MoveCommand (0,0,0, true, false);
    }

    private MoveCommand left(){
        return move(-1,0);
    }

    private MoveCommand right(){
        return move(1,0);
    }

    private MoveCommand up(){
        return move(0,-1);
    }

    private MoveCommand down(){
        return move(0,1);
    }

    private MoveCommand move(int x, int y){
        return new MoveCommand(x,y, 0, false, false);
    }

    private MoveCommand master_energy(){
        return new MoveCommand(0,0,0,false, true);
    }

    private MoveCommand spawn_mini (Integer miniEnergy) {
        return new MoveCommand(0,0, miniEnergy, false, false);
    }

}