:- dynamic
	student/2,
	availableSlots/1,
	room_capacity/2.

student(1415926, ['ec102', 'cmpe160', 'math102', 'math202']).
student(5358979, ['ec102', 'cmpe240', 'cmpe230', 'math202', 'phys201', 'ee212', 'math102']).
student(3238462, ['phys201', 'ec102', 'math102', 'math202']).
student(6433832, ['ec102', 'cmpe160', 'phys201']).
student(7950288, ['math102']).

available_slots(['m-1', 'm-2', 'm-3', 'w-1', 'w-2', 'w-3', 'f-1', 'f-2', 'f-3']).

room_capacity('nh101', 1).
room_capacity('nh105', 3).
room_capacity('ef106', 5).

% finds and retracts all knowledge base.
clear_knowledge_base :- 
	all_students(Students), write("student/2: "), length(Students,X) ,write(X), write("\navailable_slots/1: 1"), 
	findall(A,room_capacity(A,_),List), write("\nroom_capacity/2: "), length(List,Y) ,write(Y),
	retractall(student(_,_)),retractall(available_slots(_)),retractall(room_capacity(_,_)).

% Finds all students
all_students(L) :- findall(X,student(X,_),L).

% Finds all courses and gets a list. That list contain lists of courses, then uses flatten to put them into one list. 
% Then uses sort predicate to remove duplicates.
all_courses(Result) :- findall(Y,student(_,Y),Listofcourses), flatten(Listofcourses,Interlist), sort(Interlist,Result).

% For all students that are member of both C1 and C2, prolog assigns random value(we dont care what it is) and puts them into list,
% then gets the length of this list.
common_students(C1,C2,Count) :- findall(X,(student(_,Y),member(C1,Y),member(C2,Y), X = _),List), length(List,Count).

% For all students, if they take given course, we put them in a list, then get the length of this list.
student_count(CourseID,StudentCount) :- findall(_,(student(_,C),member(CourseID,C)),L), length(L,StudentCount).

% Finds number of errors in a given plan.
errors_for_plan(Plan,CountError) :- 
	% Finds a list of number of erors that stem from conflict error.
	% Also finds a list of number of errors that are stem from capacity problem.
	findall(Z,errslot(Plan,Z),Sloterrorlist), findall(T,errcapa(Plan,T),Capacityerrorlist),
	sumlist(Sloterrorlist, K), sumlist(Capacityerrorlist,L), CountError is (K/2) + L.

% For given plan, if there are students who have conflict, it counts them as an error, and finds total number of error.
errslot(Plan,CountError) :- member(X,Plan), member(Y,Plan), not(X=Y), =([X1,_,XL],X), =([Y1,_,YL],Y), XL = YL, common_students(X1,Y1,CountError).

% For all course in a given plan, if the exam place has lower capacity than the number of students who take the course,
% it counts every student as an error. And finds total error.
errcapa(Plan,CountError) :- member(Q,Plan), =([Q1,Q2,_],Q), student_count(Q1,A), room_capacity(Q2,B), A > B, CountError is A - B.

%% Finds a final plan that does not contain any conflict.
% Gets all course and slots to send to the auxillary predicate, then calls auxillary predicate final_plan_decider.
final_plan(Finalplan) :- all_courses(Courses), available_slots(Slots), final_plan_decider(Courses,[],Slots,Finalplan).

% Base case for recursive call. If course list is empty, unifies accumulator and plan, to return accumulator.
final_plan_decider([],Plan,Slots,Plan).

% Processes head of all courses list(first course in the list),  then sends the rest to the same function again.
final_plan_decider([H|T],Plan,Slots,Acc) :- process(H,Plan,Slots,Ret), final_plan_decider(T,Ret,Slots,Acc).

% Finds appropiate slot and room for a given course
process(Currrent_course,Plan,Slots,Ret) :- 
	% Gets a slot.
	member(Slot,Slots),
	% Gets a room.
	room_capacity(Room, Room_capacity),
	% Counts the number of students who take the given course.
	student_count(Currrent_course,Num_of_students),
	% Checks whether taken room has insufficient capacity or not.
	Room_capacity >= Num_of_students,
	% Checks whether determined room and slot, already in use or not.
	not(member([_,Room,Slot],Plan)),
	% Finds all errors originates from any two course which have common students, and puts those errors to a list.
	findall(Y,(member(X,Plan),=([X1,X2,X3],X),Slot=X3,common_students(Currrent_course,X1,Y)),Commonlist),
	% Finds total errors by summing all elements of given list.
	sumlist(Commonlist,E),
	% Checks whether total error is 0 or not.
	E = 0,
	% Appends found course to final plan.
	append([[Currrent_course,Room,Slot]],Plan,Ret).

