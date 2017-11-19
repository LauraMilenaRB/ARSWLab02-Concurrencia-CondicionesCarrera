package snakepackage;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import enums.GridSize;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author jd-
 *
 */
public class SnakeApp {

    private static SnakeApp app;
    public static final int MAX_THREADS = 8;
    Snake[] snakes = new Snake[MAX_THREADS];
    private static final Cell[] spawn = {
        new Cell(1, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2,
        3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, GridSize.GRID_HEIGHT - 2),
        new Cell(1, 3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2,
        GridSize.GRID_HEIGHT - 2)};
    private JFrame frame;
    private final JButton iniciar,pausar,reanudar;
    private static Board board;
    int nr_selected = 0;
    Thread[] thread = new Thread[MAX_THREADS];
    private static boolean pause=false;
    public final Object lock=new Object();
    
    public SnakeApp() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("The Snake Race");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setSize(618, 640);
        frame.setSize(GridSize.GRID_WIDTH * GridSize.WIDTH_BOX + 17,
                GridSize.GRID_HEIGHT * GridSize.HEIGH_BOX + 40);
        frame.setLocation(dimension.width / 2 - frame.getWidth() / 2,
                dimension.height / 2 - frame.getHeight() / 2);
        
        JPanel actionsBPabel=new JPanel();
        board =new Board();
        actionsBPabel.setLayout(new FlowLayout());
        iniciar=new JButton("Iniciar");
        pausar=new JButton("Pausar");
        reanudar=new JButton("Reanudar");
        actionsBPabel.add(iniciar);
        actionsBPabel.add(pausar);
        actionsBPabel.add(reanudar);
        frame.add(board,BorderLayout.CENTER);
        frame.add(actionsBPabel,BorderLayout.SOUTH);
        
    }

    public static void main(String[] args) {
        app = new SnakeApp();
        app.accionesBotones();
        app.visible();
    }
    public void visible(){
    	for (int i = 0; i != MAX_THREADS; i++) {
            snakes[i] = new Snake(i + 1, spawn[i], i + 1,lock);
            snakes[i].addObserver(board);
            thread[i] = new Thread(snakes[i]);
        }
    	frame.setVisible(true);
    }
    private void init() {
        for (int i = 0; i != MAX_THREADS && thread[i].getState()==Thread.State.NEW; i++) {
            thread[i].start();
        }
    }
    
    public static SnakeApp getApp() {
        return app;
    }
    
    public void accionesBotones(){
        iniciar.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    app.init();
                }
        });
        pausar.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setPause(true);
                    estado();
                }
            });
        reanudar.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setPause(false);
                    synchronized(lock){
                        lock.notifyAll();
                    }
                }
            });
    }
    public static boolean isPause() {
        return pause;
    }

    private void setPause(boolean pause) {
        this.pause = pause;
    }
    private void estado(){
        for(int i = 0; i != MAX_THREADS; i++){
            while(thread[i].getState()==Thread.State.RUNNABLE){
                setPause(true);
            }
        }
        int size=0;int num=-1;long muerte=0;int num2=-1;
        for (int i = 0; i != MAX_THREADS; i++) {
            int max=snakes[i].getBody().size();
            if(max>size && thread[i].getState()!=Thread.State.TERMINATED){
                num=i+1;size=max;
            }
            long max2=snakes[i].getTimeDead();
            if((max2<=muerte && thread[i].getState()==Thread.State.TERMINATED)||(muerte==0 && max2!=0)){
                muerte=max2;num2=i+1;
            }
        }
        JOptionPane.showMessageDialog(null, "\nLa serpiente que primero murió fue la #"+num2+"."
                + "\nLa serpiente viva más larga es la #"+num+" con una longitud de "+size+".");

    }
}
