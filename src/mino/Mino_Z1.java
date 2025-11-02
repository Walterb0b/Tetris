package mino;

import java.awt.*;

public class Mino_Z1 extends Mino{

    public Mino_Z1(){
        create(Color.red);
    }

    @Override
    public void setXY(int x, int y) {
        this.anchorX = x;
        this.anchorY = y;

        //   o
        // o o
        // o

        b[0].x = anchorX;                b[0].y = anchorY;
        b[1].x = anchorX;                b[1].y = anchorY - Block.SIZE;
        b[2].x = anchorX - Block.SIZE;   b[2].y = anchorY;
        b[3].x = anchorX - Block.SIZE;   b[3].y = anchorY + Block.SIZE;

        for(int i = 0; i < 4; i++) {
            tempB[i].x = b[i].x;
            tempB[i].y = b[i].y;
            tempB[i].c = b[i].c;
        }
    }

    @Override
    public void getDirection1() {
        //   o
        // o o
        // o

        tempB[0].x = anchorX;                tempB[0].y = anchorY;
        tempB[1].x = anchorX;                tempB[1].y = anchorY - Block.SIZE;
        tempB[2].x = anchorX - Block.SIZE;   tempB[2].y = anchorY;
        tempB[3].x = anchorX - Block.SIZE;   tempB[3].y = anchorY + Block.SIZE;

        updateXY(1);
    }

    @Override
    public void getDirection2() {
        // o o
        //   o o
        //
        tempB[0].x = anchorX;                tempB[0].y = anchorY;
        tempB[1].x = anchorX + Block.SIZE;   tempB[1].y = anchorY;
        tempB[2].x = anchorX;                tempB[2].y = anchorY - Block.SIZE;
        tempB[3].x = anchorX - Block.SIZE;   tempB[3].y = anchorY - Block.SIZE;

        updateXY(2);
    }

    @Override
    public void getDirection3() {
        getDirection1();
    }

    @Override
    public void getDirection4() {
        getDirection2();
    }
}
