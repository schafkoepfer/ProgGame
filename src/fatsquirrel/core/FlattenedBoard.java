package fatsquirrel.core;

import fatsquirrel.XY;
import fatsquirrel.core.Entities.*;
import fatsquirrel.core.Entities.PlayerEntities.MasterSquirrel;
import fatsquirrel.core.Entities.PlayerEntities.MiniSquirrel;
import fatsquirrel.core.Entities.PlayerEntities.PlayerEntity;

import java.util.Random;

public class FlattenedBoard implements EntityContext, BoardView {

    private final Board board;
    private final int width;
    private final int height;

    public FlattenedBoard(int width, int height, Board board){
        this.width = width;
        this.height = height;
        this.board = board;
    }

    @Override
    public XY getSize() {
        return new XY(width, height);
    }

    @Override
    public void tryMove(MiniSquirrel miniSquirrel, XY moveDirection) {

        if(moveDirection==null)
            return;
        if(moveDirection.X == 0 && moveDirection.Y == 0)
            return;

        int x = miniSquirrel.getPosition().X+moveDirection.X;
        int y = miniSquirrel.getPosition().Y+moveDirection.Y;

        //Noch Energy vorhanden
        if (miniSquirrel.getEnergy() >= 1) {
            if(board.getEntity(x,y) == null) {
                board.moveEntity(miniSquirrel, miniSquirrel.getPosition().add(moveDirection));
                miniSquirrel.updateEnergy(-1);
            }
            else if(board.getEntity(x,y) instanceof MasterSquirrel){
                //Kollision mit eigenem MasterSquirrel
                if(board.getEntity(x,y) == miniSquirrel.getMasterSquirrel()){
                    miniSquirrel.updateEnergy(-1);
                    board.getEntity(x,y).updateEnergy(miniSquirrel.getEnergy());
                    kill(miniSquirrel);
                }
                else {
                    kill(miniSquirrel);
                }
            }
            else if(board.getEntity(x,y) instanceof MiniSquirrel){
                MiniSquirrel miniSquirrelTemp = (MiniSquirrel)board.getEntity(x,y);
                //Kollision mit fremden MiniSquirrel
                if(miniSquirrel.getMasterSquirrel() != miniSquirrelTemp.getMasterSquirrel()){
                    miniSquirrel.updateEnergy(0);
                    board.getEntity(x,y).updateEnergy(0);
                    kill(miniSquirrel);
                    kill(miniSquirrelTemp);
                }
            }
            else if(board.getEntity(x,y) instanceof GoodPlant
                    || board.getEntity(x,y) instanceof BadPlant){
                miniSquirrel.updateEnergy(board.getEntity(x,y).getEnergy());
                killAndReplace(board.getEntity(x,y));

                board.moveEntity(miniSquirrel, miniSquirrel.getPosition().add(moveDirection));
            }
        }
        else if (miniSquirrel.getEnergy() == 0) {
            kill(miniSquirrel);
        }
    }

    @Override
    public void tryMove(GoodBeast goodBeast, XY moveDirection) {

        if(moveDirection==null)
            return;
        if(moveDirection.X == 0 && moveDirection.Y == 0)
            return;

        int x = goodBeast.getPosition().X+moveDirection.X;
        int y = goodBeast.getPosition().Y+moveDirection.Y;

        if(board.getEntity(x,y) == null){
            board.moveEntity(goodBeast, goodBeast.getPosition().add(moveDirection));
        }
        else if(board.getEntity(x,y) instanceof PlayerEntity){
            board.getEntity(x,y).updateEnergy(goodBeast.getEnergy());
            killAndReplace(goodBeast);
        }

    }

    @Override
    public void tryMove(BadBeast badBeast, XY moveDirection) {

        if(moveDirection==null)
            return;
        if(moveDirection.X == 0 && moveDirection.Y == 0)
            return;

        int x = badBeast.getPosition().X+moveDirection.X;
        int y = badBeast.getPosition().Y+moveDirection.Y;

        if(board.getEntity(x,y) == null){
            board.moveEntity(badBeast, badBeast.getPosition().add(moveDirection));
        }
        else if(board.getEntity(x,y) instanceof PlayerEntity){
            if(badBeast.getBiteCounter()>1){
                board.getEntity(x,y).updateEnergy(badBeast.getEnergy());
                if(board.getEntity(x,y).getEnergy()<=0){
                    kill(board.getEntity(x,y));
                }
                badBeast.decreaseBiteCounter();
            }
            else if(badBeast.getBiteCounter()==1){
                board.getEntity(x,y).updateEnergy(badBeast.getEnergy());
                if(board.getEntity(x,y).getEnergy()<=0){
                    kill(board.getEntity(x,y));
                }
                badBeast.decreaseBiteCounter(); //Reset BiteCounter
                killAndReplace(badBeast);
            }
        }
    }

    @Override
    public void tryMove(MasterSquirrel masterSquirrel, XY moveDirection) {

        if(moveDirection==null)
            return;
        if(moveDirection.X == 0 && moveDirection.Y == 0)
            return;

        //aktuelle Position holen
        int x = masterSquirrel.getPosition().X+moveDirection.X;
        int y = masterSquirrel.getPosition().Y+moveDirection.Y;

        //Kollisionsabfrage
        if(board.getEntity(x,y) == null){
            board.moveEntity(masterSquirrel, masterSquirrel.getPosition().add(moveDirection));
        }
        else if(board.getEntity(x,y) instanceof Wall){
            masterSquirrel.setCollisionCounter(3);
            masterSquirrel.updateEnergy(-50);
            return;
        }
        else if(board.getEntity(x,y) instanceof GoodPlant
                || board.getEntity(x,y) instanceof BadPlant){
            masterSquirrel.updateEnergy(board.getEntity(x,y).getEnergy());
            killAndReplace(board.getEntity(x,y));

            board.moveEntity(masterSquirrel, masterSquirrel.getPosition().add(moveDirection));
        }
        else if(board.getEntity(x,y) instanceof GoodBeast
                || board.getEntity(x,y) instanceof BadBeast){
            masterSquirrel.updateEnergy(board.getEntity(x,y).getEnergy());
            killAndReplace(board.getEntity(x,y));

            board.moveEntity(masterSquirrel, masterSquirrel.getPosition().add(moveDirection));
        }
        else if(board.getEntity(x,y) instanceof MiniSquirrel){
            MasterSquirrel masterSquirrelTemp = ((MiniSquirrel)board.getEntity(x,y)).getMasterSquirrel();
            if(masterSquirrelTemp == masterSquirrel){
                masterSquirrel.updateEnergy(board.getEntity(x,y) .getEnergy());
                kill(board.getEntity(x,y));
            }
            else{
                masterSquirrel.updateEnergy(150);
                kill(board.getEntity(x,y));
            }

            board.moveEntity(masterSquirrel, masterSquirrel.getPosition().add(moveDirection));
        }


    }

    @Override
    public PlayerEntity nearestPlayerEntity(XY pos) {

        PlayerEntity nearestPlayerEntity = null;
        int xDiff = width;
        int yDiff = height;

        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if(board.getEntity(i,j) instanceof PlayerEntity){
                    if(Math.abs(pos.X-i)+Math.abs(pos.Y-j)<xDiff+yDiff){
                        xDiff = Math.abs(pos.X-i);
                        yDiff = Math.abs(pos.Y-j);
                        nearestPlayerEntity = (PlayerEntity)board.getEntity(i,j);
                    }
                }
            }
        }

        return nearestPlayerEntity;
    }

    @Override
    public void kill(Entity entity) {
        if(entity instanceof MasterSquirrel){
            System.out.println("Mastersquirrel mit der ID " + entity.getID() + " hat verloren!");
            System.exit(0);
        }
        else{
            board.removeEntity(entity);
        }
    }

    @Override
    public void killAndReplace(Entity entity) {
        boolean success = false;
        while(!success) {
            int x = new Random().nextInt(width - 1);
            int y = new Random().nextInt(height - 1);
            if(board.setNewEntity(x,y, getEntityType(entity.getPosition())))
                success = true;

            kill(entity);
        }
    }

    @Override
    public EntityType getEntityType(XY xy) {
        EntityType type = EntityType.None;
        if(board.getEntity(xy.X, xy.Y)!=null) {
            switch (board.getEntity(xy.X, xy.Y).getClass().getSimpleName()) {
                case "Wall":
                    type = EntityType.Wall;
                    break;
                case "BadPlant":
                    type = EntityType.BadPlant;
                    break;
                case "GoodPlant":
                    type = EntityType.GoodPlant;
                    break;
                case "BadBeast":
                    type = EntityType.BadBeast;
                    break;
                case "GoodBeast":
                    type = EntityType.GoodBeast;
                    break;
                case "HandOperatedMasterSquirrel":
                    type = EntityType.HandOperatedMasterSquirrel;
                    break;
                case "MasterSquirrelBot":
                    type = EntityType.MasterSquirrelBot;
                    break;
                case "MasterSquirrel":
                    type = EntityType.MasterSquirrel;
                    break;
                case "MiniSquirrel":
                    type = EntityType.MiniSquirrel;
                    break;
                case "StandardMiniSquirrel":
                    type = EntityType.StandardMiniSquirrel;
                    break;
            }
        }
        return type;
    }
    @Override
    public EntityType getEntityType(int x, int y) {
        return getEntityType(new XY(x,y));
    }

    @Override
    public void spawn_Mini(XY direction, int energy, MasterSquirrel masterSquirrel){
        int x = masterSquirrel.getPosition().X +direction.X;
        int y=masterSquirrel.getPosition().Y +direction.Y;
        board.setNewMiniSquirrel(x, y, energy, masterSquirrel);
    }

    public String allEntitiesToString(){
        return board.allEntitiesToString();
    }

    @Override
    public Entity getEntityAt(int x, int y){
        return board.getEntity(x,y);
    }
}
