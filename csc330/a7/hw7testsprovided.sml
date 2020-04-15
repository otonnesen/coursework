use "hw7.sml";

(* Must implement preprocess_prog and Shift before running these tests *)

fun real_equal(x,y) = Real.compare(x,y) = General.EQUAL;

(*---------------------------------PREPROCESS--------------------------*)


val test1msg = "preprocess converts a LineSegment to a Point"

fun test1 () =
    let
      val point = preprocess_prog(LineSegment(3.2,4.1,3.2,4.1))
      val Point(c,d) = Point(3.2,4.1)
    in
      case point of
          Point(a,b) => real_equal(a,c) andalso real_equal(b,d)
        | _ => false
    end

val test2msg = "preprocess flips an improper LineSegment"

fun test2 () =
    let
      val line1 = preprocess_prog (LineSegment(3.2,4.1,~3.2,~4.1))
      val LineSegment(e,f,g,h) = LineSegment(~3.2,~4.1,3.2,4.1)
    in
      case line1 of
          LineSegment(a,b,c,d) =>
          real_equal(a,e) andalso real_equal(b,f) andalso real_equal(c,g) andalso real_equal(d,h)
        | _ => false
    end

val test3msg = "preprocess flips an improper LineSegment"
fun test3() =
    let
      val line = preprocess_prog (LineSegment(3.2,4.1,3.2,~4.1))
      val LineSegment(e,f,g,h) = LineSegment(3.2,~4.1,3.2,4.1)
    in
      case line of
          LineSegment(a,b,c,d) =>
	  real_equal(a,e) andalso real_equal(b,f) andalso real_equal(c,g) andalso real_equal(d,h)
        | _ => false
    end

val test4msg = "preprocess flips an improper LineSegment"
fun test4() =
    let
      val line = preprocess_prog (LineSegment(3.2,4.1,3.2,6.1))
      val LineSegment(e,f,g,h) = LineSegment(3.2,4.1,3.2,6.1)
    in
      case line of
          LineSegment(a,b,c,d) =>
	  real_equal(a,e) andalso real_equal(b,f) andalso real_equal(c,g) andalso real_equal(d,h)
        | _ => false
    end;

val test5msg = "preprocess flips an improper LineSegment"
fun test5() =
    let
      val line = preprocess_prog (LineSegment(1.2,4.1,3.2,6.1))
      val LineSegment(e,f,g,h) = LineSegment(1.2,4.1,3.2,6.1)
    in
      case line of
          LineSegment(a,b,c,d) =>
	  real_equal(a,e) andalso real_equal(b,f) andalso real_equal(c,g) andalso real_equal(d,h)
        | _ =>  false
    end;

val test6msg = "preprocess flips an improper LineSegment"
fun test6() =

    let
      val line = preprocess_prog (LineSegment(6.2,4.1,3.2,6.1))
      val LineSegment(e,f,g,h) = LineSegment(3.2,6.1,6.2,4.1)
    in
      case line of
          LineSegment(a,b,c,d) =>
          real_equal(a,e) andalso real_equal(b,f) andalso real_equal(c,g) andalso real_equal(d,h)
        | _ => false
    end;
(*---------------------------------PREPROCESS--------------------------*)


(*---------------------------------eval_prog--------------------------*)
(* eval_prog tests with Shift*)
(* Using a NoPoints *)
val test7msg = "eval_prog with NoPoints"
fun test7() =
    case (eval_prog (preprocess_prog (Shift(3.0, 4.0, NoPoints)), [])) of
	NoPoints => true
      | _ => false

(* Using a Point *)
val test8msg = "eval_prog with empty environment"
fun test8() =
    let
      val point = (eval_prog (preprocess_prog (Shift(3.0, 4.0, Point(4.0,4.0))), []))
      val Point(c,d) = Point(7.0,8.0)
    in
      case point of
          Point(a,b) =>
          real_equal(a,c) andalso real_equal(b,d)
       | _ => false
    end;

val test9msg = "eval_prog with 'a' in environment"
fun test9() =
    (* Using a Var *)
    let
      val point = (eval_prog (Shift(3.0,4.0,Var "a"), [("a",Point(4.0,4.0))]))
      val Point(c,d) = Point(7.0,8.0)
    in
      case point of
          Point(a,b) => real_equal(a,c) andalso real_equal(b,d)
       | _ => false
    end;

val test10msg = "eval_prog with Line"
fun test10() =
    (* Using a Line *)
    let
      val line = (eval_prog (Shift(3.0,4.0, Line(1.0, 5.0)), []))
      val Line(c,d) = Line(1.0,6.0)
    in
      case line of
          Line(a,b) => real_equal(a,c) andalso real_equal(b,d)
       | _ => false
    end;

val test11msg = "eval_prog with LineSegment"
fun test11() =
    (* Using a LineSegment *)
    let
      val line = (eval_prog (Shift(3.0,4.0, LineSegment(1.0, 2.0, 3.0, 4.0)), []))
      val LineSegment(c,d,x2,y2) = LineSegment(4.0, 6.0, 6.0, 8.0)
    in
      case line of
          LineSegment(a,b,x1,y1) =>
             real_equal(a,c) andalso real_equal(b,d) andalso real_equal(x1,x2) andalso real_equal(y1,y2)
       | _ => false
    end;

val test12msg = "eval_prog with shadowing 'a' in environment"
fun test12() =
    (* With Variable Shadowing *)
    let
      val point = (eval_prog (Shift(3.0,4.0,Var "a"), [("a",Point(4.0,4.0)),("a",Point(1.0,1.0))]))
      val Point(c,d) = Point(7.0,8.0)
    in
      case point of
          Point(a,b) => real_equal(a,c) andalso real_equal(b,d)
       | _ => false
    end;

val tests = [
  (test1, test1msg),
  (test2, test2msg),
  (test3, test3msg),
  (test4, test4msg),
  (test5, test5msg),
  (test6, test6msg),
  (test7, test7msg),
  (test8, test8msg),
  (test9, test9msg),
  (test10, test10msg),
  (test11, test11msg),
  (test12, test12msg)
];

fun toInt (b: bool) =
    if b then 1 else 0;

fun sum(a:int, b:int) =
    a + b;

fun tests_passed(t: string, tests: bool list) =
    let
      val len = length tests
      val count = foldl sum 0 (map toInt tests)
    in
      print ("\n**Test " ^ t ^ " "^ (if count = len then "passed" else "failed")
             ^ "\n  "^ Int.toString(count) ^ " out of " ^ Int.toString(len) ^ "\n\n");
      count = len
    end;

val results = map (fn (f,msg) => f()) tests

val totalPassed = tests_passed("**Overall", results);
