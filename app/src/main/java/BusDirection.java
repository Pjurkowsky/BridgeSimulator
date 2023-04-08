/*
 *  kierunek w ktorym jedzie autobus
 *
 *  Autor: Łukasz Wdowiak
 *   Data: 20 grudnia 2022
 */
enum BusDirection {
    EAST,
    WEST;

    @Override
    public String toString() {
        switch (this) {
            case EAST:
                return "W";
            case WEST:
                return "Z";
        }
        return "";
    }
} // koniec typu wyliczeniowego BusDirection
