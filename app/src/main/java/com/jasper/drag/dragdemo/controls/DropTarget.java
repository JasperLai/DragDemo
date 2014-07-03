/*
 * This is a modified version of a class from the Android Open Source Project. 
 * The original copyright and license information follows.
 * 
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jasper.drag.dragdemo.controls;

/**
 * Interface defining an object that reacts to objects being dragged over and dropped onto it.
 *
 */
public interface DropTarget {

    /**
     * Handle an object being dropped on the DropTarget.
     * 
     * @param source DragSource where the drag started
     * @param x X coordinate of the drop location
     * @param y Y coordinate of the drop location
     * @param xOffset Horizontal offset with the object being dragged where the original
     *          touch happened
     * @param yOffset Vertical offset with the object being dragged where the original
     *          touch happened
     * @param dragInfo Data associated with the object being dragged
     * 
     */
    void onDrop(DragSource source, int x, int y, int xOffset, int yOffset,
                Object dragInfo);

    /**
     * React to something started to be dragged.
     * @param source
	 * where this drag action originated
	 * @param x
	 * current x position of drag item
	 * @param y
	 * current y position of drag item
	 * @param xOffset 
	 * the x distance from where this drag action originated
	 * @param yOffset
	 * the y distance from where this drag action originated
	 * @param dragInfo
	 * the drag item
     */    
    void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset,
                     Object dragInfo);

    /**
     * React to something being dragged over the drop target.
     * @param source
	 * where this drag action originated
	 * @param x
	 * current x position of drag item
	 * @param y
	 * current y position of drag item
	 * @param xOffset 
	 * the x distance from where this drag action originated
	 * @param yOffset
	 * the y distance from where this drag action originated
	 * @param dragInfo
	 * the drag item
     */    
    void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset,
                    Object dragInfo);

    /**
     * React to a drag.
     * @param source
	 * where this drag action originated
	 * @param x
	 * current x position of drag item
	 * @param y
	 * current y position of drag item
	 * @param xOffset 
	 * the x distance from where this drag action originated
	 * @param yOffset
	 * the y distance from where this drag action originated
	 * @param dragInfo
	 * the drag item 
     */    
    void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset,
                    Object dragInfo);

    /**
     * Check if a drop action can occur at, or near, the requested location.
     * This may be called repeatedly during a drag, so any calls should return
     * quickly.
     * 
     * @param source DragSource where the drag started
     * @param x X coordinate of the drop location
     * @param y Y coordinate of the drop location
     * @param xOffset Horizontal offset with the object being dragged where the
     *            original touch happened
     * @param yOffset Vertical offset with the object being dragged where the
     *            original touch happened
     * @param dragInfo Data associated with the object being dragged
     * @return True if the drop will be accepted, false otherwise.
     */
    boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset,
                       Object dragInfo);


}
