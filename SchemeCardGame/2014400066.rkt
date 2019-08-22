#lang scheme
; compiling: yes
; complete: yes
; 2014400066
;--------------------------------------------------------------------
; 4.1
; (card-color) -> symbol?
; x -> pair?
;
; Gives the color of the given card. If the given card's first element is 'H or 'D, return value is 'red.
; If the given card's first element is 'S or 'C, return value is 'black. 
;
; Examples:
; > (card-color (H . 3))
; => 'red
; > (card-color (S . 3))
; => 'black
(define (card-color x)
  (case (car x)
    ((H D) 'red )
    ((S C) 'black )
    )
  )
;--------------------------------------------------------------------
; 4.2
; (card-rank) -> number?
; card -> pair?
;
; Gives the rank of the given card. The rank is calculated like the following;
; If the card's second element is A, return value is 11.
; If the card's second element is K or J or Q, return value is 10.
; Otherwise return value is the second element of card.
;
; Examples:
; > ( card-rank '(H . A) )
; => 11
; > ( card-rank '(S . 10) )
; => 10
(define card-rank
  (lambda (card)
    (cond
      ((equal? (cdr card) 'A) 11)
      ((equal? (cdr card) 'K) 10)
      ((equal? (cdr card) 'J) 10)
      ((equal? (cdr card) 'Q) 10)
      (else (cdr card))
      )
    )
  )
;--------------------------------------------------------------------
; 4.3
; (all-same-color) -> boolean?
; card-list -> list?
;
; Checks whether given list contains cards that are all have the same color
;
; Examples:
; > ( all-same-color '((H . 3) (H . 2) (H . A) (D . A) (D . Q) (D . J)) )
; => #t
; > ( all-same-color '((H . 3) (H . 2) (H . A) (D . A) (D . Q) (C . J)) )
; => #f 
(define (all-same-color cardlist)
  (let ((len (length cardlist)))
    (cond
      ((< len 2) #t)
      ((= len 2) (equal? (card-color (car cardlist)) (card-color (cadr cardlist))))
      (else (and (equal? (card-color (car cardlist)) (card-color (cadr cardlist))) (all-same-color (cdr cardlist))))
      )
    )
  )
;--------------------------------------------------------------------
; 4.4
; (fdraw allc heldc) -> list?
; allc -> list?
; heldc -> list?
;
; Draws a new card and adds it to held cards
;
; Examples:
; > ( fdraw '((H . 3) (H . 2) (H . A) (D . A) (D . Q) (D . J)) '())
; => '((H . 3))
; > ( fdraw '((H . 3) (H . 2) (H . A) (D . A) (D . Q) (D . J)) '((S . 3) (S . 2) (S . A)))
; => '((S . 3) (S . 2) (S . A) (H . 3))
(define (fdraw allc heldc)
  (if (null? allc) '() (cons (car allc) heldc ))
  )
;--------------------------------------------------------------------
; 4.5
; (fdiscard list-of-cards list-of-moves goal held-cards) -> list?
; list-of-cars -> list?
; list-of-moves -> list?
; goal -> number?
; held-cards -> list?
;
; Returns a list after discard operation is made. Discard operation removes an element from held cards
;
; Example:
; > ( fdiscard '((C . 3) (C . 2) (C . A) (S . J) (S . Q) (H . J)) '(draw draw draw discard) 66 '((H . 3) (H . 2) (H . A) (D . A) (D . Q) (D . J)) )
; => '((H . 3) (H . 2) (H . A) (D . A) (D . Q)) ;output
; > ( fdiscard '((H . 3) (H . 2) (H . A) (D . A) (D . Q) (D . J)) '(draw draw draw discard) 56 '((S . 3) (S . 2) (S . A) (C . A) (C . Q) (C . J)) )
; => '((S . 3) (S . 2) (C . A) (C . Q) (C . J))
(define (fdiscard list-of-cards list-of-moves goal held-cards)
  (if (null? held-cards) '() (cdr held-cards ))
  )
;--------------------------------------------------------------------
; 4.7
; (find-held-cards list-of-steps) -> list?
; list-of-steps -> list?
;
; returns a list containing held cards, that are left after steps in list-of-steps are made
;
; Example:
; > ( find-held-cards '((draw (H . 3)) (draw (H . 2)) (draw (H . A)) (discard (H . 3))) )
; => '((H . 2) (H . A))
(define (find-held-cards list-of-steps) (aux-find-held list-of-steps '()))

; (aux-find-held steps acc) -> list?
; steps -> list?
; acc -> list?
;
; returns a list containing held cards, that are left after steps are made
;
; Example:
; > ( aux-find-held '((draw (H . 3)) (draw (H . 2)) (draw (H . A)) (discard (H . 3))) '() )
; => '((H . 2) (H . A))
(define (aux-find-held steps acc)
  (if (= 0 (length steps)) acc
      (if (equal? (caar steps) 'draw)
          (aux-find-held (cdr steps) (append acc (cdar steps)))
          (aux-find-held (cdr steps) (remove (cadar steps) acc ))
          )
      )
  )

;--------------------------------------------------------------------
; 4.8
; (card-point) -> number?
; card -> pair?
;
; Calculates and returns the point of input card. This function is used in calc-playerpoint function
;
; Example:
; > (card-point '(H . 3))
; => 3
; > (card-point '(H . A))
; => 11
(define card-point
  (lambda (card)
    (cond
      ((equal? (cdr card) 'A) 11)
      ((equal? (cdr card) 'K) 10)
      ((equal? (cdr card) 'J) 10)
      ((equal? (cdr card) 'Q) 10)
      (else (cdr card))
      )
    )
  )

; (calc-playerpoint allc) -> number?
; allc -> pair?
;
; Calculates and returns total point that is taken by given card list
;
; Example:
; > ( calc-playerpoint '((H . A) (H . 3) (H . 2) (D . Q) (D . J) (C . J)) )
; 46
(define (calc-playerpoint allc)
  (if (null? allc) 0 ;; sonradan ekledim
      (if (zero? (length allc)) 0 (+ (card-point(car allc)) (calc-playerpoint (cdr allc)))))
  )

;--------------------------------------------------------------------
; 4.9
; (calc-score list-of-cards goal) -> number?
; list-of-cards -> list?
; goal -> number?
;
; Calculates overall score of given cards according to the goal.
;
; Example:
; > (calc-score '((H . 3) (H . 2) (H . A) (D . J) (D . Q) (C . J)) 50 )
; => 4
; > (calc-score '((H . 3) (H . 2) (H . A) (D . J) (D . Q) (C . J)) 16 )
; => 150
(define (calc-score list-of-cards goal)
  (if (> (calc-playerpoint list-of-cards) goal)
      (if (all-same-color list-of-cards)
          (* (quotient 5 2) (- (calc-playerpoint list-of-cards) goal))
          (* 5 (- (calc-playerpoint list-of-cards) goal))
          )
      (if (all-same-color list-of-cards)
          (quotient (- goal (calc-playerpoint list-of-cards)) 2)
          (- goal (calc-playerpoint list-of-cards))
          )
      )
  )

;--------------------------------------------------------------------
; 4.6
; (find-steps list-of-cards list-of-moves goal) -> list?
; list-of-cards -> list?
; list-of-moves -> list?
; goal -> number?
;
; Find all steps that are made before game over, and puts them in list.
;
; Example:
; > ( find-steps '((H . 3) (H . 2) (H . A) (D . J) (D . Q) (C . J)) '(draw draw draw discard) 16 )
; => '((draw (H . 3)) (draw (H . 2)) (draw (H . A)) (discard (H . 3)))
(define (find-steps list-of-cards list-of-moves goal) (aux-find-steps list-of-cards list-of-moves goal '() '()))

; (aux-find-steps cards moves goal acc helds) -> list?
; cards -> list?
; moves -> list?
; goal -> number?
; acc -> list?
; helds -> list?
;
; Find all steps that are made before game over, and puts them in list. This function is used find steps function.
;
; Example:
; > (aux-find-steps '((H . 3) (H . 2) (H . A) (D . J) (D . Q) (C . J)) '(draw draw draw discard) 16 '() '())
; => '((draw (H . 3)) (draw (H . 2)) (draw (H . A)) (discard (H . 3)))
(define (aux-find-steps cards moves goal acc helds)
  (if (zero? (length moves)) acc
       (cond
         ((< goal (calc-playerpoint helds)) acc)
         ((and (equal? (car moves) 'draw) (zero? (length cards))) acc)
         ((and (equal? (car moves) 'discard) (zero? (length helds))) acc)
         ((= (length moves) 0) acc)
         ((equal? (car moves) 'draw) (aux-find-steps (cdr cards) (cdr moves) goal (append acc (cons (cons (car moves) (cons (car cards) '())) '())) (append helds (cons (car cards) '()))))
         (else (aux-find-steps cards (cdr moves) goal (append acc (cons (cons (car moves) (cons (car helds) '()))'())) (cdr helds)))
         )
      )
  )

;--------------------------------------------------------------------
; 4.10
; (play list-of-cards list-of-moves goal) -> number?
; list-of-cards -> list?
; list-of-moves -> list?
; goal -> number?
;
; Plays the card games and returns the player point
;
; Example:
; > ( play '((H . 3) (H . 2) (H . A) (D . J) (D . Q) (C . J)) '(draw draw draw discard) 16 )
; => 1
; > (play '((C . 3) (C . 2) (C . A) (S . J) (S . Q) (H . J)) '(draw draw discard discard discard discard discard draw) 14) 
; => 7 
(define (play list-of-cards list-of-moves goal)
  (calc-score (find-held-cards(find-steps list-of-cards list-of-moves goal)) goal)
  )
