/*
 *  Stany busow
 *
 *  Autor: Łukasz Wdowiak
 *   Data: 20 grudnia 2022
 */
public enum BusState {
    BOARDING,
    GO_TO_THE_BRIDGE,
    WAIT_BEFORE_THE_BRIDGE,
    RIDE_THE_BRIDGE,
    GO_TO_THE_PARKING,
    UNLOADING;

    @Override
    public String toString() {
        switch (this) {
            case BOARDING:
                return "BOARDING";
            case GO_TO_THE_BRIDGE:
                return "GO_TO_THE_BRIDGE";
            case WAIT_BEFORE_THE_BRIDGE:
                return "WAIT_BEFORE_THE_BRIDGE";
            case RIDE_THE_BRIDGE:
                return "RIDE_THE_BRIDGE";
            case GO_TO_THE_PARKING:
                return "GO_TO_THE_PARKING";
            case UNLOADING:
                return "UNLOADING";
        }
        return "";
    }
}
