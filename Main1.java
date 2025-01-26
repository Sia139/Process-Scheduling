
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.util.Comparator;
import java.util.List; 
import java.util.ArrayList; 

class Controller implements KeyListener {
    private View view;
    private DefaultTableModel processTableModel;
    private DefaultTableModel accTableModel;
    private Model model;
    private int rowCount;

    private int prvTime=0;
    private int time = 0;
    private Process p;
    private Vector<Integer> queue;
    private List<String> processNames;
    private List<Integer> startTimes;
    private List<Integer> endTimes;

    Controller() {
        processTableModel = new DefaultTableModel(new String[]{"Process", "Arrival Time", "Burst Time", "Priority"}, 0);
        accTableModel = new DefaultTableModel(new String[]{"Process", "Finish Time", "Arrival Time", "Turn Around Time", "Burst Time", "Waiting Time"}, 0);
        view = new View(processTableModel, accTableModel, this, this);
    }

    public void processData() {
        model = new Model(rowCount); 

        for (int i=0; i<rowCount; i++) {
            String pNum = (String) processTableModel.getValueAt(i, 0);
            int AT = Integer.parseInt((String) processTableModel.getValueAt(i, 1));
            int BT = Integer.parseInt((String) processTableModel.getValueAt(i, 2));
            int priority = Integer.parseInt((String) processTableModel.getValueAt(i, 3));

            Process process = new Process(pNum, BT, AT, priority);
            model.addProcess(i, process);
        };
    }

    public boolean checkLeftBT(){      
        for (int i = 0; i < rowCount; i++){
            Process p = model.getProcess(i);
            if(p != null && p.getleftBT() > 0){
                return true;
            }
        }
        return false;
    }

    public void addPIntoQ(Model model, Vector<Integer> queue , int time){
        Process p = null;

        for (int k = 0; k < rowCount; k++){
            p = model.getProcess(k);                 
            if ( p != null && p.getAT() <= time && p.getleftBT() > 0 && !queue.contains(k) ){
                queue.add(k);
            }
        }
    }

    public void reset(Model model){   
        prvTime=0;
        time = 0;
        for (int i = 0; i < rowCount; i++){
            p = model.getProcess(i);
            if(p != null)
                p.setleftBT(p.getBT());
        }
    }

    public void roundRobinQ3() { 
        queue = new Vector<>();
        processNames = new ArrayList<>();
        startTimes = new ArrayList<>();
        endTimes = new ArrayList<>();

        int quantum = 3;

        addPIntoQ(model, queue, time); // for 0 geh Arrival Time

        while (checkLeftBT()) {
            
            if (queue.isEmpty()) {
                time++;
                addPIntoQ(model, queue, time);
                continue;
            }
            
            if(time - prvTime != 0){
                processNames.add("Idle");
                startTimes.add(prvTime);
                endTimes.add(time);
            }

            int q = queue.get(0);
            p = model.getProcess(q);
            String pNum = p.getpNum();
            int leftBT = p.getleftBT();
            
            processNames.add(pNum);
            startTimes.add(time);

            if (leftBT > quantum) {
                time += quantum;
                endTimes.add(time);
                p.setleftBT(leftBT - quantum);
            } 
            else {
                time += leftBT;
                endTimes.add(time);
                p.setFT(time);
                p.setTAT();
                p.setWT();
                p.setleftBT(0);
            }
            
            prvTime = time;
            p.setnewAT(time);

            addPIntoQ(model, queue, time); // Add new processes to the queue
            queue.remove(0);
            
            queue.sort(Comparator.comparingInt(Q -> model.getProcess(Q).getnewAT()));

            if (p.getleftBT() > 0) {
                queue.add(q);
            }
        }
        view.updateGanttChart(processNames, startTimes, endTimes);
        view.updateAccTable(model);
        reset(model);
    }

    public void nonPre(String algo) {
        queue = new Vector<>();
        processNames = new ArrayList<>();
        startTimes = new ArrayList<>();
        endTimes = new ArrayList<>();
    
        while (checkLeftBT()) {
            addPIntoQ(model, queue, time);
    
            if (queue.isEmpty()) {
                time++;
                addPIntoQ(model, queue, time);
                continue;
            }
    
            if (time - prvTime != 0) {
                processNames.add("Idle");
                startTimes.add(prvTime);
                endTimes.add(time);
            }
    
            if (algo.equals("sjf"))
                queue.sort(Comparator.comparingInt(q -> model.getProcess(q).getBT()));
            else
                queue.sort(Comparator.comparingInt(q -> model.getProcess(q).getpriority()));
    
            int q = queue.get(0);
            p = model.getProcess(q);
            String pNum = p.getpNum();
            int BT = p.getBT();
    
            processNames.add(pNum);
            startTimes.add(time);
            time += BT;
            endTimes.add(time);
    
            p.setleftBT(0);
            //accouting table
            p.setFT(time);
            p.setTAT();
            p.setWT();

            prvTime = time;
    
            queue.remove(0);
        }
    
        view.updateGanttChart(processNames, startTimes, endTimes);
        view.updateAccTable(model);
        reset(model);
    }    

    public void pre(String algo){
        queue = new Vector<>();
        processNames = new ArrayList<>();
        startTimes = new ArrayList<>();
        endTimes = new ArrayList<>();

        String prvProcess = "";
        int prvQ = Integer.MAX_VALUE;
        
        while (checkLeftBT()) {
            addPIntoQ(model, queue, time);

            if (queue.isEmpty()) {
                time++;
                addPIntoQ(model, queue, time);
                continue;
            }
            
            if(time - prvTime != 0){
                processNames.add("Idle");
                startTimes.add(prvTime);
                endTimes.add(time);
            }

            
            if (algo == "sjf")
                queue.sort(Comparator.comparingInt(q -> model.getProcess(q).getleftBT()));
            else{ // priority
                queue.sort(Comparator.comparingInt(q -> model.getProcess(q).getpriority()));
            }
                

            int q = queue.get(0);
            p = model.getProcess(q);
            String curProcess = p.getpNum();
            int leftBT = p.getleftBT();

            if (curProcess != prvProcess){
                processNames.add(curProcess);
                startTimes.add(time);

                if (queue.contains(prvQ)){ 
                    queue.removeElement(prvQ);
                    queue.add(prvQ);
                    endTimes.add(time);
                }
            }

            time++;
            p.setleftBT(leftBT - 1);
            
            if (p.getleftBT() == 0) {
                endTimes.add(time);
                p.setFT(time);
                p.setTAT();
                p.setWT();
                queue.remove(0);
            }
            
            //process b4 next phrase
            prvTime = time;
            prvProcess = curProcess;
            prvQ = q;
        }

        view.updateGanttChart(processNames, startTimes, endTimes);
        view.updateAccTable(model);
        reset(model);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char keyChar = e.getKeyChar();
        if (keyChar == '\n') { 
            JTextField txtField = (JTextField) e.getSource();
            String input = txtField.getText();
            try {
                rowCount = Integer.parseInt(input);
                processTableModel.setRowCount(rowCount);
                for (int i=0; i < rowCount; i++){
                    String p = "P"+i;
                    processTableModel.setValueAt(p, i, 0);
                }
            } 
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number.");
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}

public class Main1 {
    public static void main(String[] args) {
        new Controller();    
    }
}