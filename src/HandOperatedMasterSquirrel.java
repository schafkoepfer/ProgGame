import java.io.IOException;

public class HandOperatedMasterSquirrel extends MasterSquirrel {

    public HandOperatedMasterSquirrel(int id, int energy, XY position) {
        super(id, energy, position);
    }

    public void nextStep() throws IOException {

        while(true){
            char op = (char)System.in.read();
            System.in.read(new byte[System.in.available()]);

            switch (op){
                case ('a'):
                    toString("LEFT");
                    position = position.ADD(new XY(-1,0));
                    return;
                case ('d'):
                    toString("RIGHT");
                    position = position.ADD(new XY(1,0));
                    return;
                case ('w'):
                    toString("UP");
                    position = position.ADD(new XY(0,1));
                    return;
                case ('s'):
                    toString("DOWN");
                    position = position.ADD(new XY(0,-1));
                    return;
                default:
                    System.out.println("Keine Richtung ausgewählt!\nBitte erneut auswählen.");
                    break;
            }
        }


    }

    private static void toString(String direction){
        System.out.println("\nMastersquirrel turns: " + direction + "\n");
    }

}
