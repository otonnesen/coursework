;; Programming Languages, Homework 5 version 1.1
#lang racket

(provide (all-defined-out)) ;; so we can put tests in a second file

;; definition of structures for MUPL programs - Do NOT change
(struct var  (string) #:transparent)  ;; a variable, e.g., (var "foo")
(struct int  (num)    #:transparent)  ;; a constant number, e.g., (int 17)
(struct add  (e1 e2)  #:transparent)  ;; add two expressions
(struct ifgreater (e1 e2 e3 e4)    #:transparent) ;; if e1 > e2 then e3 else e4
(struct fun  (nameopt formal body) #:transparent) ;; a recursive(?) 1-argument function
(struct call (funexp actual)       #:transparent) ;; function call
(struct mlet (var e body) #:transparent) ;; a local binding (let var = e in body)
(struct apair (e1 e2)     #:transparent) ;; make a new pair
(struct fst  (e)    #:transparent) ;; get first part of a pair
(struct snd  (e)    #:transparent) ;; get second part of a pair
(struct aunit ()    #:transparent) ;; unit value -- good for ending a list
(struct isaunit (e) #:transparent) ;; evaluate to 1 if e is unit else 0

;; a closure is not in "source" programs; it is what functions evaluate to
(struct closure (env fun) #:transparent)

;; Problem A

;; CHANGE (put your solutions here)
(define (mupllist->racketlist lst)
  (if (equal? lst (aunit))
	  '()
	  (append (list (if (apair? (apair-e1 lst))
						(mupllist->racketlist (apair-e1 lst))
						(apair-e1 lst)))
			  (mupllist->racketlist (apair-e2 lst)))))
(define (racketlist->mupllist lst)
  (if (null? lst)
	  (aunit)
	  (apair (if (list? (car lst))
			 (racketlist->mupllist (car lst))
			 (car lst))
			 (racketlist->mupllist (cdr lst)))))

;; Problem B

;; lookup a variable in an environment
;; Do NOT change this function
(define (envlookup env str)
  (cond [(null? env) (error "unbound variable during evaluation" str)]
        [(equal? (car (car env)) str) (cdr (car env))]
        [#t (envlookup (cdr env) str)]))

;; Do NOT change the two cases given to you.
;; DO add more cases for other kinds of MUPL expressions.
;; We will test eval-under-env by calling it directly even though
;; "in real life" it would be a helper function of eval-exp.
(define (eval-under-env e env)
  (cond [(var? e)
         (envlookup env (var-string e))]
		[(int? e)
		 e]
		[(aunit? e)
		 (aunit)]
        [(add? e)
         (let ([v1 (eval-under-env (add-e1 e) env)]
               [v2 (eval-under-env (add-e2 e) env)])
           (if (and (int? v1)
                    (int? v2))
               (int (+ (int-num v1)
                       (int-num v2)))
               (error "MUPL addition applied to non-number")))]
		[(fun? e)
		 (closure env e)]
		[(ifgreater? e)
		 (let ([v1 (eval-under-env (ifgreater-e1 e) env)]
			   [v2 (eval-under-env (ifgreater-e2 e) env)])
		   (if (and (int? v1)
					(int? v2))
			   (if (> (int-num v1) (int-num v2))
				   (eval-under-env (ifgreater-e3 e) env)
				   (eval-under-env (ifgreater-e4 e) env))
			   (error "MUPL greaterthan applied to non-number")))]
		[(call? e)
		 (let ([cl (eval-under-env (call-funexp e) env)]
			   [param (eval-under-env (call-actual e) env)])
		   (if (closure? cl)
			   (let ([f (closure-fun cl)]
					 [cenv (closure-env cl)])
				 (if (fun-nameopt f)
					 (eval-under-env (fun-body f) (cons (cons (fun-formal f) param) (cons (cons (fun-nameopt f) cl) cenv)))
					 (eval-under-env (fun-body f) (cons (cons (fun-formal f) param) cenv))))
			   (error "MUPL call applied to non-closure")))]
		[(mlet? e)
		 (let ([v (eval-under-env (mlet-e e) env)])
				  (eval-under-env (mlet-body e) (cons (cons (mlet-var e) v) env)))]
		[(apair? e)
		 (let ([v1 (eval-under-env (apair-e1 e) env)]
			   [v2 (eval-under-env (apair-e2 e) env)])
		   (apair v1 v2))]
		 [(fst? e)
		  (let ([v (eval-under-env (fst-e e) env)])
			(if (apair? v)
				(apair-e1 v)
				(error "MUPL fst applied to non-pair")))]
		 [(snd? e)
		  (let ([v (eval-under-env (snd-e e) env)])
			(if (apair? v)
				(apair-e2 v)
				(error "MUPL snd applied to non-pair")))]
		 [(isaunit? e)
		  (let ([v (eval-under-env (isaunit-e e) env)])
			(if (equal? v (aunit))
				(int 1)
				(int 0)))]
        [#t (error (format "bad MUPL expression: ~v" e))]))

;; Do NOT change
(define (eval-exp e)
  (eval-under-env e null))

;; Problem C

(define (ifaunit e1 e2 e3)
  (ifgreater (isaunit e1) (int 0)
			 e2
			 e3))

(define (mlet* lstlst e2)
  (if (null? lstlst)
	  e2
	  (mlet (car (car lstlst))
			(cdr (car lstlst))
			(mlet* (cdr lstlst) e2))))

(define (ifeq e1 e2 e3 e4)
  (ifgreater e1 e2
	  e4
	  (ifgreater e2 e1
		  e4
		  e3)))

;; Problem D

(define mupl-map
  (fun "mupl-map" "f"
	(fun #f "lst"
		 (ifaunit (var "lst")
				  (aunit)
				  (apair
					(call (var "f") (fst (var "lst")))
					(call (call (var "mupl-map") (var "f")) (snd (var "lst"))))))))
;; this binding is a bit tricky. it must return a function.
;; the first two lines should be something like this:
;;
;;   (fun "mupl-map" "f"    ;; it is  function "mupl-map" that takes a function f
;;       (fun #f "lst"      ;; and it returns an anonymous function
;;          ...
;;
;; also remember that we can only call functions with one parameter, but
;; because they are curried instead of
;;    (call funexp1 funexp2 exp3)
;; we do
;;    (call (call funexp1 funexp2) exp3)
;; 

(define mupl-mapAddN
  (mlet "map" mupl-map
        (fun #f "N"
			 (mlet "addN" (fun #f "a" (add (var "N") (var "a")))
			 (call (var "map") (var "addN"))))))
