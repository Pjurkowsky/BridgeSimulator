/*
 *  Klasa reprezentujaca autobus
 *
 *  Autor: Łukasz Wdowiak
 *   Data: 20 grudnia 2022
 */
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.concurrent.ThreadLocalRandom;

class Bus implements Runnable {

    // Stałe określające minimalny i maksymalny czas
    // oczeiwania na nowych pasażerów.
    public static final int MIN_BOARDING_TIME = 1000;
    public static final int MAX_BOARDING_TIME = 10000;

    // Stała określająca czas dojazdu busa do mostu.
    public static final int GETTING_TO_BRIDGE_TIME = 500;

    // Stała określająca czas przejazdu przez most.
    public static final int CROSSING_BRIDGE_TIME = 3000;

    // Stała określająca czas przjezdu od mostu do końcowego parkingu.
    public static final int GETTING_PARKING_TIME = 500;

    // Stała określająca czas wysiadania pasażerów z busa
    public static final int UNLOADING_TIME = 500;


    // Liczba wszystkich busów, które zostału utworzone
    // od początku działania programu
    private static int numberOfBuses = 0;

    Double xPos;


    public BusState busState = BusState.BOARDING;

    // Metoda usypia wątek na podany czas w milisekundach
    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    // Metoda usypia wątek na losowo dobrany czas
    // z przedziału [min, max) milsekund
    public static void sleep(int min_millis, int max_milis) {
        sleep(ThreadLocalRandom.current().nextInt(min_millis, max_milis));
    }


    // Referencja na obiekt reprezentujący most.
    NarrowBridgeApp bridge;

    // Unikalny identyfikator każdego busa.
    // Jako identyfikator zostanie użyty numer busa,
    // który został utworzony od początku działania programu
    int id;

    // Kierunek jazdy busa nadany w sposób losowy
    BusDirection dir;


    public Bus(NarrowBridgeApp bridge) {
        this.bridge = bridge;
        this.id = ++numberOfBuses;
        if (ThreadLocalRandom.current().nextInt(0, 2) == 0) {
            this.dir = BusDirection.EAST;
             xPos = -20.;
        }
        else{
            this.dir = BusDirection.WEST;
            xPos = 0.;
        }
    }


    // Wydruk w konsoli informacji o stanie busa
    void printBusInfo(String message) {
        bridge.console.insert("Bus[" + id + "->" + dir + "]: " + message + "\n", 0);
    }


    // Symulacja oczekiwania na nowych pasażerów.
    void boarding() {
        printBusInfo("Czeka na nowych pasazerow");
        sleep(MIN_BOARDING_TIME, MAX_BOARDING_TIME);
    }

    // Symulacja dojazdu ze stacji początkowej do mostu
    void goToTheBridge() {
        printBusInfo("Jazda w strone mostu");
        sleep(GETTING_TO_BRIDGE_TIME);
    }

    // Symulacja przejazdu przez most
    void rideTheBridge() {
        printBusInfo("Przejazd przez most");
        sleep(CROSSING_BRIDGE_TIME);
    }

    // Symulacja przejazdu od mostu do końcowego parkingu
    void goToTheParking() {
        printBusInfo("Jazda w strone koncowego parkingu");
        sleep(GETTING_PARKING_TIME);
    }

    // Symulacja opuszczenia pojazdu na przystanku końcowym
    void unloading() {
        printBusInfo("Rozladunek pasazerow");
        sleep(UNLOADING_TIME);
    }


    // Metoda realizuje "cykl życia" pojedynczego busa
    public void run() {
        busState = BusState.BOARDING;
        boarding();

        busState = BusState.GO_TO_THE_BRIDGE;
        goToTheBridge();

        busState = BusState.WAIT_BEFORE_THE_BRIDGE;
        bridge.getOnTheBridge(this);

        busState = BusState.RIDE_THE_BRIDGE;
        rideTheBridge();

        bridge.getOffTheBridge(this);

        busState = BusState.GO_TO_THE_PARKING;
        goToTheParking();

        busState = BusState.UNLOADING;
        unloading();
        NarrowBridgeApp.allBusses.remove(this);

    }

    public void draw(Graphics g, Double x, Double y) {

        Graphics2D g2d = (Graphics2D) g;
        Path2D essa = new Path2D.Double();
        g2d.setColor(Color.BLACK);
        if (dir.equals(BusDirection.EAST)) {
            essa.moveTo(-15 + x, -10 + y);
            essa.lineTo(5 + x, -10 + y);
            essa.lineTo(15 + x, 0 + y);
            essa.lineTo(15 + x, 10 + y);
            essa.lineTo(-15 + x, 10 + y);
            essa.closePath();
            essa.append(new Ellipse2D.Double(-13 + x, 7 + y, 10, 10), false);
            essa.append(new Ellipse2D.Double(0 + x, 7 + y, 10, 10), false);
        } else {
            x = NarrowBridgeAnimation.WINDOW_WIDTH - x;
            essa.moveTo(15 + x, -10 + y);
            essa.lineTo(-5 + x, -10 + y);
            essa.lineTo(-15 + x, 0 + y);
            essa.lineTo(-15 + x, 10 + y);
            essa.lineTo(15 + x, 10 + y);
            essa.closePath();
            essa.append(new Ellipse2D.Double(-13 + x, 7 + y, 10, 10), false);
            essa.append(new Ellipse2D.Double(0 + x, 7 + y, 10, 10), false);
        }

        g2d.drawString(Integer.toString(id),x.floatValue()-5,y.floatValue()+5);
        g2d.draw(essa);

    }


}  // koniec klasy Bus
