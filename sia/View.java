import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.event.*;
import java.util.List; 
import java.util.ArrayList;

class TableController extends JPanel {
    private JTextField txtField;

    TableController(KeyListener keyListener) {
        txtField = new JTextField(20);
        txtField.addKeyListener(keyListener);

        setLayout(new FlowLayout());
        add(new JLabel("Enter Number of Processes:"));
        add(txtField);
    }
}

class AlgoButtons extends JPanel {
    AlgoButtons(JLabel algoLabel, Controller controller) {
        setLayout(new FlowLayout());

        JButton rrq3 = new JButton("Round-Robin Quantum 3");
        JButton nonPreSJF = new JButton("Non-Preemptive SJF");
        JButton nonPrePriority = new JButton("Non-Preemptive Priority");
        JButton preSJF = new JButton("Preemptive SJF");
        JButton PrePriority = new JButton("Preemptive Priority");

        rrq3.addActionListener(e ->{
            algoLabel.setText("Selected Algorithm: Round-Robin Quantum 3");
            controller.processData();
            controller.roundRobinQ3();
        });

        nonPreSJF.addActionListener(e -> {
            algoLabel.setText("Selected Algorithm: Non-Preemptive SJF");
            controller.processData();
            controller.nonPre("sjf");
        });

        nonPrePriority.addActionListener(e -> {
            algoLabel.setText("Selected Algorithm: Non-Preemptive Priority");
            controller.processData();
            controller.nonPre("priority");

        }); 
            
        preSJF.addActionListener(e ->{
            algoLabel.setText("Selected Algorithm: Preemptive SJF");
            controller.processData();
            controller.pre("sjf");
        });

        PrePriority.addActionListener(e ->{
            algoLabel.setText("Selected Algorithm: Preemptive Priority");
            controller.processData();
            controller.pre("Priority");
        });

        add(rrq3);
        add(nonPreSJF);
        add(nonPrePriority);
        add(preSJF);
        add(PrePriority);
    }
}

class GanttChartPanel extends JPanel {
    private List<String> processNames;
    private List<Integer> startTimes;
    private List<Integer> endTimes;

    GanttChartPanel() {
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Gantt Chart", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            new Font("Arial", Font.BOLD, 14)
        ));

        processNames = new ArrayList<>();
        startTimes = new ArrayList<>();
        endTimes = new ArrayList<>();

        // Example data for demo
        processNames.add("Demo1");
        processNames.add("Demo2");
        processNames.add("Demo3");
        startTimes.add(0);
        startTimes.add(3);
        startTimes.add(6);
        endTimes.add(3);
        endTimes.add(6);
        endTimes.add(10);
    }

    void updateChart(java.util.List<String> processNames, java.util.List<Integer> startTimes, java.util.List<Integer> endTimes) {
        this.processNames = processNames;
        this.startTimes = startTimes;
        this.endTimes = endTimes;
        
        int totalWidth = endTimes.get(endTimes.size() - 1) * 30 + 40;
        setPreferredSize(new Dimension(totalWidth, 150)); // Adjust the size
        revalidate(); // Notify layout manager of the size change
        repaint();    // Redraw the component
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int x = 10; // Start position
        int y = 20; // Vertical position for the bars
        int height = 30; // Height of each process block
        int scale = 30; // Time unit scale (pixels per unit)

        // Draw the Gantt chart bars and process names
        for (int i = 0; i < processNames.size(); i++) {
            int startX = x + (startTimes.get(i) * scale);
            int width = (endTimes.get(i) - startTimes.get(i)) * scale;

            g.drawRect(startX, y, width, height); // Draw process rectangle
            g.drawString(processNames.get(i), startX + 5, y + 20); // Process name inside the bar
        }

        // Draw the time labels
        for (int i = 0; i < startTimes.size(); i++) {
            int timeX = x + (startTimes.get(i) * scale); // Position for start time
            g.drawString(String.valueOf(startTimes.get(i)), timeX, y + height + 20); // Draw start time
        }

        // Add the last end time
        int lastEndTimeX = x + (endTimes.get(endTimes.size() - 1) * scale); // Position for last end time
        g.drawString(String.valueOf(endTimes.get(endTimes.size() - 1)), lastEndTimeX, y + height + 20);
    }


}

class View {
    private JFrame frame;
    private GanttChartPanel ganttChartPanel;
    private JTable accTable;

    View(DefaultTableModel processTableModel, DefaultTableModel accTableModel, KeyListener keyListener, Controller controller) {
        frame = new JFrame("CPU Scheduling Simulator");
        frame.setLayout(new BorderLayout());

        // Main panel with vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Create first table
        JTable processTable = new JTable(processTableModel);
        JScrollPane processScrollPane = new JScrollPane(processTable);

        //create Algorithem Lable;
        JLabel algoLabel = new JLabel("Selected Algorithm: None");
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.add(algoLabel, BorderLayout.WEST);

        ganttChartPanel = new GanttChartPanel();
        ganttChartPanel.setPreferredSize(new Dimension(0,140)); // Set a height for visibility
        JScrollPane ganttScrollPanel = new JScrollPane(ganttChartPanel);
        ganttScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); // Usually only horizontal scrolling is needed
        ganttScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);


        // Create second table
        accTable = new JTable(accTableModel);
        JScrollPane accScrollPane = new JScrollPane(accTable);
        
        // Add components
        mainPanel.add(new TableController(keyListener)); // Add input field
        mainPanel.add(processScrollPane); // Add first table
        mainPanel.add(new AlgoButtons(algoLabel, controller)); // Add buttons
        mainPanel.add(labelPanel); //algo selected
        mainPanel.add(ganttScrollPanel); // Add Gantt chart panel
        mainPanel.add(accScrollPane); // Add second table

        frame.add(mainPanel, BorderLayout.CENTER);
        // Set frame properties
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    void updateGanttChart(java.util.List<String> processNames, java.util.List<Integer> startTimes, java.util.List<Integer> endTimes) {
        ganttChartPanel.updateChart(processNames, startTimes, endTimes);
    }

    void updateAccTable(Model model) {
        // Clear existing rows in the table model
        DefaultTableModel accTableModel = (DefaultTableModel) accTable.getModel();
        accTableModel.setRowCount(0);

        //added section
        double totalTAT = 0;
        double totalWT = 0;
        int processCount = model.getProcessNum();
        //added section
    
        // Iterate through the processes in the model
        for (int i = 0; i < model.getProcessNum(); i++) {
            Process process = model.getProcess(i);
    
            // Extract the necessary data from the Process object
            String processName = process.getpNum();
            int finishTime = process.getFT();
            int arrivalTime = process.getAT();
            int turnAroundTime = process.getTAT();
            int burstTime = process.getBT();
            int waitingTime = process.getWT(); 

            //added section
            totalTAT += process.getTAT();
            totalWT += process.getWT();
            //added section

            // Add a new row to the table model
            accTableModel.addRow(new Object[] {
                processName,
                finishTime,
                arrivalTime,
                turnAroundTime,
                burstTime,
                waitingTime
            });
        }

        accTableModel.addRow(new Object[]{
            "Total", "-", "-", totalTAT, "-", totalWT
        });

        double avgTAT = totalTAT / processCount;
        double avgWT = totalWT / processCount;
        accTableModel.addRow(new Object[]{
            "Avg", "-", "-", avgTAT , "-", avgWT
        });


    }
    
}
