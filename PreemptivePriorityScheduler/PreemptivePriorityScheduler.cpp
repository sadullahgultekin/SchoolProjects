#include <iostream>
#include <fstream>
#include <vector>
#include <sstream>
#include <queue>

using namespace std;
/*
    Struct to keep instructions.
    Fields;
        instructionName: name of the instruction which is a normally not important, but incase it may be useful later, I keep it.
        instructioExecutionTime: time needed to execute this instructino
*/
struct Instruction{
    string instructionName;
    int instructionExecutionTime;
    Instruction(string instructionName, int instructionExecutionTime){
        this->instructionName = instructionName;
        this->instructionExecutionTime = instructionExecutionTime;
    }
};

/*
    Struct to keep processes
    Fields;
        processNumber: id number of the process
        processPriority: priority of the process
        arrivalTime: arrival to of the process to the system
        remainingExecutionTime: remaining execution time of the process:
        instructionCurser: curser that shows which line of the process will be processed next
        initialInstructionSize: total number of instruction in the process
        systemExitTime: time that the process is executed completely
        initialExecutionTime: sum of all instruction's execution time in this process
*/
struct Process{
    int processNumber, processPriority, arrivalTime, remainingExecutionTime, instructionCurser, initialInstructionSize, systemExitTime,initialExecutionTime;
    queue<Instruction> instructions;
    Process(int processNumber, int processPriority, queue<Instruction> &instructions, int arrivalTime, int totalTimeOfInstructions){
        this->processNumber = processNumber;
        this->processPriority = processPriority;
        this->arrivalTime = arrivalTime;
        this->instructions = instructions;
        this->remainingExecutionTime = totalTimeOfInstructions;
        this->instructionCurser = 1;
        this->initialInstructionSize = instructions.size();
        this->systemExitTime = 0;
        this->initialExecutionTime = totalTimeOfInstructions;
    }
};

/*
    Comparator to be used in priority queue, to sort items according to their arrival times
*/
struct CompareByArrival{
    bool operator()(const Process p1, const Process p2){
        return p1.arrivalTime > p2.arrivalTime;
    }
};

/*
    Comparator to be used in priority queue, to sort items according to their priorities.
    If two processes priority is equal, they will be sort by teir process numbers, which will
    make them sorted according to their arrival time to the system
*/
struct CompareByPriority{
    bool operator()(const Process p1, const Process p2){
        if(p1.processPriority != p2.processPriority) {
            return p1.processPriority > p2.processPriority;
        }
        return p1.processNumber > p2.processNumber;
    }
};

/*
    Comparator to be used in priority queue, to sort items according to their process numbers
*/
struct CompareByProcessNumber{
    bool operator()(const Process p1, const Process p2){
        return p1.processNumber < p2.processNumber;
    }
};

/*
    Reads input prom the file and creates instructions, fills their fields according to the file content
    Inputs;
        fileName: name of the file that holds instruction information
        v: queue that holds all instructions of the process
    Returns;
        Total execution time of all instructions
*/
int createInstructions(string fileName, queue<Instruction> &v){
    int totalTime = 0;
    fileName = fileName + ".txt";
    string line;
    ifstream infile(fileName); // open the file
    while(getline(infile, line)){
        istringstream iss(line); // use each line as a file
        string str;
        iss >> str;
        string instructionName = str;
        iss >> str;
        int instructionExecutionTime = stoi(str);
        totalTime = totalTime + instructionExecutionTime;
        v.push(Instruction(instructionName, instructionExecutionTime)); // create instuction and put them inside of the queue
    }
    return totalTime;
}

/*
    Reads input and creates processes according to the input file. Inside of each process there is an vector
    that holds instructions. This function also calls createInstruction function to create instructions.
    Inputs;
        p: Priority queue to hold all processes. Proirity is determined by the arrival times of processes
*/
void createProcesses(priority_queue<Process, vector<Process>, CompareByArrival> &p){
    ifstream infile("definition.txt"); // open the file
    string line;
    int totalTimeOfInstruction = 0;
    while (getline(infile, line)) { // do this loop for each line
        istringstream iss(line); // use each line as a file
        string str;
        iss >> str;
        str.erase(0,1);
        int processNumber = stoi(str);
        iss >> str;
        int priority = stoi(str);
        iss >> str;
        string fileName = str;
        iss >> str;
        int arrivalTime = stoi(str);
        queue<Instruction> v;
        totalTimeOfInstruction = createInstructions(fileName, v); // create instruction for process
        p.push(Process(processNumber, priority, v, arrivalTime,totalTimeOfInstruction)); // create process
    }

}

/*
    Prints current content of the ready queue. This method is called in each time that the queue is modified.
    If at the same time queue is modified twice, this method will be called only once to print the queue
    Inputs;
        readdyQueue: Ready queue that keeps all processes that are ready to be processed
        curretTime: Curret time of the system
*/
void printCurrentReadyQueue(priority_queue<Process, vector<Process>, CompareByPriority> readyQueue, int currentTime){
    cout << currentTime << ":HEAD-";
    if(readyQueue.empty()) cout << "-";
    else {
        while (!readyQueue.empty()) {
            cout << "P" << readyQueue.top().processNumber << "[" << readyQueue.top().instructionCurser << "]-";
            readyQueue.pop();
        }
    }
    cout << "TAIL\n";

}

/*
    Prints the statistics about turnaround time and waiting time of the processes.
    Turnaround time is calculated by exitTime - arrivalTime, and waitingTime is calculated by
    turnaroundTime - initialExecutionTime.
    Inputs;
        endedProcesses: Keeps processes that are ended
*/
void printStat(vector<Process> &endedProcesses){
    // stats should be printed with the order of arrival time
    sort(endedProcesses.begin(), endedProcesses.end(), CompareByProcessNumber());
    for (vector<Process>::iterator i = endedProcesses.begin(); i != endedProcesses.end(); ++i){
        cout  << endl << "Turnaround time for P" << (*i).processNumber << " = " << ((*i).systemExitTime)-((*i).arrivalTime) << " ms" << endl;
        cout << "Waiting time for P" << (*i).processNumber << " = " << ((*i).systemExitTime)-((*i).arrivalTime)-((*i).initialExecutionTime);
    }
}

/*
    This schedular is kind of a simulator, and this simulator needs to find time points that
    the system situation will be printed. This method finds next time point that the system have a change
    It looks to the first element that will enter to the system, and to the process that is processed currently.
    Gets the smaller value to determine next time point to stop.
    Inputs;
        currentTime: current time of the system
        allProcesses: all processes that will enter to the system when current time is equal to their arrival time
        readyQueue: all processes that are ready to be processed
    Returns;
        new time that the system will move on
*/
int findTimeInterval(int currentTime, priority_queue<Process, vector<Process>, CompareByArrival> &allProcesses, priority_queue<Process, vector<Process>, CompareByPriority> &readyQueue){
    int newTime = 2147483647; // biggest interger, all other numbers should be smaller that this number.
    if(!allProcesses.empty()){
        newTime = allProcesses.top().arrivalTime;
    }
    if(!readyQueue.empty() && readyQueue.top().remainingExecutionTime + currentTime < newTime){
        newTime = readyQueue.top().remainingExecutionTime + currentTime;
    }
    return newTime;
}

/*
    Uptades information of the process that is currently on the CPU
    Inputs;
        endedProcesses: all processes that are left the system
        readyQueueChangedPointer: pointer to keep wether the ready queue is changed or not
        currentTime: curretn time of the system
        timeInterval: time interval that the system will jump.
        readyQueue: all processes that are ready to be processed.
    Returns;
        time that the system should be updated again. For example if an istruction is being processed
        and a new process that has high priority has entered to the system, that process should wait until
        current instruction is over. The value that is return show that how long newly entered process
        should wait.
*/
int updateCurrentProcess(vector<Process> &endedProcesses, bool* readyQueueChangedPointer,int currentTime, int timeInterval, priority_queue<Process, vector<Process>, CompareByPriority> &readyQueue){
    Process temp = readyQueue.top();
    readyQueue.pop();
    while(timeInterval>0){
        Instruction tempInst = temp.instructions.front();
        temp.instructions.pop();
        timeInterval -= tempInst.instructionExecutionTime;
        temp.instructionCurser += 1;
        temp.remainingExecutionTime -= tempInst.instructionExecutionTime;
    }
    if(temp.instructionCurser <= temp.initialInstructionSize) readyQueue.push(temp);
    else {
        *readyQueueChangedPointer = true;
        endedProcesses.push_back(temp);
    }
    return timeInterval;
}

/*
    Main function of the program. In general, it reads the input from files, and simulates
    the preemptive priority scheduler. It doesn't take any input, always read from a file named "definition.txt"
*/
int main (int argc, char* argv[]){

    // priority queue to keep all processes, priority is determined by their arrival time to the system.
    priority_queue<Process, vector<Process>, CompareByArrival> allProcesses;
    // priority queue to keep all processes that are ready to be executed. Their priority is determined by their priority numbers.
    priority_queue<Process, vector<Process>, CompareByPriority> readyQueue;
    // to keep processes that are executed.
    vector<Process> endedProcesses;
    // creates all instructions and processes, and fills allProcess priority queue.
    createProcesses(allProcesses);

    int currentTime = 0, timeInterval = 0, newTime = 0;
    // before starting the simulation, it prints the current queue, which is empty
    printCurrentReadyQueue(readyQueue, currentTime);
    // boolean to keep track of whether ready queue is changed or not
    bool readyQueueChanged = false;
    // readyQueueChanged variable may be changed in other methods, this pointer sent to thoese
    // methods to keep track of this variable.
    bool* readyQueueChangedPointer = &readyQueueChanged;

    // program runs until all process that will come to the system and ready to be processed are over
    while(!allProcesses.empty() ||!readyQueue.empty()){

        *readyQueueChangedPointer = false;

        // takes all processes that have a smaller arrival time than current time
        while (!allProcesses.empty() && currentTime >= allProcesses.top().arrivalTime){
            readyQueue.push(allProcesses.top());
            allProcesses.pop();
            *readyQueueChangedPointer  = true;
        }

        // finds next time interval
        newTime = findTimeInterval(currentTime, allProcesses, readyQueue);
        timeInterval = newTime - currentTime;

        // if ready queue is modified, print current queue situation
        if(*readyQueueChangedPointer)  printCurrentReadyQueue(readyQueue, currentTime);
        *readyQueueChangedPointer  = false;

        // make necessary modification on the process that is inside the CPU
        if(!readyQueue.empty()){
            int a = updateCurrentProcess(endedProcesses, readyQueueChangedPointer, currentTime, timeInterval, readyQueue);
            if(a < 0) newTime -= a;
        }

        // update current time
        currentTime = newTime;

        // if ready queue is modified, print current queue situation and put the ended process to the ended queue.
        if(*readyQueueChangedPointer){
            printCurrentReadyQueue(readyQueue, currentTime);
            Process temp = endedProcesses.back();
            temp.systemExitTime = currentTime;
            endedProcesses.pop_back();
            endedProcesses.push_back(temp);
        }

    }

    // print the statistics of all processes
    printStat(endedProcesses);

    return 0;
}

