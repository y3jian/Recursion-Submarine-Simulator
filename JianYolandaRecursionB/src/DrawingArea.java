/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author yolandajian
 */
public class DrawingArea extends javax.swing.JPanel {

    Timer subTime;
    Timer haidaTime;
    public static ArrayList<Integer> subCoor = new ArrayList<Integer>();
    public static int searchRad = 0;
    public static boolean startSearch = false;
    public static boolean clear = false;
    public static int fullRadius = 0;
    int index = 0;
    double angle = 90;
    public int count = 0;
    public int curRad = 0;
    double xSearch = 0;
    double ySearch = 0;
    int i = 0;
    int curLevel = 0;
    public static boolean subFull = false;
    public static int sSpeed = 0;
    public static int hSpeed = 0;

    /**
     * Creates new form DrawingArea
     */
    public int getSSpeed(int speed) {
        //setting the speed for the submarine 
        if (speed == 6) {
            sSpeed = 300;
        } else if (speed == 7) {
            sSpeed = 450;
        } else if (speed == 8) {
            sSpeed = 600;
        }
        return sSpeed;
    }

    public int getHSpeed(int speed) {
        //setting the speed for the Haida 
        hSpeed = speed + 50;
        return hSpeed;
    }

    public boolean searchPressed(boolean pressed) {
        startSearch = pressed;
        return startSearch;
    }

    public void clear() {
        //clearing values for the new run 
        clear = true;
        MainFrame.interceptLabel.setText("");
        MainFrame.apartLabel.setText("");
        subCoor.clear();
        startSearch = false;
        subFull = false;
        index = 0;
        count = 0;
        curRad = 0;
        sSpeed = 0;
        hSpeed = 0;
        angle = 90;

        subTime.stop();
        haidaTime.stop();
    }

    public DrawingArea() {
        initComponents();
        //starting both timers
        subTime = new Timer(450, new DrawingArea.TimerListenerSub());
        haidaTime = new Timer(50, new DrawingArea.TimerListenerHaida());
        subTime.start();
        haidaTime.start();
    }

    public int subPosition(double xEqn, double yEqn, int posX, int posY, double a, double b, int count) {
        //base case: y value moves above or below the display
        if (subCoor.isEmpty()) {//if the array list is empty (runs at least once)
            yEqn = a * (Math.pow(b, xEqn));
            subCoor.add(((int) (xEqn)) + posX);//adding offset 
            subCoor.add(posY - ((int) yEqn));
        } else if (subCoor.get(count - 2) <= 700 && subCoor.get(count - 1) <= 460 && subCoor.get(count - 1) >= 0) {
            //else if y is above/below display or x surpasses display
            yEqn = a * (Math.pow(b, xEqn));
            subCoor.add((int) (xEqn) + posX);//adding offset 
            subCoor.add(posY - ((int) (yEqn)));//subtracting the offset 
        } else {
            subFull = true;//once ArrayList is full (coordinates are out of bound)
            return posY - ((int) yEqn);
        }
        //returning while increasing the x position and the x in the equation
        //adding 2 to count to set the next x and y coordinates in the arrayList
        return posY * subPosition(xEqn + 1, yEqn, posX + 20, posY, a, b, count + 2);
    }

    public int getRadius(int rad) {//getting the radius 
        searchRad = rad;
        return searchRad;
    }

    public double setSearchX(double ang, int curRadius) {
        xSearch = curRadius * Math.cos(Math.toRadians(ang));//calculating the search radius x coor
        return xSearch;
    }

    public double setSearchY(double ang, int curRadius) {
        ySearch = curRadius * Math.sin(Math.toRadians(ang));//calculating the search radius y coor
        return ySearch;
    }

    public void distanceBetween(Graphics g) {//filling the gaps in the function 
        if (subCoor.size() >= 4) {//if there are more than two points drawn
            //if the current y value miinus the previous y value is greater than the height of one oval 
            if (Math.abs(subCoor.get(i + 1) - subCoor.get(i - 1)) > 20) {
                //Finding the slope for the linear dots (y2-y1/x2-x1)
                int slope = (subCoor.get(i + 1) - subCoor.get(i - 1)) / (subCoor.get(i) - subCoor.get(i - 2));
                for (int posX = subCoor.get(i - 2); posX <= subCoor.get(i); posX += 1) {//looping through x values
                    for (int posY = subCoor.get(i + 1); posY <= subCoor.get(i - 1); posY += slope) { //looping through y values
                        g.fillOval(posX, posY, 10, 20);//drawing extra circles between the two points 
                    }
                }
            }
        }
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (clear) {//if clear button is pressed 
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(0, 0, 700, 460);//getting rid of old drawing 
            clear = false;
        }

        g.setColor(Color.blue);
        if (subFull) {//drawing the current submarine coordinate
            g.fillOval(subCoor.get(i), subCoor.get(i + 1), 10, 20);
            System.out.println("(" + subCoor.get(i) + "," + subCoor.get(i + 1) + ")");
            
            //drawing the previous coordinates 
            for (int curFrame = 0; curFrame <= i - 2; curFrame += 2) {
                g.fillOval(subCoor.get(curFrame), subCoor.get(curFrame + 1), 10, 20);
            }
        }

        if (startSearch) {
            if (curRad <= searchRad) {//the the current radius is less than the radius specified 
                g.setColor(Color.red);
                
                //drawing the previous search
                for (int rad = 0; rad <= curRad - 30; rad += 30) {
                    for (double curAngle = 0; curAngle <= 360; curAngle += 10) {//looping through increasing angles to draw the old positions 
                        g.fillRect((int) setSearchX(curAngle, rad) + MainFrame.xHaida, (int) setSearchY(curAngle, rad) + MainFrame.yHaida, 15, 25);
                    }
                }

                for (double curAngle = 0; curAngle <= angle; curAngle += 10) {//the current search
                    curAngle += 90;//add 90 to start at the 12 o clock position (PI/2 from east)
                    g.fillRect((int) setSearchX(curAngle, curRad) + MainFrame.xHaida, (int) setSearchY(curAngle, curRad) + MainFrame.yHaida, 15, 25);
                    curAngle -= 90;//subtracting 90 to keep running the loop

                    if (Math.abs(subCoor.get(i) - ((int) setSearchX(curAngle, curRad) + MainFrame.xHaida)) <= 50) {//checking if x is in range
                        if (Math.abs(subCoor.get(i + 1) - ((int) setSearchY(curAngle, curRad) + MainFrame.yHaida)) <= 50) {//checking if y is in range
                            MainFrame.interceptLabel.setText("Intercepted at: " + ((int) setSearchX(curAngle, curRad) + MainFrame.xHaida)
                                    + " , " + ((int) setSearchY(curAngle, curRad) + MainFrame.yHaida));//printing the intercept 
                            //stop timers 
                            subTime.stop();
                            haidaTime.stop();
                        }
                        
                    } else if (subFull && Math.abs(subCoor.get(i) - ((int) setSearchX(curAngle, curRad) + MainFrame.xHaida)) > 50//if out of range
                            && Math.abs(subCoor.get(i + 1) - ((int) setSearchY(curAngle, curRad) + MainFrame.yHaida)) <= 50) {
                        MainFrame.interceptLabel.setText("Did not intercept");
                    }
                    //printing the x distance apart 
                    MainFrame.apartLabel.setText("Distance Apart: " + Math.abs(((int) setSearchX(curAngle, curRad) + MainFrame.xHaida) - subCoor.get(i)));
                }
            }
            count++;
        }
    }

    private class TimerListenerSub implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {

            subTime.setDelay(sSpeed);//setting speed according to user input 

            //if the coordinates are within range of the screen 
            if (i < subCoor.size() - 2 && subCoor.get(i) >= 0 && subCoor.get(i) <= 700 && subCoor.get(i + 1) >= 0
                    && subCoor.get(i + 1) <= 460) {
                i = i + 2;//adding new index for the next set of coordinates 
            }
            repaint();
        }
    }

    private class TimerListenerHaida implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {

            haidaTime.setDelay(hSpeed);//setting speed according to user input 
            
            //if the current radius is less than the radius specified, the search has started, and coordinates are in range
            if (curRad <= searchRad && startSearch && setSearchX(angle, curRad) + MainFrame.xHaida <= 700
                    && setSearchX(angle, curRad) + MainFrame.xHaida >= 0
                    && setSearchY(angle, curRad) + MainFrame.yHaida <= 460 && setSearchY(angle, curRad) + MainFrame.yHaida >= 0) {
                
                //finding x and y points 
                setSearchX(angle, curRad);
                setSearchY(angle, curRad);
                //angle increase 
                angle += 10;
                if (angle >= 360) {//if angle surpasses 360, reset to 0
                    angle = 0;
                    curRad += 30;
                }
                count++;
            }
            repaint();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
