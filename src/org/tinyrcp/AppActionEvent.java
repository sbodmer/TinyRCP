/*
 * Copyright (C) 2017 sbodmer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.tinyrcp;

import java.awt.event.ActionEvent;

/**
 * Action event with an object as data
 * 
 * @author sbodmer
 */
public class AppActionEvent extends ActionEvent {
    /**
     * Event to open the settings dialog<p>
     * 
     * The action command is defined (could be null)<p>
     * 
     * The data is
     * <PRE>
     * TinyFactory
     *   The wanted factory to be selected or null if only the dialog should open
     * </PRE>
     * 
     */
    public static final int ACTION_ID_TINYFACTORY_SETTINGS = -1000;
    
    protected Object data = null;
    
    public AppActionEvent(Object source, int id, String command) {
        super(source, id, command);
    }
    
    public AppActionEvent(Object source, int id, String command, Object data) {
        super(source, id, command);
        this.data = data;
    }
    
    public Object getData() {
        return data;
    }
}
