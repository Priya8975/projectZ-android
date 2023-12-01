package pns.project.pz.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;

import pns.project.pz.commands.Command;
import pns.project.pz.commands.CommandType;
import pns.project.pz.commands.values.MoveValue;

public class GestureTap extends GestureDetector.SimpleOnGestureListener {

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if(e2.getPointerCount() == 1){
            CommandQueue.getInstance().push(Command.of(CommandType.MOUSE_MOVE, MoveValue.of(distanceX, distanceY)));
        } else {
            CommandQueue.getInstance().push(Command.of(CommandType.MOUSE_SCROLL, MoveValue.of(distanceX, distanceY)));
        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        CommandQueue.getInstance().push(Command.of(CommandType.MOUSE_DOUBLE_CLICK, null));
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);
        CommandQueue.getInstance().push(Command.of(CommandType.MOUSE_RIGHT_CLICK, null));
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        CommandQueue.getInstance().push(Command.of(CommandType.MOUSE_LEFT_CLICK, null));
        return true;
    }
}
