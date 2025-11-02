package main;

import mino.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class PlayManager {

    //Main play area
    public static final int COLS = 10;
    public static final int ROWS = 20;
    public static final int CELL = Block.SIZE;
    public static int left_x, right_x, top_y, bottom_y;
    public static final int BOARD_W = COLS * CELL;
    public static final int BOARD_H = ROWS * CELL;

    //Mino
    public static Mino currentMino;
    public static int MINO_START_X, MINO_START_Y;
    public static Mino nextMino;
    public static int NEXTMINO_X, NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();
    public static Mino holdMino;
    public static boolean canHold = true;
    public static int HOLD_X, HOLD_Y;

    //Others
    public static int dropInterval = 60; //mino drops in every 60 frames
    boolean gameOver;

    //Effect
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    //Score
    int level = 1;
    int lines;
    int score;

    public PlayManager(){
        //Main Play Area Frame
        left_x = (GamePanel.WIDTH - BOARD_W) / 2;
        right_x = left_x + BOARD_W;
        top_y = 50;
        bottom_y = top_y + BOARD_H;

        MINO_START_X = left_x + (COLS/2 - 1) * CELL;
        MINO_START_Y = top_y + Block.SIZE;

        //x and y coordinates for next mino ui
        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = top_y + 500;

        //Hold mino coordinates
        HOLD_X = left_x - 175;
        HOLD_Y = top_y + 120;

        //Set starting Mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

    }
    public static Mino pickMino(){
        Mino mino = null;
        int i = new Random().nextInt(7);

        switch (i){
            case 0: mino = new Mino_L1();break;
            case 1: mino = new Mino_L2();break;
            case 2: mino = new Mino_Square();break;
            case 3: mino = new Mino_Bar();break;
            case 4: mino = new Mino_T();break;
            case 5: mino = new Mino_Z1();break;
            case 6: mino = new Mino_Z2();break;
        }
        return mino;
    }

    public void update(){
        //Check if the current mino is active
        if(!currentMino.active){
            //If inactive put into staticBlocks arraylist
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            //Check if game is over
            if(currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y){
                gameOver = true;
                GamePanel.music.stop();
                GamePanel.se.play(2, false);
            }

            currentMino.deactivating = false;

            //Replace with next mino
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
            canHold = true;

            //When a mino becomes inactive, check if line(s) can be deleted
            checkDelete();

        } else {
            currentMino.update();
        }
    }

    private void checkDelete() {
        int lineCount = 0;

        // Gå række for række (top -> bund)
        for (int y = top_y; y < bottom_y; y += Block.SIZE) {
            // Tæl hvor mange blokke der er på præcis denne y
            int cellsOnRow = 0;
            for (Block b : staticBlocks) {
                if (b.y == y) {
                    cellsOnRow++;
                }
            }
            // Brug COLS i stedet for 12
            if (cellsOnRow == COLS) {
                effectCounterOn = true;
                effectY.add(y);
                // Fjern alle blokke i denne række
                for (int i = staticBlocks.size() - 1; i >= 0; i--) {
                    if (staticBlocks.get(i).y == y) {
                        staticBlocks.remove(i);
                    }
                }
                // Flyt alle blokke over rækken ned med 1
                for (Block sb : staticBlocks) {
                    if (sb.y < y) {
                        sb.y += Block.SIZE;
                    }
                }
                lineCount++;
                lines++;
                // Level/drop hastighed
                if (lines % 10 == 0 && dropInterval > 1) {
                    level++;
                    if (dropInterval > 10) dropInterval -= 10;
                    else dropInterval -= 1;
                }
                // VIGTIGT: re-check samme y igen efter nedfald
                y -= Block.SIZE;
            }
        }
        // Score og lyd
        if (lineCount > 0) {
            GamePanel.se.play(1, false);
            int singleLineScore = 10 * level;
            score += singleLineScore * lineCount;
        }
    }

    public void draw(Graphics2D g2){
        //Draw play area
        int w = right_x - left_x;
        int h = bottom_y - top_y;
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x, top_y, w, h);

        //Draw next mino frame
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x+60, y+60);

        //Draw score frame
        g2.drawRect(x, top_y, 250, 300);
        x += 40;
        y = top_y + 90;
        g2.drawString("LEVEL: " + level, x, y); y += 70;
        g2.drawString("LINES: " + lines, x, y); y += 70;
        g2.drawString("SCORE: " + score, x, y);

        // --- HOLD UI ---
        g2.setColor(Color.WHITE);
        g2.drawString("HOLD", HOLD_X, HOLD_Y - 10);

        // En 4x4-cellers boks til preview
        int boxW = Block.SIZE * 4;
        int boxH = Block.SIZE * 4;
        g2.drawRect(HOLD_X - 10, HOLD_Y - 10, boxW + 20, boxH + 20);

        // Tegn selve previewet hvis der er noget i hold
        if (holdMino != null) {
            drawHoldPreview(g2, holdMino, HOLD_X, HOLD_Y);
        }

        //Draw currentMino
        if(currentMino != null){
            currentMino.draw(g2);
        }
        //Draw the next Mino
        nextMino.draw(g2);

        //Draw static blocks
        for (Block staticBlock : staticBlocks) {
            staticBlock.draw(g2);
        }

        //Draw effect
        if(effectCounterOn){
            effectCounter++;

            g2.setColor(Color.red);
            for (Integer integer : effectY) {
                g2.fillRect(left_x, integer, COLS*CELL, Block.SIZE);
            }

            if(effectCounter == 10){
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }

        //Draw pause or GameOver
        g2.setColor(Color.yellow);
        g2.setFont(g2.getFont().deriveFont(50f));
        if(gameOver){
            x = left_x + 25;
            y = top_y + 320;
            g2.drawString("GAME OVER", x, y);
        }
        if(KeyHandler.pausePressed){
            x = left_x + 70;
            y = top_y + 320;
            g2.drawString("PAUSED", x, y);
        }

        //Draw game title
        x = 100;
        y = top_y + 320;
        g2.setColor(Color.white);
        g2.setFont(new Font("Times New Roman", Font.ITALIC, 60));
        g2.drawString("Tetris", x, y);
    }

    // Tegner en mino som preview uden at ændre dens rigtige positioner.
    // Vi bruger minoens eget pivot (b[0]) som reference og tegner med offset.
    private void drawHoldPreview(Graphics2D g2, Mino m, int x, int y) {
        int margin = 2;
        // Læg pivot i “center-ish” af boksen: 1 celle ind i begge retninger
        int pivotX = x + Block.SIZE;
        int pivotY = y + Block.SIZE;

        g2.setColor(m.b[0].c);
        for (int i = 0; i < 4; i++) {
            int dx = m.b[i].x - m.b[0].x;  // offset relativt til pivot
            int dy = m.b[i].y - m.b[0].y;

            int px = pivotX + dx;
            int py = pivotY + dy;

            g2.fillRect(px + margin, py + margin,
                    Block.SIZE - (margin * 2), Block.SIZE - (margin * 2));
        }
    }

}
