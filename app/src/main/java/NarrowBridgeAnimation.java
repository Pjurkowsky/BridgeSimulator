/*
 *  Animacja mostu
 *
 *  Autor: Łukasz Wdowiak
 *   Data: 20 grudnia 2022
 */

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class NarrowBridgeAnimation extends JPanel {
    LinkedList<Bus> buses;
    static public final int WINDOW_HEIGHT = 500;
    static public final int WINDOW_WIDTH = 620;

    long deltaTime = 20;

    NarrowBridgeAnimation(NarrowBridgeApp narrowBridgeApp) {
        JFrame frame = new JFrame("Animacja");
        this.buses = NarrowBridgeApp.allBusses;
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);


        frame.setLocationRelativeTo(narrowBridgeApp);
        //frame.setLocation(NarrowBridgeApp.WIDTH + 10, 0);
        frame.setContentPane(this);
        frame.setVisible(true);
        frame.setResizable(false);
        new Thread(() -> {
            while (true) {
                this.repaint();
                try {
                    Thread.sleep(deltaTime);
                } catch (InterruptedException ex) {
                }
            }
        }).start();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, 40, WINDOW_HEIGHT);
        g.fillRect(WINDOW_WIDTH - 55, 0, 40, WINDOW_HEIGHT);

        g.setColor(Color.gray);
        g.fillRect(40, 0, 120, WINDOW_HEIGHT);
        g.fillRect(WINDOW_WIDTH - 55 - 120, 0, 120, WINDOW_HEIGHT);

        g.setColor(Color.RED);
        g.fillRect(140, 0, 40, WINDOW_HEIGHT);
        g.fillRect(WINDOW_WIDTH - 60 - 140, 0, 40, WINDOW_HEIGHT);

        g.setColor(Color.BLUE);
        g.fillRect(180, 0, 240, WINDOW_HEIGHT);


        g.setColor(Color.BLACK);
        int yPos = 20;

        synchronized (buses) {
            for (int i = 0; i < buses.size(); i++) {
                Bus bus = buses.get(i);
                int c = 0;
                if (bus.dir.equals(BusDirection.WEST)) {
                    c = 20;
                }
                if (bus.busState.equals(BusState.BOARDING) &&(bus.xPos <= 20 && bus.dir.equals(BusDirection.EAST) || bus.xPos <= 15 + c && bus.dir.equals(BusDirection.WEST) )) {
                    bus.xPos += deltaTime * 40. / 1000.;
                }
                if (bus.busState.equals(BusState.GO_TO_THE_BRIDGE) && (bus.xPos <= 160 && bus.dir.equals(BusDirection.EAST) || bus.xPos <= 160 + c && bus.dir.equals(BusDirection.WEST) )) {
                    bus.xPos += deltaTime * 160 / 500.;
                }
                if(bus.busState.equals(BusState.WAIT_BEFORE_THE_BRIDGE) ){
                    bus.xPos = 160. + c;
                }
                if (bus.busState.equals(BusState.RIDE_THE_BRIDGE))
                    bus.xPos += deltaTime * 225. / 3000.;
                if (bus.busState.equals(BusState.GO_TO_THE_PARKING)  &&  (bus.xPos <= 580 && bus.dir.equals(BusDirection.EAST) || bus.xPos <= 580 + c && bus.dir.equals(BusDirection.WEST) ))
                    bus.xPos += deltaTime * 350. / 500.;
                if (buses.size() <= 15)
                    yPos += 30;
                else
                    yPos += 15 * 30 / buses.size();

                bus.draw(g, bus.xPos, (double) yPos);
            }
        }


    }
}
