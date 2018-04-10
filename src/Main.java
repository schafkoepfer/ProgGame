import java.io.IOException;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws IOException {

        Random random = new Random();

        EntitySet entitySet = new EntitySet(20);

        MasterSquirrel masterSquirrel = new HandOperatedMasterSquirrel(0, 1000, new XY(20, 20));
        MiniSquirrel miniSquirrel = new MiniSquirrel(1, 50, new XY(random.nextInt(50), random.nextInt(50)), masterSquirrel);
        MiniSquirrel miniSquirrel2 = new MiniSquirrel(2, 50, new XY(random.nextInt(50), random.nextInt(50)), masterSquirrel);
        GoodPlant goodPlant = new GoodPlant(3, 20, new XY(21, 20));
        GoodPlant goodPlant2 = new GoodPlant(4, 10, new XY(random.nextInt(50), random.nextInt(50)));
        BadPlant badPlant = new BadPlant(5, 20, new XY(random.nextInt(50), random.nextInt(50)));

        entitySet.addEntity(masterSquirrel);
        entitySet.addEntity(miniSquirrel);
        entitySet.addEntity(miniSquirrel2);
        entitySet.addEntity(goodPlant);
        entitySet.addEntity(goodPlant2);
        entitySet.addEntity(badPlant);

        System.out.println(entitySet.toString());

        //Loeschen einer Entity
        entitySet.deleteEntity(miniSquirrel2);
        System.out.println(entitySet.toString());

        //Energie setzen einer Entity
        miniSquirrel.setEnergy(20);
        System.out.println(entitySet.toString());

        //Energie senden
        masterSquirrel.sendEnergy(miniSquirrel, 80);
        System.out.println(entitySet.toString());

        while (true) {
            entitySet.nextStep();

            if (masterSquirrel.testTile() != null) {
                Entity other = masterSquirrel.testTile();
                if (other instanceof GoodPlant) {
                    masterSquirrel.AddEnergy(other.getEnergy());
                } else if (other instanceof BadPlant) {
                    masterSquirrel.AddEnergy(-other.getEnergy());
                } else if (other instanceof GoodBeast) {
                    masterSquirrel.AddEnergy(other.getEnergy());
                } else if (other instanceof BadBeast) {
                    masterSquirrel.AddEnergy(-other.getEnergy());
                }

                entitySet.deleteEntity(other);
            }

            System.out.println(entitySet.toString());
        }
    }
}

