package pns.project.pz.commands.values;

import pns.project.pz.commands.CommandValue;
 
public class MoveValue implements CommandValue {
    private final float x;
    private final float y;

    private MoveValue(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public static MoveValue of(float x, float y) {
        return new MoveValue(x, y);
    }

    @Override
    public String toString() {
        return String.format("%s,%s", getX(), getY());
    }
}

