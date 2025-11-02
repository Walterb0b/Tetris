package mino;

import java.awt.*;

public class Mino_Square extends Mino{

    public Mino_Square(){
        create(Color.yellow);
    }

    @Override
    public void setXY(int x, int y) {

        this.anchorX = x;
        this.anchorY = y;
        //o o
        //o o

        b[0].x = x;
        b[0].y = y;
        b[1].x = b[0].x;
        b[1].y = b[0].y + Block.SIZE;
        b[2].x = b[0].x + Block.SIZE;
        b[2].y = b[0].y;
        b[3].x = b[0].x + Block.SIZE;
        b[3].y = b[0].y + Block.SIZE;

        for(int i = 0; i < 4; i++) {
            tempB[i].x = b[i].x;
            tempB[i].y = b[i].y;
            tempB[i].c = b[i].c;
        }
    }
    public void getDirection1(){}
    public void getDirection2(){}
    public void getDirection3(){}
    public void getDirection4(){}
}
