package org.bukkit.block;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Represents either a SignPost or a WallSign
 */
public interface Sign extends BlockState {

    /**
     * Gets all the lines of text currently on this sign.
     *
     * @return Array of BaseComponents containing each line of text
     */
    BaseComponent[] lines();

    @Deprecated
    public String[] getLines();

    /**
     * Gets the line of text at the specified index.
     * <p>
     * For example, getLine(0) will return the first line of text.
     *
     * @param index Line number to get the text from, starting at 0
     * @throws IndexOutOfBoundsException Thrown when the line does not exist
     * @return Text on the given line
     */
    BaseComponent line(int index);

    @Deprecated
    public String getLine(int index) throws IndexOutOfBoundsException;

    /**
     * Sets the line of text at the specified index.
     * <p>
     * For example, setLine(0, "Line One") will set the first line of text to
     * "Line One".
     *
     * @param index Line number to set the text at, starting from 0
     * @param line New text to set at the specified index
     * @throws IndexOutOfBoundsException If the index is out of the range 0..3
     */
    void setLine(int index, BaseComponent line);

    @Deprecated
    public void setLine(int index, String line) throws IndexOutOfBoundsException;
}
