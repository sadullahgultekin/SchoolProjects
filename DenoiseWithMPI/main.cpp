/*
Student Name: Sadullah Gültekin
Student Number: 2014400066
Compile Status: Compiling
Program Status: Working
*/
#include <mpi.h> 
#include <stdio.h> 
#include <fstream> 
#include <iostream> 
#include <math.h>

using namespace std;

#define IMAGE_SIZE 200

int main(int argc, char ** argv) {

    // input file
	string INPUT_FILE = argv[1];
    // output file
  	string OUTPUT_FILE = argv[2];
    // beta value
  	double BETA = atof(argv[3]);
    // pi value
  	double PI = atof(argv[4]);

    MPI_Init(NULL, NULL);
    // number of processors
    int world_size;
    MPI_Comm_size(MPI_COMM_WORLD, & world_size);
    // in which processor we are
    int world_rank;
    MPI_Comm_rank(MPI_COMM_WORLD, & world_rank);

    // number of line that each processors need to process
    int numberOfLineByProcessor = IMAGE_SIZE / (world_size - 1);
    // gamma value to be used in probability calculation
    double GAMMA = 0.5 * log2((1.0 - PI) / PI);
    // number of iteration
    double NUMBER_OF_ITERATION = 500000.0 / (double) (world_size-1);

    if (world_rank == 0) { // master

        // array to keep the image
        int masterImg[IMAGE_SIZE][IMAGE_SIZE];
        // input file
        ifstream infile(INPUT_FILE);

        // reads the input from file
        for (int i = 0; i < IMAGE_SIZE; i++)
            for (int j = 0; j < IMAGE_SIZE; j++)
                infile >> masterImg[i][j];
            
        // send the picture to the slave processes
        // each processor gets the part that only they will use
        for (int i = 1; i < world_size; i++)
            MPI_Send(masterImg[(i-1)*numberOfLineByProcessor], IMAGE_SIZE*numberOfLineByProcessor, MPI_INT, i, 0, MPI_COMM_WORLD);

        // array to keep new image that will be put
        int clearImage[IMAGE_SIZE][IMAGE_SIZE];

        // collect the cleared image from slaves
        for(int i = 1; i < world_size; i++)
            MPI_Recv(clearImage[(i-1)*numberOfLineByProcessor], IMAGE_SIZE*numberOfLineByProcessor, MPI_INT, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        
        // output file
        ofstream myfile;
        myfile.open (OUTPUT_FILE);
        
        // writes the output
        for(int i = 0; i < IMAGE_SIZE; i++){
            for(int j = 0; j < IMAGE_SIZE; j++)
                myfile << clearImage[i][j] << " ";
            myfile << endl;
        }

        myfile.close();

    } else { // slaves

        // array that the slave processor keeps the image
        int slaveArr[numberOfLineByProcessor][IMAGE_SIZE];
        // original version of the image
        int originalSubarr[numberOfLineByProcessor][IMAGE_SIZE];

        // slave receives the data from master
        MPI_Recv(slaveArr[0], IMAGE_SIZE*numberOfLineByProcessor, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

        // makes the copy of the input and puts it into the originalSubarr array
        for (int i = 0; i < numberOfLineByProcessor; i++)
            for (int j = 0; j < IMAGE_SIZE; j++)
                originalSubarr[i][j] = slaveArr[i][j];

        // arrays to keep the pixels that will come from other processors
        int upperPixels [IMAGE_SIZE];
        int lowerPixels [IMAGE_SIZE];

        // initialize the arrays to 0, no trust to c++
        for(int i = 0; i < IMAGE_SIZE; i++){
            upperPixels[i] = 0;
            lowerPixels[i] = 0;
        }
        
        // seed the random function, so that random function can work properly
        // world_rank is used so that the seed will be different for each processor
        srand(time(0) + world_rank);

        // iteration over the picture
        for(int i = 0; i < NUMBER_OF_ITERATION; i++){
            
            // find a random line number and index number
            int randomLine = rand() % numberOfLineByProcessor;
            int randomIndex = rand() % IMAGE_SIZE;

            // get the upper and lower pixels from other processors
            if(world_rank == 1){ // processor that processes the most upper part of the image
                if (world_size > 2){ // make nothing if there is only one processor
                    MPI_Send(slaveArr[numberOfLineByProcessor-1], IMAGE_SIZE, MPI_INT, 2, 0, MPI_COMM_WORLD);
                    MPI_Recv(lowerPixels, IMAGE_SIZE, MPI_INT, 2, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);                    
                }
            } else if (world_rank == world_size - 1){ // processor that processes the most lower part of the image
                if (world_size > 2){ // make nothing if there is only one processor
                    MPI_Send(slaveArr[0], IMAGE_SIZE, MPI_INT, world_size-2, 0, MPI_COMM_WORLD);
                    MPI_Recv(upperPixels, IMAGE_SIZE, MPI_INT, world_size-2, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                }
            } else { // all other processors
                MPI_Send(slaveArr[numberOfLineByProcessor-1], IMAGE_SIZE, MPI_INT, world_rank+1, 0, MPI_COMM_WORLD);
                MPI_Send(slaveArr[0], IMAGE_SIZE, MPI_INT, world_rank-1, 0, MPI_COMM_WORLD);
                MPI_Recv(lowerPixels, IMAGE_SIZE, MPI_INT, world_rank+1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                MPI_Recv(upperPixels, IMAGE_SIZE, MPI_INT, world_rank-1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
            }

            // sum of the neighbour
            int sums = 0;
            // booleans to show whether the pixel is an edge point or not
            bool isTop = false, isBottom = false, isLeft = false, isRight = false;

            // initialize the boolean values properly
            if (randomLine == 0) isTop = true;
            if (randomLine == numberOfLineByProcessor - 1) isBottom = true;
            if (randomIndex == 0) isLeft = true;
            if (randomIndex == IMAGE_SIZE - 1) isRight = true;

            // array ot keep he neighbour of the pixel
            int neighbour[3][3];
            
            // initialize the array, no trust to c++
            for(int k = 0; k < 3; k++)
                for(int l = 0; l < 3; l++)
                    neighbour[k][l] = 0;
            
            if (isTop) {
                // 0 1 2 => 0

                // get the upper pixels
                // for communication
                neighbour[0][1] = upperPixels[randomIndex];
                if (!isLeft) neighbour[0][0] = upperPixels[randomIndex-1];
                if (!isRight) neighbour[0][2] = upperPixels[randomIndex+1];
                // for communication

                if (isRight) {
                    // 3 4 => 0
                    // 5 6 7 fill
                    neighbour[2][1] = slaveArr[randomLine + 1][randomIndex];
                    neighbour[2][0] = slaveArr[randomLine + 1][randomIndex - 1];
                    neighbour[1][0] = slaveArr[randomLine][randomIndex - 1];
                } else if (isLeft) {
                    // 6 7 => 0
                    //3 4 5 fill
                    neighbour[1][2] = slaveArr[randomLine][randomIndex + 1];
                    neighbour[2][2] = slaveArr[randomLine + 1][randomIndex + 1];
                    neighbour[2][1] = slaveArr[randomLine + 1][randomIndex];
                } else {
                    // 3 4 5 6 7 fill
                    neighbour[1][2] = slaveArr[randomLine][randomIndex + 1];
                    neighbour[2][2] = slaveArr[randomLine + 1][randomIndex + 1];
                    neighbour[2][1] = slaveArr[randomLine + 1][randomIndex];
                    neighbour[2][0] = slaveArr[randomLine + 1][randomIndex - 1];
                    neighbour[1][0] = slaveArr[randomLine][randomIndex - 1];
                }
            } else if(isBottom) {
                // 4 5 6 => 0

                // gets the lower pixels
                // for communication
                neighbour[2][1] = lowerPixels[randomIndex];
                if (!isLeft) neighbour[2][0] = lowerPixels[randomIndex-1];
                if (!isRight) neighbour[2][2] = lowerPixels[randomIndex+1];
                // for communication

                if (isRight) {
                    // 2 3 => 0
                    // 0 1 7 fill
                    neighbour[0][0] = slaveArr[randomLine - 1][randomIndex - 1];
                    neighbour[0][1] = slaveArr[randomLine - 1][randomIndex];
                    neighbour[1][0] = slaveArr[randomLine][randomIndex - 1];
                } else if (isLeft) {
                    // 0 7 => 0
                    // 1 2 3 fill
                    neighbour[0][1] = slaveArr[randomLine - 1][randomIndex];
                    neighbour[0][2] = slaveArr[randomLine - 1][randomIndex + 1];
                    neighbour[1][2] = slaveArr[randomLine][randomIndex + 1];
                } else {
                    // 0 1 2 3 7 fill
                    neighbour[0][0] = slaveArr[randomLine - 1][randomIndex - 1];
                    neighbour[0][1] = slaveArr[randomLine - 1][randomIndex];
                    neighbour[0][2] = slaveArr[randomLine - 1][randomIndex + 1];
                    neighbour[1][2] = slaveArr[randomLine][randomIndex + 1];
                    neighbour[1][0] = slaveArr[randomLine][randomIndex - 1];
                }
            } 
            if ((!isBottom) && (!isTop)){    
                if (isRight) {
                    // 2 3 4 => 0
                    // 0 1 5 6 7 fill
                    neighbour[0][0] = slaveArr[randomLine - 1][randomIndex - 1];
                    neighbour[0][1] = slaveArr[randomLine - 1][randomIndex];
                    neighbour[2][1] = slaveArr[randomLine + 1][randomIndex];
                    neighbour[2][0] = slaveArr[randomLine + 1][randomIndex - 1];
                    neighbour[1][0] = slaveArr[randomLine][randomIndex - 1];
                } else if (isLeft) {
                    // 6 7 0 => 0
                    //1 2 3 4 5 fill
                    neighbour[0][1] = slaveArr[randomLine - 1][randomIndex];
                    neighbour[0][2] = slaveArr[randomLine - 1][randomIndex + 1];
                    neighbour[1][2] = slaveArr[randomLine][randomIndex + 1];
                    neighbour[2][2] = slaveArr[randomLine + 1][randomIndex + 1];
                    neighbour[2][1] = slaveArr[randomLine + 1][randomIndex];
                } else {
                    // 0 1 2 3 4 5 6 7 fill
                    neighbour[0][0] = slaveArr[randomLine - 1][randomIndex-1];
                    neighbour[0][1] = slaveArr[randomLine - 1][randomIndex];
                    neighbour[0][2] = slaveArr[randomLine - 1][randomIndex + 1];
                    neighbour[1][0] = slaveArr[randomLine][randomIndex - 1];
                    neighbour[1][2] = slaveArr[randomLine][randomIndex + 1];
                    neighbour[2][0] = slaveArr[randomLine + 1][randomIndex - 1];
                    neighbour[2][1] = slaveArr[randomLine + 1][randomIndex];
                    neighbour[2][2] = slaveArr[randomLine + 1][randomIndex + 1];
                }
            }

            // sum of the neighbours
            sums = neighbour[0][0] + neighbour[0][1] + neighbour[0][2] + neighbour[1][0] + neighbour[1][2] + neighbour[2][0] + neighbour[2][1] + neighbour[2][2];

            // probability of flip operation
            double probability = ((-2.0)*GAMMA*((double)slaveArr[randomLine][randomIndex])*((double)originalSubarr[randomLine][randomIndex])) + ((-2.0)*BETA*((double)slaveArr[randomLine][randomIndex])*((double)sums));

            // random number between 0 and 1
            double ALPHA = double(rand()) / (double(RAND_MAX) + 1.0);
            
            // probaility check
            if (probability > log(ALPHA))
                slaveArr[randomLine][randomIndex] *= -1; 

        }

        // send the cleared image back to master
        MPI_Send(slaveArr[0], IMAGE_SIZE*numberOfLineByProcessor, MPI_INT, 0, 0, MPI_COMM_WORLD);

    }

    // finalize the mpi
    MPI_Finalize();
}
