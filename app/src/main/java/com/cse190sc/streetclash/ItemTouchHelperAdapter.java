package com.cse190sc.streetclash;

/**
 * Created by jiayinghu on 11/16/15.
 */
public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
