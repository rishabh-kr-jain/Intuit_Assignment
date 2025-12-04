package model;

public class Item {
    private final char character;

    public Item(char character) {
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }

    // Poison pill marker
    public static final char POISON_PILL = '\0';

    public boolean isPoisonPill() {
        return character == POISON_PILL;
    }
}
