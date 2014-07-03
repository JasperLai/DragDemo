package com.jasper.drag.dragdemo.controls;

import android.view.View;

/**
 * handle list event.
 *
 * @author laimubin
 */
public interface DragListListener {
    /**
     * GRID ID.
     */
    int LIST_TYPE_GRIDVIEW = 0;
    /**
     * LIST ID.
     */
    int LIST_TYPE_LISTVIEW = 1;

    /**
     * An item in the DynGridView has been clicked.
     *
     * @param v        the view of this item
     * @param position the position in the adapter array
     * @param id       the item id
     */
    void onItemClick(View v, int position, int id);

    /**
     * This item is to delete, by dragging over the delete zone.
     *
     * @param item       the item
     * @param sourceType current list type
     */
    void onItemDragToDelete(Object item, int sourceType);
}
