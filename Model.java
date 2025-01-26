class Process {
    String pNum;
    int BT;  
    int AT; 
    int priority;
    int leftBT; 
    int newAT;
    int FT; //Finish time;
    int TAT; //Turn Around Time
    int WT; //Waiting Time
    
    Process(String pNum, int BT, int AT, int priority) {
        this.pNum = pNum;
        this.BT = BT;
        this.AT = AT;
        this.priority = priority;
        leftBT = BT;
        newAT = AT;
        FT = 0;
        TAT = 0;
        WT = 0;
    }
    
    //setter
    void setleftBT(int leftBT){
        this.leftBT = leftBT;
    }
    
    void setnewAT(int newAT){
        this.newAT = newAT;
    }

    void setFT(int FT){
        this.FT = FT;
    }

    void setTAT(){
        TAT = FT - AT;
    }

    void setWT(){
        WT = TAT - BT;
    }
    
    //getter
    String getpNum(){
        return pNum;
    }
    
    int getBT(){
        return BT;
    }
    
    int getAT() { 
        return AT;
    }
        
    int getpriority(){
        return priority;
    }
    
    int getleftBT(){
        return leftBT;
    }

    int getnewAT(){
        return newAT;
    }

    int getFT(){
        return FT;
    }
    
    int getTAT(){
        return TAT;
    }

    int getWT(){
        return WT;
    }
}

public class Model {
    int pNum;
    Process[] processes;
    
    public Model(int pNum) { // Constructor
        this.pNum = pNum;
        this.processes = new Process[pNum];
    }
    
    public void addProcess(int index, Process process) {
        if (index >= 0 && index < pNum) {
            processes[index] = process;
        } 
        else {
            throw new IndexOutOfBoundsException("Index out of bounds for processes array.");
        }
    }

    public Process getProcess(int index) {
        if (index >= 0 && index < pNum) {
            return processes[index];
        } 
        else {
            throw new IndexOutOfBoundsException("Index out of bounds for processes array.");
        }
    }

    public int getProcessNum() {
        return pNum;
    }

}