#lang racket

(provide (all-defined-out)) ;; so we can put tests in a second file

;; these definitions are simply for the purpose of being able to run the tests
;; you MUST replace them with your solutions
;;

(define (sequence low high stride)
  (if (> low high)
	  '()
	  (cons low (sequence (+ low stride) high stride))))

(define (string-append-map xs suffix)
  (map (lambda (x) (string-append x suffix)) xs))
 


(define (list-nth-mod xs n)
  (if (< n 0)
	  (error "list-nth-mod: negative number")
	  (if (null? xs)
		  (error "list-nth-mod: empty list")
		  (let ([i (remainder n (length xs))])
			(car (list-tail xs i))))))

(define (stream-for-n-steps s n)
  (if (= n 0)
	  '()
	  (cons (car (s)) (stream-for-n-steps (cdr (s)) (- n 1)))))

(define funny-number-stream
  (letrec ([f (lambda (x)
				(cons x (lambda ()
						  (f (cond [(= 0 (modulo x 5)) (+ 1 (* -1 x))]
								 [(= 0 (modulo (+ 1 x) 5)) (* -1 (+ 1 x))]
								 [#t (+ 1 x)])))))])
	(lambda () (f 1))))

(define cat-then-dog
  (letrec ([f (lambda (x y)
				(cons x (lambda () (f y x))))])
	(lambda () (f "cat.jpg" "dog.jpg"))))

(define (stream-add-zero s)
	(lambda () (cons (cons 0 (car (s))) (stream-add-zero (cdr (s))))))

(define (cycle-lists xs ys)
  (letrec ([f (lambda (n)
				(cons (cons (list-nth-mod xs n) (list-nth-mod ys n))
					  (lambda () (f (+ 1 n)))))])
	(lambda () (f 0))))

(define (vector-assoc v vec)
  (letrec ([f (lambda (n)
				(if (< n (vector-length vec))
					(let ([v1 (vector-ref vec n)])
					  (if (pair? v1)
						  (if (equal? v (car v1))
							  v1
							  (f (+ 1 n)))
						  (f (+ 1 n))))
					#f))])
	(f 0)))

(define (cached-assoc xs n)
  (let ([cache (make-vector n #f)]
		[index 0])
	(lambda (v)
	  (let ([hit (vector-assoc v cache)])
		(if hit
			hit
			(let ([hit (assoc v xs)])
			  (begin
				(vector-set! cache (modulo index n) hit)
				(set! index (+ 1 index))
				hit)))))))

(define-syntax while-less
  (syntax-rules (do)
	[(while-less e1 do e2)
	 (letrec ([e e1]
			  [f (lambda () (if (< e2 e)
								(f)
								#t))])
	   (f))]))
