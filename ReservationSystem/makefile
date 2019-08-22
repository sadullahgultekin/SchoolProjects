run: executable
	./executable 10
executable: server_test.cpp
	g++ -pthread server_test.cpp -o executable
clean:
	rm -f executable output.txt