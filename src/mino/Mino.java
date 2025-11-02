package mino;

import main.GamePanel;
import main.KeyHandler;
import main.PlayManager;

import java.awt.*;

public class Mino {

    public Block b[] = new Block[4];
    public Block tempB[] = new Block[4];
    int autoDropCounter = 0;
    public int direction = 1;
    boolean leftCollision, rightCollision, bottomCollision;
    public boolean active = true;
    public boolean deactivating;
    int deactivateCounter = 0;
    protected int anchorX, anchorY;

    public void create(Color c){
        b[0] = new Block(c);
        b[1] = new Block(c);
        b[2] = new Block(c);
        b[3] = new Block(c);
        tempB[0] = new Block(c);
        tempB[1] = new Block(c);
        tempB[2] = new Block(c);
        tempB[3] = new Block(c);
    }

    public void setXY(int x, int y){
        this.anchorX = x;
        this.anchorY = y;
        getDirection1();
    }
    public void updateXY(int direction){

        checkRotationCollision();

        if(!leftCollision && !rightCollision && !bottomCollision){
            this.direction = direction;
            b[0].x = tempB[0].x;    b[0].y = tempB[0].y;
            b[1].x = tempB[1].x;    b[1].y = tempB[1].y;
            b[2].x = tempB[2].x;    b[2].y = tempB[2].y;
            b[3].x = tempB[3].x;    b[3].y = tempB[3].y;

            anchorX = b[0].x;
            anchorY = b[0].y;
        }
    }

    public void getDirection1(){}
    public void getDirection2(){}
    public void getDirection3(){}
    public void getDirection4(){}

    public void checkMovementCollision(){
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        //Check static block collision
        checkStaticBlockCollision();

        //Check frame collision
        //Left wall
        for(int i = 0; i < b.length; i++){
            if(b[i].x == PlayManager.left_x){
                leftCollision = true;
            }
        }

        //Right wall
        for(int i = 0; i < b.length; i++){
            if(b[i].x + Block.SIZE == PlayManager.right_x){
                rightCollision = true;
            }
        }

        //Bottom
        for(int i = 0; i < b.length; i++){
            if(b[i].y + Block.SIZE == PlayManager.bottom_y){
                bottomCollision = true;
            }
        }

    }

    public void checkRotationCollision(){
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        //Check static block collision
        checkStaticBlockCollision();

        //Check frame collision
        //Left wall
        for(int i = 0; i < b.length; i++){
            if(tempB[i].x < PlayManager.left_x){
                leftCollision = true;
            }
        }

        //Right wall
        for(int i = 0; i < b.length; i++){
            if(tempB[i].x + Block.SIZE > PlayManager.right_x){
                rightCollision = true;
            }
        }

        //Bottom
        for(int i = 0; i < b.length; i++){
            if(tempB[i].y + Block.SIZE > PlayManager.bottom_y){
                bottomCollision = true;
            }
        }
    }

    private void checkStaticBlockCollision(){
        for(int i = 0; i < PlayManager.staticBlocks.size(); i++) {

            int targetX = PlayManager.staticBlocks.get(i).x;
            int targetY = PlayManager.staticBlocks.get(i).y;

            //Check down
            for (Block block : b) {
                if (block.y + Block.SIZE == targetY && block.x == targetX) {
                    bottomCollision = true;
                }
            }
            //Check left
            for (Block block : b) {
                if (block.x - Block.SIZE == targetX && block.y == targetY) {
                    leftCollision = true;
                }
            }

            //Check right
            for (Block block : b) {
                if (block.x + Block.SIZE == targetX && block.y == targetY) {
                    rightCollision = true;
                }
            }
        }
    }

    public void resetToSpawn() {

        // nulstil state
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        deactivating = false;
        deactivateCounter = 0;
        autoDropCounter = 0;

        active = true;
        direction = 1;  // start rotation

        // placer i top-center af brættet (PlayManager bestemmer koordinaterne)
        setXY(PlayManager.MINO_START_X, PlayManager.MINO_START_Y);

        // efter setXY() ligger b[] korrekt i rotation 1
        // opdater anchor efter pivot-blokken
        anchorX = b[0].x;
        anchorY = b[0].y;

        // sync tempB[] så første rotation virker
        for (int i = 0; i < 4; i++) {
            tempB[i].x = b[i].x;
            tempB[i].y = b[i].y;
            tempB[i].c = b[i].c;
        }
    }

    public void update() {

        if (KeyHandler.holdPressed && PlayManager.canHold) {

            // 1) Første gang: læg current i hold og spawn next med det samme
            if (PlayManager.holdMino == null) {

                PlayManager.holdMino = this;                 // gem den nuværende
                // skift current → next
                PlayManager.currentMino = PlayManager.nextMino;
                PlayManager.currentMino.resetToSpawn();

                // forbered ny next
                PlayManager.nextMino = PlayManager.pickMino();
                PlayManager.nextMino.setXY(PlayManager.NEXTMINO_X, PlayManager.NEXTMINO_Y);

            } else {
                // 2) Swap: byt current med det der ligger i hold
                Mino temp = PlayManager.holdMino;
                PlayManager.holdMino = this;

                PlayManager.currentMino = temp;              // gør hold-brikken aktiv
                PlayManager.currentMino.resetToSpawn();
            }

            // vigtigt: INGEN active=false her og INGEN staticBlocks-tilføjelse!
            PlayManager.canHold = false;                     // én gang pr. drop
            KeyHandler.holdPressed = false;
            return; // ikke mere input i denne frame
        }
        if (KeyHandler.spacePressed) {

            // Drop til ghost-position
            Block[] ghost = getGhostBlocks();
            for (int i = 0; i < 4; i++) {
                b[i].x = ghost[i].x;
                b[i].y = ghost[i].y;
            }

            // Opdatér anchor til pivot (antager b[0] = pivot)
            anchorX = b[0].x;
            anchorY = b[0].y;

            // Lås brikken med det samme i din arkitektur
            // (PlayManager bør i sin update spawne next, flytte b[] til staticBlocks, osv.)
            active = false;
            deactivating = false;
            deactivateCounter = 0;

            GamePanel.se.play(4, false);    // samme lyd som normal landing (valgfrit)
            KeyHandler.spacePressed = false;
            return; // undgå mere input i samme frame
        }

        if(deactivating){
            deactivating();
        }

        //Move the mino
        if (KeyHandler.upPressed) {
            switch (direction) {
                case 1: getDirection2();break;
                case 2: getDirection3();break;
                case 3: getDirection4();break;
                case 4: getDirection1();break;
            }
            KeyHandler.upPressed = false;
            GamePanel.se.play(3, false);
        }

        checkMovementCollision();

        if (KeyHandler.downPressed) {
            if (!bottomCollision) {
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;
                anchorY += Block.SIZE;

                autoDropCounter = 0;
            }
            KeyHandler.downPressed = false;
        }
        if (KeyHandler.leftPressed) {

            if (!leftCollision) {
                b[0].x -= Block.SIZE;
                b[1].x -= Block.SIZE;
                b[2].x -= Block.SIZE;
                b[3].x -= Block.SIZE;
                anchorX -= Block.SIZE;
            }
            KeyHandler.leftPressed = false;
        }
        if (KeyHandler.rightPressed) {

            if (!rightCollision) {
                b[0].x += Block.SIZE;
                b[1].x += Block.SIZE;
                b[2].x += Block.SIZE;
                b[3].x += Block.SIZE;
                anchorX += Block.SIZE;
            }
            KeyHandler.rightPressed = false;
        }
        if (bottomCollision) {
            if(!deactivating){
                GamePanel.se.play(4, false);
            }
            deactivating = true;
        } else {
            autoDropCounter++; // the counter increases every frame
            if (autoDropCounter == PlayManager.dropInterval) {
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;
                anchorY += Block.SIZE;
                autoDropCounter = 0;
            }
        }
    }

    private void deactivating(){

        deactivateCounter++;

        //Wait 45 frames to deactivate
        if(deactivateCounter == 45){

            deactivateCounter = 0;
            checkMovementCollision();

            //If bottom is hitting after 45 frames, deactivate mino
            if(bottomCollision){
                active = false;
            }
        }
    }

    private boolean willHitBottom(Block[] blocks) {
        // Bundramme
        for (Block b : blocks) {
            if (b.y + Block.SIZE == PlayManager.bottom_y) return true;
        }
        // Statiske klodser
        for (Block s : PlayManager.staticBlocks) {
            for (Block b : blocks) {
                if (b.x == s.x && b.y + Block.SIZE == s.y) return true;
            }
        }
        return false;
    }

    public Block[] getGhostBlocks() {
        Block[] ghost = new Block[4];
        for (int i = 0; i < 4; i++) {
            ghost[i] = new Block(java.awt.Color.LIGHT_GRAY);
            ghost[i].x = b[i].x;
            ghost[i].y = b[i].y;
        }

        // Hvis vi allerede står på noget, flyt ikke
        if (willHitBottom(ghost)) return ghost;

        // Flyt ned til første kollision (safety mod edge cases)
        int safety = 0;
        while (!willHitBottom(ghost) && safety++ < PlayManager.ROWS + 2) {
            for (Block g : ghost) g.y += Block.SIZE;
        }
        return ghost;
    }


    public void draw(Graphics2D g2){

        int margin = 2;
        // 1) Ghost (grå)
        Block[] ghost = getGhostBlocks();
        g2.setColor(java.awt.Color.LIGHT_GRAY);
        for (Block gb : ghost) {
            g2.fillRect(gb.x + margin, gb.y + margin,
                    Block.SIZE - (margin * 2), Block.SIZE - (margin * 2));
        }
        // 2) Den aktive mino (din eksisterende farve)
        g2.setColor(b[0].c);
        for (Block bb : b) {
            g2.fillRect(bb.x + margin, bb.y + margin,
                    Block.SIZE - (margin * 2), Block.SIZE - (margin * 2));
        }
    }
}
