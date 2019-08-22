#include <iostream>
#include <pthread.h>
#include <unistd.h>
#include <vector>
#include <fstream>

#define OUTPUT_FILE "output.txt"

using namespace std;

// Structure to keep seat
// seatNumber: shows the seat number
// client: shows the id of the client
struct Seat{
   int seatNumber, client;
   Seat(int seatNumber = -1, int client = -1){
      this->seatNumber = seatNumber;
      this->client = client;
   }
};

// total number of seats
int number_of_seats;
// counter to keep track of the client id
int currentEmptyClientNumber = 1;
// vector to keep empty seats
vector<Seat> emptySeats;
// mutexes for critical sections
pthread_mutex_t lock1;
pthread_mutex_t lock2;
pthread_mutex_t lock3;
// the output file
ofstream myfile;

// makes necessary changes in the allSeats vector. 
// argument = seat information to print output.
void* alternateSeat(void* argument){
   // pointer that points to the seat we want to change
   Seat *s = (Seat*) argument;
   // busy wait... Waits for client thread to make a seat selection
   while(s->client == -1 || s->seatNumber == -1);

   // prints the output
   pthread_mutex_lock(&lock3);
   myfile << "Client" << s->client << " reserves Seat" << s->seatNumber << endl;
   pthread_mutex_unlock(&lock3);

   return 0;
}

// makes the reservations. This function is called for a thread. 
// Every thread should call this function and needs to make a reservation.
// At the very beginning the thread makes a sleep.
// By making sleep time random, we ensure that all clients are coming to the system in different times.
// param: is not used, just added to complete the function definition
void *MakeReservation(void *param){
   
   // seat to send server thread as an argument
   struct Seat argument;
   
   // creates server threads, and calls alternateSeat function with it
   pthread_t serverThread;
   pthread_create(&serverThread, NULL, alternateSeat, &argument);

   // thread gets a client number
   // first item that comes here, gets the smaller client id
   pthread_mutex_lock(&lock1);
   int clientNumber = currentEmptyClientNumber;
   currentEmptyClientNumber++;
   pthread_mutex_unlock(&lock1);

   // thread makes a random sleep
   int sleep_time = rand() % 151 + 50;
   usleep(sleep_time);
   
   pthread_mutex_lock(&lock2);
   // choses a random seat number
   int chosenSeat = rand() % emptySeats.size();
   // adjusts Seat structure sent before seat selection
   argument.seatNumber = emptySeats[chosenSeat].seatNumber;
   argument.client = clientNumber;
   // deletes selected seats from emptySeats
   emptySeats.erase(emptySeats.begin()+chosenSeat);
   pthread_mutex_unlock(&lock2);

   // joins server thread and client thread
   pthread_join(serverThread, NULL);

   pthread_exit(0);
}

// main function of the program
// makes a flight ticket reservation system simulation
int main(int argc, char** argv){

   // if user doesn't give enough argument, error occurs
   if (argc < 2){
      cout << "plese give an appropriate input" << endl;
      return -1;
   }

   // seeds the time
   srand((unsigned) time(0));

   // opens the output file
   myfile.open (OUTPUT_FILE);

   // initiates the mutexes
   pthread_mutex_init(&lock1,NULL);
   pthread_mutex_init(&lock2,NULL);
   pthread_mutex_init(&lock3,NULL);

   // gets the argument which is the total number of seats
   number_of_seats = atoi(argv[1]);

   // creates emptySeats vector, and fills it. Initially, all seats' clients are assigned to -1
   for (int i = 0; i < number_of_seats; i++){
      struct Seat s(i+1,-1);
      emptySeats.push_back(s);
   }

   myfile << "Number of total seats: " << number_of_seats << endl;

   // creates client treads, and runs MakeReservation function
   pthread_t threads[number_of_seats];
   for(int i = 0; i < number_of_seats; i++){
      pthread_create(&threads[i], NULL, MakeReservation, NULL);
   }
   
   // waits for all threads to finished their job
   for (int i = 0; i < number_of_seats; i++){
      pthread_join(threads[i], NULL);
   }

   myfile << "All seats are reserved." << endl;

   return 0;
}
