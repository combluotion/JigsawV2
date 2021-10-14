package com.uocp8.jigsawv2.adapters;

public interface OrderableAdapter {
    void reorderItems(int originalPosition, int newPosition);

    int getColumnCount();

    boolean canReorder(int position);
}
