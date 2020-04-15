(* Assign 03 Provided Code *)

(*  Version 1.0 *)

exception NoAnswer

datatype pattern = Wildcard
                 | Variable of string
                 | UnitP
                 | ConstP of int
                 | TupleP of pattern list
                 | ConstructorP of string * pattern

datatype valu = Const of int
              | Unit
              | Tuple of valu list
              | Constructor of string * valu

(* Description of g:
* g takes three arguments -
* f1: () -> int
* f2: string -> int
* p: pattern
*
* If p is a Wildcard, g applies f1 to it,
* and if p is a Variable , g applies f2 * to it.
* Otherwise, g either tries to "unwrap" * p
* into a Wildcard or Variable and call itself on it
* (if p is a TupleP or ConstructorP) and otherwise
* just returns 0;
*)

fun g f1 f2 p =
let
  val r = g f1 f2
in
  case p of
       Wildcard          => f1 ()
     | Variable x        => f2 x
     | TupleP ps         => List.foldl (fn (p,i) => (r p) + i) 0 ps
     | ConstructorP(_,p) => r p
     | _                 => 0
end


(**** put all your code after this line ****)

fun only_capitals sl =
  List.filter (fn s => Char.isUpper(String.sub(s,0))) sl

fun longest_string1 sl =
let
  val f = fn (s1, s2) =>
    if String.size(s1) > String.size(s2)
    then s1
    else s2
in
  List.foldl f "" sl
end

fun longest_string2 sl =
let
  val f = fn (s1, s2) =>
    if String.size(s1) >= String.size(s2)
    then s1
    else s2
in
  List.foldl f "" sl
end

fun longest_string_helper f =
let
  val g = fn (s1, s2) =>
  if f(String.size(s1), String.size(s2))
  then s1
  else s2
in
  fn sl =>
    List.foldl g "" sl
end

fun longest_string3 sl =
let
  val f = fn(l1, l2) => l1 > l2
in
  longest_string_helper f sl
end

fun longest_string4 sl =
let
  val f = fn(l1, l2) => l1 >= l2
in
  longest_string_helper f sl
end

val longest_capitalized = longest_string1 o only_capitals

val rev_string = String.implode o rev o String.explode;

fun first_answer f =
  fn xs =>
    case xs of
         [] => raise NoAnswer
       | x::xs' =>
           case f x of
                SOME v => v
              | NONE => first_answer f xs'

fun all_answers f =
let
  fun aux (acc, xs) =
    case xs of
         [] => SOME acc
       | x::xs' =>
           case f x of
                SOME v => aux (acc@v, xs')
              | NONE => NONE
in
  fn xs =>
    aux([], xs)
end

val count_wildcards = g (fn x => 1) (fn x => 0)

val count_wild_and_variable_lengths =
  g (fn x => 1) (fn x => String.size(x))

fun count_some_var (s, p) =
  g (fn x => 0) (fn x => if x = s then 1 else 0) p

fun check_pat p =
let
  fun get_strings acc p =
  let
    val r = get_strings acc
  in
    case p of
         Variable x => acc@[x]
       | TupleP ps => List.foldl (fn (p,i) => (r p)@i) acc ps
       | ConstructorP(_,p) => acc@(r p)
       | _ => []
  end
  fun has_repeats xs =
    case xs of
         [] => false
       | x::xs' => List.exists (fn x' => x' = x) xs'
in
  not (has_repeats (get_strings [] p))
end

fun match (v, p) =
let
  fun get_bindings acc (v, p) =
  let
    val r = get_bindings acc
  in
    case p of
         Wildcard => acc@[[]]
       | Variable x => acc@[[(x,v)]]
       | UnitP => if v = Unit then acc@[[]] else acc
       | ConstP v' =>
           (case v of
                 Const v'' => if v' = v'' then acc@[[]] else acc
               | _ => acc)
       | TupleP ps =>
           (case v of
                 Tuple vs =>
                   if List.length ps = List.length vs
                   then
                     if List.all (fn x => (case x of SOME _ => true | NONE =>
                     false)) (List.map match (ListPair.zip (vs,ps)))
                     then acc@List.mapPartial match (ListPair.zip (vs,ps))
                     else acc
                   else acc
               | _ => acc)
       | ConstructorP (s1,p') =>
           (case v of
                Constructor(s2,v') => if s1 = s2 then acc@(r (v',p')) else acc
              | _ => acc)
  end
in
  let
    val bs = get_bindings [] (v,p)
  in
    if bs = []
    then NONE
    else SOME (List.concat bs)
  end
end

fun first_match v ps =
  case ps of
       [] => NONE
     | p::ps' =>
         case match (v,p) of
              SOME v' => SOME v'
            | NONE => first_match v ps'
