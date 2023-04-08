/*
 *  Symulacja problemu przejazdu przez waski most
 *  Wersja okienkowa
 *
 *  Autor: Łukasz Wdowiak
 *   Data: 20 grudnia 2022
 */


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NarrowBridgeApp extends JFrame {

    static private final int WINDOW_HEIGHT = 600;
    static private final int WINDOW_WIDTH = 500;

    public static int TRAFFIC = 1000;

    String[] trafficLimitTypes = {"ruch bez ograniczen", "ruch dwukierunkowy", "ruch jednokierunkowy", "ruch ogranicznowy (max 1 bus)"};

    // Lista busów (kolejka) oczekujących na wjazd na most
    List<Bus> busesWaiting = new LinkedList<Bus>();

    // Lista busów poruszających się aktualnie po moście
    List<Bus> busesOnTheBridge = new LinkedList<Bus>();

    static LinkedList<Bus> allBusses = new LinkedList<>();

    JPanel panel = new JPanel();
    JPanel controlPanel = new JPanel();

    static BusDirection currentDirection = BusDirection.WEST;

    JLabel trafficLimit = new JLabel("Ograniczenie ruchu: ");
    JComboBox trafficLimitChooser = new JComboBox(trafficLimitTypes);
    JLabel traffic = new JLabel("Natezenie ruchu: ");
    JSlider trafficSpeed = new JSlider(0, 5000, 0);
    JLabel onBridge = new JLabel("Na moscie: ");
    JTextField onBridgeText = new JTextField();
    JLabel queue = new JLabel("Kolejka: ");
    JTextField queueText = new JTextField();

    JTextArea console = new JTextArea(20, 40);
    JScrollPane consolePane = new JScrollPane(console);

    NarrowBridgeApp() {
        super("Okienko");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        controlPanel.add(trafficLimit);
        controlPanel.add(trafficLimitChooser);
        controlPanel.add(traffic);
        controlPanel.add(trafficSpeed);
        controlPanel.add(onBridge);
        controlPanel.add(onBridgeText);
        controlPanel.add(queue);
        controlPanel.add(queueText);

        controlPanel.setPreferredSize(new Dimension(400, 200));
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0, new JLabel("Male"));
        labelTable.put(5000, new JLabel("Duze"));
        trafficSpeed.setLabelTable(labelTable);
        trafficSpeed.setMinorTickSpacing(1);
        trafficSpeed.setPaintTicks(false);
        trafficSpeed.setPaintLabels(true);

        onBridgeText.setEditable(false);
        queueText.setEditable(false);
        trafficLimitChooser.setSelectedIndex(3);
        controlPanel.setLayout(new GridLayout(4, 1, 0, 10));

        trafficSpeed.addChangeListener(e -> TRAFFIC = trafficSpeed.getValue());

        panel.add(controlPanel);
        panel.add(consolePane);

        console.setLineWrap(true);
        consolePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        new NarrowBridgeAnimation(this);
        //setResizable(false);
        setContentPane(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        final NarrowBridgeApp bridge = new NarrowBridgeApp();

        long time = System.currentTimeMillis();

        while (true) {
            if (System.currentTimeMillis() - time >= 5000) {
                time = System.currentTimeMillis();
                if (currentDirection == BusDirection.WEST)
                    currentDirection = BusDirection.EAST;
                else
                    currentDirection = BusDirection.WEST;
            }

            final Bus bus = new Bus(bridge);
            allBusses.add(bus);
            new Thread(bus).start();
            try {
                Thread.sleep(5500 - NarrowBridgeApp.TRAFFIC);
            } catch (InterruptedException ex) {
            }
        }
    }


    void printBridgeInfo(Bus bus, String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bus[").append(bus.id).append("->").append(bus.dir).append("]  ");
        sb.append(message).append("\n");
        console.insert(sb.toString(), 0);
        sb.setLength(0);
        for (Bus b : busesOnTheBridge) sb.append(b.id).append("  ");
        onBridgeText.setText(sb.toString());
        sb.setLength(0);
        for (Bus b : busesWaiting) sb.append(b.id).append("  ");
        queueText.setText(sb.toString());
    }

    synchronized void getOnTheBridge(Bus bus) {
        int x = 0;
        if (trafficLimitChooser.getSelectedIndex() == 3)
            x = 1;
        else if (trafficLimitChooser.getSelectedIndex() == 1 || trafficLimitChooser.getSelectedIndex() == 2)
            x = 3;

        if (trafficLimitChooser.getSelectedIndex() == 0 && !busesWaiting.isEmpty()) {
            for (Bus bus2 : busesWaiting) {
                busesOnTheBridge.add(bus2);
                printBridgeInfo(bus2, "WJEZDZA NA MOST");
            }
            busesWaiting.clear();
        }

        System.out.println(currentDirection);
        if (trafficLimitChooser.getSelectedIndex() != 0)
            while (busesOnTheBridge.size() >= x || bus.dir != currentDirection && trafficLimitChooser.getSelectedIndex() == 2) {
                if (trafficLimitChooser.getSelectedIndex() == 0)
                    break;

                // dodanie busa do listy oczekujących
                busesWaiting.add(bus);
                printBridgeInfo(bus, "CZEKA NA WJAZD");
                try {
                    wait();
                } catch (InterruptedException e) {
                }
                // usunięcie busa z listy oczekujących.
                busesWaiting.remove(bus);
            }

        // dodanie busa do listy jadących przez most
        busesOnTheBridge.add(bus);
        printBridgeInfo(bus, "WJEZDZA NA MOST");
    }

    synchronized void getOffTheBridge(Bus bus) {
        // usunięcie busa z listy poruszających się przez most
        busesOnTheBridge.removeAll(Collections.singleton(bus));
        printBridgeInfo(bus, "OPUSZCZA MOST");
        // powiadomienie innych oczekujących.
        notify();
    }

}

