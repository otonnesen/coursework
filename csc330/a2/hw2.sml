(* if you use this function to compare two strings (returns true if the same
   string), then you avoid some warning regarding polymorphic comparison  *)

fun same_string(s1 : string, s2 : string) =
    s1 = s2

(* put your solutions for Part 1 here *)

fun all_except_option(s: string, sl: string list): string list option =
  case sl of
       [] => NONE
     | s'::sl' => if same_string(s, s')
                  then SOME sl'
                  else case all_except_option(s, sl') of
                            NONE => NONE
                          | SOME sl'' => SOME (s' :: sl'')

fun get_substitutions1(subs: string list list, s: string): string list =
  case subs of
       [] => []
     | sub::subs' => case all_except_option(s, sub) of
                          NONE => get_substitutions1(subs', s)
                        | SOME sub => sub@get_substitutions1(subs', s)

fun get_substitutions2(subs: string list list, s: string): string list =
let
  fun aux(subs: string list list, acc: string list): string list =
    case subs of
         [] => acc
       | sub::subs' => case all_except_option(s, sub) of
                            NONE => aux(subs', acc)
                          | SOME sub' => aux(subs', acc@sub')
in
  aux(subs, [])
end

fun similar_names(subs: string list list, full_name:
  {first:string,middle:string,last:string}):
  {first:string,middle:string,last:string} list =
let
  val {first=f,middle=m,last=l} = full_name
  fun aux(subs: string list, acc: {first:string,middle:string,last:string}
  list):
    {first:string,middle:string,last:string} list =
      case subs of
           [] => acc
         | sub::subs' => aux(subs', acc@{first=sub,middle=m,last=l}::[])
in
  aux(get_substitutions2(subs, f), full_name::[])
end

(************************************************************************)
(* Game *)

(* you may assume that Num is always used with valid values 2, 3, ..., 10 *)

datatype suit = Clubs | Diamonds | Hearts | Spades
datatype rank = Jack | Queen | King | Ace | Num of int
type card = suit * rank

datatype color = Red | Black
datatype move = Discard of card | Draw


exception IllegalMove

(* put your solutions for Part 2 here *)

fun card_color(c: card): color =
let
  val (s,r) = c
in
  case s of
       Clubs => Black
     | Spades => Black
     | Diamonds => Red
     | Hearts => Red
end

fun card_value(c: card): int =
let
  val (s,r) = c
in
  case r of
       Jack => 10
     | Queen => 10
     | King => 10
     | Ace => 11
     | Num i => i
end

fun remove_card(cs: card list, c: card, e: exn): card list =
  case cs of
       [] => raise e
     | c'::cs' => if c' = c
                  then cs'
                  else c' :: remove_card(cs', c, e)

fun all_same_color(cs: card list): bool =
  case cs of
       [] => true
     | c::cs' =>
         case cs' of
              [] => true
            | c'::cs'' =>
                if card_color(c) = card_color(c')
                then all_same_color(cs')
                else false

fun sum_cards(cs: card list): int =
let
  fun aux(cs: card list, acc: int) =
    case cs of
         [] => acc
       | c::cs' => aux(cs', acc+card_value(c))
in
  aux(cs, 0)
end

fun score(cs: card list, goal: int) =
let
  val sum = sum_cards(cs)
  fun final_score(prelim_score: int) =
    if all_same_color(cs)
    then prelim_score div 2
    else prelim_score
in
  if sum > goal
  then
    final_score(2 * (sum-goal))
  else
    final_score(goal-sum)
end

fun officiate(cs: card list, ms: move list, goal: int): int =
let
  fun run_game(cs: card list, held: card list, ms: move list, goal: int):
  int =
    case ms of
         [] => score(held, goal)
       | m::ms' =>
           case m of
                Discard c =>
                run_game(cs, remove_card(held, c, IllegalMove), ms', goal)
              | Draw =>
                  case cs of
                       [] => score(held, goal)
                     | c::cs' =>
                         if sum_cards(c::held) > goal
                         then score(c::held, goal)
                         else run_game(cs', c::held, ms', goal)
in
  run_game(cs, [], ms, goal)
end

val test1_0=all_except_option("3",["3","3"]) = SOME ["3"];
val test2_0=get_substitutions1([["David", "Dave"]], "Oliver") = [];
val test3_0=get_substitutions2([[]], "David") = [];
val test4_0=similar_names([["Oliver", "Ollie", "Liv"],["David", "Davie"]],
{last="Tonnesen",middle="P",first="Oliver"}) = [
{first="Oliver",last="Tonnesen",middle="P"},
{first="Ollie",last="Tonnesen",middle="P"},
{first="Liv",last="Tonnesen",middle="P"}];
val test5_0=card_color((Spades,Num 12)) = Black;
val test6_0=card_value((Spades,Num 12)) = 12;
val test7_0=remove_card([(Diamonds, Num 2),(Diamonds, Num 2)], (Diamonds, Num
2), IllegalMove) = [(Diamonds, Num 2)];
val test8_0=all_same_color([(Diamonds, Num 2),(Hearts, Num 21),(Hearts, Jack)])
= true;
val test9_0=sum_cards([(Diamonds, Num 20), (Spades, Num 20)]) = 40;
val test10_0=score([(Diamonds, Num 20), (Spades, Num 20)], 4) = 36 * 2;
val test11_0=officiate([(Diamonds, Num 20), (Spades, Num 20)], [Draw, Discard
(Diamonds, Num 20)], 4) = 16;
